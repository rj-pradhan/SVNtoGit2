package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.servlet.EnvironmentWrappingServlet;
import com.icesoft.faces.webapp.http.core.ResourceServer;
import com.icesoft.util.IdGenerator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import java.io.IOException;

public class MainServlet extends HttpServlet {
    private PathDispatchingServlet dispatcher = new PathDispatchingServlet();

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        try {
            ServletContext servletContext = servletConfig.getServletContext();
            final Configuration configuration = new ServletContextConfiguration("com.icesoft.faces", servletContext);
            final IdGenerator idGenerator = new IdGenerator(servletContext.getResource("/").getPath());

            dispatcher.dispatchOn(".*xmlhttp\\/.*", new ServerAdapterServlet(new ResourceServer(configuration)));
            dispatcher.dispatchOn(".*block\\/.*|.*\\.iface$", new SessionDispatcher() {
                protected ServletServer newServlet(HttpSession session) {
                    return new EnvironmentWrappingServlet(session, configuration, idGenerator);
                }
            });
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
