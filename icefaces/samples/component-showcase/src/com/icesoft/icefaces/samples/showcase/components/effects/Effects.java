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

package com.icesoft.icefaces.samples.showcase.components.effects;

import com.icesoft.faces.context.effects.*;

import javax.faces.model.SelectItem;

/**
 * <p>The Effects class is used to swap the effects used for the
 * "effect-specific attributes" and "results" panelStacks.</p>
 */
public class Effects {

    private String selectedEffect = "effectAppearFade";
    private String specific = "AppearFade";

    private String sampleData = "";

    private PulsateBean pulsateBean;
    private HighlightBean highlightBean;
    private AppearFadeBean appearFadeBean;
    private MoveBean moveBean;
    private float scalePercent = 50;

    private Effect panelGroupEffect;
    private Effect commandButtonEffect;
    private Effect inputTextEffect;

    private static final SelectItem[] durationChoices = new SelectItem[]{
            new SelectItem(new Float(0.5f), "0.5s"),
            new SelectItem(new Float(1.0f), "1.0s"),
            new SelectItem(new Float(2.0f), "2.0s"),
            new SelectItem(new Float(3.5f), "3.5s"),
            new SelectItem(new Float(5.0f), "5.0s"),
    };

    private float duration = 0.5f;

    public Effects() {
        pulsateBean = new PulsateBean(this);
        highlightBean = new HighlightBean(this);
        appearFadeBean = new AppearFadeBean(this);
        moveBean = new MoveBean(this);
    }

    public AppearFadeBean getAppearFadeBean() {
        return appearFadeBean;
    }

    public void setAppearFadeBean(AppearFadeBean appearFadeBean) {
        this.appearFadeBean = appearFadeBean;
    }

    public HighlightBean getHighlightBean() {
        return highlightBean;
    }

    public void setHighlightBean(HighlightBean highlightBean) {
        this.highlightBean = highlightBean;
    }

    public MoveBean getMoveBean() {
        return moveBean;
    }

    public void setMoveBean(MoveBean moveBean) {
        this.moveBean = moveBean;
    }

    public PulsateBean getPulsateBean() {
        return pulsateBean;
    }

    public void setPulsateBean(PulsateBean pulsateBean) {
        this.pulsateBean = pulsateBean;
    }

    public float getDurationPrimitive() {
        return duration;
    }

    public Float getDuration() {
        return new Float(duration);
    }

    public void setDuration(Float duration) {
        if (duration != null) {
            this.duration = duration.floatValue();
        }
    }

    public SelectItem[] getDurationChoices() {
        return durationChoices;
    }


    public String getSpecific() {
        return specific;
    }

    public String getSelectedEffect() {
        return selectedEffect;
    }

    public void setSelectedEffect(String selectedEffect) {
        this.specific = selectedEffect.substring(6, selectedEffect.length());
        this.selectedEffect = selectedEffect;
    }

    public String getSampleData() {
        return sampleData;
    }

    public void setSampleData(String sampleData) {
        this.sampleData = sampleData;
    }

    public String sampleDataAction() {
        return null;
    }

    public Effect getPanelGroupEffect() {
        return panelGroupEffect;
    }

    public void setPanelGroupEffect(Effect panelGroupEffect) {
        this.panelGroupEffect = panelGroupEffect;
    }

    public Effect getCommandButtonEffect() {
        return commandButtonEffect;
    }

    public void setCommandButtonEffect(Effect commandButtonEffect) {
        this.commandButtonEffect = commandButtonEffect;
    }

    public Effect getInputTextEffect() {
        return inputTextEffect;
    }

    public void setInputTextEffect(Effect inputTextEffect) {
        this.inputTextEffect = inputTextEffect;
    }

    public float getScalePercent() {
        return scalePercent;
    }

    public void setScalePercent(float scalePercent) {
        this.scalePercent = scalePercent;
    }

    public String scale() {
        panelGroupEffect = new Scale(scalePercent);
        inputTextEffect = new Scale(scalePercent);
        commandButtonEffect = new Scale(scalePercent);
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

     public String resetScale() {
        float defaultScale = 150;
        panelGroupEffect = new Scale(defaultScale);
        inputTextEffect = new Scale(defaultScale);
        commandButtonEffect = new Scale(defaultScale);
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

    public String puff() {
        panelGroupEffect = new Puff();
        inputTextEffect = new Puff();
        commandButtonEffect = new Puff();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

     public String resetDisappearEffect() {
        panelGroupEffect = new Appear();
        inputTextEffect = new Appear();
        commandButtonEffect = new Appear();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

    public String blindUp() {
        panelGroupEffect = new BlindUp();
        inputTextEffect = new BlindUp();
        commandButtonEffect = new BlindUp();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

    public String blindDown() {
        panelGroupEffect = new BlindDown();
        inputTextEffect = new BlindDown();
        commandButtonEffect = new BlindDown();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

    public String switchOff() {
        panelGroupEffect = new SwitchOff();
        inputTextEffect = new SwitchOff();
        commandButtonEffect = new SwitchOff();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;

    }

    public String dropOut() {
        panelGroupEffect = new DropOut();
        inputTextEffect = new DropOut();
        commandButtonEffect = new DropOut();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

    public String shake() {
        panelGroupEffect = new Shake();
        inputTextEffect = new Shake();
        commandButtonEffect = new Shake();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

    public String slideDown() {
        panelGroupEffect = new SlideDown();
        inputTextEffect = new SlideDown();
        commandButtonEffect = new SlideDown();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

    public String slideUp() {
        panelGroupEffect = new SlideUp();
        inputTextEffect = new SlideUp();
        commandButtonEffect = new SlideUp();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

    public String squish() {
        panelGroupEffect = new Squish();
        inputTextEffect = new Squish();
        commandButtonEffect = new Squish();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

    public String grow() {
        panelGroupEffect = new Grow();
        inputTextEffect = new Grow();
        commandButtonEffect = new Grow();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

    public String shrink() {
        panelGroupEffect = new Shrink();
        inputTextEffect = new Shrink();
        commandButtonEffect = new Shrink();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }

    public String fold() {
        panelGroupEffect = new Fold();
        inputTextEffect = new Fold();
        commandButtonEffect = new Fold();
        panelGroupEffect.setDuration(duration);
        inputTextEffect.setDuration(duration);
        commandButtonEffect.setDuration(duration);
        return null;
    }


}
