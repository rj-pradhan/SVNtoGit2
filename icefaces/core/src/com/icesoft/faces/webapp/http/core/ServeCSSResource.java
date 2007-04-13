package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.NotFoundHandler;
import com.icesoft.util.ContentUtilities;

import java.io.InputStream;

public class ServeCSSResource implements Server {
    private static final String Package = "com/icesoft/faces/resources/css/";
    private ClassLoader loader;

    public ServeCSSResource() {
        loader = this.getClass().getClassLoader();
    }

    public void service(Request request) throws Exception {
        String path = request.getURI().getPath();
        final String contentType = ContentUtilities.guessTypeFromName(path);
        String file = path.substring(path.lastIndexOf("css/") + 4, path.length());
        final InputStream in = loader.getResourceAsStream(Package + file);

        if (in == null) {
            request.respondWith(NotFoundHandler.HANDLER);
        } else {
            request.respondWith(new ResponseHandler() {
                public void respond(Response response) throws Exception {
                    response.setHeader("Content-Type", contentType);
                    response.writeBodyFrom(in);
                }
            });
        }
    }

    public void shutdown() {
    }
}
