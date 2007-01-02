package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.Server;
import com.icesoft.faces.webapp.http.Request;
import com.icesoft.faces.webapp.http.Configuration;
import com.icesoft.faces.webapp.http.ResponseHandler;
import com.icesoft.faces.webapp.http.Response;

import java.io.InputStream;
import java.io.SequenceInputStream;
import java.io.ByteArrayInputStream;

public class ServeBridgeJSCode implements Server {
    private static final String ENTERPRISE_JS = "com/icesoft/faces/async/server/icefaces-d2d.js";
    private static final String COMMUNITY_JS = "com/icesoft/faces/webapp/xmlhttp/icefaces-d2d.js";
    private ClassLoader loader = this.getClass().getClassLoader();
    private String resource;
    private Configuration configuration;

    public ServeBridgeJSCode(Configuration configuration) {
        this.configuration = configuration;
        this.resource = (loader.getResourceAsStream(ENTERPRISE_JS) == null) ? COMMUNITY_JS : ENTERPRISE_JS;
    }

    public void service(Request request) throws Exception {
        String path = request.getURI().getPath();
        String contextName = path.substring(0, path.indexOf("/", 1));//first atom in the resource!

        //create bridge configuration
        final InputStream bridgeConfiguration = new ByteArrayInputStream((
            "configuration = {" +
            "   synchronous: " + configuration.getAttribute("synchronousUpdate", "true") + "," +
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
        final InputStream bridgeCode = loader.getResourceAsStream(resource);

        request.respondWith(new ResponseHandler() {
            public void respond(Response response) throws Exception {
                response.writeBodyFrom(new SequenceInputStream(bridgeConfiguration, bridgeCode));
            }
        });
    }
}
