package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.PageServer;
import com.icesoft.faces.webapp.http.core.ViewQueue;
import com.icesoft.faces.webapp.xmlhttp.ResponseStateManager;
import com.icesoft.util.IdGenerator;

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

    public MultiViewServlet(HttpSession session, SessionDispatcher.Listener.Monitor sessionMonitor, IdGenerator idGenerator, Map views, ViewQueue asynchronouslyUpdatedViews, Configuration configuration) {
        super(new PageServer());
        this.sessionID = idGenerator.newIdentifier();
        //ContextEventRepeater needs this
        session.setAttribute(ResponseStateManager.ICEFACES_ID_KEY, sessionID);
        ContextEventRepeater.iceFacesIdRetrieved(session, sessionID);

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
            ContextEventRepeater.viewNumberRetrieved(session, Integer.parseInt(viewNumber));
        } else {
            view = (ServletView) views.get(redirectViewNumber);
            if (view == null || view.differentURI(request)) {
                view = new ServletView(redirectViewNumber, sessionID, request, response, asynchronouslyUpdatedViews, configuration);
                views.put(redirectViewNumber, view);
                ContextEventRepeater.viewNumberRetrieved(session, Integer.parseInt(redirectViewNumber));
            } else {
                view.setAsCurrentDuring(request, response);
                view.switchToNormalMode();
            }
        }
        sessionMonitor.touchSession();
        super.service(request, response);
        view.switchToPushMode();
        view.release();
    }
}
