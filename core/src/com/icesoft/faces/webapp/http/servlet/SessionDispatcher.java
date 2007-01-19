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
        HttpSession session = request.getSession(false);

        //the servlet container expired the session
        if (session == null) {
            SessionExpired.Server.service(request, response);
            return;
        }
        
        final ServletServer server;
        if (session.isNew()) {
            server = this.newServlet(session);
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
            SessionExpired.Server.service(request, response);            
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
            HttpSession session = event.getSession();
            ServletServer server = (ServletServer) SessionBoundServers.remove(session);
            server.shutdown();
            SessionBoundServers.put(session, SessionExpired.Server);
        }
    }

    private static class SessionExpired implements ServletServer {
        public static final ServletServer Server = new SessionExpired();

        public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.setHeader("X-SESSION-EXPIRED", ".");
            response.getOutputStream().write(".\n".getBytes());
        }

        public void shutdown() {
        }
    }
}
