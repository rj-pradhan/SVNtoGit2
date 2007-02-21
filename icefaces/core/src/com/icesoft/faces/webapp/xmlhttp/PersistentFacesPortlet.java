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
import com.icesoft.faces.env.PortletEnvironmentRequest;
import com.icesoft.util.IdGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.Portlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import java.io.IOException;

/**
 * The primary differenct between the servlet and portlet environments are: -
 * portlets split the lifecycle.execute and lifecycle.render calls between two
 * different methods - there is restricted access to things like HTTP headers
 * and some URL information in the portlet environment.
 * <p/>
 * Otherwise, this is practically identical to the logic in the
 * PersistentFacesServlet with common logic extracted into the
 * PersistentFacesCommonlet class.
 */
public class PersistentFacesPortlet implements Portlet {

    private PortletConfig portletConfig;
    private PersistentFacesCommonlet commonlet;

    private String REDIRECT_URL = "com.icesoft.faces.redirectURL";
    public static final String PARAM_VIEW_PAGE = "ViewPage";
    public static final String DEFAULT_VIEW_PAGE = "default-view";
    public static final String DEFAULT_VIEW_ID = "index.jsf";
    public static final String REQUEST_SERVLET_PATH =
            "org.apache.portals.bridges.jsf.REQUEST_SERVLET_PATH";

    private ResponseStateManager stateManager;

    //Session scope is probably right for viewNumber, but if different
    //portlets are served by different web applications, the sessions will
    //be different. Note that making this static fails in that case as well
    static private int viewNumber = 1000;

    private static final Log log =
            LogFactory.getLog(PersistentFacesPortlet.class);

    public void init(PortletConfig config) throws PortletException {

        portletConfig = config;
        commonlet = new PersistentFacesCommonlet();
        commonlet.init(commonlet.getInitParams(portletConfig));
        stateManager = new ResponseStateManager();
    }

