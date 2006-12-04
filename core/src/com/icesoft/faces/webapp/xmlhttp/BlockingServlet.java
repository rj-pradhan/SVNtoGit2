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

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.DOMResponseWriter;
import com.icesoft.faces.context.SessionMap;
import com.icesoft.faces.context.RedirectException;
import com.icesoft.faces.context.effects.CurrentStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FactoryFinder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.*;


public class BlockingServlet extends HttpServlet {
    private static final String CHARSET = "UTF-8";
    public static final String DEBUG_DOMUPDATE =
            "com.icesoft.faces.debugDOMUpdate";
    public static final String STANDARD_REQUEST_SCOPE =
            "com.icesoft.faces.standardRequestScope";
    public static boolean standardRequestScope = false;
    static boolean debugDOMUpdate = false;

    private ResponseStateManager stateManager;

    private static String postBackKey;

    private static final Log log = LogFactory.getLog(BlockingServlet.class);
    private static final Log domLog = LogFactory.getLog("blocking.dom.update");

    static {
        //We will place VIEW_STATE_PARAM in the requestMap so that
        //JSF 1.2 doesn't think the request is a postback and skip
        //execution
        try {
            Field field = javax.faces.render.ResponseStateManager.class
                    .getField("VIEW_STATE_PARAM");
            if (null != field) {
                postBackKey = (String) field.get(
                        javax.faces.render.ResponseStateManager.class);
            }
        } catch (Exception e) {
        }
    }

    public void init(ServletConfig config) {
        stateManager = ResponseStateManager
                .getResponseStateManager(config.getServletContext());
        ServletContext servletContext = config.getServletContext();
        debugDOMUpdate = "true".equalsIgnoreCase(servletContext
                .getInitParameter(DEBUG_DOMUPDATE));
        standardRequestScope = "true".equalsIgnoreCase(servletContext
                .getInitParameter(STANDARD_REQUEST_SCOPE));
    }

