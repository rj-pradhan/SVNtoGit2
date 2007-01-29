package com.icesoft.faces.webapp.http.servlet;

import sun.tools.hprof.Tracker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedList;

public abstract class SessionDispatcher implements ServletServer {
    //having a static field here is ok because web applications are started in separate classloaders 
    private final static Map SessionBoundServers = new HashMap();
    private boolean run;

    protected SessionDispatcher() {
        Thread monitor = new Thread() {
            public void run() {
                while (run) {
                    //iterate over the session using a copying iterator
                    Iterator iterator = new LinkedList(SessionBoundServers.keySet()).iterator();
                    while (iterator.hasNext()) {
                        HttpSession session = (HttpSession) iterator.next();
                        long elapsedInterval = System.currentTimeMillis() - session.getLastAccessedTime();
                        long maxInterval = session.getMaxInactiveInterval() * 1000;
                        if (elapsedInterval > maxInterval) {
                            session.invalidate();
                        }
                    }
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        //ignore interrupts
                    }
                }
            }
        };
        run = true;
        monitor.start();

    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);
        //track new session
        if (session.isNew()) {
            SessionBoundServers.put(session, this.newServlet(session));
        }
        //test if session is still around
        if (SessionBoundServers.containsKey(session)) {
            ServletServer server = (ServletServer) SessionBoundServers.get(session);
            try {
                server.service(request, response);
            } catch (IllegalStateException e) {
                //session has expired
                SessionBoundServers.remove(session);
                server.shutdown();
                sendSessionExpired(response);
            }
        } else {
            //session has expired in the mean time, server removed by the session listener
            sendSessionExpired(response);
        }
    }

    public void shutdown() {
        run = false;
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
