package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.core.UpdateManager;
import com.icesoft.faces.webapp.http.core.PushServer;
import com.icesoft.faces.webapp.xmlhttp.ResponseStateManager;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesServlet;
import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.util.IdGenerator;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SingleViewServlet implements ServletServer {
    private static final String viewNumber = "1";
    private HttpSession session;

    private ServletServer server;
    private UpdateManager updateManager;
    private ResponseStateManager responseStateManager;
    private ServletView view;

    public SingleViewServlet(HttpSession session, IdGenerator idGenerator, ResponseStateManager responseStateManager) {
        String sessionID = idGenerator.newIdentifier();
        //ContextEventRepeater needs this
        session.setAttribute(ResponseStateManager.ICEFACES_ID_KEY, sessionID);
        ContextEventRepeater.iceFacesIdRetrieved(session, sessionID);
        //FileUploadServlet needs this
        session.setAttribute(PersistentFacesServlet.CURRENT_VIEW_NUMBER, viewNumber);

        this.session = session;
        this.responseStateManager = responseStateManager;
        this.updateManager = new UpdateManager(session);
        this.server = new ServerAdapterServlet(new PushServer(updateManager));
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //create single view or re-create view if the request is the result of a redirect 
        if (view == null || request.getParameter("rvn") != null) {
            view = new ServletView(viewNumber, request, response, responseStateManager);
            ContextEventRepeater.viewNumberRetrieved(session, Integer.parseInt(viewNumber));
        }

        //reuse already created view
        if (request.getParameter("viewNumber") == null) {
            view.setAsCurrentDuring(request);
            view.switchToImmediateMode(response);
            server.service(request, response);
            view.redirectIfRequired();
            view.switchToPushMode();
        } else {
            view.setAsCurrentDuring(request);
            server.service(request, response);            
        }
        view.release();
    }

    public void shutdown() {
        updateManager.shutdown();
        server.shutdown();
    }
}
