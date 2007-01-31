package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.PathDispatcherServer;

import java.io.PrintStream;

public class PushServer implements Server {
    private Server server;

    public PushServer(UpdateManager updateManager) {
        PathDispatcherServer dispatcher = new PathDispatcherServer();
        dispatcher.dispatchOn(".*send\\-receive\\-updates$", new ReceiveSendUpdates(updateManager));
        dispatcher.dispatchOn(".*receive\\-updates$", new SendUpdates(updateManager));
        dispatcher.dispatchOn(".*receive\\-updated\\-views$", new SendUpdatedViews(updateManager));
        this.server = dispatcher;
    }

    public void service(Request request) throws Exception {
        try {
            server.service(request);
        } catch (IllegalStateException e) {
            request.respondWith(new SessionExpired());
        } catch (Exception e) {
            request.respondWith(new ServerError(e));
        }
    }

    public void shutdown() {
        server.shutdown();
    }

    private static class ServerError implements ResponseHandler {
        private final Exception exception;

        public ServerError(Exception exception) {
            this.exception = exception;
        }

        public void respond(Response response) throws Exception {
            response.setHeader("Content-Type", "text/xml;charset=UTF-8");
            PrintStream printer = new PrintStream(response.writeBody(), true, "UTF-8");
            printer.print("<server-error><![CDATA[");
            exception.printStackTrace(printer);
            printer.println("]]></server-error>");
        }
    }

    private static class SessionExpired implements ResponseHandler {
        public void respond(Response response) throws Exception {
            response.setHeader("Content-Type", "text/xml;charset=UTF-8");
            response.writeBody().write("<session-expired/>".getBytes("UTF-8"));
        }
    }
}
