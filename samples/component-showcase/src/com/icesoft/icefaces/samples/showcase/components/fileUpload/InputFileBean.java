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

package com.icesoft.icefaces.samples.showcase.components.fileUpload;

import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.component.inputfile.FileInfo;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;

import javax.faces.event.ActionEvent;

/**
 * <p>The InputFileBean class is the backing bean for the inputfile showcase
 * demonstration. It is used to store the state of the uploaded file.</p>
 *
 * @since 0.3.0
 */
public class InputFileBean implements Renderable {

    private RenderManager renderManager;
    private String fileName = "";
    private String contentType = "";
    private int progress;

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

    public PersistentFacesState getState() {
        return PersistentFacesState.getInstance();
    }

    /**
     * Handles rendering exceptions for the progress bar.
     *
     * @param renderingException the exception that occured
     */
    public void renderingException(RenderingException renderingException) {
        renderingException.printStackTrace();
    }

    public void action(ActionEvent event) {
        FileInfo fileInfo = (FileInfo) event;
        this.fileName = fileInfo.getFileName();
        this.contentType = fileInfo.getContentType();
        fileInfo.getFile();//do something with it...
    }
    
    public void setProgress(int progress) throws RenderingException {
        this.progress = progress;
        PersistentFacesState.getInstance().renderLater();
    }

    public int getProgress() {
        return progress;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public String getDownloadFolder() {
        return null;
    }
}
