package com.icesoft.faces.webapp.http.standard;

import com.icesoft.faces.webapp.http.ResponseHandler;
import com.icesoft.faces.webapp.http.Response;

public class OKHandler implements ResponseHandler {
    public static final ResponseHandler HANDLER = new OKHandler(); 

    public void respond(Response response) throws Exception {
        response.setStatus(200);
    }
}