    public void processAction(ActionRequest request, ActionResponse response)
            throws IOException, PortletException {

        // Set up and configure our own FacesContext and ExternalContext
        // implementations. Our implementations should take care to ensure
        // that the type of environment is handled transparently.
        FacesContext facesContext = commonlet.getFacesContext(
                portletConfig.getPortletContext(),
                request,
                response);

        ExternalContext externalContext = facesContext.getExternalContext();

        if (log.isDebugEnabled()) {
            log.debug("PersistentFacesPortlet.processAction: FacesContext =" +
                      facesContext);
            log.debug(
                    "PersistentFacesPortlet.processAction: ExternalContext = " +
                    externalContext);
        }

        PortletSession session = request.getPortletSession();

        //a placeholder PersistentFacesState on processAction()?
        PersistentFacesState.setLocalInstance(null, null);

        // Execute the normal lifecycle then set up a persistent environment
        try {
            Lifecycle lifecycle = commonlet.getLifecycle();
            lifecycle.execute(facesContext);
            setupPersistentContext(externalContext, request);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            try {
                session.invalidate();
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error(ex.getMessage(), ex);
                }
            }

            //TODO
            // This is a redirect when there is an error.  We should likely
            //do this a different way.
            String redirectURL = externalContext.getInitParameter(REDIRECT_URL);
            response.sendRedirect(redirectURL);
        } finally {
            facesContext.release();
        }
    }

    public void render(RenderRequest request, RenderResponse response)
            throws PortletException,
                   IOException {

        request.setAttribute(PersistentFacesCommonlet.SERVLET_KEY,
                             PersistentFacesCommonlet.PERSISTENT);
        request.setAttribute(PersistentFacesCommonlet.REQUEST_PATH_KEY,
                             request.getContextPath());
        PortletSession session = request.getPortletSession();

        // Set up and configure our own FacesContext and ExternalContext
        // implementations. Our implementations should take care to ensure
        // that the type of environment is handled transparently.
        FacesContext facesContext = commonlet.getFacesContext(
                portletConfig.getPortletContext(), request, response);

        String iceID =
                (String) session.getAttribute(
                        ResponseStateManager.ICEFACES_ID_KEY,
                        PortletSession.APPLICATION_SCOPE);
        if (iceID == null) {
            iceID =
                    IdGenerator.create(
                            portletConfig.getPortletContext()
                                    .getResource("/").getPath());
            session.setAttribute(ResponseStateManager.ICEFACES_ID_KEY, iceID,
                                 PortletSession.APPLICATION_SCOPE);
        }
        //Ignoring the fact they might reload the page in portlet prototype
        viewNumber++;

        //Can we assume our factory is in place?
        BridgeExternalContext externalContext = (BridgeExternalContext)
                facesContext.getExternalContext();
        externalContext.getRequestMap().put("com.sun.faces.portlet.INIT",
                                            "com.sun.faces.portlet.INIT_VIEW");
        request.setAttribute("com.sun.faces.portlet.INIT",
                             "com.sun.faces.portlet.INIT_VIEW");

        if (log.isDebugEnabled()) {
            log.debug("PersistentFacesPortlet.render: FacesContext =" +
                      facesContext);
            log.debug("PersistentFacesPortlet.render: ExternalContext = " +
                      externalContext);
        }

        externalContext.getRequestMap().put(
                PersistentFacesCommonlet.SERVLET_KEY,
                PersistentFacesCommonlet.PERSISTENT);
        externalContext.getRequestParameterMap()
                .put("viewNumber", String.valueOf(viewNumber));
        externalContext.getApplicationSessionMap()
                .put(PersistentFacesServlet.CURRENT_VIEW_NUMBER,
                     String.valueOf(viewNumber));

        PersistentFacesState.setLocalInstance(
                externalContext.getApplicationSessionMap(),
                String.valueOf(viewNumber));

        //try to find viewId on either JetSpeed or JBoss portal
        String viewId = null;
        String paramViewPage = portletConfig.getInitParameter(PARAM_VIEW_PAGE);
        if (null == paramViewPage) {
            paramViewPage = portletConfig.getInitParameter(DEFAULT_VIEW_PAGE);
        }
        if (null != paramViewPage) {
            viewId = paramViewPage.replaceAll("[.]jsp", ".jsf");
        } else {
            viewId = DEFAULT_VIEW_ID;
        }

        request.setAttribute(REQUEST_SERVLET_PATH, viewId);
        if (externalContext instanceof BridgeExternalContext) {
            ((BridgeExternalContext) externalContext)
                    .setRequestServletPath(viewId);
        }

        // Execute the normal lifecycle then set up a persistent environment
        try {
            Lifecycle lifecycle = commonlet.getLifecycle();
            lifecycle.execute(facesContext);
            lifecycle.render(facesContext);
            setupPersistentContext(externalContext, request);

            ResponseState state =
                    stateManager.getState(session, String.valueOf(viewNumber));
            session.setAttribute(String.valueOf(viewNumber) +
                                 "/" + ResponseState.STATE,
                                 state,
                                 PortletSession.APPLICATION_SCOPE);

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            try {
                session.invalidate();
            } catch (Exception ex) {
                if (log.isErrorEnabled()) {
                    log.error(e.getMessage(), e);
                }
            }

        } finally {
            facesContext.release();
        }
    }


    /**
     * ICEfaces requires a more pesistent set or resources compared to stock JSF
     * applications so here we make that happen by stuffing the required items
     * into the session.
     *
     * @param request
     */
    void setupPersistentContext(ExternalContext extContext,
                                PortletRequest request) {

        // We have our own copy of the request because when a request has completed, we need to
        // ensure access to some of its information.  So we keep relevant information
        // in a environment-friendly (servlet,portlet) container for future use.
        PortletEnvironmentRequest envReq =
                new PortletEnvironmentRequest(request);

        // Create instances of the appropriate contexts and state and stuff it in the session.
        BridgeExternalContext perExtContext =
                new BridgeExternalContext(extContext.getContext(), envReq,
                                          null);
        perExtContext.setRequestServletPath(extContext.getRequestServletPath());
        BridgeFacesContext persistentContext =
                new BridgeFacesContext(perExtContext);
        PersistentFacesState state =
                new PersistentFacesState(persistentContext);
        //not yet clear how to apply viewNumber to portlets
        perExtContext.getRequestParameterMap()
                .put("viewNumber", String.valueOf(viewNumber));
        PersistentFacesState.setInstance(
                perExtContext.getApplicationSessionMap(),
                String.valueOf(viewNumber), state);
        PersistentFacesState.setLocalInstance(
                perExtContext.getApplicationSessionMap(),
                String.valueOf(viewNumber));

    }

    /**
     * Release the various Faces resources
     */
    public void destroy() {
        commonlet.destroy();
        portletConfig = null;
    }
}
