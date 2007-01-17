package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Response;

import java.io.PrintWriter;

public class ServerErrorHandler implements ResponseHandler {
    private Throwable throwable;

    public ServerErrorHandler(Throwable t) {
        throwable = t;
    }

    public void respond(Response response) throws Exception {
        response.setStatus(500);
        response.setHeader("Content-Type", "text/plain;charset=UTF-8");
        PrintWriter writer = new PrintWriter(response.writeBody());
        throwable.printStackTrace(writer);
        writer.flush();
    }
}
