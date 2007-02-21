package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Response;

public class OKHandler implements ResponseHandler {
    public static final ResponseHandler HANDLER = new OKHandler(); 

    public void respond(Response response) throws Exception {
        response.setStatus(200);
    }
}
