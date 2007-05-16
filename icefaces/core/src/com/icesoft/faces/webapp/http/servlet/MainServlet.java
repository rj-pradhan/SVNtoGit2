package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.ResourceServer;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesCommonlet;
import com.icesoft.util.IdGenerator;
import com.icesoft.jasper.Constants;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MainServlet extends HttpServlet {
    
    private static Log log = LogFactory.getLog(MainServlet.class);

    private PathDispatcher dispatcher = new PathDispatcher();
    private static final String AWT_HEADLESS = "java.awt.headless";

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        try {
            ServletContext servletContext = servletConfig.getServletContext();
            String awtHeadless = System.getProperty(AWT_HEADLESS);
            if (null == awtHeadless) {
                System.setProperty(AWT_HEADLESS, "true");
            }
            final Configuration configuration = new ServletContextConfiguration("com.icesoft.faces", servletContext);
            final IdGenerator idGenerator = new IdGenerator(servletContext.getResource("/").getPath());

            PseudoServlet sessionServer = new SessionDispatcher() {
                protected PseudoServlet newServlet(HttpSession session, Listener.Monitor sessionMonitor) {
                    return new MainSessionBoundServlet(session, sessionMonitor, idGenerator, configuration);
                }
            };
            PseudoServlet resourceServer = new BasicAdaptingServlet(new ResourceServer(configuration));

            dispatcher.dispatchOn(".*xmlhttp\\/.*", resourceServer);
            dispatcher.dispatchOn(".*", sessionServer);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if( log.isTraceEnabled() ){
            log.trace(getIncludeInfo(request,"entering main servlet"));
        }

        //set flag to indicate an ICEfaces request so that delegateNonIface
        //will detect this and execute D2DViewHandler for it
        request.setAttribute(PersistentFacesCommonlet.SERVLET_KEY,
                             PersistentFacesCommonlet.PERSISTENT);

        try {
            dispatcher.service(request, response);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private String getIncludeInfo(HttpServletRequest req, String header) {
        StringBuffer buff = new StringBuffer(header);
        buff.append("\nvia Attributes - via Methods");

        buff.append("\n");
        buff.append(Constants.INC_CONTEXT_PATH);
        buff.append(": ");
        buff.append(req.getAttribute(Constants.INC_CONTEXT_PATH));
        buff.append(" - ");
        buff.append(req.getContextPath());

        buff.append("\n");
        buff.append(Constants.INC_PATH_INFO);
        buff.append(": ");
        buff.append(req.getAttribute(Constants.INC_PATH_INFO));
        buff.append(" - ");
        buff.append(req.getPathInfo());

        buff.append("\n");
        buff.append(Constants.INC_QUERY_STRING);
        buff.append(": ");
        buff.append(req.getAttribute(Constants.INC_QUERY_STRING));
        buff.append(" - ");
        buff.append(req.getQueryString());

        buff.append("\n");
        buff.append(Constants.INC_REQUEST_URI);
        buff.append(": ");
        buff.append(req.getAttribute(Constants.INC_REQUEST_URI));
        buff.append(" - ");
        buff.append(req.getRequestURI());

        buff.append("\n");
        buff.append(Constants.INC_SERVLET_PATH);
        buff.append(": ");
        buff.append(req.getAttribute(Constants.INC_SERVLET_PATH));
        buff.append(" - ");
        buff.append(req.getServletPath());
        
        buff.append("\n\n");
        buff.append("via getAttributeNames():\n");
        Enumeration attrNames = req.getAttributeNames();
        while (attrNames.hasMoreElements()) {
            String key = (String)attrNames.nextElement();
            Object val = req.getAttribute(key);
            buff.append("\t");
            buff.append(key);
            buff.append(": ");
            buff.append(val.toString());
            buff.append("\n");
        }

        return buff.toString();
    }

    public void destroy() {
        dispatcher.shutdown();
    }
}
