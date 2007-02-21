package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.xmlhttp.ResponseStateManager;
import com.icesoft.util.IdGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class MainSessionBoundServlet implements ServerServlet {
    private PathDispatcher dispatcher = new PathDispatcher();
    private Map views = new HashMap();

    public MainSessionBoundServlet(HttpSession session, IdGenerator idGenerator, ResponseStateManager stateManager, Configuration configuration) {
        final ServerServlet viewServer;
        if (configuration.getAttributeAsBoolean("concurrentDOMViews", false)) {
            viewServer = new MultiViewServlet(session, idGenerator, stateManager, views);
        } else {
            viewServer = new SingleViewServlet(session, idGenerator, stateManager, views);
        }
        final ServerServlet pushServer = new PushServlet(session, views);

        dispatcher.dispatchOn(".*block\\/.*", pushServer);
        dispatcher.dispatchOn(".*", viewServer);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        dispatcher.service(request, response);
    }

    public void shutdown() {
        dispatcher.shutdown();
    }
}
