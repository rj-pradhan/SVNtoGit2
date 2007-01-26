package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.webapp.http.core.PushServer;
import com.icesoft.faces.webapp.http.core.UpdateManager;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesServlet;
import com.icesoft.faces.webapp.xmlhttp.ResponseStateManager;
import com.icesoft.util.IdGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class MultiViewServlet implements ServletServer {
    private int viewCount = 0;
    private HttpSession session;

    private ServletServer server;
    private UpdateManager updateManager;
    private ResponseStateManager responseStateManager;
    private Map views = new HashMap();

    public MultiViewServlet(HttpSession session, IdGenerator idGenerator, ResponseStateManager responseStateManager) {
        String sessionID = idGenerator.newIdentifier();
        //ContextEventRepeater needs this
        session.setAttribute(ResponseStateManager.ICEFACES_ID_KEY, sessionID);
        ContextEventRepeater.iceFacesIdRetrieved(session, sessionID);

        this.session = session;
        this.responseStateManager = responseStateManager;
        this.updateManager = new UpdateManager(session);
        this.server = new ServerAdapterServlet(new PushServer(updateManager));
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String viewNumber = request.getParameter("viewNumber");
        //FileUploadServlet needs this
        session.setAttribute(PersistentFacesServlet.CURRENT_VIEW_NUMBER, viewNumber);

        final ServletView view;
        if (viewNumber == null) {
            //extract viewNumber if this request is from a redirect
            String redirectViewNumber = request.getParameter("rvn");
            if (redirectViewNumber == null) {
                viewNumber = String.valueOf(++viewCount);
                ContextEventRepeater.viewNumberRetrieved(session, Integer.parseInt(viewNumber));
            } else {
                viewNumber = redirectViewNumber;
            }
            view = new ServletView(viewNumber, request, response, responseStateManager);
            views.put(viewNumber, view);
            server.service(request, response);
            view.redirectIfRequired();
            view.switchToPushMode();
        } else {
            view = (ServletView) views.get(viewNumber);
            view.setAsCurrentDuring(request);
            server.service(request, response);
        }

        view.release();
    }

    public void shutdown() {
        updateManager.shutdown();
        server.shutdown();
    }
}
