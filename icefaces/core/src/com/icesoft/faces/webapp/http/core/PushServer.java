package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;
import com.icesoft.faces.webapp.http.common.standard.PathDispatcherServer;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class PushServer implements Server {
    private Server server;

    public PushServer(Map commandQueues, ViewQueue allUpdatedViews) {
        PathDispatcherServer dispatcher = new PathDispatcherServer();
        Collection synchronouslyUpdatedViews = new HashSet();
        dispatcher.dispatchOn(".*send\\-receive\\-updates$", new ReceiveSendUpdates(commandQueues, synchronouslyUpdatedViews));
        dispatcher.dispatchOn(".*receive\\-updates$", new SendUpdates(commandQueues, allUpdatedViews));
        dispatcher.dispatchOn(".*receive\\-updated\\-views$", new SendUpdatedViews(synchronouslyUpdatedViews, allUpdatedViews));
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

    private static class ServerError extends FixedXMLContentHandler {
        private final Exception exception;

        public ServerError(Exception exception) {
            this.exception = exception;
        }

        public void writeTo(Writer writer) throws IOException {
            writer.write("<server-error><![CDATA[");
            exception.printStackTrace(new PrintWriter(writer, true));
            writer.write("]]></server-error>");
        }
    }

    private static class SessionExpired extends FixedXMLContentHandler {

        public void writeTo(Writer writer) throws IOException {
            writer.write("<session-expired/>");
        }
    }
}
