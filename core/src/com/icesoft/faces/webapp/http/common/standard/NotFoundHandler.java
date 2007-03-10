package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;

public class NotFoundHandler implements ResponseHandler {
    public static final NotFoundHandler HANDLER = new NotFoundHandler();

    public void respond(Response response) {
        response.setStatus(404);
    }
}
