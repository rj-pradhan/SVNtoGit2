package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class PathDispatcherServer implements Server {
    private List matchers = new ArrayList();

    public void service(Request request) throws Exception {
        String path = request.getURI().getPath();
        Iterator i = matchers.iterator();
        boolean matched = false;
        while (!matched && i.hasNext()) {
            matched = ((Matcher) i.next()).serviceOnMatch(path, request);
        }

        if (!matched) {
            request.respondWith(NotFoundHandler.HANDLER);
        }
    }

    public void dispatchOn(String pathExpression, Server toServer) {
        matchers.add(new Matcher(pathExpression, toServer));
    }

    public void shutdown() {
        Iterator i = matchers.iterator();
        while (i.hasNext()) {
            Matcher matcher = (Matcher) i.next();
            matcher.shutdown();
        }
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

        public void shutdown() {
            server.shutdown();
        }
    }
}
