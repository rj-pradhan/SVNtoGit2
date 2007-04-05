package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.PushServer;
import com.icesoft.faces.webapp.http.core.ViewQueue;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class PushServlet implements PseudoServlet {
    private Map views;
    private PseudoServlet server;

    public PushServlet(Map views, ViewQueue allUpdatedViews, Configuration configuration) {
        this.server = new EnvironmentAdaptingServlet(new PushServer(views, allUpdatedViews, configuration), configuration);
        this.views = views;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String viewNumber = request.getParameter("viewNumber");
        //FileUploadServlet needs this
        if (viewNumber == null) {
            response.sendError(500, "Cannot match view instance. 'viewNumber' parameter is missing.");
        } else {
            request.getSession().setAttribute(PersistentFacesServlet.CURRENT_VIEW_NUMBER, viewNumber);
            ServletView view = (ServletView) views.get(viewNumber);
            view.setAsCurrentDuring(request, response);
            server.service(request, response);
            view.release();
        }
    }

    public void shutdown() {
        server.shutdown();
    }
}
