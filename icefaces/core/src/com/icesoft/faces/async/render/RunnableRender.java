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

package com.icesoft.faces.async.render;

import com.icesoft.faces.webapp.xmlhttp.FatalRenderingException;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.icesoft.faces.webapp.xmlhttp.TransientRenderingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * RunnableRender implements Runnable and is designed to wrap up a {@link
 * Renderable} so that it can be used on the {@link RenderHub}s rendering
 * queue.
 *
 * @author ICEsoft Technologies, Inc.
 */
class RunnableRender implements Runnable {

    private static Log log = LogFactory.getLog(RenderHub.class);

    private Renderable renderable;

    public RunnableRender(Renderable renderable) {
        this.renderable = renderable;
    }

    public Renderable getRenderable() {
        return renderable;
    }

    /**
     * Using the supplied {@link Renderable}, extract its {@link
     * com.icesoft.faces.webapp.xmlhttp.PersistentFacesState
     * PersistentFacesState} and call the {@link com.icesoft.faces.webapp.xmlhttp.PersistentFacesState#render
     * PersistentFacesState.render} method.
     * <p/>
     * If a {@link com.icesoft.faces.webapp.xmlhttp.RenderingException
     * RenderingException} occurs, it is caught and the {@link
     * Renderable#renderingException} callback is called.
     */
    public void run() {
        if (renderable == null) {
            return;
        }

        PersistentFacesState state = renderable.getState();

        // This in response to an application with ServerInitiatedRendering coupled
        // with user interaction, and GET requests. If we don't update the thread
        // local, once user action creates a new ViewRoot, the Render thread's
        // version of state would forever be detached from the real view, resulting
        // in no more updates 
        state.setCurrentInstance();

        if (state == null) {
            if (log.isWarnEnabled()) {
                log.warn("state is null");
            }
            return;
        }

        //JIRA case ICE-1365
        //Server-side render calls can potentially be called from threads
        //that are outside the context of the web app which means that the
        //context classloader for the newly created thread might be unable
        //to access JSF artifacts.  If the render is to succeed, the classloader
        //that created the PersistentFacesState must be correct so we ensure
        //that the context classloader of the new render thread is set
        //accordingly. If the current security policy does not allow this then
        //we have to hope that the appropriate class loader settings were
        //transferred to this new thread.  If not, then the security policy
        //will need to be altered to allow this.
        try {
            Thread.currentThread().setContextClassLoader( state.getRenderableClassLoader() );
        } catch (SecurityException se) {
            if (log.isDebugEnabled()) {
                log.debug("setting context class loader is not permitted", se);
            }
        }

        try {
            state.execute();
            state.render();
        } catch (RenderingException ex) {
            renderable.renderingException(ex);
            if (ex instanceof TransientRenderingException) {
                if (log.isTraceEnabled()) {
                    log.trace("transient render exception", ex);
                }
            } else if (ex instanceof FatalRenderingException) {
                if (log.isDebugEnabled()) {
                    log.debug("fatal render exception", ex);
                }
            } else {
                if (log.isErrorEnabled()) {
                    log.error("unknown render exception", ex);
                }
            }
        }
    }

    /**
     * We override the equals method of Object so that we can compare
     * RunnableRender instances against each other.  Since the important pieces
     * are "wrapped" up, we need to unwrap them to compare them correctly. For
     * our purposes, we are really interested in whether the associated {@link
     * com.icesoft.faces.webapp.xmlhttp.PersistentFacesState
     * PersistentFacesState}s are equal so we "unwrap" each RunnableRender and
     * compare the internal PersistentFacesStates.
     *
     * @param obj The RunnableRender to compare to.
     * @return True if the internal PersistentFacesStates of each RunnableRender
     *         are equal. False otherwise.
     */
    public boolean equals(Object obj) {
        if (obj == null ||
            !(obj instanceof RunnableRender) ||
            renderable == null) {
            return false;
        }

        Renderable comparedRenderable = ((RunnableRender) obj).getRenderable();
        if (comparedRenderable == null) {
            return false;
        }

        PersistentFacesState comparedState = comparedRenderable.getState();
        if (comparedState == null) {
            return false;
        }

        PersistentFacesState myState = renderable.getState();
        if (myState == null) {
            return false;
        }

        return myState.equals(comparedState);
    }
}
