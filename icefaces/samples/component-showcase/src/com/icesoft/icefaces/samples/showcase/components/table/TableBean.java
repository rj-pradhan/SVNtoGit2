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

package com.icesoft.icefaces.samples.showcase.components.table;

import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.icefaces.samples.showcase.common.Person;

import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>The TableBean class is the backing bean for the table Component showcase
 * demonstration. It is used to store the visibility state of the four data
 * columns used in the demonstration.</p>
 *
 * @since 0.3.0
 */
public class TableBean {

    // column visibility, true to render false otherwise.
    private boolean renderFirstName = true;
    private boolean renderLastName = true;
    private boolean renderPhone = true;
    private boolean renderEmail = true;
    private List selectedRows = new ArrayList();
    private boolean multipleSelection = true;
    // enable scrollable property
    private boolean scrollable = true;
    // scrollable height property;
    private String scrollableHeight = "100px";
    private Person[] personsList = DataTablePaginatorBean.buildPersonList();

    public TableBean() {
    }

    /**
     * Is the first name column rendered?
     *
     * @return rendered state of column
     */
    public boolean isRenderFirstName() {
        return renderFirstName;
    }

    /**
     * Sets the first name column rendered state.
     *
     * @param renderFirstName true to render column; false otherwise
     */
    public void setRenderFirstName(boolean renderFirstName) {
        this.renderFirstName = renderFirstName;
    }

    /**
     * Is the last name column rendered?
     *
     * @return rendered state of column
     */
    public boolean isRenderLastName() {
        return renderLastName;
    }

    /**
     * Sets the last name column rendered state.
     *
     * @param renderLastName true to render column; false otherwise
     */
    public void setRenderLastName(boolean renderLastName) {
        this.renderLastName = renderLastName;
    }

    /**
     * Is the phone column rendered?
     *
     * @return rendered state of column
     */
    public boolean isRenderPhone() {
        return renderPhone;
    }

    /**
     * Sets the phone column rendered state.
     *
     * @param renderPhone true to render column; false otherwise
     */
    public void setRenderPhone(boolean renderPhone) {
        this.renderPhone = renderPhone;
    }

    /**
     * Is the email column rendered?
     *
     * @return rendered state of column
     */
    public boolean isRenderEmail() {
        return renderEmail;
    }

    /**
     * Sets the email column rendered state.
     *
     * @param renderEmail true to render column; false otherwise
     */
    public void setRenderEmail(boolean renderEmail) {
        this.renderEmail = renderEmail;
    }

    public List getSelectedRows() {

        return selectedRows;
    }

    public void setSelectedRows(List selectedRows) {
        this.selectedRows = selectedRows;
    }


    public void rowSelection(RowSelectorEvent e) {
        selectedRows.clear();
        for (int i = personsList.length-1; i >= 0 ; i--) {
            if (personsList[i].getSelected().booleanValue()) {
                selectedRows.add(personsList[i]);
            }
        }
    }

    public void clearSelections(ActionEvent event){
        selectedRows.clear();
         for (int i = personsList.length-1; i >= 0 ; i--) {
            personsList[i].setSelected(new Boolean(false));
        }
    }

    public boolean isMultipleSelection() {
        return multipleSelection;
    }

    public void setMultipleSelection(boolean multipleSelection) {
        this.multipleSelection = multipleSelection;
    }

    public void selectionChanged(ValueChangeEvent event) {

        selectedRows.clear();
        Person[] persons = personsList;
        for (int i = 0; i < persons.length; i++) {
            persons[i].setSelected(Boolean.FALSE);
        }

    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public Person[] getPersonsList() {
        return personsList;
    }

    public void setPersonsList(Person[] personsList) {
        this.personsList = personsList;
    }


    public String getScrollableHeight() {
        return scrollableHeight;
    }
    public void setScrollableHeight(String scrollableHeight) {
        this.scrollableHeight = scrollableHeight;
    }
}