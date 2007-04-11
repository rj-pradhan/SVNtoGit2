package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class SendUpdatedViews implements Server {
    private static final Runnable Noop = new Runnable() {
        public void run() {
        }
    };
    private static final ResponseHandler EmptyResponseHandler = new ResponseHandler() {
        public void respond(Response response) throws Exception {
            response.setHeader("Content-Length", 0);
        }
    };
    private BlockingQueue pendingRequest = new LinkedBlockingQueue(1);
    private ViewQueue allUpdatedViews;

    public SendUpdatedViews(final Collection synchronouslyUpdatedViews, final ViewQueue allUpdatedViews) {
        this.allUpdatedViews = allUpdatedViews;
        this.allUpdatedViews.onPut(new Runnable() {
            public void run() {
                try {
                    allUpdatedViews.removeAll(synchronouslyUpdatedViews);
                    synchronouslyUpdatedViews.clear();
                    Set viewIdentifiers = new HashSet(allUpdatedViews);
                    if (!viewIdentifiers.isEmpty()) {
                        Request request = (Request) pendingRequest.poll();
                        if (request != null) {
                            request.respondWith(new UpdatedViewsHandler((String[]) viewIdentifiers.toArray(new String[viewIdentifiers.size()])));
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void service(final Request request) throws Exception {
        respondToPreviousRequest();
        pendingRequest.put(request);
    }

    private void respondToPreviousRequest() {
        Request previousRequest = (Request) pendingRequest.poll();
        if (previousRequest != null) {
            try {
                previousRequest.respondWith(EmptyResponseHandler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        allUpdatedViews.onPut(Noop);
        respondToPreviousRequest();
    }

    private class UpdatedViewsHandler extends FixedXMLContentHandler {
        private String[] viewIdentifiers;

        public UpdatedViewsHandler(String[] viewIdentifiers) {
            this.viewIdentifiers = viewIdentifiers;
        }

        public void writeTo(Writer writer) throws IOException {
            writer.write("<updated-views>");
            for (int i = 0; i < viewIdentifiers.length; i++) {
                writer.write(viewIdentifiers[i]);
                writer.write(' ');
            }
            writer.write("</updated-views>");
        }
    }
}
