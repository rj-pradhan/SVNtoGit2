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
import com.icesoft.faces.webapp.http.servlet.ContextBoundServer;
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
import javax.servlet.RequestDispatcher;
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

public class BlockingServlet extends ContextBoundServer {
    public static boolean standardRequestScope = false;
    private ResponseStateManager stateManager;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        stateManager = ResponseStateManager.getResponseStateManager(config.getServletContext());
    }

    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //a session should not be created by the blocking servlet
        //we should use request.getSession(false) and communicate
        //the expired session to the browser
        HttpSession session = request.getSession();
        Map sessionMap = new SessionMap(session);

        String[] viewNumbers = request.getParameterValues("viewNumber");
        String viewNumber = null;
        try {
            if (viewNumbers.length > 0) {
                viewNumber = viewNumbers[0];
            }
            Integer.parseInt(viewNumber);
        } catch (Exception e) {
            //Use the one current viewNumber.  This needs to
            //be fixed to support multiple includes per page.
            viewNumber = (String) sessionMap.get(
                    PersistentFacesServlet.CURRENT_VIEW_NUMBER);
            viewNumbers = new String[]{ viewNumber };
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

            //this call actually creates the state...
            stateManager.getState(session, viewNumber);

            PersistentFacesState PFstate = PersistentFacesState.getInstance(sessionMap, viewNumber);
            PersistentFacesState.setLocalInstance(sessionMap, viewNumber);

            if (PFstate.facesContext instanceof BridgeFacesContext) {
                BridgeFacesContext facesContext =
                        (BridgeFacesContext) PFstate.facesContext;
                facesContext.setCurrentInstance();
                bridgeExternalContext =
                        (BridgeExternalContext) facesContext
                                .getExternalContext();
                if (standardRequestScope) {
                    bridgeExternalContext.clearRequestMap();
                }
                // Allow the context to clear Request scoped contexts if we're in a Seam environment. 
                bridgeExternalContext.clearRequestContext();
                Map map = request.getParameterMap();
                bridgeExternalContext.populateRequestParameters(map);

                String[] cssUpdates = request.getParameterValues(
                        CurrentStyle.CSS_UPDATE_FIELD);
                if (cssUpdates != null) {
                    for (int i = 0; i < cssUpdates.length; i++) {
                        String cssUpdate = cssUpdates[i];
                        try {
                            CurrentStyle.decode(request, cssUpdate);
                        } catch (Exception e) {
                            //TODO: FIXME
                            //Sometimes the request map can't be found because facesContext does not
                            //exists. This needs to be fixed.
                        }
                    }
                }
                
                //Bug 264:  The IncrementalNodeWriter instance has already been set
                //in the PersistentFacesServlet and does not need to be set again here.
                //session.setAttribute(IncrementalNodeWriter.NODE_WRITER, state);
                facesContext.setCurrentInstance();

                //the superclass is handling most of the requests.
                //refactoring steps should follow to move the all the functionality in it.
                super.service(request, response);
            }
        } catch (RedirectException re) {
            // This exception occurs if some action method has logged out and
            // invalidated the session. The IllegalStateException is caught
            // elsewhere and rebroadcast as this
            if (bridgeExternalContext != null) {
                String url = bridgeExternalContext.redirectTo();
                response.setHeader("X-REDIRECT", url);
                response.getOutputStream().write('.');
                response.getOutputStream().write('\n');
                response.flushBuffer();
            }
        } finally {
            PersistentFacesState.clearLocalInstance();
        }
    }

    public void destroy() {
        // Do nothing to prevent a NullPointerException when Weblogic tries to redeploy the
        // web application.
    }
}
