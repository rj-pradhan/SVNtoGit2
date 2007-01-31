package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.core.PushServer;
import com.icesoft.faces.webapp.http.core.UpdateManager;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class PushServlet implements ServerServlet {
    private HttpSession session;
    private Map views;
    private UpdateManager updateManager;
    private ServerServlet server;

    public PushServlet(HttpSession session, Map views) {
        this.updateManager = new UpdateManager(session);
        this.server = new AdapterServlet(new PushServer(this.updateManager));
        this.session = session;
        this.views = views;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String viewNumber = request.getParameter("viewNumber");
        //FileUploadServlet needs this
        session.setAttribute(PersistentFacesServlet.CURRENT_VIEW_NUMBER, viewNumber);
        ServletView view = (ServletView) views.get(viewNumber);
        view.setAsCurrentDuring(request);
        server.service(request, response);
        view.release();
    }

    public void shutdown() {
        server.shutdown();
        updateManager.shutdown();
    }
}
