package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.ResourceServer;
import com.icesoft.faces.webapp.xmlhttp.ResponseStateManager;
import com.icesoft.faces.application.StartupTime;
import com.icesoft.util.IdGenerator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class MainServlet extends HttpServlet {
    private PathDispatcher dispatcher = new PathDispatcher();



    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        StartupTime.started();
        try {
            ServletContext servletContext = servletConfig.getServletContext();
            final Configuration configuration = new ServletContextConfiguration("com.icesoft.faces", servletContext);
            final IdGenerator idGenerator = new IdGenerator(servletContext.getResource("/").getPath());
            final ResponseStateManager responseStateManager = ResponseStateManager.getResponseStateManager(servletContext);

            ServerServlet sessionServer = new SessionDispatcher() {
                protected ServerServlet newServlet(HttpSession session) {
                    return new MainSessionBoundServlet(session, idGenerator, responseStateManager, configuration);
                }
            };
            AdapterServlet resourceServer = new AdapterServlet(new ResourceServer(configuration));

            dispatcher.dispatchOn(".*xmlhttp\\/.*", resourceServer);
            dispatcher.dispatchOn(".*", sessionServer);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            dispatcher.service(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void destroy() {
        dispatcher.shutdown();
    }
}
