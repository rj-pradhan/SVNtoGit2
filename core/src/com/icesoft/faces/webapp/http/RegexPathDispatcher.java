package com.icesoft.faces.webapp.http;

import com.icesoft.faces.webapp.http.standard.NotFoundHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class RegexPathDispatcher implements Server {
    private List matchers = new ArrayList();

    public void service(Request request) throws Exception {
        String path = request.getURI().getPath();
        Iterator i = matchers.iterator();
        boolean matched = false;
        try {
            while (!matched && i.hasNext()) {
                matched = ((Matcher) i.next()).serviceOnMatch(path, request);
            }

            if (!matched) {
                request.respondWith(NotFoundHandler.HANDLER);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            request.respondWith(new ServerErrorHandler(t));
        }
    }

    public void dispatchOn(String pathExpression, Server toServer) {
        matchers.add(new Matcher(pathExpression, toServer));
    }

    private class Matcher {
        private Pattern pattern;
        private Server server;

        public Matcher(String expression, Server server) {
            this.pattern = Pattern.compile(expression);
            this.server = server;
        }

        boolean serviceOnMatch(String path, Request request) throws Exception {
            if (pattern.matcher(path).find()) {
                server.service(request);
                return true;
            } else {
                return false;
            }
        }
    }
}
