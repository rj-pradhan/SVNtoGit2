package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ViewBoundAdaptingServlet extends BasicAdaptingServlet {
    private Map views;
    private SessionDispatcher.Listener.Monitor sessionMonitor;

    public ViewBoundAdaptingServlet(Server server, SessionDispatcher.Listener.Monitor sessionMonitor, Map views) {
        super(server);
        this.sessionMonitor = sessionMonitor;
        this.views = views;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String viewNumber = request.getParameter("viewNumber");
        if (viewNumber == null) {
            response.sendError(500, "Cannot match view instance. 'viewNumber' parameter is missing.");
        } else {
            ServletView view = (ServletView) views.get(viewNumber);
            try {
                view.setAsCurrentDuring(request, response);
                sessionMonitor.touchSession();
                super.service(request, response);
            } finally {
                view.release();
            }
        }
    }
}
