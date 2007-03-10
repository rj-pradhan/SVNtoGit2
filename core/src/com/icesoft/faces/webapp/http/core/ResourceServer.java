package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.CacheControlledServer;
import com.icesoft.faces.webapp.http.common.standard.CompressingServer;
import com.icesoft.faces.webapp.http.common.standard.PathDispatcherServer;

public class ResourceServer implements Server {
    private Server dispatcher;

    public ResourceServer(Configuration configuration) {
        PathDispatcherServer pathDispatcher = new PathDispatcherServer();
        pathDispatcher.dispatchOn(".*xmlhttp\\/javascript-blocked$", new RedirectOnJSBlocked(configuration));
        pathDispatcher.dispatchOn(".*xmlhttp\\/.*\\/icefaces\\-d2d\\.js$", new CacheControlledServer(new ServeBridgeJSCode(configuration)));
        pathDispatcher.dispatchOn(".*xmlhttp\\/.*\\/.*\\.js$", new CacheControlledServer(new ServeExtraJSCode()));
        pathDispatcher.dispatchOn(".*xmlhttp\\/css\\/.*", new CacheControlledServer(new ServeCSSResource()));
        pathDispatcher.dispatchOn(".*xmlhttp\\/blank\\.iface$", new ServeBlankPage());

        if (configuration.getAttributeAsBoolean("compressResources", true)) {
            dispatcher = new CompressingServer(pathDispatcher);
        } else {
            dispatcher = pathDispatcher;
        }
    }

    public void service(Request request) throws Exception {

        dispatcher.service(request);
    }

    public void shutdown() {
        dispatcher.shutdown();
    }
}
