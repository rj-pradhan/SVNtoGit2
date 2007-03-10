package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.PushServer;
import com.icesoft.faces.webapp.http.core.ViewQueue;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class PushServlet implements ServerServlet {
    private Map views;
    private ServerServlet server;

    public PushServlet(Map views, ViewQueue allUpdatedViews, Configuration configuration) {
        this.server = new EnvironmentAdaptingServlet(new PushServer(views, allUpdatedViews), configuration);
        this.views = views;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String viewNumber = request.getParameter("viewNumber");
        //FileUploadServlet needs this
        request.getSession().setAttribute(PersistentFacesServlet.CURRENT_VIEW_NUMBER, viewNumber);
        ServletView view = (ServletView) views.get(viewNumber);
        if (view == null) {
            byte[] content = "<session-expired/>".getBytes("UTF-8");
            response.setContentType("text/xml;charset=UTF-8");
            response.setContentLength(content.length);
            response.getOutputStream().write(content);
        } else {
            view.setAsCurrentDuring(request, response);
            server.service(request, response);
            view.release();
        }
    }

    public void shutdown() {
        server.shutdown();
    }
}
