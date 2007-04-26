package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Response;

public class IDVerifier implements Server, ResponseHandler {
    private Server server;

    public IDVerifier(Server server) {
        this.server = server;
    }

    public void service(Request request) throws Exception {
        if (!request.containsParameter("icefacesID") || request.getParameter("icefacesID").equals("")) {
            request.respondWith(this);
        } else {
            server.service(request);
        }
    }

    public void shutdown() {
        server.shutdown();
    }

    public void respond(Response response) throws Exception {
        response.setStatus(500);
        response.setHeader("Content-Type", "text/plain; charset=UTF-8");
        response.writeBody().write("Cannot match session instance".getBytes("UTF-8"));
    }
}
