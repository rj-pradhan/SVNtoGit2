package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.servlet.RequestResponse;
import com.icesoft.faces.webapp.http.servlet.ContextConfiguration;
import com.icesoft.faces.webapp.http.Configuration;
import com.icesoft.faces.webapp.http.Server;
import com.icesoft.faces.webapp.http.SessionBoundServer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

public class ContextBoundServer extends HttpServlet implements HttpSessionListener, ServletContextListener {
    private final static Map SessionBoundServers = new HashMap();
    private Configuration configuration;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            Server server = (Server) SessionBoundServers.get(request.getSession());
            server.service(new RequestResponse(request, response));
            response.flushBuffer();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
    
    public void destroy() {
        super.destroy();
    }

    public void contextInitialized(ServletContextEvent event) {
        configuration = new ContextConfiguration("com.icesoft.faces", event.getServletContext());
    }

    public void contextDestroyed(ServletContextEvent event) {
    }

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        SessionBoundServers.put(session, new SessionBoundServer(session, configuration));
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        SessionBoundServer server = (SessionBoundServer) SessionBoundServers.remove(session);
        server.destroy();
    }
}
