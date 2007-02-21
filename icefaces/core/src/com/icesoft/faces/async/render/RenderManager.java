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

import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledThreadPoolExecutor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The RenderManager is the central class for developers wanting to do
 * server-initiated rendering.  The recommended use is to have a single
 * RenderManager for your application, typically configured as a
 * application-scope, managed bean.  Any class that needs to request server side
 * render calls can do so using the RenderManager.
 * <p/>
 * Server-initiated renders can be requested directly via the {@link
 * RenderManager#requestRender requestRender} method or you get a named {@link
 * GroupAsyncRenderer} and add a Renderable implementation to request and
 * receive render calls as part of a group.
 * <p/>
 * The RenderManager contains a reference to a single {@link RenderHub} that
 * uses a special queue and thread pool to optimize render calls for reliability
 * and scalability.
 */
public class RenderManager implements Disposable {

    private static Log log = LogFactory.getLog(RenderManager.class);

    static final int MIN = 1;
    public static final int ON_DEMAND = 2;
    public static final int INTERVAL = 3;
    public static final int DELAY = 4;
    static final int MAX = 4;

    private RenderHub renderHub;
    private Map groupMap;

    //The ContextEventRepeater stores listeners in a weakly referenced fashion
    //so we need to hang on to a reference or it will quietly disappear.
    private ContextDestroyedListener shutdownListener;

    /**
     * No argument constructor suitable for using as a managed bean.
     */
    public RenderManager() {
        shutdownListener = new ContextDestroyedListener(this);
        ContextEventRepeater.addListener(shutdownListener);
        groupMap = Collections.synchronizedMap(new HashMap());
        renderHub = new RenderHub();
    }

    /**
     * Internal utility method that returns the proper type of {@link
     * GroupAsyncRenderer} and ensures that is added to the managed collection,
     * indexed by name.
     *
     * @param name the unique name of the GroupAsyncRenderer
     * @param type the type of GroupAsyncRenderer
     * @return the requested type of GroupAsyncRenderer
     */
    private synchronized AsyncRenderer getRenderer(String name, int type) {
        if (name == null || name.trim().length() == 0) {
            throw new IllegalArgumentException(
                    "illegal renderer name: " + name);
        }

        if (type < MIN || type > MAX) {
            throw new IllegalArgumentException(
                    "illegal renderer type: " + type);
        }

        Object obj = groupMap.get(name);
        if (obj != null) {
            if (log.isTraceEnabled()) {
                log.trace("existing renderer retrieved: " + name);
            }
            return (AsyncRenderer) obj;
        }

        AsyncRenderer renderer = null;
        switch (type) {
            case ON_DEMAND:
                renderer = new OnDemandRenderer();
                break;
            case INTERVAL:
                renderer = new IntervalRenderer();
                break;
            case DELAY:
                renderer = new DelayRenderer();
                break;
        }

        renderer.setName(name);
        renderer.setRenderManager(this);
        groupMap.put(name, renderer);

        if (log.isTraceEnabled()) {
            log.trace("new renderer retrieved: " + name);
        }

        return renderer;
    }

    /**
     * When an AsyncRenderer disposes itself, it also needs to remove itself
     * from the RenderManager's collection.
     *
     * @param renderer  The Renderer to remove
     */
    protected void removeRenderer(AsyncRenderer renderer) {
        if (renderer == null) {
            if (log.isInfoEnabled()) {
                log.info("renderer is null");
            }
            return;
        }

        Object removedRenderer = groupMap.remove(renderer.getName());
        if (removedRenderer == null) {
            if (log.isTraceEnabled()) {
                log.trace("renderer " + renderer.getName() + " not found");
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("renderer " + renderer.getName() + " removed");
            }
        }
    }

    /**
     * Submits the supplied Renderable instance to the RenderHub for
     * server-initiated render.
     *
     * @param renderable The {@link Renderable} instance to render.
     */
    public void requestRender(Renderable renderable) {
        renderHub.requestRender(renderable);
    }

    void relayRender(String rendererName) {
        //no relay with the standard RenderManaager
    }

