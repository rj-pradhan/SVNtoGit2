package com.icesoft.faces.webapp.http.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class SessionDispatcher implements ServletServer {
    private static Map SessionBoundServers = new HashMap();

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);
        final ServletServer sessionBoundServer;
        if (session.isNew()) {
            sessionBoundServer = newServlet(session);
            SessionBoundServers.put(session, sessionBoundServer);
        } else {
            sessionBoundServer = (ServletServer) SessionBoundServers.get(session);
        }

        try {
            sessionBoundServer.service(request, response);
        } catch (IllegalStateException e) {
            //session has expired
            ServletServer server = (ServletServer) SessionBoundServers.remove(session);
            server.shutdown();            
        }
    }

    public void shutdown() {
        Iterator i = SessionBoundServers.values().iterator();
        while (i.hasNext()) {
            ServletServer server = (ServletServer) i.next();
            server.shutdown();
        }
    }

    protected abstract ServletServer newServlet(HttpSession session) throws Exception;

    public static class Listener implements HttpSessionListener {
        public void sessionCreated(HttpSessionEvent event) {
        }

        public void sessionDestroyed(HttpSessionEvent event) {
            ServletServer server = (ServletServer) SessionBoundServers.remove(event.getSession());
            server.shutdown();
        }
    }
}
