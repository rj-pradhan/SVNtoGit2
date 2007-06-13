package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.core.ViewQueue;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EnvironmentAdaptingServlet implements PseudoServlet {
    private static final Log LOG =
        LogFactory.getLog(EnvironmentAdaptingServlet.class);

    private PseudoServlet servlet;

    public EnvironmentAdaptingServlet(
        final Server server, final Configuration configuration,
        final String icefacesID, final Collection synchronouslyUpdatedViews,
        final ViewQueue allUpdatedViews, final ServletContext servletContext) {

        boolean useAsyncHttpServerByDefault;
        try {
            getClass().getClassLoader().
                loadClass(
                    "com.icesoft.faces.async.server." +
                        "AsyncHttpServerAdaptingServlet");
            useAsyncHttpServerByDefault = true;
        } catch (ClassNotFoundException exception) {
            useAsyncHttpServerByDefault = false;
        }
        boolean useJettyContinuationsByDefault;
        try {
            getClass().getClassLoader().
                loadClass("org.mortbay.util.ajax.Continuation");
            useJettyContinuationsByDefault = true;
        } catch (ClassNotFoundException e) {
            useJettyContinuationsByDefault = false;
        }
        if (configuration.getAttributeAsBoolean(
                "useAsyncHttpServer", useAsyncHttpServerByDefault)) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Adapting to Asynchronous HTTP Server environment.");
            }
            try {
                servlet =
                    (PseudoServlet)
                        this.getClass().getClassLoader().loadClass(
                            "com.icesoft.faces.async.server." +
                                "AsyncHttpServerAdaptingServlet").
                                    getDeclaredConstructor(
                                        new Class[] {
                                            Server.class,
                                            String.class,
                                            Collection.class,
                                            ViewQueue.class,
                                            ServletContext.class
                                        }).
                                    newInstance(
                                        new Object[] {
                                            server,
                                            icefacesID,
                                            synchronouslyUpdatedViews,
                                            allUpdatedViews,
                                            servletContext
                                        });
            } catch (Exception exception) {
                // Possible exceptions: ClassNotFoundException,
                //                      NoSuchMethodException,
                //                      IntantiationException,
                //                      InvocationTargetException,
                //                      IllegalAccessException
                if (LOG.isFatalEnabled()) {
                    LOG.fatal(
                        "Failed to instantiate AsyncHttpServerAdaptingServlet!",
                        exception);
                }
            }
        } else if (configuration.getAttributeAsBoolean(
                "useJettyContinuations", useJettyContinuationsByDefault)) {

            if (LOG.isDebugEnabled()) {
                LOG.debug("Adapting to Jetty's Continuation environment.");
            }
            servlet = new ContinuationAdaptingServlet(server);
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Adapting to thread blocking environment.");
            }
            servlet = new ThreadBlockingAdaptingServlet(server);
        }
    }

    public void service(
        final HttpServletRequest request, final HttpServletResponse response)
    throws Exception {
        servlet.service(request, response);
    }

    public void shutdown() {
        servlet.shutdown();
    }
}
