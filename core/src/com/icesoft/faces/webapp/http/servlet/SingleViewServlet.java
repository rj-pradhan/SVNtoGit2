package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.core.UpdateManager;
import com.icesoft.faces.webapp.http.core.PushServer;
import com.icesoft.faces.webapp.xmlhttp.ResponseStateManager;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesServlet;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.BlockingResponseState;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesCommonlet;
import com.icesoft.faces.context.SessionMap;
import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.env.ServletEnvironmentRequest;
import com.icesoft.util.IdGenerator;
import com.icesoft.util.SeamUtilities;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class SingleViewServlet implements ServletServer {
    private static final String viewNumber = "1";
    private HttpSession session;
    private Map sessionMap;
    private String sessionID;

    private ServletServer server;
    private UpdateManager updateManager;
    private View view;
    private Map bundles = new HashMap();

    public SingleViewServlet(HttpSession session, IdGenerator idGenerator) {
        this.sessionID = idGenerator.newIdentifier();
        //ContextEventRepeater needs this
        session.setAttribute(ResponseStateManager.ICEFACES_ID_KEY, sessionID);
        this.session = session;
        this.sessionMap = new SessionMap(session);
        this.updateManager = new UpdateManager(session);
        this.server = new ServerAdapterServlet(new PushServer(updateManager));
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //FileUploadServlet needs this
        session.setAttribute(PersistentFacesServlet.CURRENT_VIEW_NUMBER, viewNumber);

        //create single view or re-create view if the request is the result of a redirect 
        if (view == null || request.getParameter("rvn") != null) {
            view = new View(viewNumber, request, response);
            PersistentFacesState.setLocalInstance(sessionMap, viewNumber);
            ContextEventRepeater.iceFacesIdRetrieved(session, sessionID);
            ContextEventRepeater.viewNumberRetrieved(session, Integer.parseInt(viewNumber));
            view.externalContext.setupSeamEnvironment();
            //collect bundles put by Tag components when the page is parsed
            bundles = view.externalContext.collectBundles();
        }

        if (request.getParameter("viewNumber") == null) {
            //run lifecycle
            view.externalContext.updateRequest(request);
            view.externalContext.updateResponse(response);
            view.facesContext.setCurrentInstance();
            server.service(request, response);
            // If the GET request handled by this servlet results in a
            // redirect (not likely under icefaces demo apps, but happens
            // all the time in Seam) then, we're stuck. We don't use the
            // X-REDIRECT mechanism in this servlet, so resort to some
            // good old fashioned manual redirect code.
            if (view.externalContext.redirectRequested()) {
                response.sendRedirect(view.externalContext.redirectTo());
            }

            PersistentFacesState.resetInstance(sessionMap, viewNumber, view.facesContext);
            //by making the request null DOMResponseWriter will redirect its output to the coresponding ResponseState
            //todo: find better (less subversive) solution -- like creating two different implementions for DOMResponseWriter
            view.externalContext.updateResponse(null);
        } else {
            PersistentFacesState.getInstance(sessionMap, viewNumber).setFacesContext(view.facesContext);
            PersistentFacesState.setLocalInstance(sessionMap, viewNumber);

            view.externalContext.getRequestMap().putAll(bundles);
            view.externalContext.updateRequest(request);
            view.facesContext.setCurrentInstance();

            server.service(request, response);
        }

        view.facesContext.release();
        PersistentFacesState.clearLocalInstance();
    }

    public void shutdown() {
        updateManager.shutdown();
        server.shutdown();
    }

    //todo: refactor this structure into an object with behavior
    private class View {
        private ServletExternalContext externalContext;
        private ServletFacesContext facesContext;
        private BlockingResponseState responseState;

        public View(String viewIdentifier, HttpServletRequest request, HttpServletResponse response) {
            externalContext = new ServletExternalContext(session.getServletContext(), new ServletEnvironmentRequest(request), response);
            facesContext = new ServletFacesContext(externalContext, viewIdentifier);
            //the call has the side effect of creating and setting up the state
            //todo: make this concept more visible and less subversive
            responseState = (BlockingResponseState) ResponseStateManager.getState(session, viewIdentifier);
        }
    }
}
