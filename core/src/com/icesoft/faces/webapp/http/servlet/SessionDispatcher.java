package com.icesoft.faces.webapp.http.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class SessionDispatcher implements PseudoServlet {
    //having a static field here is ok because web applications are started in separate classloaders
    private final static List SessionDispatchers = new ArrayList();
    private Map sessionBoundServers = new HashMap();

    protected SessionDispatcher() {
        SessionDispatchers.add(this);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);
        //test if session is still around
        if (sessionBoundServers.containsKey(session)) {
            PseudoServlet server = (PseudoServlet) sessionBoundServers.get(session);
            server.service(request, response);
        } else {
            //session has expired in the mean time, server removed by the session listener
            throw new ServletException("Session expired");
        }
    }

    public void shutdown() {
        Iterator i = sessionBoundServers.values().iterator();
        while (i.hasNext()) {
            PseudoServlet server = (PseudoServlet) i.next();
            server.shutdown();
        }
    }

    private void sessionCreated(HttpSession session) {
        try {
            sessionBoundServers.put(session, this.newServlet(session));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sessionDestroyed(HttpSession session) {
        PseudoServlet server = (PseudoServlet) sessionBoundServers.remove(session);
        server.shutdown();
    }

    protected abstract PseudoServlet newServlet(HttpSession session) throws Exception;

    public static class Listener implements HttpSessionListener, ServletContextListener {
        private List sessions = new ArrayList();
        private boolean run = true;

        public void sessionCreated(HttpSessionEvent event) {
            HttpSession session = event.getSession();
            sessions.add(session);

            Iterator i = SessionDispatchers.iterator();
            while (i.hasNext()) {
                try {
                    SessionDispatcher sessionDispatcher = (SessionDispatcher) i.next();
                    sessionDispatcher.sessionCreated(session);
                } catch (Exception e) {
                    new RuntimeException(e);
                }
            }
        }

        public void sessionDestroyed(HttpSessionEvent event) {
            HttpSession session = event.getSession();
            sessions.remove(session);

            Iterator i = SessionDispatchers.iterator();
            while (i.hasNext()) {
                try {
                    SessionDispatcher sessionDispatcher = (SessionDispatcher) i.next();
                    sessionDispatcher.sessionDestroyed(session);
                } catch (Exception e) {
                    new RuntimeException(e);
                }
            }
        }

        public void contextInitialized(ServletContextEvent servletContextEvent) {
            Thread monitor = new Thread("Session Monitor") {
                public void run() {
                    while (run) {
                        //iterate over the sessions using a copying iterator
                        Iterator iterator = new ArrayList(sessions).iterator();
                        while (iterator.hasNext()) {
                            HttpSession session = (HttpSession) iterator.next();
                            try {
                                long elapsedInterval = System.currentTimeMillis() - session.getLastAccessedTime();
                                long maxInterval = session.getMaxInactiveInterval() * 1000;
                                if (elapsedInterval > maxInterval) {
                                    session.invalidate();
                                }
                            } catch (IllegalStateException e) {
                                //session was expired by the container
                                sessions.remove(session);
                            } catch (Throwable t) {
                                //just inform that something went wrong
                                //todo: replace this with a warning log call 
                                t.printStackTrace();
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
            monitor.setDaemon(true);
            monitor.start();
        }

        public void contextDestroyed(ServletContextEvent servletContextEvent) {
            run = false;
        }
    }
}
