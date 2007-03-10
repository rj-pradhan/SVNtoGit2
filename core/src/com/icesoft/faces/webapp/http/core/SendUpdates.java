package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class SendUpdates implements Server {
    private Map commandQueues;
    private Collection allUpdatedViews;

    public SendUpdates(Map commandQueues, Collection allUpdatedViews) {
        this.commandQueues = commandQueues;
        this.allUpdatedViews = allUpdatedViews;
    }

    public void service(final Request request) throws Exception {
        request.respondWith(new FixedXMLContentHandler() {

            public void writeTo(Writer writer) throws IOException {
                HashSet viewIdentifiers = new HashSet(Arrays.asList(request.getParameterAsStrings("viewNumber")));
                Iterator i = viewIdentifiers.iterator();
                while (i.hasNext()) {
                    CommandQueue commandQueue = (CommandQueue) commandQueues.get(i.next());
                    commandQueue.take().serializeTo(writer);
                }
                allUpdatedViews.removeAll(viewIdentifiers);
            }
        });
    }

    public void shutdown() {
    }
}
