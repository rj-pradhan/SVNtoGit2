package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.ViewListener;
import com.icesoft.faces.env.ServletEnvironmentRequest;
import com.icesoft.faces.webapp.command.Command;
import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.command.NOOP;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.core.ViewQueue;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.util.SeamUtilities;
import edu.emory.mathcs.backport.java.util.concurrent.locks.Lock;
import edu.emory.mathcs.backport.java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

public class ServletView implements CommandQueue {
    private static final Log Log = LogFactory.getLog(ServletView.class);
    private static final NOOP NOOP = new NOOP();
    private Lock lock = new ReentrantLock();
    private ServletExternalContext externalContext;
    private BridgeFacesContext facesContext;
    private ViewQueue allServedViews;
    private PersistentFacesState persistentFacesState;
    private Map bundles = Collections.EMPTY_MAP;
    private ServletEnvironmentRequest wrappedRequest;
    private Command currentCommand = NOOP;
    private String viewIdentifier;
    private Collection viewListeners = new ArrayList();


    public ServletView(final String viewIdentifier, String sessionID, HttpServletRequest request, HttpServletResponse response, ViewQueue allServedViews, Configuration configuration) {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        this.wrappedRequest = new ServletEnvironmentRequest(request);
        this.viewIdentifier = viewIdentifier;
        this.allServedViews = allServedViews;
        this.externalContext = new ServletExternalContext(viewIdentifier, servletContext, wrappedRequest, response, this, configuration);
        this.facesContext = new BridgeFacesContext(externalContext, viewIdentifier, sessionID, this);
        this.persistentFacesState = new PersistentFacesState(facesContext, viewListeners, configuration);
        this.notifyViewCreation();
    }

    public void setAsCurrentDuring(HttpServletRequest request, HttpServletResponse response) {
        externalContext.update(request, response);
        externalContext.injectBundles(bundles);
        persistentFacesState.setCurrentInstance();
        facesContext.setCurrentInstance();
        facesContext.applyBrowserDOMChanges();
    }

    public void switchToNormalMode() {
        facesContext.switchToNormalMode();
        externalContext.switchToNormalMode();
    }

    public void switchToPushMode() {
        //collect bundles put by Tag components when the page is parsed
        this.bundles = externalContext.collectBundles();
        facesContext.switchToPushMode();
        externalContext.switchToPushMode();
        externalContext.resetRequestMap();
    }

    /**
     * Check to see if the URI is different in any material (or Seam) way.
     *
     * @param request ServletRequest
     * @return true if the URI is considered different
     */
    public boolean differentURI(HttpServletRequest request) {
        // As a temporary fix, all GET requests are non-faces requests, and thus,
        // are considered different to force a new ViewRoot to be constructed.
        return (SeamUtilities.isSeamEnvironment()) ||
                !request.getRequestURI().equals(wrappedRequest.getRequestURI());
    }

    public void put(Command command) {
        lock.lock();
        currentCommand = currentCommand.coalesceWith(command);
        lock.unlock();
        try {
            allServedViews.put(viewIdentifier);
        } catch (InterruptedException e) {
            Log.warn("Failed to queue updated view", e);
        }
    }

    public Command take() {
        lock.lock();
        Command command = currentCommand;
        currentCommand = NOOP;
        lock.unlock();

        return command;
    }

    public void release() {
        facesContext.release();
        persistentFacesState.release();
        externalContext.resetRequestMap();        
    }

    public BridgeFacesContext getFacesContext() {
        return facesContext;
    }

    public void dispose() {
        this.notifyViewDisposal();
        this.release();
    }

    private void notifyViewCreation() {
        Iterator i = viewListeners.iterator();
        while (i.hasNext()) {
            try {
                ViewListener listener = (ViewListener) i.next();
                listener.viewCreated();
            } catch (Throwable t) {
                Log.warn("Failed to invoke view listener", t);
            }
        }
    }

    private void notifyViewDisposal() {
        Iterator i = viewListeners.iterator();
        while (i.hasNext()) {
            try {
                ViewListener listener = (ViewListener) i.next();
                listener.viewDisposed();
            } catch (Throwable t) {
                Log.warn("Failed to invoke view listener", t);
            }
        }
    }
}