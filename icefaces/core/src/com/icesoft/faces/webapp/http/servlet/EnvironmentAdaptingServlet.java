package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.Server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EnvironmentAdaptingServlet implements ServerServlet {
    private ServerServlet servlet;

    public EnvironmentAdaptingServlet(Server server, Configuration configuration) {
        boolean useJettyContinuationsByDefault;
        try {
            this.getClass().getClassLoader().loadClass("org.mortbay.util.ajax.Continuation");
            useJettyContinuationsByDefault = true;
        } catch (ClassNotFoundException e) {
            useJettyContinuationsByDefault = false;
        }

        if (configuration.getAttributeAsBoolean("useJettyContinuations", useJettyContinuationsByDefault)) {
            servlet = new ContinuationAdaptingServlet(server);
        } else {
            servlet = new ThreadBlockingAdaptingServlet(server);
        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        servlet.service(request, response);
    }

    public void shutdown() {
        servlet.shutdown();
    }
}
