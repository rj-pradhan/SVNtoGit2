package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.ResourceServer;
import com.icesoft.faces.webapp.xmlhttp.ResponseStateManager;
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
    private PathDispatchingServlet dispatcher = new PathDispatchingServlet();

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);

        try {
            ServletContext servletContext = servletConfig.getServletContext();
            final Configuration configuration = new ServletContextConfiguration("com.icesoft.faces", servletContext);
            final IdGenerator idGenerator = new IdGenerator(servletContext.getResource("/").getPath());
            final ResponseStateManager responseStateManager = ResponseStateManager.getResponseStateManager(servletContext);
            final ServletServer sessionDispatcher;
            if (configuration.getAttributeAsBoolean("concurrentDOMViews", false)) {
                sessionDispatcher = new SessionDispatcher() {
                    protected ServletServer newServlet(HttpSession session) {
                        return new MultiViewServlet(session, idGenerator, responseStateManager);
                    }
                };
            } else {
                sessionDispatcher = new SessionDispatcher() {
                    protected ServletServer newServlet(HttpSession session) {
                        return new SingleViewServlet(session, idGenerator, responseStateManager);
                    }
                };
            }
            ServerAdapterServlet resourceServer = new ServerAdapterServlet(new ResourceServer(configuration));

            dispatcher.dispatchOn(".*xmlhttp\\/.*", resourceServer);
            dispatcher.dispatchOn(".*block\\/.*|.*\\.iface$", sessionDispatcher);
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