    /**
     * The OnDemandRenderer is a {@link GroupAsyncRenderer} that requests a
     * server-initiated render of all the Renderables in its collection.  The
     * request is made immediately upon request.
     * <p/>
     * Thsi method returns the appropriate GroupAsyncRenderer based on the name.
     * If the name is new, a new instance is created and stored in the
     * RenderManager's collection.  If the named GroupAsyncRenderer already
     * exists, and it's of the proper type, then the existing instance is
     * returned.
     *
     * @param name the name of the GroupAsyncRenderer
     * @return a new or existing GroupAsyncRenderer, based on the name
     */
    public OnDemandRenderer getOnDemandRenderer(String name) {
        return (OnDemandRenderer) getRenderer(name, ON_DEMAND);
    }

    /**
     * The IntervalRenderer is a {@link GroupAsyncRenderer} that requests a
     * server-initiated render of all the Renderables in its collection.  The
     * request to render is made repeatedly at the set interval.
     * <p/>
     * Thsi method returns the appropriate GroupAsyncRenderer based on the name.
     * If the name is new, a new instance is created and stored in the
     * RenderManager's collection.  If the named GroupAsyncRenderer already
     * exists, and it's of the proper type, then the existing instance is
     * returned.
     *
     * @param name the name of the GroupAsyncRenderer
     * @return a new or existing GroupAsyncRenderer, based on the name
     */
    public IntervalRenderer getIntervalRenderer(String name) {
        return (IntervalRenderer) getRenderer(name, INTERVAL);
    }

    /**
     * The DelayRenderer is a {@link GroupAsyncRenderer} that requests a
     * server-initiated render of all the Renderables in its collection.  The
     * request to render is made once at a set point in the future.
     * <p/>
     * Thsi method returns the appropriate GroupAsyncRenderer based on the name.
     * If the name is new, a new instance is created and stored in the
     * RenderManager's collection.  If the named GroupAsyncRenderer already
     * exists, and it's of the proper type, then the existing instance is
     * returned.
     *
     * @param name the name of the GroupAsyncRenderer
     * @return a new or existing GroupAsyncRenderer, based on the name
     */
    public DelayRenderer getDelayRenderer(String name) {
        return (DelayRenderer) getRenderer(name, DELAY);
    }

    /**
     * This method is used by {@link GroupAsyncRenderer}s that need to request
     * render calls based on some sort of schedule.  It uses a separate,
     * configurable thread pool and queue than the core rendering service.
     *
     * @return the scheduled executor for this RenderManager
     */
    ScheduledThreadPoolExecutor getScheduledService() {
        return renderHub.getScheduledService();
    }

    /**
     * This is typically called when the application is shutting down and we
     * need to dispose of all the RenderManager's resources.  It iterates
     * through all of the named {@link GroupAsyncRenderer}s, calling dispose on
     * each of them in turn.  It then calls dispose on the {@link RenderHub}.
     * Once the RenderManager has been disposed, it can no longer be used.
     */
    public void dispose() {
        synchronized (groupMap) {

            //Bug 944
            //We make a copy of the list of renderers to remove simply to
            //iterate through them.  This avoids a concurrent modification
            //issue when each individual renderers dispose method attempts
            //to remove itself from the official groupMap.
            ArrayList renderList = new ArrayList(groupMap.size());
            renderList.addAll( groupMap.values() );
            Iterator renderers = renderList.iterator();
            while (renderers.hasNext()) {
                AsyncRenderer renderer = (AsyncRenderer) renderers.next();
                renderer.dispose();
                if (log.isTraceEnabled()) {
                    log.trace("renderer disposed: " + renderer);
                }
            }
            groupMap.clear();
        }
        renderHub.dispose();
        if (log.isDebugEnabled()) {
            log.debug("all renderers and hub have been disposed");
        }

    }

    /**
     * Returns the named instance of an AsyncRenderer if it already exists
     * otherwise it returns null.
     *
     * @param rendererName The name of the AsycRender to retrieve.
     * @return An instance of an AsyncRenderer that is associated with the
     *         provided name.
     */
    public AsyncRenderer getRenderer(String rendererName) {
        if (rendererName == null) {
            return null;
        }
        return (AsyncRenderer) groupMap.get(rendererName);
    }
}
