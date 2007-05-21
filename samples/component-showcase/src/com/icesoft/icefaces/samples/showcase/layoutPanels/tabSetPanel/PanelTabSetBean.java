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

package com.icesoft.icefaces.samples.showcase.layoutPanels.tabSetPanel;

import com.icesoft.faces.component.paneltabset.PanelTabSet;
import com.icesoft.faces.component.paneltabset.TabChangeEvent;
import com.icesoft.faces.component.paneltabset.TabChangeListener;

import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlSelectOneRadio;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>The PanelTabSetBean class is a backing bean for the TabbedPane showcase
 * demonstration and is used to store the various states of the the
 * ice:panelTabSet component.  These states are visibility, tab selection and
 * tab placement. </p>
 *
 * @since 0.3.0
 */
public class PanelTabSetBean implements TabChangeListener {

    /**
     * The demo contains three tabs and thus we need three variables to store
     * their respective rendered states.
     */
    private boolean tabbedPane1Visible;
    private boolean tabbedPane2Visible;
    private boolean tabbedPane3Visible;
    private HtmlSelectOneRadio selectedTabObject;
    
    private int tabIndex = 0;
    private String newTabLabel = "";
    private String newTabContent = "";
    private List tabs = new ArrayList();
    private String removedTab;
    private List tabItems = new ArrayList();

    public PanelTabSetBean() {

        //pre-defined two tabs into the panelTabSet
        Tab newTab1 = new Tab();
        newTab1.setLabel("Label1");
        newTab1.setContent("Content1");
        newTab1.setIndex(tabIndex++);

        Tab newTab2 = new Tab();
        newTab2.setLabel("Label2");
        newTab2.setContent("Content2");
        newTab2.setIndex(tabIndex++);

        tabItems.add(
                new SelectItem(Integer.toString(newTab1.index), newTab1.label));
        tabItems.add(
                new SelectItem(Integer.toString(newTab2.index), newTab2.label));

        tabs.add(newTab1);
        tabs.add(newTab2);
    }

    /**
     * Tabbed placement, possible values are "top" and "bottom", the default is
     * "bottom".
     */
    private String tabPlacement = "bottom";

    // default tab focus.
    private String selectedTabFocus = "1";

    /**
     * Binding used by example to listen
     */
    private PanelTabSet tabSet;

    /**
     * Binding used by example to listen
     */
    private PanelTabSet dynamicTabSet;

    /**
     * Return the visibility of tab panel 1.
     *
     * @return true if tab is visible; false, otherwise.
     */
    public boolean isTabbedPane1Visible() {
        return tabbedPane1Visible;
    }

    /**
     * Sets the tabbed pane visibility
     *
     * @param tabbedPane1Visible true to make the panel visible; false,
     *                           otherwise.
     */
    public void setTabbedPane1Visible(boolean tabbedPane1Visible) {
        this.tabbedPane1Visible = tabbedPane1Visible;
    }

    /**
     * Return the visibility of tab panel 2.
     *
     * @return true if tab is visible; false, otherwise.
     */
    public boolean isTabbedPane2Visible() {
        return tabbedPane2Visible;
    }

    /**
     * Sets the tabbed pane visibility
     *
     * @param tabbedPane2Visible true to make the panel visible; false,
     *                           otherwise.
     */
    public void setTabbedPane2Visible(boolean tabbedPane2Visible) {
        this.tabbedPane2Visible = tabbedPane2Visible;
    }

    /**
     * Return the visibility of tab panel 3.
     *
     * @return true if tab is visible; false, otherwise.
     */
    public boolean isTabbedPane3Visible() {
        return tabbedPane3Visible;
    }

    /**
     * Sets the tabbed pane visibility
     *
     * @param tabbedPane3Visible true to make the panel visible; false,
     *                           otherwise.
     */
    public void setTabbedPane3Visible(boolean tabbedPane3Visible) {
        this.tabbedPane3Visible = tabbedPane3Visible;
    }

    /**
     * Gets the tabbed pane object bound to this bean.
     *
     * @return bound tabbed pane
     */
    public PanelTabSet getTabSet() {
        return tabSet;
    }

    /**
     * Set a tabbed pane object which will be bound to this object
     *
     * @param tabSet
     */
    public void setTabSet(PanelTabSet tabSet) {
        // remove tab change listener
        if (this.tabSet != null) {
            this.tabSet.removeTabChangeListener(this);
        }

        // assign the new binding
        this.tabSet = tabSet;

        // add a new listener
        if (this.tabSet != null) {
            this.tabSet.addTabChangeListener(this);
        }
    }

