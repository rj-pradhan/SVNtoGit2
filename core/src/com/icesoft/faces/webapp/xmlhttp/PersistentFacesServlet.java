/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.faces.webapp.xmlhttp;

import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.DOMResponseWriter;
import com.icesoft.faces.env.ServletEnvironmentRequest;
import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.util.IdGenerator;
import com.icesoft.util.SeamUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

public class PersistentFacesServlet extends HttpServlet {
    private static final String COMPRESS_RESOURCES =
            "com.icesoft.faces.compressResources";
    private static final String SYNCHRONOUS_UPDATE =
            "com.icesoft.faces.synchronousUpdate";
    private static final String HEARTBEAT_INTERVAL =
            "com.icesoft.faces.heartbeatInterval";
    private static final String HEARTBEAT_TIMEOUT =
            "com.icesoft.faces.heartbeatTimeout";
    private static final String HEARTBEAT_RETRIES =
            "com.icesoft.faces.heartbeatRetries";
    private static final String CONNECTION_TIMEOUT =
            "com.icesoft.faces.connectionTimeout";
    private static final String CONNECTION_LOST_REDIRECT_URI =
            "com.icesoft.faces.connectionLostRedirectURI";
    private static final String JAVASCRIPT_BLOCKED_REDIRECT_URI =
            "com.icesoft.faces.javascriptBlockedRedirectURI";
    public static final String UPLOAD_MAX_FILE_SIZE =
            "com.icesoft.faces.uploadMaxFileSize";
    public static final String UPLOAD_NOTHING_KEY =
            "com.icesoft.faces.uploadNothing";
    public static final String CURRENT_VIEW_NUMBER =
            "com.icesoft.faces.currentViewNumber";
    public static final String FILENAME_KEY = "_filename";
    public static final String MIMETYPE_KEY = "_mimetype";
    private static final long STARTUP_TIME = System.currentTimeMillis();
    private static final long EXPIRATION_TIME = STARTUP_TIME + 2629743830l;//startup time + one month

    private ServletConfig config;
    private PersistentFacesCommonlet commonlet;
    private int globalViewNumber = 1000;
    private long uploadMaxFileSize;

    private ResponseStateManager stateManager;

    private static final Log log =
            LogFactory.getLog(PersistentFacesServlet.class);
    private long connectionTimeout;
    private long heartbeatRetries;
    private long heartbeatTimeout;
    private long heartbeatInterval;
    private boolean synchronousUpdate;
    private URI connectionLostRedirectURI;
    private URI javascriptBlockedRedirectURI;
    private boolean compressResources;

