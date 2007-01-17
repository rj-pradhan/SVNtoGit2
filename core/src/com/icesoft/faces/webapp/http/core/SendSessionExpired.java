package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.xmlhttp.SessionExpiredException;
import com.icesoft.faces.webapp.xmlhttp.SessionLifetimeManager;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;

public class SendSessionExpired implements Server {
    private Server server;
    private HttpSession session;
    private final static ResponseHandler Handler = new ResponseHandler() {

        public void respond(Response response) throws Exception {
            response.setHeader("X-SESSION-EXPIRED", ".");
            //send some content since Safari will not read the header.
            response.writeBodyFrom(new ByteArrayInputStream(".\n".getBytes()));
        }
    };

    public SendSessionExpired(HttpSession session, Server server) {
        this.server = server;
        this.session = session;
    }

    public void service(Request request) throws Exception {
        try {
            if (!request.getURI().getPath().endsWith("ping")) {
                //todo: move SessionLifetimeManager's implementation in this class 
                SessionLifetimeManager.touch(session);
            }
            server.service(request);
        } catch (SessionExpiredException e) {
            request.respondWith(Handler);
        }
    }

    public void shutdown() {
        server.shutdown();
    }
}
