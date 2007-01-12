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
import com.icesoft.faces.context.RequestMapWrapper;
import com.icesoft.faces.env.ServletEnvironmentRequest;
import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.util.IdGenerator;
import com.icesoft.util.SeamUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.net.MalformedURLException;

public class PersistentFacesServlet extends HttpServlet {
    public static final String CURRENT_VIEW_NUMBER =
            "com.icesoft.faces.currentViewNumber";
    private PersistentFacesCommonlet commonlet;
    private int globalViewNumber = 1000;

    private ResponseStateManager stateManager;

    private static final Log log =
            LogFactory.getLog(PersistentFacesServlet.class);
    private RequestDispatcher dispatcher;
    private String seed;

    public void init(ServletConfig config) throws ServletException {
        this.commonlet = new PersistentFacesCommonlet();
        this.commonlet.init(this.commonlet.getInitParams(config));
        System.setProperty("java.awt.headless", "true");
        ServletContext servletContext = config.getServletContext();

        stateManager = ResponseStateManager.getResponseStateManager(servletContext);
        try {
            seed = servletContext.getResource("/").getPath();
        } catch (MalformedURLException e) {
            throw new ServletException(e);
        }
        try {
            commonlet.concurrentDOMViews = Boolean
                    .valueOf(servletContext.getInitParameter(
                            PersistentFacesCommonlet.CONCURRENT_DOM_VIEWS))
                    .booleanValue();
        } catch (NullPointerException e) {
            commonlet.concurrentDOMViews = false;
        }
        dispatcher = servletContext.getNamedDispatcher("Blocking Servlet");
    }

    protected void service(HttpServletRequest request,
                           HttpServletResponse response)
            throws IOException, ServletException {
        if (this.redirectOnJavascriptBlocked(request, response)) return;
        // Set up some information in the session including attributes required
        // by the DOM renderers.
        HttpSession session = request.getSession();

        // If not already there, add our own unique ICEfaces ID to the session
        // and as a cookie in the response
        String iceID =
                (String) session
                        .getAttribute(ResponseStateManager.ICEFACES_ID_KEY);
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
            iceID = IdGenerator.create(seed);
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

            if (null == requestURI) {
                requestURI = httpRequest.getRequestURI();
            }

            if (this.writeJSResource(requestURI, request, response)) return;
            if (this.writeCSSResource(requestURI, request, response)) return;
            if (this.writeIFrame(requestURI, request, response)) return;

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
                    session.getServletContext(),
                    request,
                    response);

            Map map = request.getParameterMap();

            ExternalContext ec = facesContext.getExternalContext();
            if (ec instanceof BridgeExternalContext) {
                ((BridgeExternalContext)ec).populateRequestParameters(map);
            }

            // Check if lifecycle shortcuts required
            checkSeamRequestParameters(
                    facesContext.getExternalContext().getRequestParameterMap());


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

                // Set the correct ResponseState as the IncrementalNodeWriter for this
                // combination of session and view number, then store the state so that is
                // accessible from the async server.
                String viewNumberString = String.valueOf(viewNumber);
                stateManager.getState(session, viewNumberString);
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

                // If the GET request handled by this servlet results in a
                // redirect (not likely under icefaces demo apps, but happens
                // all the time in Seam) then, we're stuck. We don't use the
                // X-REDIRECT mechanism in this servlet, so resort to some
                // good old fashioned manual redirect code.
                if (ec != null) {
                    if (ec instanceof BridgeExternalContext) {
                        if ( ((BridgeExternalContext) ec).redirectRequested() ){
                            String url = ((BridgeExternalContext)ec).redirectTo();
                            log.debug("Response.redirect() to: " + url);
                            response.sendRedirect( url );
                        }
                    }
                }

                setupPersistentContext(
                        session.getServletContext(), request, viewNumber );

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
            throws IOException, ServletException {
        String uri = request.getRequestURI();
        if (uri.endsWith("/javascript-blocked")) {
            dispatcher.forward(request, response);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check to see if Seam specific keywords are in the request. If so,
     * then flag that a new ViewRoot is to be constructed.
     *
     * @param externalContextMap Map to insert Flag
     */
        private void checkSeamRequestParameters(
            Map externalContextMap) {




        boolean seamEnabled = SeamUtilities.isSeamEnvironment();
        // Always on a GET request, create a new ViewRoot. New theory.
        // This works now that the ViewHandler only calls restoreView once,
        // as opposed to calling it again from createView
        if (seamEnabled) {
             externalContextMap.put(
                     PersistentFacesCommonlet.SEAM_LIFECYCLE_SHORTCUT,
                     Boolean.TRUE );
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
        httpResponse.addHeader("Cache-Control", "no-cache"); //HTTP 1.1
        httpResponse.addHeader("Cache-Control", "no-store"); //HTTP 1.1
        httpResponse.addHeader("Cache-Control", "must-revalidate"); //HTTP 1.1
        httpResponse.addHeader("Pragma", "no-cache"); //HTTP 1.0
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
        if (requestMap instanceof RequestMapWrapper)  {
            RequestMapWrapper wrapperMap = (RequestMapWrapper) requestMap;
            wrapperMap.clearMostly();
            wrapperMap.primeMap();
        }
    }

    private boolean writeJSResource(String requestURI,
                                    HttpServletRequest request,
                                    HttpServletResponse response
    )
            throws IOException, ServletException {
        if (requestURI.endsWith(".js")) {
            dispatcher.forward(request, response);
            return true;
        } else {
            return false;
        }
    }

    private boolean writeCSSResource(String requestURI,
                                     HttpServletRequest request,
                                     HttpServletResponse response)
            throws IOException, ServletException {
        String prefix = "xmlhttp/css/";
        int position = requestURI.indexOf(prefix);
        if (position > -1) {
            dispatcher.forward(request, response);
            return true;
        } else {
            return false;
        }
    }

    private boolean writeIFrame(String requestURI, HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (requestURI.endsWith("blank.iface")) {
            dispatcher.forward(request, response);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Release the various Faces resources
     */
    public void destroy() {
        commonlet.destroy();
    }
}
