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
    //having a static field here is ok because web applications are started in separate classloaders 
    private static Map SessionBoundServers = new HashMap();

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);
        final ServletServer server;
        if (session.isNew()) {
            server = newServlet(session);
            SessionBoundServers.put(session, server);
        } else {
            server = (ServletServer) SessionBoundServers.get(session);
        }

        try {
            server.service(request, response);
        } catch (IllegalStateException e) {
            //session has expired
            SessionBoundServers.remove(session);
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
