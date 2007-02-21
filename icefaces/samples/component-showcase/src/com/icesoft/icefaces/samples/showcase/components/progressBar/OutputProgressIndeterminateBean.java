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

package com.icesoft.icefaces.samples.showcase.components.progressBar;

import javax.faces.event.ActionEvent;

import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Backs the indeterminate mode of the outputProgress component.
 */
public class OutputProgressIndeterminateBean implements Renderable {

    private static Log log =
            LogFactory.getLog(OutputProgressIndeterminateBean.class);

    /**
     * Renderable Interface
     */
    private PersistentFacesState state;
    private RenderManager renderManager;

    /**
     * Get the PersistentFacesState.
     *
     * @return state the PersistantFacesState
     */
    public PersistentFacesState getState() {

        return state;
    }

    /**
     * Handles rendering exceptions for the progress bar.
     *
     * @param renderingException the exception that occured
     */
    public void renderingException(RenderingException renderingException) {
        renderingException.printStackTrace();
    }

    /**
     * Sets the Render Manager.
     *
     * @param renderManager
     */
    public void setRenderManager(RenderManager renderManager) {
        this.renderManager = renderManager;
    }

    /**
     * Gets RenderManager, just try to satisfy WAS
     *
     * @return RenderManager null
     */
    public RenderManager getRenderManager() {
        return null;
    }

    // progress active status
    private boolean runningTask;

    // percentage completed
    private int percent;

    /**
     * The constructor gets the PersistentFacesState for manual render
     * requests.
     */
    public OutputProgressIndeterminateBean() {
        state = PersistentFacesState.getInstance();
    }

    /**
     * Gets the percentage completed.
     *
     * @return the current percentage
     */
    public int getPercent() {
        return percent;
    }

    /**
     * Sets the percentage.
     *
     * @param percent the new percentage
     */
    public void setPercent(int percent) {
        this.percent = percent;
    }

    /**
     * Determine whether the progress bar is active.
     *
     * @return the activity status
     */
    public boolean isRunningTask() {
        return runningTask;
    }

    /**
     * Set whether the progress bar is active.
     *
     * @param runningTask the new activity status
     */
    public void setRunningTask(boolean runningTask) {
        this.runningTask = runningTask;
    }

    /**
     * Start a routine that will take time and allow progress updates.
     */
    public void longOperation(ActionEvent event) {
        setPercent(1);
        Thread copyThread = new Thread(new LongOperationRunner(this,
                                                               PersistentFacesState.getInstance()));
        copyThread.start();
    }

    /**
     * Routine that takes time and updates percentage as it runs.
     */
    class LongOperationRunner implements Runnable {
        PersistentFacesState state = null;
        private OutputProgressIndeterminateBean pbBean;

        public LongOperationRunner(OutputProgressIndeterminateBean progress,
                                   PersistentFacesState state) {
            this.state = state;
            this.pbBean = progress;
        }

        public void run() {

            runningTask = true;
            pbBean.setPercent(1);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                if (log.isErrorEnabled()) {
                    log.error("interrupted", e);
                }
            }

            //enable the "start" button
            runningTask = false;
            pbBean.setPercent(100);
            renderManager.requestRender(pbBean);
        }
    }
}