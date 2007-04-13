package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.command.SessionExpired;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.ReceivePing;
import com.icesoft.faces.webapp.http.core.ReceiveSendUpdates;
import com.icesoft.faces.webapp.http.core.SendUpdatedViews;
import com.icesoft.faces.webapp.http.core.SendUpdates;
import com.icesoft.faces.webapp.http.core.ViewQueue;
import com.icesoft.util.IdGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

public class MainSessionBoundServlet implements PseudoServlet {
    private static final Log Log = LogFactory.getLog(MainSessionBoundServlet.class);
    private static final SessionExpired SessionExpired = new SessionExpired();
    private PathDispatcher dispatcher = new PathDispatcher();
    private Map views = new HashMap();
    private ViewQueue allUpdatedViews = new ViewQueue();
    private Collection synchronouslyUpdatedViews = new HashSet();

    public MainSessionBoundServlet(HttpSession session, SessionDispatcher.Listener.Monitor sessionMonitor, IdGenerator idGenerator, Configuration configuration) {
        final PseudoServlet viewServlet;
        if (configuration.getAttributeAsBoolean("concurrentDOMViews", false)) {
            viewServlet = new MultiViewServlet(session, sessionMonitor, idGenerator, views, allUpdatedViews, configuration);
        } else {
            viewServlet = new SingleViewServlet(session, sessionMonitor, idGenerator, views, allUpdatedViews, configuration);
        }

        PseudoServlet upload = new UploadServlet(views, configuration, session.getServletContext());
        PseudoServlet receiveSendUpdates = new ViewBoundAdaptingServlet(new ReceiveSendUpdates(views, synchronouslyUpdatedViews), sessionMonitor, views);
        PseudoServlet sendUpdates = new BasicAdaptingServlet(new SendUpdates(views));

        if (configuration.getAttributeAsBoolean("synchronousUpdate", false)) {
            //just drain the updated views if in 'synchronous mode'
            allUpdatedViews.onPut(new DrainUpdatedViews());
        } else {
            //setup blocking connection server
            PseudoServlet sendUpdatedViews = new EnvironmentAdaptingServlet(new SendUpdatedViews(synchronouslyUpdatedViews, allUpdatedViews), configuration);
            PseudoServlet receivePing = new BasicAdaptingServlet(new ReceivePing(views));

            dispatcher.dispatchOn(".*block\\/receive\\-updated\\-views$", sendUpdatedViews);
            dispatcher.dispatchOn(".*block\\/ping$", receivePing);
        }

        dispatcher.dispatchOn(".*block\\/send\\-receive\\-updates$", receiveSendUpdates);
        dispatcher.dispatchOn(".*block\\/receive\\-updates$", sendUpdates);
        dispatcher.dispatchOn(".*uploadHtml", upload);
        dispatcher.dispatchOn(".*", viewServlet);
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        dispatcher.service(request, response);
    }

    public void shutdown() {
        Iterator i = views.values().iterator();
        while (i.hasNext()) {
            CommandQueue commandQueue = (CommandQueue) i.next();
            commandQueue.put(SessionExpired);
        }
        try {
            //wait for the for the bridge to receive the 'session-expire' command
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            //do nothing
        } finally {
            dispatcher.shutdown();
        }
    }

    private class DrainUpdatedViews implements Runnable {
        public void run() {
           allUpdatedViews.removeAll(synchronouslyUpdatedViews);
           if (!allUpdatedViews.isEmpty()) {
               Log.warn(allUpdatedViews + " views have accumulated updates");
           }
           allUpdatedViews.clear();
        }
    }
}
