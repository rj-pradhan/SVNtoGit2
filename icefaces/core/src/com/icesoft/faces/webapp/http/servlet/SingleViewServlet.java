package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.PageServer;
import com.icesoft.faces.webapp.http.core.ViewQueue;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesServlet;
import com.icesoft.faces.webapp.xmlhttp.ResponseStateManager;
import com.icesoft.util.IdGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class SingleViewServlet extends ThreadBlockingAdaptingServlet {
    private static final String viewNumber = "1";
    private HttpSession session;
    private Map views;
    private String sessionID;
    private ViewQueue allUpdatedViews;
    private Configuration configuration;
    private SessionDispatcher.Listener.Monitor sessionMonitor;

    public SingleViewServlet(HttpSession session, SessionDispatcher.Listener.Monitor sessionMonitor, IdGenerator idGenerator, Map views, ViewQueue allUpdatedViews, Configuration configuration) {
        super(new PageServer());

        this.sessionID = idGenerator.newIdentifier();
        //ContextEventRepeater needs this
        session.setAttribute(ResponseStateManager.ICEFACES_ID_KEY, sessionID);
        ContextEventRepeater.iceFacesIdRetrieved(session, sessionID);
        //FileUploadServlet needs this
        session.setAttribute(PersistentFacesServlet.CURRENT_VIEW_NUMBER, viewNumber);

        this.session = session;
        this.sessionMonitor = sessionMonitor;
        this.views = views;
        this.allUpdatedViews = allUpdatedViews;
        this.configuration = configuration;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //create single view or re-create view if the request is the result of a redirect 
        ServletView view = (ServletView) views.get(viewNumber);
        if (view == null || view.differentURI(request)) {
            view = new ServletView(viewNumber, sessionID, request, response, allUpdatedViews, configuration);
            views.put(viewNumber, view);
            ContextEventRepeater.viewNumberRetrieved(session, Integer.parseInt(viewNumber));
        }

        view.setAsCurrentDuring(request, response);
        view.switchToNormalMode();
        sessionMonitor.touchSession();
        super.service(request, response);
        view.switchToPushMode();
        view.release();
    }

    public void shutdown() {
        super.shutdown();
    }
}
