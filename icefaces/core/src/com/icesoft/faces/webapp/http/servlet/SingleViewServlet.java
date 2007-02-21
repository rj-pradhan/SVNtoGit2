package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.webapp.http.core.PageServer;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesServlet;
import com.icesoft.faces.webapp.xmlhttp.ResponseStateManager;
import com.icesoft.util.IdGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class SingleViewServlet extends AdapterServlet {
    private static final String viewNumber = "1";
    private HttpSession session;

    private ResponseStateManager responseStateManager;
    private Map views;

    public SingleViewServlet(HttpSession session, IdGenerator idGenerator, ResponseStateManager responseStateManager, Map views) {
        super(new PageServer());

        String sessionID = idGenerator.newIdentifier();
        //ContextEventRepeater needs this
        session.setAttribute(ResponseStateManager.ICEFACES_ID_KEY, sessionID);
        ContextEventRepeater.iceFacesIdRetrieved(session, sessionID);
        //FileUploadServlet needs this
        session.setAttribute(PersistentFacesServlet.CURRENT_VIEW_NUMBER, viewNumber);

        this.session = session;
        this.responseStateManager = responseStateManager;
        this.views = views;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //create single view or re-create view if the request is the result of a redirect 
        ServletView view = (ServletView) views.get(viewNumber);
        if (view == null || view.differentURI(request)) {
            view = new ServletView(viewNumber, request, response, responseStateManager);
            views.put(viewNumber, view);
            ContextEventRepeater.viewNumberRetrieved(session, Integer.parseInt(viewNumber));
        }

        view.setAsCurrentDuring(request);
        view.switchToImmediateMode(response);
        super.service(request, response);
        view.redirectIfRequired();
        view.switchToPushMode();
        view.release();
    }

    public void shutdown() {
        //discard view
        views.remove(viewNumber);
        super.shutdown();
    }
}
