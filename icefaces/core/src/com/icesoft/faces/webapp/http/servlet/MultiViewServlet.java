package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.PageServer;
import com.icesoft.faces.webapp.http.core.ViewQueue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class MultiViewServlet extends BasicAdaptingServlet {
    private int viewCount = 0;
    private HttpSession session;
    private Map views;
    private ViewQueue asynchronouslyUpdatedViews;
    private String sessionID;
    private Configuration configuration;
    private SessionDispatcher.Listener.Monitor sessionMonitor;

    public MultiViewServlet(HttpSession session, String sessionID, SessionDispatcher.Listener.Monitor sessionMonitor, Map views, ViewQueue asynchronouslyUpdatedViews, Configuration configuration) {
        super(new PageServer());
        this.sessionID = sessionID;
        this.session = session;
        this.sessionMonitor = sessionMonitor;
        this.views = views;
        this.asynchronouslyUpdatedViews = asynchronouslyUpdatedViews;
        this.configuration = configuration;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //extract viewNumber if this request is from a redirect
        ServletView view;
        String redirectViewNumber = request.getParameter("rvn");
        if (redirectViewNumber == null) {
            String viewNumber = String.valueOf(++viewCount);
            view = new ServletView(viewNumber, sessionID, request, response, asynchronouslyUpdatedViews, configuration);
            views.put(viewNumber, view);
            ContextEventRepeater.viewNumberRetrieved(session, sessionID, Integer.parseInt(viewNumber));
        } else {
            view = (ServletView) views.get(redirectViewNumber);
            if (view == null) {
                view = new ServletView(redirectViewNumber, sessionID, request, response, asynchronouslyUpdatedViews, configuration);
                views.put(redirectViewNumber, view);
                ContextEventRepeater.viewNumberRetrieved(session, sessionID, Integer.parseInt(redirectViewNumber));
            } else {
                view.updateOnRequest(request, response);
                view.switchToNormalMode();
            }
        }
        sessionMonitor.touchSession();
        super.service(request, response);
        view.switchToPushMode();
        view.release();
    }
}
