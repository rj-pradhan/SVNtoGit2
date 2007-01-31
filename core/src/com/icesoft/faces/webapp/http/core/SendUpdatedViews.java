package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;

import java.io.StringWriter;

public class SendUpdatedViews implements Server {
    private ResponseHandler responseHandler;

    public SendUpdatedViews(final UpdateManager updateManager) {
        this.responseHandler = new ResponseHandler() {

            public void respond(Response response) throws Exception {
                StringWriter writer = new StringWriter();
                String[] views = updateManager.getUpdatedViews();
                writer.write("<updated-views>");
                for (int i = 0; i < views.length; i++) {
                    writer.write(views[i]);
                    if (i < views.length - 1) writer.write(' ');
                }
                writer.write("</updated-views>");
                byte[] content = writer.toString().getBytes("UTF-8");
                response.setHeader("Content-Type", "text/xml;charset=UTF-8");
                response.setHeader("Content-Length", content.length);
                response.writeBody().write(content);
            }
        };
    }

    public void service(Request request) throws Exception {
        request.respondWith(responseHandler);
    }

    public void shutdown() {
    }
}
