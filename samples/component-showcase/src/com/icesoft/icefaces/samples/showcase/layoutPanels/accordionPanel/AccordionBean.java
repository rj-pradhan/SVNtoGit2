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

package com.icesoft.icefaces.samples.showcase.layoutPanels.accordionPanel;

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;


public class AccordionBean {

    private List data = new ArrayList();
    private boolean open = true;
    private Highlight effectOutputText = new Highlight("#a4bdd2");
    private boolean infoBoolean = false;
    private String name;
    private String status;
    private String selectedPanel = "";

    public AccordionBean() {

        data.add(new AccordionItem("Fruits"));
        data.add(new AccordionItem("Vegetables"));

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }


    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public Effect getEffectOutputText() {
        return effectOutputText;
    }

    /*
    * Sets the output text effect
    *@param Effect effectOutputText
    */
    public void setEffectOutputText(Effect effectOutputText) {
        this.effectOutputText = (Highlight) effectOutputText;
    }

    public void setInfoBoolean(boolean bool) {
        infoBoolean = bool;
    }

    public boolean getInfoBoolean() {
        return infoBoolean;
    }

    public void loginActionListener(ActionEvent event) {
        setInfoBoolean(true);
        setStatus("Logged In");
    }

    public void registerActionListener(ActionEvent event) {
        setInfoBoolean(true);
        setStatus("Registered");

    }

    /**
     * Used in the accordion panel example to change between the selected
     * panels.
     *
     * @param event from the action listener.
     */
    public void selectedPanelChangedAction(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map map = context.getExternalContext().getRequestParameterMap();
        selectedPanel = (String) map.get("products");

    }

    /**
     * Sets the selected panel name to the specified panel name.
     *
     * @param selectedPanel panel name to be set as selected.
     */
    public void setSelectedPanel(String selectedPanel) {
        this.selectedPanel = selectedPanel;
    }

    /**
     * Gets the selected panel name.
     *
     * @return currently selected panel.
     */
    public String getSelectedPanel() {
        return selectedPanel;
    }
}
