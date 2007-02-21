package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;

import java.io.PrintStream;

public class ServeBlankPage implements Server {
    private static final ResponseHandler ResponseHandler = new ResponseHandler() {
        public void respond(Response response) throws Exception {
            new PrintStream(response.writeBody()).println("<html><body></body></html>");
        }
    };

    public void service(Request request) throws Exception {
        request.respondWith(ResponseHandler);
    }

    public void shutdown() {
    }
}
