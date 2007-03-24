package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.ViewQueue;
import com.icesoft.util.IdGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class MainSessionBoundServlet implements PseudoServlet {
    private PathDispatcher dispatcher = new PathDispatcher();
    private Map views = new HashMap();
    private ViewQueue allUpdatedViews = new ViewQueue();

    public MainSessionBoundServlet(HttpSession session, IdGenerator idGenerator, Configuration configuration) {
        final PseudoServlet viewServlet;
        if (configuration.getAttributeAsBoolean("concurrentDOMViews", false)) {
            viewServlet = new MultiViewServlet(session, idGenerator, views, allUpdatedViews);
        } else {
            viewServlet = new SingleViewServlet(session, idGenerator, views, allUpdatedViews);
        }
        final PseudoServlet pushServlet = new PushServlet(views, allUpdatedViews, configuration);
        final PseudoServlet uploadServlet = new UploadServlet(views, configuration, session.getServletContext());

        dispatcher.dispatchOn(".*uploadHtml", uploadServlet);
        dispatcher.dispatchOn(".*block\\/.*", pushServlet);
        dispatcher.dispatchOn(".*", viewServlet);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        dispatcher.service(request, response);
    }

    public void shutdown() {
        dispatcher.shutdown();
    }
}