    protected void service(HttpServletRequest request,
                           HttpServletResponse response)
            throws ServletException, IOException {

        //a session should not be created by the blocking servlet
        //we should use request.getSession(false) and communicate
        //the expired session to the browser
        HttpSession session = request.getSession();
        Map sessionMap = new SessionMap(session);

        if (log.isDebugEnabled()) {
            log.debug("request.pathInfo " + request.getPathInfo());
            Iterator keys = request.getParameterMap().keySet().iterator();
            while (keys.hasNext()) {
                Object key = keys.next();
                log.debug("request." + key + " = " +
                          ((String[]) request.getParameterMap().get(key))[0]);
            }
        }

        String[] viewNumbers = null;
        String viewNumber = request.getParameter("viewNumber");
        if( log.isTraceEnabled() ) {
            log.trace("service(-)  viewNumber: " + viewNumber);
        }
        try {
            if (null != viewNumber) {
                viewNumbers = viewNumber.split(",");
                viewNumber = viewNumbers[0];
                if( log.isTraceEnabled() ) {
                    log.trace("service(-)  viewNumbers.length: " + viewNumbers.length);
                    for(int i = 0; i < viewNumbers.length; i++)
                        log.trace("service(-)  viewNumbers["+i+"]: " + viewNumbers[i]);
                }
            }
            Integer.parseInt(viewNumber);
            if( log.isTraceEnabled() ) {
                log.trace("service(-)  PARSED");
            }
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Could not parse viewNumber " + viewNumber +
                          ": " + e.getMessage());
            }
            //Use the one current viewNumber.  This needs to
            //be fixed to support multiple includes per page.
            viewNumber = (String) sessionMap.get(
                    PersistentFacesServlet.CURRENT_VIEW_NUMBER);
            viewNumbers = new String[]{viewNumber};
            if( log.isTraceEnabled() ) {
                log.trace("service(-)  RECOVERED  viewNumber: " + viewNumber);
            }
        }

        // TODO- If a session has expired, then the call to this servlet will have an issue
        // as the session does not contain an icefacesID.  Should we reroute back to the
        // PersistentFacesServlet?

        BridgeExternalContext bridgeExternalContext = null;
        try {
            // The implemented state we get here depends on whether the Async server is configured
            // running.  If not, then the BlockingResponseState is used and the scalability is
            // restricted since the connections remain open and a Thread is left open and waiting.
            // If the Async server is used, then an AsyncResponseState is used which provides a
            // more scalable design.  The response/request cycle is separated which frees up the
            // Threads.

            ResponseState state = null;
            PersistentFacesState PFstate = null;
            state = stateManager.getState(session, viewNumber);

            PFstate = PersistentFacesState
                    .getInstance(sessionMap, viewNumber);
            PersistentFacesState.setLocalInstance(sessionMap, viewNumber);

            if (PFstate.facesContext instanceof BridgeFacesContext) {
                BridgeFacesContext facesContext =
                        (BridgeFacesContext) PFstate.facesContext;
                bridgeExternalContext =
                        (BridgeExternalContext) facesContext
                                .getExternalContext();
                if (standardRequestScope) {
                    bridgeExternalContext.clearRequestMap();
                }

                // Allow the context to clear Request scoped contexts if we're in a Seam environment. 
                bridgeExternalContext.clearRequestContext();

                Map map = request.getParameterMap();


                bridgeExternalContext.populateRequestParameters(
                        this.convertParametersMap(map));


                String[] cssUpdates = request.getParameterValues(
                        CurrentStyle.CSS_UPDATE_FIELD);
                if (cssUpdates == null) {
                    if( log.isTraceEnabled() ) {
                        log.trace("service(-)  No CSS update");
                    }
                } else {
                    for (int i = 0; i < cssUpdates.length; i++) {
                        String cssUpdate = cssUpdates[i];
                        if( log.isTraceEnabled() ) {
                            log.trace("service(-)  cssUpdate: " + cssUpdate);
                        }
                        try {
                            CurrentStyle.decode(request, cssUpdate);
                        } catch (Exception e) {
                            //TODO: FIXME
                            //Sometimes the request map can't be found because facesContext does not
                            //exists. This needs to be fixed.
                            if (log.isWarnEnabled())
                                log.warn("Unable to parse CSS update [" +
                                         cssUpdate + "]", e);
                        }
                    }
                }


                state.setFocusID(request.getParameter("focus"));
                
                //Bug 264:  The IncrementalNodeWriter instance has already been set
                //in the PersistentFacesServlet and does not need to be set again here.
                //session.setAttribute(IncrementalNodeWriter.NODE_WRITER, state);
                String method = request.getPathInfo();
                if ("/receive-updates".startsWith(method)) {
                    if( log.isTraceEnabled() ) {
                        log.trace("service(-)  receive-updates");
                    }
                    state.block(request);

                    applyCookies(bridgeExternalContext, response);
                    //If the state has a handler (as it does for the Async server), then
                    //let the handler take care of things.
                    if (!state.hasHandler()) {
                        if( log.isTraceEnabled() ) {
                            log.trace("service(-)    !state.hasHandler()");
                        }
                        if (applyRedirect(session, response, viewNumber)) {
                            if( log.isTraceEnabled() ) {
                                log.trace("service(-)    applyRedirect -> state.cancel");
                            }
                            state.cancel();
                        } else {
                            if( log.isTraceEnabled() ) {
                                log.trace("service(-)    sendAllUpdates");
                            }
                            sendAllUpdates(session, response, viewNumbers);
                        }
                    }
                } else if ("/send-updates".equals(method)) {
                    if( log.isTraceEnabled() ) {
                        log.trace("service(-)  send-updates");
                    }
                    SessionLifetimeManager.touch(session);
                    receiveUpdates(request, facesContext, state);
                    applyCookies(bridgeExternalContext, response);
                    if (applyRedirect(session, response, viewNumber)) {
                        state.cancel();
                    }
                } else if ("/receive-send-updates".equals(method)) {
                    if( log.isTraceEnabled() ) {
                        log.trace("service(-)  receive-send-updates");
                    }
                    SessionLifetimeManager.touch(session);
                    receiveUpdates(request, facesContext, state);
                    applyCookies(bridgeExternalContext, response);
                    if (applyRedirect(session, response, viewNumber)) {
                        state.cancel();
                    } else {
                        sendAllUpdates(session, response, viewNumbers);
                    }
                } else if ("/ping".equals(method)) {
                    if( log.isTraceEnabled() ) {
                        log.trace("service(-)  ping");
                    }
                    state.flush();
                    response.setContentLength(0);
                    if (!response.isCommitted()) {
                        response.flushBuffer();
                    }
                } else {
                    SessionLifetimeManager.touch(session);
                    throw new IllegalAccessException(
                            "Unknown request type: '" + method + "'.");
                }
            }
        } catch (RedirectException re ) {

            // This exception occurs if some action method has logged out and
            // invalidated the session. The IllegalStateException is caught
            // elsewhere and rebroadcast as this
            if (bridgeExternalContext != null) {
                String url = bridgeExternalContext.redirectTo();
                response.setHeader("X-REDIRECT", url);
                response.getOutputStream().write('.');
                response.getOutputStream().write('\n');          
                response.flushBuffer();
                if (log.isTraceEnabled()) {
                    log.trace("Redirect SENT -> RedirectException: " + url);
                }
            }
        } catch (SessionExpiredException e) {
            //attempt to detect session expiry explicitly.  Add catch blocks
            //here
            if (log.isTraceEnabled()) log.trace(
                    "User session expired. ViewNumber = [" + viewNumber + "]");
            response.setHeader("X-SESSION-EXPIRED", ".");
            //send some content since Safari will not read the header.
            response.getOutputStream().write('.');
            response.getOutputStream().write('\n');
            response.flushBuffer();

            if (log.isTraceEnabled()) {
                log.trace("User session expired. ViewNumber ["+viewNumber+"]");
            }
        } catch (IllegalAccessException e) {
            if( log.isDebugEnabled() ) {
                log.debug(e);
            }
            throw(new ServletException(e));
        } finally {
            PersistentFacesState.clearLocalInstance();
        }
    }

    //derived from sendUpdates to iterate through all viewNumbers
    //on the browser page
    private void sendAllUpdates(HttpSession session,
                                HttpServletResponse response,
                                String[] viewNumbers)
            throws IOException {
        if( log.isTraceEnabled() ) {
            log.trace("sendAllUpdates(-)  BEGIN");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(out, CHARSET);
        writer.write("<updates>");
        if( log.isTraceEnabled() ) {
            log.trace("sendAllUpdates(-)    viewNumbers.length: " +
                      viewNumbers.length);
        }
        for (int i = 0; i < viewNumbers.length; i++) {
            ResponseState responseState =
                    stateManager.getState(session, viewNumbers[i]);
            if( log.isTraceEnabled() ) {
                log.trace("sendAllUpdates(-)    viewNumbers["+i+"]: " +
                          viewNumbers[i] + ", session: " + session +
                          ", responseState: " + responseState);
            }
            responseState.serialize(writer);
        }
        writer.write("</updates>\n");
        writer.flush();
        if (domLog.isDebugEnabled()) {
            domLog.debug("Update\n" + out);
        }
        byte[] content = out.toByteArray();
        response.setContentType("text/xml;charset=" + CHARSET);
        response.setContentLength(content.length);
        response.getOutputStream().write(content);
        response.flushBuffer();
        if( log.isTraceEnabled() ) {
            log.trace("sendAllUpdates(-)  DONE");
        }
    }


    private Map convertParametersMap(Map parameters) {
        Map convertedParameters = new HashMap(parameters);
        Iterator iterator = convertedParameters.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String[] values = (String[]) entry.getValue();
            if (values.length > 1) {
                // convert the String[] to a List
                entry.setValue(Arrays.asList(values));
            } else if (values.length == 1) {
                // convert the String[] to a String
                entry.setValue(values[0]);
            }
        }

        return convertedParameters;
    }

    // doPost handler

    // Bug 794
    // Removed synchronized modifier.
    private void receiveUpdates(HttpServletRequest request,
                                BridgeFacesContext context,
                                ResponseState state) {
        LifecycleFactory factory = (LifecycleFactory) FactoryFinder
                .getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        Lifecycle lifecycle =
                factory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
        if ("true".equals(request.getParameter("partial"))) {
            UIComponent component = D2DViewHandler
                    .findComponent(state.getFocusID(), context.getViewRoot());            
            renderCyclePartial(context, lifecycle, component);
        } else {
            renderCycle(context, lifecycle);
        }
    }

    void renderCycle(BridgeFacesContext context, Lifecycle lifecycle) {
        synchronized (context) {
            context.setCurrentInstance();
            DOMResponseWriter.applyBrowserDOMChanges(context);
            if (null != postBackKey) {
                context.getExternalContext().getRequestParameterMap()
                        .put(postBackKey, "not reload");
            }
            lifecycle.execute(context);
            lifecycle.render(context);
            context.release();
        }
    }

    void renderCyclePartial(BridgeFacesContext context, Lifecycle lifecycle,
                            UIComponent component) {
        synchronized (context) {
            context.setCurrentInstance();
            DOMResponseWriter.applyBrowserDOMChanges(context);
            List alteredRequiredComponents =
                    setRequiredFalseInFormContaining(component);
            if (null != postBackKey) {
                context.getExternalContext().getRequestParameterMap()
                        .put(postBackKey, "not reload");
            }
            lifecycle.execute(context);
            lifecycle.render(context);
            context.release();
            setRequiredTrue(alteredRequiredComponents);
        }
    }


    /**
     * Add application-provided cookies to the response
     *
     * @param session
     * @param response
     */
    private void applyCookies(BridgeExternalContext bridgeContext,
                                  HttpServletResponse response) {
        if( log.isTraceEnabled() ) {
            log.trace("applyCookies()");
        }
        Map cookieMap = bridgeContext.getResponseCookieMap();

        Iterator cookieNames = cookieMap.keySet().iterator();
        while (cookieNames.hasNext())  {
            response.addCookie(
                    (Cookie) cookieMap.get(cookieNames.next()) );
        }
    }

    /**
     * Set the redirect header if a redirect is active
     *
     * @param session
     * @param response
     */
    private boolean applyRedirect(HttpSession session,
                                  HttpServletResponse response,
                                  String viewNumber) {
        if( log.isTraceEnabled() ) {
            log.trace("applyRedirect()  viewNumber: "  + viewNumber +
                      "  session: " + session + "  response: " + response);
        }
        boolean isRedirect = false;
        try {
            PersistentFacesState PFstate = PersistentFacesState
                    .getInstance(new SessionMap(session), viewNumber);
            FacesContext context = PFstate.facesContext;
            if( log.isTraceEnabled() ) {
                log.trace("applyRedirect()  session: "  + session +
                          "  PFstate: " + PFstate + "  context: " + context);
            }

            if (context instanceof BridgeFacesContext) {
                if( log.isTraceEnabled() ) {
                    log.trace("applyRedirect()  BridgeFacesContext");
                }
                BridgeExternalContext externalContext =
                        (BridgeExternalContext) context.getExternalContext();
                if (externalContext.redirectRequested()) {
                    if( log.isTraceEnabled() ) {
                        log.trace("applyRedirect()  REDIRECT");
                    }
                    isRedirect = true;
                    response.setHeader("X-REDIRECT",
                                       externalContext.redirectTo());
                    //send some content since Safari will not read the header.
                    response.getOutputStream().write('.');
                    externalContext.redirectComplete();
                }
            }
        } finally {
            return isRedirect;
        }
    }

    /**
     * Set the required attribute to true on all components in the List
     *
     * @param requiredComponents
     */
    private void setRequiredTrue(List requiredComponents) {
        Iterator i = requiredComponents.iterator();
        UIInput next = null;
        while (i.hasNext()) {
            next = (UIInput) i.next();
            ((UIInput) next).setRequired(true);
        }
    }

    /**
     * Set the required attribute to false on all components in the containing
     * form of the source component; omit the source component
     *
     * @param component the source of the partial submit
     * @return List of the required components whose required attribute was set
     *         to false
     */
    private List setRequiredFalseInFormContaining(UIComponent component) {
        List alteredComponents = new ArrayList();
        UIComponent form = getContainingForm(component);
        setRequiredFalseOnAllChildrenExceptOne(form, component,
                                               alteredComponents);
        return alteredComponents;
    }

    private void setRequiredFalseOnAllChildrenExceptOne(UIComponent parent,
                                                        UIComponent componentToAvoid,
                                                        List alteredComponents) {
        int length = parent.getChildCount();
        UIComponent next = null;
        for (int i = 0; i < length; i++) {
            next = (UIComponent) parent.getChildren().get(i);
            if (next instanceof UIInput && next != componentToAvoid) {
                if (((UIInput) next).isRequired()) {
                    ((UIInput) next).setRequired(false);
                    alteredComponents.add(next);
                }
            }
            setRequiredFalseOnAllChildrenExceptOne(next, componentToAvoid,
                                                   alteredComponents);
        }
    }

    private UIComponent getContainingForm(UIComponent component) {
        if (null == component) {
            return FacesContext.getCurrentInstance().getViewRoot();
        }
        UIComponent parent = component.getParent();
        while (parent != null) {
            if (parent instanceof UIForm) {
                break;
            }
            parent = parent.getParent();
        }
        return (UIForm) parent;
    }


    public void destroy() {
        // Do nothing to prevent a NullPointerException when Weblogic tries to redeploy the
        // web application.
        //super.destroy();
    }
}
