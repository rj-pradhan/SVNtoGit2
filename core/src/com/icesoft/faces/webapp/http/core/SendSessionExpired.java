package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.Server;
import com.icesoft.faces.webapp.http.Request;
import com.icesoft.faces.webapp.http.ResponseHandler;
import com.icesoft.faces.webapp.http.Response;
import com.icesoft.faces.webapp.xmlhttp.SessionExpiredException;

import java.io.StringWriter;
import java.io.ByteArrayInputStream;

public class SendSessionExpired implements Server {
    private Server server;
    private final static ResponseHandler Handler = new ResponseHandler() {

        public void respond(Response response) throws Exception {
            response.setHeader("X-SESSION-EXPIRED", ".");
            //send some content since Safari will not read the header.
            response.writeBodyFrom(new ByteArrayInputStream(".\n".getBytes()));
        }
    };

    public SendSessionExpired(Server server) {
        this.server = server;
    }

    public void service(Request request) throws Exception {
        try {
            server.service(request);
        } catch (SessionExpiredException e) {
            request.respondWith(Handler);
        }
    }
}
