package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class SendUpdatesHandler extends FixedXMLContentHandler {
    private final Request request;
    private Map commandQueues;

    public SendUpdatesHandler(Map commandQueues, Request request) {
        this.commandQueues = commandQueues;
        this.request = request;
    }

    public void writeTo(Writer writer) throws IOException {
        Iterator viewIdentifiers = new HashSet(Arrays.asList(request.getParameterAsStrings("viewNumber"))).iterator();
        while (viewIdentifiers.hasNext()) {
            Object viewIdentifier = viewIdentifiers.next();
            if (commandQueues.containsKey(viewIdentifier)) {
                CommandQueue queue = (CommandQueue) commandQueues.get(viewIdentifier);
                queue.take().serializeTo(writer);
            }
        }
    }
}