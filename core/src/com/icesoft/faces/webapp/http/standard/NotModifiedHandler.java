package com.icesoft.faces.webapp.http.standard;

import com.icesoft.faces.webapp.http.ResponseHandler;
import com.icesoft.faces.webapp.http.Response;

import java.util.Date;

public class NotModifiedHandler implements ResponseHandler {
    private Date expirationDate;

    public NotModifiedHandler(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public void respond(Response response) throws Exception {
        response.setStatus(304);
        response.setHeader("Date", new Date());
        response.setHeader("Expires", expirationDate);
    }
}
