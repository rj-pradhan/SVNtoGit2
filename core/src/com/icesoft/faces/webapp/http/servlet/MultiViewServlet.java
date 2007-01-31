package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.webapp.http.core.PageServer;
import com.icesoft.faces.webapp.xmlhttp.ResponseStateManager;
import com.icesoft.util.IdGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

public class MultiViewServlet extends ServerAdapter {
    private int viewCount = 0;
    private HttpSession session;
    private ResponseStateManager responseStateManager;
    private Map views;

    public MultiViewServlet(HttpSession session, IdGenerator idGenerator, ResponseStateManager responseStateManager, Map views) {
        super(new PageServer());

        String sessionID = idGenerator.newIdentifier();
        //ContextEventRepeater needs this
        session.setAttribute(ResponseStateManager.ICEFACES_ID_KEY, sessionID);
        ContextEventRepeater.iceFacesIdRetrieved(session, sessionID);

        this.session = session;
        this.responseStateManager = responseStateManager;
        this.views = views;
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //extract viewNumber if this request is from a redirect
        final String viewNumber;
        String redirectViewNumber = request.getParameter("rvn");
        if (redirectViewNumber == null) {
            viewNumber = String.valueOf(++viewCount);
            ContextEventRepeater.viewNumberRetrieved(session, Integer.parseInt(viewNumber));
        } else {
            viewNumber = redirectViewNumber;
        }
        ServletView view = new ServletView(viewNumber, request, response, responseStateManager);
        views.put(viewNumber, view);
        super.service(request, response);
        view.redirectIfRequired();
        view.switchToPushMode();
        view.release();
    }
}
