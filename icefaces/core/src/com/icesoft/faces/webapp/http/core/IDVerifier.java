package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.command.Command;
import com.icesoft.faces.webapp.command.SessionExpired;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;

import java.io.IOException;
import java.io.Writer;

public class IDVerifier extends FixedXMLContentHandler implements Server {
    private final static Command SessionExpired = new SessionExpired();
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

    public void writeTo(Writer writer) throws IOException {
        SessionExpired.serializeTo(writer);
    }
}
