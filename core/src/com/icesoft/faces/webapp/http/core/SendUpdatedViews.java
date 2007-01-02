package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.Server;
import com.icesoft.faces.webapp.http.Request;
import com.icesoft.faces.webapp.http.ResponseHandler;
import com.icesoft.faces.webapp.http.Response;

import java.io.StringWriter;
import java.io.ByteArrayInputStream;

public class SendUpdatedViews implements Server {
    private ResponseHandler responseHandler;

    public SendUpdatedViews(final UpdateManager updateManager) {
        this.responseHandler = new ResponseHandler() {

            public void respond(Response response) throws Exception {
                StringWriter writer = new StringWriter();
                String[] views = updateManager.getUpdatedViews();
                for (int i = 0; i < views.length; i++) {
                    writer.write(views[i]);
                    if (i < views.length - 1) writer.write(' ');
                }
                byte[] content = writer.toString().getBytes("UTF-8");
                response.setHeader("Content-Type", "text/plain;charset=UTF-8");
                response.setHeader("Content-Length", content.length);
                response.writeBodyFrom(new ByteArrayInputStream(content));
            }
        };
    }

    public void service(Request request) throws Exception {
        request.respondWith(responseHandler);
    }
}