    private final String seamShortcutKeywords[] = {
            "actionMethod", "dataModelSelection"
    };

    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        this.commonlet = new PersistentFacesCommonlet();
        this.commonlet.init(this.commonlet.getInitParams(this.config));
        ServletContext servletContext = config.getServletContext();
        try {
            commonlet.concurrentDOMViews = Boolean
                    .valueOf(servletContext.getInitParameter(
                            PersistentFacesCommonlet.CONCURRENT_DOM_VIEWS))
                    .booleanValue();
        } catch (NullPointerException e) {
            commonlet.concurrentDOMViews = false;
        }
        try {
            compressResources = Boolean.valueOf(
                    servletContext.getInitParameter(COMPRESS_RESOURCES))
                    .booleanValue();
        } catch (NullPointerException e) {
            compressResources = false;
        }
        try {
            synchronousUpdate = Boolean.valueOf(
                    servletContext.getInitParameter(SYNCHRONOUS_UPDATE))
                    .booleanValue();
        } catch (NullPointerException e) {
            synchronousUpdate = false;
        }
        try {
            heartbeatInterval = Long.parseLong(
                    servletContext.getInitParameter(HEARTBEAT_INTERVAL));
        } catch (NumberFormatException e) {
            heartbeatInterval = 20000;
        }
        try {
            heartbeatTimeout = Long.parseLong(
                    servletContext.getInitParameter(HEARTBEAT_TIMEOUT));
        } catch (NumberFormatException e) {
            heartbeatTimeout = 3000;
        }
        try {
            heartbeatRetries = Long.parseLong(
                    servletContext.getInitParameter(HEARTBEAT_RETRIES));
        } catch (NumberFormatException e) {
            heartbeatRetries = 3;
        }
        try {
            connectionTimeout = Long.parseLong(
                    servletContext.getInitParameter(CONNECTION_TIMEOUT));
        } catch (NumberFormatException e) {
            connectionTimeout = 30000;
        }
        try {
            connectionLostRedirectURI = URI.create(
                    servletContext.getInitParameter(
                            CONNECTION_LOST_REDIRECT_URI));
        } catch (IllegalArgumentException e) {
            connectionLostRedirectURI = null;
        } catch (NullPointerException e) {
            connectionLostRedirectURI = null;
        }
        try {
            javascriptBlockedRedirectURI = URI.create(
                    servletContext.getInitParameter(
                            JAVASCRIPT_BLOCKED_REDIRECT_URI));
        } catch (IllegalArgumentException e) {
            javascriptBlockedRedirectURI = null;
        } catch (NullPointerException e) {
            javascriptBlockedRedirectURI = null;
        }
        try {
            uploadMaxFileSize = Long.parseLong(
                    servletContext.getInitParameter(UPLOAD_MAX_FILE_SIZE));
        } catch (NumberFormatException e) {
            uploadMaxFileSize = 2 * 1048576;
        }

        stateManager = ResponseStateManager
                .getResponseStateManager(servletContext);

