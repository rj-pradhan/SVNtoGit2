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
import java.util.ArrayList;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

/**
 * The DynamicTabSetBean is the backing bean for the TabbedPane showcase
 * demonstration.  It is used to dynamically add and remove tabs used in 
 * conjunction with the ice:panelTabSet component.
 *
 * @since 0.3.0
 */
public class DynamicTabSetBean{
    
    private int tabIndex;
    private String newTabLabel;
    private String newTabContent;
    private List tabs = new ArrayList();
    private String removedTab;
    private List tabItems = new ArrayList();
    private PanelTabSet dynamicTabSet;
        
    public DynamicTabSetBean() {
        
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
     * remove a tab from panelTabSet.
     *
     * @param event remove button click.
     */
    public void removeTab(ActionEvent event) {
        int selectedIndex =  dynamicTabSet.getSelectedIndex();
        //remove from tabs
        for (int i = 0; i < tabs.size(); i++) {
            if (((Tab) tabs.get(i)).getIndex() ==
                Integer.parseInt(removedTab)) {
                tabs.remove(i);
                if (selectedIndex  > i) {
                    dynamicTabSet.setSelectedIndex((selectedIndex > 0)? 
                        (selectedIndex -1) : selectedIndex);
                } else if (tabs.size() ==1) {
                    dynamicTabSet.setSelectedIndex(0);
                }
                break;
            }
        }

        //remove select option from selectRadiobox
        for (int i = 0; i < tabItems.size(); i++) {
            if ((((SelectItem) tabItems.get(i)).getValue())
                    .equals(removedTab)) {
                tabItems.remove(i);
                break;
            }
        }
    }
    
    /**
     * add a new tab to the panelTabSet.
     *
     * @param event add button click.
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
     * Gets the label of a new tab.
     *
     * @return newTabLabel current tab label.
     */
    public String getNewTabLabel() {
        return newTabLabel;
    }
    
    /**
     * Sets the label of a new tab.
     * 
     * @param newTabLabel label of the new tab.
     */
    public void setNewTabLabel(String newTabLabel) {
        this.newTabLabel = newTabLabel;
    }
    
    /**
     * Gets the content of a new tab.
     *
     * @return newTabContent of a new tab.
     */
    public String getNewTabContent() {
        return newTabContent;
    }
    
    /**
     * Sets the content of a new tab.
     *
     * @param newTabContent of a new tab.
     */
    public void setNewTabContent(String newTabContent) {
        this.newTabContent = newTabContent;
    }
    
    /**
     * Gets the removed tab.
     *
     * @return removedTab label of the removed tab.
     */
    public String getRemovedTab() {
        return removedTab;
    }
    
    /**
     * Sets the removed tab.
     *
     * @param removedTab label of tab to be removed.
     */
    public void setRemovedTab(String removedTab) {
        this.removedTab = removedTab;
    }

    /**
     * Gets the list of tab items.
     * 
     * @return tabItems list of tabs.
     */
    public List getTabItems() {
        return tabItems;
    }
    
    /**
     * Sets the list of tab items.
     *
     * @param tabItems list of tabs.
     */
    public void setTabItems(List tabItems) {
        this.tabItems = tabItems;
    }
    
    /**
     * Gets the list of tabs.
     *
     * @return tabs list of tabs.
     */
    public List getTabs() {
        return tabs;
    }

    /**
     * Sets the list of tabs.
     *
     * @param tabs list of tabs.
     */
    public void setTabs(List tabs) {
        this.tabs = tabs;
    }
    
    /**
     * Gets the dynamicTabSet PanelTabSet object.
     *
     * @return dynamicTabSet PanelTabSet object.
     */
    public PanelTabSet getDynamicTabSet() {
        return dynamicTabSet;
    }
    
    /**
     * Sets the dynamicTabSet PanelTabSet object.
     *
     * @param dynamicTabSet PanelTabSet object.
     */
    public void setDynamicTabSet(PanelTabSet dynamicTabSet) {
        this.dynamicTabSet = dynamicTabSet;
    }
    
    /**
     * Inner class that represents a tab object with a label, content, and an
     * index.
     */
    public class Tab {
        String label;
        String content;
        int index;
        
        /**
         * Gets the content of the tab.
         * 
         * @return content of the tab.
         */
        public String getContent() {
            return content;
        }
        
        /**
         * Sets the content of the tab.
         *
         * @param content of the tab.
         */
        public void setContent(String content) {
            this.content = content;
        }

        /**
         * Gets the label of the tab.
         *
         * @return label of the tab.
         */
        public String getLabel() {
            return label;
        }

        /**
         * Sets the label of the tab.
         *
         * @param label of the tab.
         */
        public void setLabel(String label) {
            this.label = label;
        }

        /**
         * Gets the index of the tab.
         *
         * @return index of the tab.
         */
        public int getIndex() {
            return index;
        }
        
        /**
         * Sets the index of the tab.
         *
         * @param index of the tab.
         */
        public void setIndex(int index) {
            this.index = index;
        }

    }
    
}
