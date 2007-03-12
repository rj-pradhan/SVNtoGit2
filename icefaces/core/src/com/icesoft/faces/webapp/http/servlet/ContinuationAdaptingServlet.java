package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;
import org.mortbay.util.ajax.Continuation;
import org.mortbay.util.ajax.ContinuationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class ContinuationAdaptingServlet implements PseudoServlet {
    private Map requests = new HashMap();
    private Server server;

    public ContinuationAdaptingServlet(Server server) {
        this.server = server;
    }

    public void service(final HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (requests.containsKey(request)) {
            ResponseHandler handler = (ResponseHandler) requests.remove(request);
            handler.respond(new ServletRequestResponse(request, response));
        } else {
            ContinuationRequestResponse requestResponse = new ContinuationRequestResponse(request, response);
            server.service(requestResponse);
            requestResponse.captureContinuation();
        }
    }

    public void shutdown() {
        server.shutdown();
    }

    private class ContinuationRequestResponse extends ServletRequestResponse {
        private boolean captureContinuation = true;
        private Continuation continuation;

        public ContinuationRequestResponse(HttpServletRequest request, HttpServletResponse response) {
            super(request, response);
        }

        public synchronized void respondWith(ResponseHandler handler) throws Exception {
            if (continuation == null) {
                captureContinuation = false;
                super.respondWith(handler);
            } else {
                continuation.resume();
                requests.put(request, handler);
            }
        }

        public synchronized void captureContinuation() {
            if (captureContinuation) {
                continuation = ContinuationSupport.getContinuation(request, this);
                continuation.suspend(request.getSession().getMaxInactiveInterval() * 1000);
            }
        }
    }
}
