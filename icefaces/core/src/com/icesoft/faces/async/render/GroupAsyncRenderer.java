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

import edu.emory.mathcs.backport.java.util.concurrent.CopyOnWriteArraySet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Set;

/**
 * The GroupAsyncRenderer is the foundation class for other types of renderers
 * that are designed to operate on a group of {@link Renderable}s.  It
 * implements the {@link AsyncRenderer} interface and is mainly responsible for
 * smartly managing a group of Renderable instances.
 * <p/>
 * Groups of Renderables are stored as WeakReferences in special sets that are
 * copied before each render pass so that Renderables can be safely added and
 * removed from the group while a render pass is in progress.
 * <p/>
 * Although it is possible to create and use GroupRenderers directly, developers
 * are advised to use the {@link RenderManager} to create and use named render
 * groups.
 *
 * @author ICEsoft Technologies, Inc.
 * @see RenderManager, OnDemandRenderer, IntervalRenderer, DelayRenderer
 */
public class GroupAsyncRenderer implements AsyncRenderer {

    private static Log log = LogFactory.getLog(GroupAsyncRenderer.class);

    protected String name;
    protected Set group;
    protected RenderManager renderManager;

    protected boolean stopRequested = false;

    public GroupAsyncRenderer() {
        group = new CopyOnWriteArraySet();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRenderManager(RenderManager renderManager) {
        this.renderManager = renderManager;
    }

    /**
     * Adds a Renderable, via a WeakReference, to the set of Renderables of this
     * group.  If the Renderable is already in this set, it is not added again.
     *
     * @param renderable the Renderable instance to add to the group.
     */
    public void add(Renderable renderable) {
        synchronized (group) {
            if (!contains(renderable)) {
                if (group.add(new WeakReference(renderable))) {
                    if (log.isTraceEnabled()) {
                        log.trace(name + " added " + renderable);
                    }
                } else {
                    if (log.isWarnEnabled()) {
                        log.warn(name + " already contains " + renderable);
                    }
                }
            }
        }
    }

    public boolean contains(Renderable renderable) {
        Iterator iter = group.iterator();
        while (iter.hasNext()) {
            if (renderable ==
                (Renderable) ((WeakReference) iter.next()).get()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a Renderable, via a WeakReference, from the set of Renderables of
     * this group.
     *
     * @param renderable the Renderable instance to remove
     */
    public void remove(Renderable renderable) {
        synchronized (group) {
            Iterator iter = group.iterator();
            while (iter.hasNext()) {
                WeakReference ref = (WeakReference) iter.next();
                if (renderable == (Renderable) ref.get()) {
                    group.remove(ref);
                    if (log.isTraceEnabled()) {
                        log.trace(name + " removing " + renderable);
                    }
                    return;
                }
            }
            if (log.isWarnEnabled()) {
                log.warn(name + " does not contain " + renderable);
            }
        }
    }

    /**
     * Removes all Renderables from the group.
     */
    public void clear() {
        synchronized (group) {
            group.clear();
        }
    }

    /**
     * Used to determine if the Renderer has any Renderables left in its
     * collection.
     *
     * @return false if there are 1 or more Renderables left in the Renderer's
     *         collection, true otherwise
     */
    public boolean isEmpty() {
        return group.isEmpty();
    }

    /**
     * Request a render pass on all the Renderables in the group.  Render calls
     * that generate exceptions are passed back to the Renderable.renderException
     *
     * @throws IllegalStateException If a reference to a {@link RenderHub} has
     *                               not yet been set.
     */
    public void requestRender() {
        if (renderManager == null) {
            String message = "RenderManager has not been set";
            if (log.isErrorEnabled()) {
                log.error(message);
            }
            throw new IllegalStateException(message);

        }

        Iterator iter = group.iterator();
        if (log.isTraceEnabled()) {
            log.trace(name + " preparing to render " + group.size());
        }

        stopRequested = false;
        while (iter.hasNext() && !stopRequested) {
            Renderable renderable =
                    (Renderable) ((WeakReference) iter.next()).get();
            renderManager.requestRender(renderable);
        }
    }

    /**
     * The method called by dispose to halt a render pass at the current {@link
     * Renderable}s.
     */
    public void requestStop() {
        stopRequested = true;
    }

    /**
     * Remove all Renderables from the group and removes the reference to the
     * RenderHub.  Once disposed, a GroupAsyncRenderer cannot be re-used.  This
     * method is typically used by the RenderManager to cleanly dispose of all
     * managed Renderers when the application is shutting down.
     */
    public void dispose() {
        requestStop();
        group.clear();
        group = null;
        renderManager.removeRenderer(this);
        name = null;
        renderManager = null;
    }
}
