package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;

public class ServeBridgeJSCode implements Server {
    private Configuration configuration;

    public ServeBridgeJSCode(Configuration configuration) {
        this.configuration = configuration;
    }

    public void service(Request request) throws Exception {
        String path = request.getURI().getPath();

        String contextName = path.substring(0, path.indexOf("/", 1));//first atom in the resource!

        //create bridge configuration
        final InputStream bridgeConfiguration = new ByteArrayInputStream((
                "configuration = {" +
                        "   synchronous: " + configuration.getAttribute("synchronousUpdate", "false") + "," +
                        "   redirectURI: " + configuration.getAttribute("connectionLostRedirectURI", "null") + "," +
                        "   connection: {" +
                        "       context: '" + contextName + "'," +
                        "       timeout: " + configuration.getAttributeAsLong("connectionTimeout", 30000) + "," +
                        "       heartbeat: {" +
                        "           interval: " + configuration.getAttributeAsLong("heartbeatInterval", 20000) + "," +
                        "           timeout: " + configuration.getAttributeAsLong("heartbeatTimeout", 3000) + "," +
                        "           retries: " + configuration.getAttributeAsLong("heartbeatRetries", 3) +
                        "       }" +
                        "   }" +
                        "};\n").getBytes());
        //load bridge code
        final InputStream bridgeCode = this.getClass().getClassLoader().getResourceAsStream("com/icesoft/faces/webapp/xmlhttp/icefaces-d2d.js");

        request.respondWith(new ResponseHandler() {
            public void respond(Response response) throws Exception {
                response.setHeader("Content-Type", "text/javascript");
                response.writeBodyFrom(new SequenceInputStream(bridgeConfiguration, bridgeCode));
            }
        });
    }

    public void shutdown() {
    }
}