        if (log.isTraceEnabled()) {
            log.trace("SYNCHRONOUS_UPDATE = " + synchronousUpdate);
            log.trace("HEARTBEAT_INTERVAL=" + heartbeatInterval
                      + "; HEARTBEAT_TIMEOUT=" + heartbeatTimeout
                      + "; HEARTBEAT_RETRIES=" + heartbeatRetries
                      + "; CONNECTION_TIMEOUT=" + connectionTimeout
                      + "; UPLOAD_MAX_FILE_SIZE=" + uploadMaxFileSize);
        }
    }

    protected void service(HttpServletRequest request,
                           HttpServletResponse response)
            throws IOException, ServletException {
        if( log.isTraceEnabled() ) {
            log.trace("service()");
        }
        if (this.redirectOnJavascriptBlocked(request, response)) return;
        // Set up some information in the session including attributes required
        // by the DOM renderers.
        HttpSession session = request.getSession();

        // If not already there, add our own unique ICEfaces ID to the session
        // and as a cookie in the response
        String iceID =
                (String) session
                        .getAttribute(ResponseStateManager.ICEFACES_ID_KEY);
        if( log.isTraceEnabled() ) {
            log.trace("service()  session id: " + session.getId() +
                      "  iceID: " + iceID);
        }
        if (iceID == null) {
            /*
             * For certain tasks, we cannot rely on the application server not
             * messing with the session ID so we have our own unique ICEfaces ID
             * that we include in every session.
             *
             * The original reason for adding this was to use it as part of a
             * reliable key so that we could associate response updates in the
             * Asynchronous HTTP Server. Weblogic seemed to munge the session ID
             * in certain ways that caused us some grief when trying to use it
             * as a key.
             */
            iceID =
                    IdGenerator.create(
                            config.getServletContext()
                                    .getResource("/").getPath());
            session.setAttribute(ResponseStateManager.ICEFACES_ID_KEY, iceID);
        }

        // Bug 794
        // Synchronizing concurrent operations on iceID instead of servlet's
        // service method.
        synchronized (iceID) {
            ContextEventRepeater.iceFacesIdRetrieved(session, iceID);

            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String requestURI = (String) httpRequest.getAttribute(
                    BridgeExternalContext.INCLUDE_REQUEST_URI);
            if( log.isTraceEnabled() ) {
                log.trace("service()  requestURI: " + requestURI);
            }
            if (null == requestURI) {
                requestURI = httpRequest.getRequestURI();
                if( log.isTraceEnabled() ) {
                    log.trace("service()  requestURI2: " + requestURI);
                }
            }

            boolean compress = compressResources &&
                               (request.getHeader("Accept-Encoding")
                                       .indexOf("gzip") > -1);
            if (this.writeJSResource(requestURI, request, response, iceID,
                                     compress)) return;
            if (this.writeCSSResource(requestURI, request, response, compress)) return;
            if (this.writeIFrame(requestURI, response)) return;

            // Set up the cache control headers on the response
            setCacheControls(response);

            int viewNumber = -1;
            if (commonlet.concurrentDOMViews) {
                //check for a redirect view number to preserve
                try {
                    int redirectViewNumber = Integer.parseInt(
                            getCookie(request, "redirectViewNumber"));
                    viewNumber = redirectViewNumber;
                } catch (NumberFormatException e) {
                }
                if (-1 == viewNumber)  {
                    viewNumber = getNextViewNumber();
                }
                //ContextEventRepeater.viewNumberRetrieved(session, viewNumber);
            } else {
                viewNumber = 1;
            }
            if( log.isTraceEnabled() ) {
                log.trace("service()  viewNumber: " + viewNumber);
            }
            ContextEventRepeater.viewNumberRetrieved(session, viewNumber);
            // Set up and configure our own FacesContext and ExternalContext
            // implementations. Our implementations should take care to ensure
            // that the type of environment is handled transparently.  We need
            // to add this key to the request so that the FacesContextFactory
            // can properly determine which FacesContext to provide.  If we're
            // coming from this servlet, then we need the ICEfaces implementations.
            // Otherwise, we're running in "plain" Faces mode.

            request.setAttribute(PersistentFacesCommonlet.SERVLET_KEY,
                                 PersistentFacesCommonlet.PERSISTENT);

            FacesContext facesContext = commonlet.getFacesContext(
                    config.getServletContext(),
                    request,
                    response);

            // Copy the Seam request parameters to the external context.
            // If the author puts in <f:param> tags for the purposes of passing args in
            // a get request, how else would they be copied?? 

            copyRequestParameters(request.getParameterMap(),
                                  facesContext
                                          .getExternalContext().getRequestParameterMap());


            facesContext.getExternalContext().getRequestMap()
                    .put(PersistentFacesCommonlet.SERVLET_KEY,
                         PersistentFacesCommonlet.PERSISTENT);
            facesContext.getExternalContext().getRequestParameterMap()
                    .put("viewNumber", String.valueOf(viewNumber));
            facesContext.getExternalContext().getSessionMap()
                    .put(CURRENT_VIEW_NUMBER, String.valueOf(viewNumber));

            Object requestAttribute;
            Map requestMap =
                    facesContext.getExternalContext().getRequestMap();
            //requestMap appears to contain stale values; a new standard
            //HTTP request should reset it.  This behaviour should be
            //removed when JSF has "process" or "conversation" scope
            primeRequestMap(request, requestMap);

            requestAttribute = httpRequest.getAttribute(
                    BridgeExternalContext.INCLUDE_REQUEST_URI);
            requestMap.remove(BridgeExternalContext.INCLUDE_REQUEST_URI);
            if (null != requestAttribute) {
                requestMap.put(
                        BridgeExternalContext.INCLUDE_REQUEST_URI,
                        requestAttribute);
            }

            requestAttribute = httpRequest.getAttribute(
                    BridgeExternalContext.INCLUDE_SERVLET_PATH);
            requestMap.remove(BridgeExternalContext.INCLUDE_SERVLET_PATH);
            if (null != requestAttribute) {
                requestMap.put(
                        BridgeExternalContext.INCLUDE_SERVLET_PATH,
                        requestAttribute);
            }

            session.setAttribute("isJavaBrowser", "false");
            session.setAttribute("requiresJavaScript", "true");

            // If we don't have our persistent state yet, initialize one
            PersistentFacesState.setLocalInstance(
                    facesContext.getExternalContext().getSessionMap(),
                    String.valueOf(viewNumber));

            session.removeAttribute(DOMResponseWriter.getOldDOMKey(
                    String.valueOf(viewNumber)));

            // Allow for downstream removal of Seam PageContext objects
            doClearSeamContexts();

            // Execute the normal lifecycle then set up a persistent environment
            try {
                Lifecycle lifecycle = commonlet.getLifecycle();
                FacesContext ctxt =
                        PersistentFacesState.getInstance().facesContext;
                if( log.isTraceEnabled() ) {
                    log.trace("service()  FacesContext: " + ctxt);
                }

                // Bug 350: If the FacesContext is not available (likely the very first request)
                // then there is no need to synchronize the lifecycle calls.  If there is a
                // available FacesContext, it should be used to synchronize on to ensure thread-
                // safe DOM rendering.  This ensures that other render calls (e.g. server-side
                // renders) do not get the DOM into a nasty state.
                if (ctxt == null) {
                    lifecycle.execute(facesContext);
                    lifecycle.render(facesContext);
                } else {
                    synchronized (ctxt) {
                        lifecycle.execute(facesContext);
                        lifecycle.render(facesContext);
                    }
                }

                setupPersistentContext(
                        config.getServletContext(), request, viewNumber );

                // Set the correct ResponseState as the IncrementalNodeWriter for this
                // combination of session and view number, then store the state so that is
                // accessible from the async server.
                String viewNumberString = String.valueOf(viewNumber);
                ResponseState state =
                        stateManager.getState(session, viewNumberString);
                state.setFocusID(request.getParameter("focus"));

                //Bug 256:  We need to include the view number as part of the key
                session.setAttribute(viewNumberString + "/" +
                                     ResponseState.STATE, state);
            } catch (Exception e) {

                if (log.isErrorEnabled()) {
                    log.error(
                            "Exception executing lifecycle or setting up persistent context. " +
                            e.getMessage(), e);
                }
                throw new ServletException(e);
            } finally {
                if (null != facesContext) {
                    facesContext.release();
                }
                PersistentFacesState.clearLocalInstance();
            }
        }
    }

    /**
     * Any request handled by the PersistentFacesServlet should have the Seam
     * PageContext removed from our internal context complex. But we cannot do
     * it here, since the machinery is not yet in place, so put a flag into
     * the external context, allowing someone else to do it later.  
     */
    private void doClearSeamContexts() {

        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getExternalContext().getRequestMap().put(
                PersistentFacesCommonlet.REMOVE_SEAM_CONTEXTS, Boolean.TRUE);
    }

    private boolean redirectOnJavascriptBlocked(HttpServletRequest request,
                                                HttpServletResponse response)
            throws IOException {
        String uri = request.getRequestURI();
        if (uri.endsWith("/javascript-blocked")) {
            if (javascriptBlockedRedirectURI == null) {
                response.sendError(403, "Javascript not enabled.");
            } else {
                response.sendRedirect(javascriptBlockedRedirectURI.toString());
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Copy all the parameters from the request map to the externalContext Map
     *
     * @param requestParameters  From this map
     * @param externalContextMap Into this map
     */
    private void copyRequestParameters(Map requestParameters,
                                       Map externalContextMap) {


        boolean seamEnabled = SeamUtilities.isSeamEnvironment();
        Iterator i = requestParameters.keySet().iterator();
        Object key;
        Object value;
        while (i.hasNext()) {
            key = i.next();
            value = requestParameters.get(key);

            // Check for certain Seam keywords that need a certain
            // environment to work properly. Set a flag indicating to
            // the D2DViewHandler to do the right thing.
            if (seamEnabled) {
                for (int idx = 0; idx < seamShortcutKeywords.length; idx++) {

                    if (seamShortcutKeywords[idx].equalsIgnoreCase(key.toString())) {
                        externalContextMap.put(
                                PersistentFacesCommonlet.SEAM_LIFECYCLE_SHORTCUT,
                                Boolean.TRUE);
                        log.debug("Seam shortcut keyword found: " + key.toString() );
                    }
                }
            }


            if (key instanceof String && value instanceof String) {
                externalContextMap.put(key, value);
            } else if (key instanceof String && value instanceof String[]) {
//                log.debug("PFS copying key: " + key + ", value: " +
//                          ((String[]) value)[0]);
                externalContextMap.put(key, ((String[]) value)[0]);
            } else {
                log.warn("Not copying key-class: " + key.getClass().getName() +
                         ", value-class: " + value.getClass().getName());
            }
        }
    }

    /**
     * Set the cache controls on the response headers. Still in need of some
     * experimentation here.
     *
     * @param response
     */
    private void setCacheControls(ServletResponse response) {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Cache-Control", "no-cache"); //HTTP 1.1
        httpResponse.setHeader("Cache-Control", "no-store"); //HTTP 1.1
        httpResponse.setHeader("Cache-Control", "must-revalidate"); //HTTP 1.1
        httpResponse.setHeader("Pragma", "no-cache"); //HTTP 1.0
        httpResponse.setDateHeader("Expires", 0); //prevents proxy caching
    }

    /**
     * ICEfaces requires a more pesistent set of resources compared to stock JSF
     * applications so here we make that happen by stuffing the required items
     * into the session.
     *
     * @param request
     */
    void setupPersistentContext(ServletContext context,
                                HttpServletRequest request, int viewNumber) {

        // We have our own copy of the request because when a request has completed, we need to
        // ensure access to some of its information.  So we keep relevant information
        // in a environment-friendly (servlet,portlet) container for future use.
        ServletEnvironmentRequest envReq =
                new ServletEnvironmentRequest(request);

        // Create instances of the appropriate contexts and state and stuff it in the session.
        BridgeExternalContext perExtContext =
                new BridgeExternalContext(context, envReq, null);
        BridgeFacesContext persistentContext =
                new BridgeFacesContext(perExtContext);
        perExtContext.getRequestParameterMap()
                .put("viewNumber", String.valueOf(viewNumber));

        //Make sure that any PersistentFacesState instances retrieved
        //by applications are still valid with the current context
        //objects
        PersistentFacesState.resetInstance(perExtContext.getSessionMap(),
                                           String.valueOf(viewNumber),
                                           persistentContext);
    }

    private synchronized int getViewNumber()  {
        return globalViewNumber;
    }
    
    private synchronized int getNextViewNumber()  {
        globalViewNumber++;
        return globalViewNumber;
    }

    String getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (null == cookies) {
            return null;
        }
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals(name)) {
                return (cookies[i].getValue());
            }
        }
        return null;
    }

    void primeRequestMap(ServletRequest request, Map requestMap) {
        //instead of requestMap.clear() we must remove everything
        //except for the stuff that loadBundle puts in the requestMap
        //because the loadBundle tag only gets to run once
        Iterator keys = requestMap.keySet().iterator();
        while (keys.hasNext()) {
            Object key = keys.next();
            Object value = requestMap.get(key);
            if (null != value) {
                String className = value.getClass().getName();
                if ((className.indexOf("LoadBundleTag") > 0) ||  //Sun RI
                    (className.indexOf("BundleMap") > 0)) {     //MyFaces
                    //leave it in the map
                } else {
                    keys.remove();
                }
            } else {
                keys.remove();
            }
        }
        Enumeration names = request.getAttributeNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            requestMap.put(name, request.getAttribute(name));
        }
    }

    private InputStream createConfigurationStream(String sessionID,
                                                  String contextName) {
        return new ByteArrayInputStream((
                "configuration = {" +
                "   sessionID: '" + sessionID + "'," +
                "   synchronous: " + synchronousUpdate + "," +
                "   redirectURI: " +
                (connectionLostRedirectURI == null ? "null," :
                 ("'" + connectionLostRedirectURI.toString()) +
                 "',") +
                       "   connection: {" +
                       "       context: '" + contextName + "'," +
                       "       timeout: " + connectionTimeout + "," +
                       "       heartbeat: {" +
                       "           interval: " + heartbeatInterval + "," +
                       "           timeout: " + heartbeatTimeout + "," +
                       "           retries: " + heartbeatRetries +
                       "       }" +
                       "   }" +
                       "};"
        ).getBytes());
    }

    private boolean writeJSResource(String requestURI,
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    String sessionID, boolean compressed)
            throws IOException {
        if (requestURI.endsWith(".js")) {
            String prefix = "xmlhttp/";
            int position = requestURI.indexOf(prefix);
            String postfix = requestURI.substring(position + prefix.length());
            String enterpriseJS = "com/icesoft/faces/async/server/" + postfix;
            InputStream resource = this.getClass().getClassLoader()
                    .getResourceAsStream(enterpriseJS);
            if (resource == null) {
                String communityJS =
                        "com/icesoft/faces/webapp/xmlhttp/" + postfix;
                resource = this.getClass().getClassLoader()
                        .getResourceAsStream(communityJS);
            }

            String contextName = requestURI
                    .substring(0, requestURI.indexOf(request.getServletPath()));
            InputStream configurationStream =
                    createConfigurationStream(sessionID, contextName);
            writeResource(
                    new SequenceInputStream(configurationStream, resource),
                    request, response, compressed);
            return true;
        } else {
            return false;
        }
    }

    private boolean writeCSSResource(String requestURI,
                                     HttpServletRequest request,
                                     HttpServletResponse response,
                                     boolean compressed)
            throws IOException {
        String prefix = "xmlhttp/css/";
        int position = requestURI.indexOf(prefix);
        if (position > -1) {
            String path = "com/icesoft/faces/resources/css/" +
                          requestURI.substring(position + prefix.length());
            InputStream resource =
                    this.getClass().getClassLoader().getResourceAsStream(path);
            if (resource == null) {
                log.debug(" restricted access or following path not exists " +
                          path);
            } else {
                writeResource(resource, request, response, compressed);
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean writeIFrame(String requestURI, HttpServletResponse response)
            throws IOException {
        if (requestURI.endsWith("blank.iface")) {
            response.getWriter().println("<html><body></body></html>");
            return true;
        } else {
            return false;
        }
    }

    private static void writeResource(InputStream resource,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      boolean compressed) throws IOException {
        //tell to IE to cache these resources
        //see: http://mir.aculo.us/articles/2005/08/28/internet-explorer-and-ajax-image-caching-woes
        //see: http://www.bazon.net/mishoo/articles.epl?art_id=958
        //see: http://support.microsoft.com/default.aspx?scid=kb;en-us;319546
        long ifModifiedSince = request.getDateHeader("If-Modified-Since");
        if (ifModifiedSince == -1 || STARTUP_TIME - ifModifiedSince > 1000) {
            response.setHeader("Cache-Control", "private, max-age=86400");
            response.setDateHeader("Expires", EXPIRATION_TIME);
            response.setDateHeader("Last-Modified", STARTUP_TIME);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            if (compressed) {
                response.setHeader("Content-Encoding", "gzip");
                GZIPOutputStream gzip = new GZIPOutputStream(output);
                copy(resource, gzip);
                gzip.finish();
            } else {
                copy(resource, output);
            }
            byte[] resourceBytes = output.toByteArray();
            //set the length to enable the browser caching
            response.setContentLength(resourceBytes.length);
            response.getOutputStream().write(resourceBytes);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            response.setDateHeader("Expires", EXPIRATION_TIME);
        }

        response.flushBuffer();
    }

    private static void copy(InputStream input, OutputStream output)
            throws IOException {
        byte[] buf = new byte[2000];
        int len = 0;
        while ((len = input.read(buf)) > -1) output.write(buf, 0, len);
    }

    /**
     * Release the various Faces resources
     */
    public void destroy() {
        commonlet.destroy();
        config = null;
    }


}
