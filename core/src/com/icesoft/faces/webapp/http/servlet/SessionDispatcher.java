package com.icesoft.faces.webapp.http.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class SessionDispatcher implements ServletServer {
    //having a static field here is ok because web applications are started in separate classloaders 
    private final static Map SessionBoundServers = new HashMap();

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(false);

        //the servlet container expired the session
        if (session == null) {
            sendSessionExpired(response);
        } else {
            final ServletServer server;
            if (session.isNew()) {
                server = this.newServlet(session);
                SessionBoundServers.put(session, server);
            } else {
                server = (ServletServer) SessionBoundServers.get(session);
            }

            if (server == null) {
                //session has expired in the mean time, server removed by the session listener
                sendSessionExpired(response);
            } else {
                try {
                    server.service(request, response);
                } catch (IllegalStateException e) {
                    //session has expired
                    SessionBoundServers.remove(session);
                    server.shutdown();
                    sendSessionExpired(response);
                }
            }
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
        }
    }

    private static void sendSessionExpired(HttpServletResponse response) throws IOException {
        response.setHeader("Content-Type", "text/xml;charset=UTF-8");
        response.getOutputStream().write("<session-expired/>".getBytes("UTF-8"));
    }
}
