package com.icesoft.faces.webapp.http.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class PathDispatcher implements ServerServlet {
    private List matchers = new ArrayList();

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getRequestURI();        
    

        Iterator i = matchers.iterator();
        boolean matched = false;
        while (!matched && i.hasNext()) {
            matched = ((Matcher) i.next()).serviceOnMatch(path, request, response);
        }

        if (!matched) {
            
            response.sendError(404, "Resource at '" + path + "' not found.");
        }
    }



    public static String getPath(String path, String inc) {
        int start = path.indexOf(inc);
        if (start == -1) return path;
        int end = start + inc.length() - 1;
        if (start > 0) {
            String stringStart = path.substring(0, start);
            String stringEnd = path.substring(end);
            path = stringStart + stringEnd;
        } else {
            path = path.substring(end);
        }
        return path;
    }

    public void dispatchOn(String pathExpression, ServerServlet toServer) {
        matchers.add(new Matcher(pathExpression, toServer));
    }

    private class Matcher {
        private Pattern pattern;
        private ServerServlet server;

        public Matcher(String expression, ServerServlet server) {
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
