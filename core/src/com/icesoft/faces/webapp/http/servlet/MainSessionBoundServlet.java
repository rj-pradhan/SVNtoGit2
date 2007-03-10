package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.ViewQueue;
import com.icesoft.util.IdGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class MainSessionBoundServlet implements ServerServlet {
    private PathDispatcher dispatcher = new PathDispatcher();
    private Map views = new HashMap();
    private ViewQueue allUpdatedViews = new ViewQueue();

    public MainSessionBoundServlet(HttpSession session, IdGenerator idGenerator, Configuration configuration) {
        final ServerServlet viewServer;
        if (configuration.getAttributeAsBoolean("concurrentDOMViews", false)) {
            viewServer = new MultiViewServlet(session, idGenerator, views, allUpdatedViews);
        } else {
            viewServer = new SingleViewServlet(session, idGenerator, views, allUpdatedViews);
        }
        final ServerServlet pushServer = new PushServlet(views, allUpdatedViews, configuration);

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
