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

import javax.faces.event.ValueChangeEvent;

/**
 * <p>The OutputProgressPropertyBean class manages the customizable aspects of
 * the outputProgress component. These values for custom labels and alignment
 * etc. are shared between the two separate outputProgress components
 * (determinate and indeterminate).</p>
 *
 * @see OutputProgressRenderBean
 * @since 1.0
 */
public class OutputProgressPropertyBean {

    // switch between standard and indeterminate modes
    private String mode = "standard";
    
    // position of label with respect to the progress bar
    private String labelPosition = "top";
    
    // values stored in selectBooleanCheckbox and inputText for custom progress
    private boolean customProgress;
    private String progressLabel;

    // values stored in selectBooleanCheckbox and inputText for custom completion
    private boolean customComplete;
    private String progressCompleteLabel;


    /**
     * Sets the mode of the progress bar.
     *
     * @param mode the mode of the progress bar
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * Gets the mode of the progress bar.
     *
     * @return the mode of the progress bar
     */
    public String getMode() {
        return mode;
    }

    /**
     * Sets the label position.  Values can be as follows: left, right, top,
     * topcenter, topright, bottom, bottomcenter, bottomright, embed.
     *
     * @param labelPosition new label position.
     */
    public void setLabelPosition(String labelPosition) {
        this.labelPosition = labelPosition;
    }

    /**
     * Gets the current position of the label.
     *
     * @return current position of label.
     */
    public String getLabelPosition() {
        return labelPosition;
    }

    /**
     * Sets the state of the custom progress label.
     *
     * @param customProgress the status of the custom progress label
     */
    public void setCustomProgress(boolean customProgress) {
        this.customProgress = customProgress;
    }

    /**
     * Determines whether a custom progress label is used.
     *
     * @return the status of custom progress label
     */
    public boolean isCustomProgress() {
        return customProgress;
    }

    /**
     * Sets the value of the progress Label.
     *
     * @param newLabel new value for label.
     */
    public void setProgressLabel(String newLabel) {
        progressLabel = newLabel;
    }
    
    /**
     * Gets the current progress label.
     *
     * @return progress label, null if there is no progress label.
     */
    public String getProgressLabel() {
        return progressLabel;
    }

    /**
     * @return
     */
    public String getProgressLabelAfter() {
        //by default, indeterminate mode has no in-progress label
        if (mode.equals("indeterminate")) {
            if (!customProgress)
                return "";
        }
        // if there's a custom progress label, use it, otherwise
        // returning null will make the component use the default
        if (customProgress) {
            return progressLabel;
        } else return null;
    }

    /**
     * Sets the state of the custom complete label.
     *
     * @param customComplete
     */
    public void setCustomComplete(boolean customComplete) {
        this.customComplete = customComplete;
    }
    
    /**
     * Determines whether a custom complete label is used.
     *
     * @return the status of custom complete label
     */
    public boolean isCustomComplete() {
        return customComplete;
    }
    
    /**
     * Sets the value of the progress complete label.
     *
     * @param newLabel new value for the complete label.
     */
    public void setProgressCompleteLabel(String newLabel) {
        progressCompleteLabel = newLabel;
    }

    /**
     * Gets the current progress label.
     *
     * @return progress label, null if there is no progress label.
     */
    public String getProgressCompleteLabel() {
        return progressCompleteLabel;
    }

    /**
     * @return
     */
    public String getProgressCompleteLabelAfter() {
        if (customComplete) {
            return progressCompleteLabel;
        } else return null;
    }
    
    /**
     * Progress label postion has changed. Update the state of the progress
     * label position and calls a render on the persistent faces context.
     *
     * @param event values changed event containing new position information.
     */
    public void progressPositionChanged(ValueChangeEvent event) {
        labelPosition = (String) event.getNewValue();
    }
}