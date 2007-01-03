/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.faces.webapp.xmlhttp;

import com.icesoft.faces.context.BridgeFacesContext;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FactoryFinder;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.Map;

/**
 * The {@link PersistentFacesState} class allows an application to initiate
 * rendering asynchronously and independently of user interaction.
 * <p/>
 * Typical use is to obtain a {@link PersistentFacesState} instance in a managed
 * bean constructor and then use that instance for any relevant rendering
 * requests.
 * <p/>
 * Applications should obtain the <code>PersistentFacesState</code> instance
 * using the public static getInstance() method.  The recommended approach is to
 * call this method from a mangaged-bean constructor and use the instance
 * obtained for any {@link #render} requests.
 */
public class PersistentFacesState implements Serializable {
    private static final Log log =
            LogFactory.getLog(PersistentFacesState.class);

    static String
            PERSISTENT_FACES_STATE = "com.icesoft.faces.PersistentFacesState";

    public FacesContext facesContext;
    transient Lifecycle lifecycle;
    private boolean isBridgeFacesContext = false;

    static ExecutorService executorService;

    PersistentFacesState() {
    }

    PersistentFacesState(FacesContext facesContext) {
        //remove this once we round out the API to include execute
        //and render
        setFacesContext(facesContext);
    }

    void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
        if (facesContext instanceof BridgeFacesContext) {
            isBridgeFacesContext = true;
        }
        LifecycleFactory factory =
                (LifecycleFactory) FactoryFinder.
                        getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        lifecycle = factory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
    }

    private static InheritableThreadLocal localInstance =
            new InheritableThreadLocal();

    public static void setLocalInstance(Map sessionMap, String viewNumber) {
        localInstance.set(new Key(sessionMap, viewNumber));
    }

    /**
     * Obtain the <code>PersistentFacesState</code> instance appropriate for the
     * current context.  This is managed through InheritableThreadLocal
     * variables.  The recommended approach is to call this method from a
     * mangaged-bean constructor and use the instance obtained for any {@link
     * #render} requests.
     *
     * @return the PersistentFacesState appropriate for the calling Thread
     */
    public static PersistentFacesState getInstance() {
        Key key = (Key) localInstance.get();
        if (null == key) {
            return null;
        }
        return (getInstance(key.sessionMap, key.viewNumber));
    }


    /**
     * Obtain the {@link PersistentFacesState} instance keyed by viewNumber from
     * the specified sessionMap. This API is not intended for application use.
     *
     * @param sessionMap session-scope parameters
     * @param viewNumber unique number identifying particular browser frame
     * @return the PersistentFacesState
     */
    public static synchronized PersistentFacesState getInstance(
            Map sessionMap, String viewNumber) {
        PersistentFacesState persistentFacesState = (PersistentFacesState)
                sessionMap.get(viewNumber + "/" + PERSISTENT_FACES_STATE);
        if (null == persistentFacesState) {
            persistentFacesState = new PersistentFacesState();
            sessionMap.put(viewNumber + "/" + PERSISTENT_FACES_STATE,
                           persistentFacesState);
        }
        return persistentFacesState;
    }

    static void setInstance(Map sessionMap, String viewNumber,
                            PersistentFacesState persistentFacesState) {
        sessionMap.put(viewNumber + "/" + PERSISTENT_FACES_STATE,
                       persistentFacesState);
    }

    static void resetInstance(Map sessionMap, String viewNumber,
                              FacesContext facesContext) {
        PersistentFacesState state = getInstance(sessionMap, viewNumber);
        state.setFacesContext(facesContext);
    }

    /**
     * Return the FacesContext associated with this instance.
     *
     * @return the FacesContext for this instance
     */
    public FacesContext getFacesContext() {
        return facesContext;
    }

    /**
     * Render the view associated with this <code>PersistentFacesState</code>.
     * The user's browser will be immediately updated with any changes.
     */
    public void render() throws RenderingException {
        if (isBridgeFacesContext) {
            ((BridgeFacesContext) facesContext).setCurrentInstance();
            // Clear focus on forced Renders
            ((BridgeFacesContext) facesContext).getExternalContext()
                    .getRequestParameterMap().remove("focus");
            ((BridgeFacesContext) facesContext).setFocusId("");

            String viewNumber = ((BridgeFacesContext) facesContext)
                    .getViewNumber();
            synchronized (facesContext) {
                try {
                    lifecycle.render(facesContext);
                } catch (IllegalStateException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("fatal render failure for viewNumber "
                                  + viewNumber, e);
                    }
                    throw new FatalRenderingException(
                            "fatal render failure for viewNumber "
                            + viewNumber, e);
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("transient render failure for viewNumber "
                                  + viewNumber, e);
                    }
                    throw new TransientRenderingException(
                            "transient render failure for viewNumber "
                            + viewNumber, e);
                }
            }
        }
    }


    /**
     * Render the view associated with this <code>PersistentFacesState</code>.
     * This takes place on a separate thread to guard against potential deadlock
     * from calling {@link #render} during view rendering.
     */
    public void renderLater() {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor();
        }
        executorService.execute(new RenderRunner());
    }

    /**
     * Redirect browser to a different URI. The user's browser will be
     * immediately redirected without any user interaction required.
     *
     * @param uri the relative or absolute URI.
     */
    public void redirectTo(String uri) {
        try {
            if (isBridgeFacesContext)
                ((BridgeFacesContext) facesContext).setCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.redirect(uri);
            ResponseStateManager manager =
                    ResponseStateManager.getResponseStateManager(
                            (ServletContext) externalContext.getContext());
            ResponseState state = manager.getState(
                    (HttpSession) externalContext.getSession(true),
                    ((BridgeFacesContext) facesContext).getViewNumber());
            state.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Redirect browser to a different page. The redirect page is selected based
     * on the navigation rule. The user's browser will be immediately redirected
     * without any user interaction required.
     *
     * @param outcome the 'from-outcome' field in the navigation rule.
     */
    public void navigateTo(String outcome) {
        try {
            if (isBridgeFacesContext)
                ((BridgeFacesContext) facesContext).setCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            facesContext.getApplication().getNavigationHandler()
                    .handleNavigation(facesContext,
                                      facesContext.getViewRoot().getViewId(),
                                      outcome);
            ResponseStateManager manager =
                    ResponseStateManager.getResponseStateManager(
                            (ServletContext) externalContext.getContext());
            ResponseState state = manager.getState(
                    (HttpSession) externalContext.getSession(true),
                    ((BridgeFacesContext) facesContext).getViewNumber());
            state.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Return a String representation of this <code> PersistentFacesState</code>
     * instance.</p>
     */
    public String toString() {
        String facesContextString = "(null facesContext)";
        if (null != facesContext) {
            facesContextString = facesContext.toString();
        }
        return ("com.icesoft.faces.webapp.xmlhttp.PersistentFacesState@" +
                hashCode() + "[" + facesContextString + "]");
    }

    /**
     * Threads that used to server request/response cycles in an application
     * server are generally pulled from a pool.  Just before the thread is done
     * completing the cycle, we should clear any local instance variables to
     * ensure that they are not hanging on to any session references, otherwise
     * the session and their resources are not released.
     */
    static void clearLocalInstance() {
        if (localInstance != null) {
            localInstance.set(null);
        }
    }

    /**
     * execute and Render the view associated with this <code>PersistentFacesState</code>.
     * The user's browser will be immediately updated with any changes.
     */
    void execute() throws RenderingException {
        if (isBridgeFacesContext) {
            ((BridgeFacesContext) facesContext).setCurrentInstance();
            String viewNumber = ((BridgeFacesContext) facesContext)
                    .getViewNumber();
            BridgeFacesContext bContext = ((BridgeFacesContext) facesContext);
            synchronized (bContext) {
                try {
                    lifecycle.execute(facesContext);
                    lifecycle.render(facesContext);
                    bContext.release();
                } catch (IllegalStateException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("fatal render failure for viewNumber "
                                  + viewNumber, e);
                    }
                    throw new FatalRenderingException(
                            "fatal render failure for viewNumber "
                            + viewNumber, e);
                } catch (Exception e) {
                    if (log.isDebugEnabled()) {
                        log.debug("transient render failure for viewNumber "
                                  + viewNumber, e);
                    }
                    throw new TransientRenderingException(
                            "transient render failure for viewNumber "
                            + viewNumber, e);
                }
            }
        }
    }

    private static class Key {
        Key(Map sessionMap, String viewNumber) {
            this.sessionMap = sessionMap;
            this.viewNumber = viewNumber;
        }

        String viewNumber;
        Map sessionMap;
    }

    private class RenderRunner implements Runnable {
        private final Log log = LogFactory.getLog(RenderRunner.class);

        /**
         * <p>Not for application use. Entry point for {@link
         * PersistentFacesState#renderLater}.</p>
         */
        public void run() {
            try {
                PersistentFacesState.this.render();
            } catch (RenderingException e) {
                if (log.isDebugEnabled()) {
                    log.debug("renderLater failed ", e);
                }
            }
        }
    }
}



