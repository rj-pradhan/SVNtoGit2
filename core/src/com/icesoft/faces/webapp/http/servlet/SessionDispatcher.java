package com.icesoft.faces.webapp.http.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private static final Log Log = LogFactory.getLog(SessionDispatcher.class);
    private final static List SessionDispatchers = new ArrayList();
    private Map sessionBoundServers = new HashMap();

    protected SessionDispatcher() {
        SessionDispatchers.add(this);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession(true);
        //test if session is still around
        if (sessionBoundServers.containsKey(session.getId())) {
            PseudoServlet server = (PseudoServlet) sessionBoundServers.get(session.getId());
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
            sessionBoundServers.put(session.getId(), this.newServlet(session,Listener.lookupSessionMonitor(session)));
        } catch (Exception e) {
            Log.warn(e);
            throw new RuntimeException(e);
        } catch (Throwable t) {
            Log.warn(t);
            throw new RuntimeException(t);
        }
    }

    private void sessionShutdown(HttpSession session) {
        PseudoServlet server = (PseudoServlet) sessionBoundServers.get(session.getId());
        server.shutdown();
    }

    private void sessionDestroyed(HttpSession session) {
        sessionBoundServers.remove(session.getId());
    }

    protected abstract PseudoServlet newServlet(HttpSession session, Listener.Monitor sessionMonitor) throws Exception;

    public static class Listener implements HttpSessionListener, ServletContextListener {
        private static Map sessionMonitors = new HashMap();
        private boolean run = true;

        public void sessionCreated(HttpSessionEvent event) {
            HttpSession session = event.getSession();
            sessionMonitors.put(session, new Monitor(session));

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

        public void sessionShutdown(HttpSession session) {
            Iterator i = SessionDispatchers.iterator();
            while (i.hasNext()) {
                try {
                    SessionDispatcher sessionDispatcher = (SessionDispatcher) i.next();
                    sessionDispatcher.sessionShutdown(session);
                } catch (Exception e) {
                    new RuntimeException(e);
                }
            }
            session.invalidate();
        }

        public void sessionDestroyed(HttpSessionEvent event) {
            HttpSession session = event.getSession();

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
                        try {
                            //iterate over the sessions using a copying iterator
                            Iterator iterator = new ArrayList(sessionMonitors.values()).iterator();
                            while (iterator.hasNext()) {
                                final Monitor sessionMonitor = (Monitor) iterator.next();
                                sessionMonitor.shutdownIfExpired();
                            }

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

        public static Monitor lookupSessionMonitor(HttpSession session) {
            return (Monitor) sessionMonitors.get(session);
        }

        public class Monitor {
            private HttpSession session;
            private long lastAccess;

            public Monitor(HttpSession session) {
                this.session = session;
                this.lastAccess = session.getLastAccessedTime();
            }

            public void touchSession() {
                lastAccess = System.currentTimeMillis();
            }

            private boolean isExpired() {
                long elapsedInterval = System.currentTimeMillis() - lastAccess;
                long maxInterval = session.getMaxInactiveInterval() * 1000;
                //shutdown the session a bit (15s) before session actually expires
                return elapsedInterval + 15000 > maxInterval;
            }

            private void shutdownIfExpired() {
                try {
                    if (isExpired()) {
                        sessionMonitors.remove(session);
                        sessionShutdown(session);
                    }
                } catch (IllegalStateException e) {
                    //session was already invalidated by the container
                } catch (Throwable t) {
                    //just inform that something went wrong
                    Log.warn("Failed to monitor session expiry", t);
                }
            }
        }
    }
}
