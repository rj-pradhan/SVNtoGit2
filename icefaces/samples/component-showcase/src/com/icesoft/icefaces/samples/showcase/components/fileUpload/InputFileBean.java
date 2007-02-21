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

import com.icesoft.faces.component.inputfile.InputFile;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;
import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;

import javax.faces.event.ActionEvent;
import java.io.File;
import java.util.EventObject;

/**
 * <p>The InputFileBean class is the backing bean for the inputfile showcase
 * demonstration. It is used to store the state of the uploaded file.</p>
 *
 * @since 0.3.0
 */
public class InputFileBean  implements Renderable {

    private int percent = -1;
    private File file = null;

    /**
     * Renderable Interface
     */
    private PersistentFacesState state;
    private RenderManager renderManager;

    private String fileName = "";
    private String contentType = "";


    public InputFileBean() {
        state = PersistentFacesState.getInstance();
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

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void action(ActionEvent event) {
        InputFile inputFile = (InputFile) event.getSource();
        if (inputFile.getStatus() == InputFile.SAVED) {
            fileName = inputFile.getFileInfo().getFileName();
            contentType = inputFile.getFileInfo().getContentType();
            setFile(inputFile.getFile());
        }

        if (inputFile.getStatus() == InputFile.INVALID) {
            inputFile.getFileInfo().getException().printStackTrace();
        }

        if (inputFile.getStatus() == InputFile.SIZE_LIMIT_EXCEEDED) {
            inputFile.getFileInfo().getException().printStackTrace();
        }

        if (inputFile.getStatus() == InputFile.UNKNOWN_SIZE) {
            inputFile.getFileInfo().getException().printStackTrace();
        }


    }

    public void progress(EventObject event) {
        InputFile file = (InputFile) event.getSource();
        this.percent = file.getFileInfo().getPercent();

        if (renderManager != null) {
           renderManager.requestRender(this);
        }

    }


    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

}
