package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;

import java.io.IOException;
import java.io.Writer;

public class SendUpdates implements Server {
    private UpdateManager updateManager;

    public SendUpdates(UpdateManager updateManager) {
        this.updateManager = updateManager;
    }

    public void service(final Request request) throws Exception {
        request.respondWith(new FixedXMLContentHandler() {

            public void writeTo(Writer writer) throws IOException {
                String[] views = request.getParameterAsStrings("viewNumber");
                updateManager.serialize(views, writer);
            }
        });
    }
    
    public void shutdown() {
    }
}
