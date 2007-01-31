package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdapterServlet implements ServerServlet {
    private Server server;

    public AdapterServlet(Server server) {
        this.server = server;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        server.service(new ServletRequestResponse(request, response));
    }

    public void shutdown() {
        server.shutdown();
    }
}
