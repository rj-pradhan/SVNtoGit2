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

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;

/**
 * <p>The HighlightBean class backs the Highlight effect demo.</p>
 */
public class HighlightBean {
    private String[] selectManyValues = new String[]{
            "Value One",
            "Value Two",
            "Value Three",
            "Value Four"
    };
    private Effect effectCommandButton;
    private Effect effectCommandLink;
    private Effect effectInputText;
    private Effect effectInputSecret;
    private Effect effectInputTextArea;
    private Effect effectSelectManyCheckBox;
    private Effect effectSelectOneMenu;
    private Effect effectPanelGroup;
    private Effect effectOutputText;
    private Effect effectSelectManyListBox;

    private String effectColor = "#ffff99";

    private Effects effectsBean;

    public HighlightBean(Effects effectsBean) {
        this.effectsBean = effectsBean;
    }

    public Effect getEffectSelectManyListBox() {
        return effectSelectManyListBox;
    }

    public void setEffectSelectManyListBox(Effect effectSelectManyListBox) {
        this.effectSelectManyListBox = effectSelectManyListBox;
    }

    public Effect getEffectOutputText() {
        return effectOutputText;
    }

    public void setEffectOutputText(Effect effectOutputText) {
        this.effectOutputText = effectOutputText;
    }

    public String[] getSelectManyValues() {
        return selectManyValues;
    }

    public void setSelectManyValues(String[] selectManyValues) {
        this.selectManyValues = selectManyValues;
    }

    public Effect getEffectCommandButton() {
        return effectCommandButton;
    }

    public void setEffectCommandButton(Effect effectCommandButton) {
        this.effectCommandButton = effectCommandButton;
    }

    public Effect getEffectCommandLink() {
        return effectCommandLink;
    }

    public void setEffectCommandLink(Effect effectCommandLink) {
        this.effectCommandLink = effectCommandLink;
    }

    public Effect getEffectInputText() {
        return effectInputText;
    }

    public void setEffectInputText(Effect effectInputText) {
        this.effectInputText = effectInputText;
    }

    public Effect getEffectInputSecret() {
        return effectInputSecret;
    }

    public void setEffectInputSecret(Effect effectInputSecret) {
        this.effectInputSecret = effectInputSecret;
    }

    public Effect getEffectInputTextArea() {
        return effectInputTextArea;
    }

    public void setEffectInputTextArea(Effect effectInputTextArea) {
        this.effectInputTextArea = effectInputTextArea;
    }

    public Effect getEffectSelectManyCheckBox() {
        return effectSelectManyCheckBox;
    }

    public void setEffectSelectManyCheckBox(Effect effectSelectManyCheckBox) {
        this.effectSelectManyCheckBox = effectSelectManyCheckBox;
    }

    public Effect getEffectSelectOneMenu() {
        return effectSelectOneMenu;
    }

    public void setEffectSelectOneMenu(Effect effectSelectOneMenu) {
        this.effectSelectOneMenu = effectSelectOneMenu;
    }

    public Effect getEffectPanelGroup() {
        return effectPanelGroup;
    }

    public void setEffectPanelGroup(Effect effectPanelGroup) {
        this.effectPanelGroup = effectPanelGroup;
    }

    public String getEffectColor() {
        return effectColor;
    }

    public void setEffectColor(String effectColor) {
        this.effectColor = effectColor;
    }

    public String invokeEffect() {
        invokeEffect(effectColor);
        return null;
    }

    private void invokeEffect(String color) {
        float seconds = effectsBean.getDurationPrimitive();

        //colour
        effectCommandButton = new Highlight(color);
        effectCommandLink = new Highlight(color);
        effectInputText = new Highlight(color);
        effectInputSecret = new Highlight(color);
        effectInputTextArea = new Highlight(color);
        effectSelectManyCheckBox = new Highlight(color);
        effectSelectOneMenu = new Highlight(color);
        effectPanelGroup = new Highlight(color);
        effectSelectManyListBox = new Highlight(color);
        effectOutputText = new Highlight(color);

        //duration
        effectCommandButton.setDuration(seconds);
        effectCommandLink.setDuration(seconds);
        effectInputText.setDuration(seconds);
        effectInputSecret.setDuration(seconds);
        effectInputTextArea.setDuration(seconds);
        effectSelectManyCheckBox.setDuration(seconds);
        effectSelectOneMenu.setDuration(seconds);
        effectPanelGroup.setDuration(seconds);
        effectSelectManyListBox.setDuration(seconds);
        effectOutputText.setDuration(seconds);
    }
}
