package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.command.SessionExpired;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.DisposeViews;
import com.icesoft.faces.webapp.http.core.IDVerifier;
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
    private static final PseudoServlet NOOPServlet = new PseudoServlet() {
        public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        }
        public void shutdown() {
        }
    };
    private Runnable drainUpdatedViews = new Runnable() {
        public void run() {
            allUpdatedViews.removeAll(synchronouslyUpdatedViews);
            if (!allUpdatedViews.isEmpty()) {
                Log.warn(allUpdatedViews + " views have accumulated updates");
            }
            allUpdatedViews.clear();
        }
    };
    private PathDispatcher dispatcher = new PathDispatcher();
    private Map views = new HashMap();
    private ViewQueue allUpdatedViews = new ViewQueue();
    private Collection synchronouslyUpdatedViews = new HashSet();

    public MainSessionBoundServlet(HttpSession session, SessionDispatcher.Listener.Monitor sessionMonitor, IdGenerator idGenerator, Configuration configuration) {
        String sessionID = idGenerator.newIdentifier();
        ContextEventRepeater.iceFacesIdRetrieved(session, sessionID);

        final PseudoServlet viewServlet;
        final PseudoServlet disposeViews;
        if (configuration.getAttributeAsBoolean("concurrentDOMViews", false)) {
            viewServlet = new MultiViewServlet(session, sessionID, sessionMonitor, views, allUpdatedViews, configuration);
            disposeViews = new BasicAdaptingServlet(new IDVerifier(sessionID, new DisposeViews(views)));
        } else {
            viewServlet = new SingleViewServlet(session, sessionID, sessionMonitor, views, allUpdatedViews, configuration);
            disposeViews = NOOPServlet;
        }

        final PseudoServlet sendUpdatedViews;
        final PseudoServlet sendUpdates;
        final PseudoServlet receivePing;
        if (configuration.getAttributeAsBoolean("synchronousUpdate", false)) {
            //drain the updated views queue if in 'synchronous mode'
            allUpdatedViews.onPut(drainUpdatedViews);
            sendUpdatedViews = NOOPServlet;
            sendUpdates = NOOPServlet;
            receivePing = NOOPServlet;
        } else {
            //setup blocking connection server
            sendUpdatedViews = new EnvironmentAdaptingServlet(new IDVerifier(sessionID, new SendUpdatedViews(synchronouslyUpdatedViews, allUpdatedViews)), configuration, sessionID, synchronouslyUpdatedViews, allUpdatedViews, session.getServletContext());
            sendUpdates = new BasicAdaptingServlet(new IDVerifier(sessionID, new SendUpdates(views)));
            receivePing = new BasicAdaptingServlet(new IDVerifier(sessionID, new ReceivePing(views)));
        }

        PseudoServlet upload = new UploadServlet(views, configuration, session.getServletContext());
        PseudoServlet receiveSendUpdates = new ViewBoundAdaptingServlet(new IDVerifier(sessionID, new ReceiveSendUpdates(views, synchronouslyUpdatedViews)), sessionMonitor, views);

        dispatcher.dispatchOn(".*block\\/send\\-receive\\-updates$", receiveSendUpdates);
        dispatcher.dispatchOn(".*block\\/receive\\-updated\\-views$", sendUpdatedViews);
        dispatcher.dispatchOn(".*block\\/receive\\-updates$", sendUpdates);
        dispatcher.dispatchOn(".*block\\/ping$", receivePing);
        dispatcher.dispatchOn(".*block\\/dispose\\-views$", disposeViews);        
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

        Iterator viewIterator = views.values().iterator();
        while (viewIterator.hasNext()) {
            ServletView view = (ServletView) viewIterator.next();
            view.dispose();
        }
    }

    //Exposing queues for Tomcat 6 Ajax Push
    public ViewQueue getAllUpdatedViews()  {
        return allUpdatedViews;
    }

    public Collection getSynchronouslyUpdatedViews()  {
        return synchronouslyUpdatedViews;
    }
}
