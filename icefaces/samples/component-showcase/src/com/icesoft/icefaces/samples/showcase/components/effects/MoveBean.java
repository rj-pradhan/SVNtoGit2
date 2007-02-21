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
import com.icesoft.faces.context.effects.Move;

/**
 * <p>The MoveBean class backs the Move effect demo.</p>
 */
public class MoveBean {

    // coordinates
    private int x = 25;
    private int y = 25;


    // mode of move effect ("relative" or "absolute")
    private String mode;

    //effects
    private Effect effectPanelGroup;
    private Effect effectCommandButton;
    private Effect effectInputText;
    private Effect effectOutputText;
    private Effect effectSelectOneMenu;
    private Effect effectSelectManyListBox;
    private String dummy;

    private Effects effectsBean;

    public MoveBean(Effects effectsBean) {
        this.effectsBean = effectsBean;
    }

    /**
     * Gets the X-Coordinate.
     *
     * @return the X-Coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the X-Coordinate.
     *
     * @param x the X-Coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets the Y-Coordinate.
     *
     * @return the Y-Coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the Y-Coordinate.
     *
     * @param y the Y-Coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the mode of the move effect ("relative" or "absolute").
     *
     * @return the mode of move effect
     */
    public String getMode() {
        return mode;
    }

    /**
     * Sets the mode of the move effect ("relative" or "absolute").
     *
     * @param mode of move effect
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * Applies the move effect to each component.
     *
     * @return null
     */
    public String invokeEffect() {
        float seconds = effectsBean.getDurationPrimitive();

        effectPanelGroup = new Move(x, y, mode);
        effectCommandButton = new Move(x, y, mode);
        effectInputText = new Move(x, y, mode);
        effectOutputText = new Move(x, y, mode);
        effectSelectOneMenu = new Move(x, y, mode);
        effectSelectManyListBox = new Move(x, y, mode);

        effectPanelGroup.setDuration(seconds);
        effectCommandButton.setDuration(seconds);
        effectInputText.setDuration(seconds);
        effectOutputText.setDuration(seconds);
        effectSelectOneMenu.setDuration(seconds);
        effectSelectManyListBox.setDuration(seconds);
        return null;
    }

    /**
     * Returns the demo panels to their original place.
     *
     * @return null, no navigation
     */
    public String reset() {
        mode = "absolute";
        x = 0;
        y = 0;
        invokeEffect();
        return null;
    }

    public Effect getEffectPanelGroup() {
        return effectPanelGroup;
    }

    public void setEffectPanelGroup(Effect effectPanelGroup) {
        this.effectPanelGroup = effectPanelGroup;
    }

    public Effect getEffectCommandButton() {
        return effectCommandButton;
    }

    public void setEffectCommandButton(Effect effectCommandButton) {
        this.effectCommandButton = effectCommandButton;
    }

    public Effect getEffectInputText() {
        return effectInputText;
    }

    public void setEffectInputText(Effect effectInputText) {
        this.effectInputText = effectInputText;
    }

    public Effect getEffectOutputText() {
        return effectOutputText;
    }

    public void setEffectOutputText(Effect effectOutputText) {
        this.effectOutputText = effectOutputText;
    }

    public Effect getEffectSelectOneMenu() {
        return effectSelectOneMenu;
    }

    public void setEffectSelectOneMenu(Effect effectSelectOneMenu) {
        this.effectSelectOneMenu = effectSelectOneMenu;
    }

    public Effect getEffectSelectManyListBox() {
        return effectSelectManyListBox;
    }

    public void setEffectSelectManyListBox(Effect effectSelectManyListBox) {
        this.effectSelectManyListBox = effectSelectManyListBox;
    }

    public String getDummy() {
        return dummy;
    }

    public void setDummy(String dummy) {
        this.dummy = dummy;
    }

    public String dummy() {
        return null;
    }
}
