package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.application.StartupTime;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.ResourceServer;
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
    private static final String AWT_HEADLESS = "java.awt.headless";

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        StartupTime.started();
        try {
            ServletContext servletContext = servletConfig.getServletContext();
            String awtHeadless = System.getProperty(AWT_HEADLESS);
            if (null == awtHeadless) {
                System.setProperty(AWT_HEADLESS, "true");
            }
            final Configuration configuration = new ServletContextConfiguration("com.icesoft.faces", servletContext);
            final IdGenerator idGenerator = new IdGenerator(servletContext.getResource("/").getPath());

            PseudoServlet sessionServer = new SessionDispatcher() {
                protected PseudoServlet newServlet(HttpSession session) {
                    return new MainSessionBoundServlet(session, idGenerator, configuration);
                }
            };
            ThreadBlockingAdaptingServlet resourceServer = new ThreadBlockingAdaptingServlet(new ResourceServer(configuration));

            dispatcher.dispatchOn(".*xmlhttp\\/.*", resourceServer);
            dispatcher.dispatchOn(".*", sessionServer);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            dispatcher.service(request, response);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void destroy() {
        dispatcher.shutdown();
    }
}
