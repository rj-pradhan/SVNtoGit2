package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.command.Pong;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ReceivePing implements Server, ResponseHandler {

    private static Log log = LogFactory.getLog(ReceivePing.class);

    private static final Pong PONG = new Pong();
    private Map commandQueues;

    public ReceivePing(Map commandQueues) {
        this.commandQueues = commandQueues;
    }

    public void service(Request request) throws Exception {
        String[] viewIdentifiers = request.getParameterAsStrings("viewNumber");
        for (int i = 0; i < viewIdentifiers.length; i++) {
            CommandQueue queue = (CommandQueue) commandQueues.get(viewIdentifiers[i]);
            if( queue != null ){
                queue.put(PONG);
            } else {                              
                if( log.isWarnEnabled() ){
                    log.warn( "could not get a valid queue for " + viewIdentifiers[i] );
                }
            }
        }
        request.respondWith(this);        
    }

    public void respond(Response response) throws Exception {
        response.setHeader("Content-Length", 0);
        response.writeBody().write("".getBytes());
    }

    public void shutdown() {
    }
}
