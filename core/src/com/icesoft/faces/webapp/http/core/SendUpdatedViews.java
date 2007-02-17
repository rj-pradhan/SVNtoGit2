package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;

import java.io.IOException;
import java.io.Writer;

public class SendUpdatedViews implements Server {
    private ResponseHandler responseHandler;

    public SendUpdatedViews(final UpdateManager updateManager) {
        this.responseHandler = new FixedXMLContentHandler() {
            
            public void writeTo(Writer writer) throws IOException {
                String[] views = updateManager.getUpdatedViews();
                writer.write("<updated-views>");
                for (int i = 0; i < views.length; i++) {
                    writer.write(views[i]);
                    if (i < views.length - 1) writer.write(' ');
                }
                writer.write("</updated-views>");
            }
        };
    }

    public void service(Request request) throws Exception {
        request.respondWith(responseHandler);
    }

    public void shutdown() {
    }
}
