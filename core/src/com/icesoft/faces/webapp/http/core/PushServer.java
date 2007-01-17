package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.standard.PathDispatcherServer;

public class PushServer implements Server {
    private PathDispatcherServer dispatcher = new PathDispatcherServer();;

    public PushServer(UpdateManager updateManager) {
        dispatcher.dispatchOn(".*block\\/send\\-receive\\-updates$", new ReceiveSendUpdates(updateManager));
        dispatcher.dispatchOn(".*block\\/receive\\-updates$", new SendUpdates(updateManager));
        dispatcher.dispatchOn(".*block\\/receive\\-updated\\-views$", new SendUpdatedViews(updateManager));
        dispatcher.dispatchOn(".*", new ServePage());
    }

    public void service(Request request) throws Exception {
        dispatcher.service(request);
    }

    public void shutdown() {
        dispatcher.shutdown();
    }
}
