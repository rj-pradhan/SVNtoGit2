package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.Server;
import com.icesoft.faces.webapp.http.Request;
import com.icesoft.faces.webapp.http.ResponseHandler;
import com.icesoft.faces.webapp.http.Response;
import com.icesoft.faces.webapp.http.standard.NotFoundHandler;

import java.io.InputStream;

public class ServeExtraJSCode implements Server {
    private static final String Package = "com/icesoft/faces/webapp/xmlhttp/";
    private ClassLoader loader;

    public ServeExtraJSCode() {
        loader = this.getClass().getClassLoader();
    }

    public void service(Request request) throws Exception {
        String path = request.getURI().getPath();
        String file = path.substring(path.lastIndexOf("/") + 1, path.length());
        final InputStream in = loader.getResourceAsStream(Package + file);

        if (in == null) {
            request.respondWith(NotFoundHandler.HANDLER);
        } else {
            request.respondWith(new ResponseHandler() {
                public void respond(Response response) throws Exception {
                    response.setHeader("Content-Type", "text/javascript");
                    response.writeBodyFrom(in);
                }
            });
        }
    }
}
