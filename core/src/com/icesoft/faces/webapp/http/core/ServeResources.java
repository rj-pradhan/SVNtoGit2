package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.Server;
import com.icesoft.faces.webapp.http.Request;
import com.icesoft.faces.webapp.http.ResponseHandler;
import com.icesoft.faces.webapp.http.Response;
import com.icesoft.faces.webapp.http.standard.NotFoundHandler;

import java.io.InputStream;

public class ServeResources implements Server {
    private static final String Package = "com/icesoft/faces/resources/css/";
    private ClassLoader loader;

    public ServeResources() {
        loader = this.getClass().getClassLoader();
    }

    public void service(Request request) throws Exception {
        String path = request.getURI().getPath();
        String file = path.substring(path.lastIndexOf("css/") + 4, path.length());
        final InputStream in = loader.getResourceAsStream(Package + file);

        if (in == null) {
            request.respondWith(NotFoundHandler.HANDLER);
        } else {
            request.respondWith(new ResponseHandler() {
                public void respond(Response response) throws Exception {
                    response.writeBodyFrom(in);
                }
            });
        }
    }
}
