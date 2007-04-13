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
import com.icesoft.faces.webapp.http.common.Configuration;
import edu.emory.mathcs.backport.java.util.concurrent.ExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.Executors;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FactoryFinder;
import javax.faces.context.ExternalContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
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
    private static final Log log = LogFactory.getLog(PersistentFacesState.class);
    private static ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static InheritableThreadLocal localInstance = new InheritableThreadLocal();
    private BridgeFacesContext facesContext;
    private Lifecycle lifecycle;

    private ClassLoader renderableClassLoader = null;
    private boolean synchronousMode;

    public PersistentFacesState(BridgeFacesContext facesContext, Configuration configuration) {
        //JIRA case ICE-1365
        //Save a reference to the web app classloader so that server-side
        //render requests work regardless of how they are originated.
        renderableClassLoader = Thread.currentThread().getContextClassLoader();

        this.facesContext = facesContext;
        this.synchronousMode = configuration.getAttributeAsBoolean("synchronousUpdate", false);

        //put this state in the session -- mainly for the fileupload
        //todo: try to pass this state using object references
        Map sessionMap = facesContext.getExternalContext().getSessionMap();
        sessionMap.put(facesContext.getViewNumber() + "/" + PersistentFacesState.class, this);

        LifecycleFactory factory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
        this.lifecycle = factory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
        this.setCurrentInstance();
    }

    public void setCurrentInstance() {
        localInstance.set(this);
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
        return (PersistentFacesState) localInstance.get();
    }

    //todo: remove this method when possible
    /**
     * Obtain the {@link PersistentFacesState} instance keyed by viewNumber from
     * the specified sessionMap. This API is not intended for application use.
     *
     * @param sessionMap session-scope parameters
     * @return the PersistentFacesState
     */
    public static synchronized PersistentFacesState getInstance(Map sessionMap) {
        Object viewNumber = sessionMap.get(PersistentFacesServlet.CURRENT_VIEW_NUMBER);
        PersistentFacesState facesState = (PersistentFacesState) sessionMap.get(viewNumber + "/" + PersistentFacesState.class);
        return facesState;
    }

    /**
     * Render the view associated with this <code>PersistentFacesState</code>.
     * The user's browser will be immediately updated with any changes.
     */
    public void render() throws RenderingException {
        warn();
        facesContext.setCurrentInstance();
        facesContext.setFocusId("");
        synchronized (facesContext) {
            try {
                lifecycle.render(facesContext);
                facesContext.resetRenderResponse();
            } catch (IllegalStateException e) {
                if (log.isDebugEnabled()) {
                    log.debug("fatal render failure for viewNumber "
                            + facesContext.getViewNumber(), e);
                }
                throw new FatalRenderingException(
                        "fatal render failure for viewNumber "
                                + facesContext.getViewNumber(), e);
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("transient render failure for viewNumber "
                            + facesContext.getViewNumber(), e);
                }
                throw new TransientRenderingException(
                        "transient render failure for viewNumber "
                                + facesContext.getViewNumber(), e);
            }
        }
    }

    /**
     * Render the view associated with this <code>PersistentFacesState</code>.
     * This takes place on a separate thread to guard against potential deadlock
     * from calling {@link #render} during view rendering.
     */
    public void renderLater() {
        warn();
        executorService.execute(new RenderRunner());
    }

    public void renderLater(long miliseconds) {
        warn();
        executorService.execute(new RenderRunner(miliseconds));
    }

    /**
     * Redirect browser to a different URI. The user's browser will be
     * immediately redirected without any user interaction required.
     *
     * @param uri the relative or absolute URI.
     */
    public void redirectTo(String uri) {
        warn();
        try {
            facesContext.setCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            externalContext.redirect(uri);
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
        warn();
        try {
            facesContext.setCurrentInstance();
            facesContext.getApplication().getNavigationHandler()
                    .handleNavigation(facesContext,
                            facesContext.getViewRoot().getViewId(),
                            outcome);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * <p>Return a String representation of this <code> PersistentFacesState</code>
     * instance.</p>
     */
    public String toString() {
        return "com.icesoft.faces.webapp.xmlhttp.PersistentFacesState@" +
                hashCode() + "[" + facesContext + "]";
    }

    /**
     * Threads that used to server request/response cycles in an application
     * server are generally pulled from a pool.  Just before the thread is done
     * completing the cycle, we should clear any local instance variables to
     * ensure that they are not hanging on to any session references, otherwise
     * the session and their resources are not released.
     */
    public void release() {
        localInstance.set(null);
    }

    /**
     * Execute  the view associated with this <code>PersistentFacesState</code>.
     * This is typically followed immediatly by a call to
     * {@link PersistentFacesState#render}.
     */
    public void execute() throws RenderingException {
        facesContext.setCurrentInstance();
        synchronized (facesContext) {
            try {
                facesContext.renderResponse();
                lifecycle.execute(facesContext);
            } catch (IllegalStateException e) {
                if (log.isDebugEnabled()) {
                    log.debug("fatal render failure for viewNumber "
                            + facesContext.getViewNumber(), e);
                }
                throw new FatalRenderingException(
                        "fatal render failure for viewNumber "
                                + facesContext.getViewNumber(), e);
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug("transient render failure for viewNumber "
                            + facesContext.getViewNumber(), e);
                }
                throw new TransientRenderingException(
                        "transient render failure for viewNumber "
                                + facesContext.getViewNumber(), e);
            }
        }
    }


    public ClassLoader getRenderableClassLoader() {
        return renderableClassLoader;
    }

    private class RenderRunner implements Runnable {
        private long delay = 0;

        public RenderRunner() {
        }

        public RenderRunner(long miliseconds) {
            delay = miliseconds;
        }

        /**
         * <p>Not for application use. Entry point for {@link
         * PersistentFacesState#renderLater}.</p>
         */
        public void run() {
            try {
                Thread.sleep(delay);
                render();
            } catch (RenderingException e) {
                if (log.isDebugEnabled()) {
                    log.debug("renderLater failed ", e);
                }
            } catch (InterruptedException e) {
                //ignore
            }
        }
    }

    private void warn() {
        if (synchronousMode) {
            log.warn("Running in 'synchronous mode'. The page updates were queued but not sent.");
        }
    }
}