    /**
     * remove a tab from panelTabSet
     *
     * @param event
     */
    public void removeTab(ActionEvent event) {
        int selectedIndex =  dynamicTabSet.getSelectedIndex();
        //remove from tabs
        for (int i = 0; i < tabs.size(); i++) {
            if (((Tab) tabs.get(i)).getIndex() ==
                Integer.parseInt(removedTab)) {
                tabs.remove(i);
                if (selectedIndex  > i) {
                    dynamicTabSet.setSelectedIndex((selectedIndex > 0)? (selectedIndex -1) : selectedIndex);
                } else if (tabs.size() ==1) {
                    dynamicTabSet.setSelectedIndex(0);
                }
                break;
            }
        }

        //remove select option from selectRadiobox
        for (int i = 0; i < tabItems.size(); i++) {
            if (((String) ((SelectItem) tabItems.get(i)).getValue())
                    .equals(removedTab)) {
                tabItems.remove(i);
                break;
            }
        }
    }

    /**
     * add a new tab to the panelTabSet
     *
     * @param event
     */
    public void addTab(ActionEvent event) {

        //assign default label if it's blank
        if (newTabLabel.equals("")) {
            newTabLabel = "Tab " + (tabIndex + 1);
        }

        //set the new tab from the input
        Tab newTab = new Tab();
        newTab.setContent(newTabContent);
        newTab.setLabel(newTabLabel);
        newTab.setIndex(tabIndex++);

        //add to both tabs and select options of selectRadiobox
        tabs.add(newTab);
        tabItems.add(
                new SelectItem(Integer.toString(newTab.index), newTabLabel));

        //clean up input field
        newTabLabel = "";
        newTabContent = "";
    }

    /**
     * Called when the tab pane focus is to be changed.
     *
     * @param event new value is the new selected tab index.
     */
    public void selectTabFocus(ValueChangeEvent event) {
        int index = Integer.parseInt((String) event.getNewValue());
        tabSet.setSelectedIndex(index);
    }

    public void selectTab(ValueChangeEvent event) {
        UIInput component = (UIInput) event.getComponent();
        int index = 1;
        try {
            index = new Integer(component.getValue().toString()).intValue();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        tabSet.setSelectedIndex(index);
    }

    /**
     * Gets the tab placement, either top or bottom.
     *
     * @return top if the tab is on the top of the component, bottom if the tab
     *         is on the bottom of the component.
     */
    public String getTabPlacement() {
        return tabPlacement;
    }

    /**
     * Sets the tab placement.
     *
     * @param tabPlacement two options top or bottom.
     */
    public void setTabPlacement(String tabPlacement) {
        this.tabPlacement = tabPlacement;
    }

    /**
     * Method is called when there has been a request to change the tab
     * placement.
     *
     * @param event contains new tab placement data.
     */
    public void selectTabPlacement(ValueChangeEvent event) {
        tabPlacement = (String) event.getNewValue();
    }

    /**
     * Called when the table binding's tab focus changes.
     *
     * @param tabChangeEvent
     * @throws AbortProcessingException
     */
    public void processTabChange(TabChangeEvent tabChangeEvent)
            throws AbortProcessingException {
        setSelectedTabFocus(String.valueOf(tabChangeEvent.getNewTabIndex()));
        if (selectedTabObject != null) {
            selectedTabObject.setSubmittedValue(selectedTabFocus);
        }
    }

    /**
     * Gets the currently selected tab
     *
     * @return
     */
    public String getSelectedTabFocus() {
        return selectedTabFocus;
    }

    /**
     * Sets the currently selected tab.
     *
     * @param selectedTabFocus
     */
    public void setSelectedTabFocus(String selectedTabFocus) {
        this.selectedTabFocus = selectedTabFocus;
    }

    public HtmlSelectOneRadio getBindSelectedTabObject() {
        return selectedTabObject;
    }

    public void setBindSelectedTabObject(HtmlSelectOneRadio selectedTabObject) {
        this.selectedTabObject = selectedTabObject;
    }

    public String getNewTabLabel() {
        return newTabLabel;
    }

    public void setNewTabLabel(String newTabLabel) {
        this.newTabLabel = newTabLabel;
    }

    public String getNewTabContent() {
        return newTabContent;
    }

    public void setNewTabContent(String newTabContent) {
        this.newTabContent = newTabContent;
    }

    public class Tab {
        String label;
        String content;
        int index;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

    }

    public String getRemovedTab() {
        return removedTab;
    }

    public void setRemovedTab(String removedTab) {
        this.removedTab = removedTab;
    }

    public List getTabItems() {
        return tabItems;
    }

    public void setTabItems(List tabItems) {
        this.tabItems = tabItems;
    }

    public List getTabs() {
        return tabs;
    }

    public void setTabs(List tabs) {
        this.tabs = tabs;
    }

    public PanelTabSet getDynamicTabSet() {
        return dynamicTabSet;
    }

    public void setDynamicTabSet(PanelTabSet dynamicTabSet) {
        this.dynamicTabSet = dynamicTabSet;
    }

}