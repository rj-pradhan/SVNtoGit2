package com.icesoft.faces.webapp.http;

import com.icesoft.faces.webapp.http.core.RedirectOnJSBlocked;
import com.icesoft.faces.webapp.http.core.ServeBridgeJSCode;
import com.icesoft.faces.webapp.http.core.ServeCSSResource;
import com.icesoft.faces.webapp.http.core.ServeBlankPage;
import com.icesoft.faces.webapp.http.core.ServeExtraJSCode;
import com.icesoft.faces.webapp.http.core.SendUpdates;
import com.icesoft.faces.webapp.http.core.UpdateManager;
import com.icesoft.faces.webapp.http.core.ReceiveSendUpdates;
import com.icesoft.faces.webapp.http.core.SendUpdatedViews;
import com.icesoft.faces.webapp.http.core.SendSessionExpired;
import com.icesoft.faces.webapp.http.standard.CompressingServer;
import com.icesoft.faces.webapp.http.standard.CacheControlledServer;

import javax.servlet.http.HttpSession;

public class SessionBoundServer implements Server {
    private Server dispatcher;

    public SessionBoundServer(HttpSession session, Configuration configuration) {
        UpdateManager updateManager = new UpdateManager(session);
        RegexPathDispatcher pathDispatcher = new RegexPathDispatcher();
        pathDispatcher.dispatchOn(".*javascript-blocked$", new RedirectOnJSBlocked(configuration));
        pathDispatcher.dispatchOn(".*xmlhttp\\/icefaces\\-d2d\\.js$", new CacheControlledServer(new ServeBridgeJSCode(configuration)));
        pathDispatcher.dispatchOn(".*xmlhttp\\/.*\\.js$", new CacheControlledServer(new ServeExtraJSCode()));
        pathDispatcher.dispatchOn(".*xmlhttp\\/css\\/.*", new CacheControlledServer(new ServeCSSResource()));
        pathDispatcher.dispatchOn(".*blank\\.iface$", new ServeBlankPage());
        pathDispatcher.dispatchOn(".*block\\/send\\-receive\\-updates$", new ReceiveSendUpdates(updateManager));
        pathDispatcher.dispatchOn(".*block\\/receive\\-updates$", new SendUpdates(updateManager));
        pathDispatcher.dispatchOn(".*block\\/receive\\-updated\\-views$", new SendUpdatedViews(updateManager));

        if (configuration.getAttributeAsBoolean("compressResources", true)) {
            dispatcher = new CompressingServer(pathDispatcher);
        } else {
            dispatcher = pathDispatcher;
        }
        
        dispatcher = new SendSessionExpired(session, dispatcher);
    }

    public void service(final Request request) throws Exception {
        dispatcher.service(request);
    }

    public void destroy() {        
    }
}
