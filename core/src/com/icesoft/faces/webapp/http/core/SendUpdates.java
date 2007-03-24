package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;

import java.util.Collection;
import java.util.Map;

public class SendUpdates implements Server {
    private Map commandQueues;
    private Collection allUpdatedViews;

    public SendUpdates(Map commandQueues, Collection allUpdatedViews) {
        this.commandQueues = commandQueues;
        this.allUpdatedViews = allUpdatedViews;
    }

    public void service(final Request request) throws Exception {
        request.respondWith(new SendUpdatesHandler(commandQueues, request));
    }

    public void shutdown() {
    }
}
