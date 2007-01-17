package com.icesoft.faces.webapp.http.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class PathDispatchingServlet implements ServletServer {
    private List matchers = new ArrayList();

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getRequestURI();
        Iterator i = matchers.iterator();
        boolean matched = false;
        try {
            while (!matched && i.hasNext()) {
                matched = ((Matcher) i.next()).serviceOnMatch(path, request, response);
            }

            if (!matched) {
                response.sendError(404, "Resource at '" + path + "' not found.");
            }
        } catch (Throwable t) {
            t.printStackTrace();
            response.sendError(500, t.toString());
        }
    }

    public void dispatchOn(String pathExpression, ServletServer toServer) {
        matchers.add(new Matcher(pathExpression, toServer));
    }

    private class Matcher {
        private Pattern pattern;
        private ServletServer server;

        public Matcher(String expression, ServletServer server) {
            this.pattern = Pattern.compile(expression);
            this.server = server;
        }

        boolean serviceOnMatch(String path, HttpServletRequest request, HttpServletResponse response) throws Exception {
            if (pattern.matcher(path).find()) {
                server.service(request, response);
                return true;
            } else {
                return false;
            }
        }

        void shutdown() {
            server.shutdown();
        }
    }

    public void shutdown() {
        Iterator i = matchers.iterator();
        while (i.hasNext()) {
            Matcher matcher = (Matcher) i.next();
            matcher.shutdown();
        }
    }
}
