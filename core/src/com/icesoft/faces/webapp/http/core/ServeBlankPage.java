package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.Request;
import com.icesoft.faces.webapp.http.Response;
import com.icesoft.faces.webapp.http.ResponseHandler;
import com.icesoft.faces.webapp.http.Server;

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
}
