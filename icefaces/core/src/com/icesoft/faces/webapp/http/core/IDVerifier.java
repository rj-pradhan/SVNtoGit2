package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;

public class IDVerifier implements Server, ResponseHandler {
    private String sessionID;
    private Server server;

    public IDVerifier(String sessionID, Server server) {
        this.sessionID = sessionID;
        this.server = server;
    }

    public void service(Request request) throws Exception {
        String id = request.getParameter("icefacesID");
        if (id != null && sessionID.equals(id)) {
            server.service(request);
        } else {
            request.respondWith(this);
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
