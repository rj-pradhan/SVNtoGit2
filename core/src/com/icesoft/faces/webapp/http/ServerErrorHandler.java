package com.icesoft.faces.webapp.http;

import java.io.PrintWriter;

public class ServerErrorHandler implements ResponseHandler {
    private Throwable throwable;

    public ServerErrorHandler(Throwable t) {
        throwable = t;
    }

    public void respond(Response response) throws Exception {
        response.setStatus(500);
        throwable.printStackTrace(new PrintWriter(response.writeBody()));
    }
}
