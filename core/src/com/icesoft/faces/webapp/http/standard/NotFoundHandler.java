package com.icesoft.faces.webapp.http.standard;

import com.icesoft.faces.webapp.http.ResponseHandler;
import com.icesoft.faces.webapp.http.Response;

public class NotFoundHandler implements ResponseHandler {
    public static final NotFoundHandler HANDLER = new NotFoundHandler();

    public void respond(Response response) {
        response.setStatus(404);
    }
}
