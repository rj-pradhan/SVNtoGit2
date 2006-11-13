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

package com.icesoft.icefaces.samples.showcase.components.expandableTable;


import javax.faces.event.ActionEvent;
import java.util.ArrayList;

/**
 * <p>The <code>SalesGroupRecordBean</code> class is responsible for storing
 * view specific information for the salesGroupRecord Bean. Such things as
 * expand/contract images and css attributes are stored in this Bean. Model can
 * be found in the sub class <code>SalesGroupRecord</code>. </p>
 * <p/>
 * <p>This class is also responsible for handling all events that control how
 * the view (jspx) page behaves.</p>
 */
public class SalesGroupRecordBean extends SalesGroupRecord {

    public static final String SPACER_IMAGE =
            "images/tableExpandable/spacer.gif";

    // style for column that holds expand/contract image toggle, in the sales
    // record row.
    protected String indentStyleClass = "";

    // style for all other columns in the sales record row.
    protected String rowStyleClass = "";

    // Images used to represent expand/contract, spacer by default
    protected String expandImage = SPACER_IMAGE;  // arrow points right
    protected String contractImage = SPACER_IMAGE; // arrow point down

    // image which will be drawn to screen
    protected String expandContractImage = SPACER_IMAGE;

    // callback to list which contains all data in the dataTable.  This callback
    // is needed so that a node can be set in the expanded state at construction time.
    protected ArrayList parentInventoryList;

    // indicates if node is in expanded state.
    protected boolean isExpanded;

    /**
     * <p>Creates a new <code>SalesGroupRecordBean</code>.  This constructor
     * should be use when creating SalesGroupRecordBeans which will contain
     * children</p>
     *
     * @param isExpanded true, indicates that the specified node will be
     *                   expanded by default; otherwise, false.
     */
    public SalesGroupRecordBean(String indentStyleClass,
                                String rowStyleClass,
                                String expandImage,
                                String contractImage,
                                ArrayList parentInventoryList,
                                boolean isExpanded) {

        this.indentStyleClass = indentStyleClass;
        this.rowStyleClass = rowStyleClass;
        this.expandImage = expandImage;
        this.contractImage = contractImage;
        this.parentInventoryList = parentInventoryList;
        this.parentInventoryList.add(this);
        this.isExpanded = isExpanded;
        // update the default state of the node.
        if (this.isExpanded) {
            expandContractImage = contractImage;
            expandNodeAction();
        } else {
            expandContractImage = expandImage;
        }
    }

    /**
     * <p>Creates a new <code>SalesGroupRecordBean</code>.  This constructor
     * should be used when creating a SalesGroupRecordBean which will be a child
     * of some other SalesGroupRecordBean.</p>
     * <p/>
     * <p>The created SalesGroupRecordBean has no image states defined.</p>
     *
     * @param indentStyleClass
     * @param rowStyleClass
     */
    public SalesGroupRecordBean(String indentStyleClass,
                                String rowStyleClass) {

        this.indentStyleClass = indentStyleClass;
        this.rowStyleClass = rowStyleClass;
    }

    /**
     * Gets the renderable state of the contract/expand image toggle.
     *
     * @return true if images should be drawn; otherwise, false.
     */
    public boolean isRenderImage() {
        return childSalesRecords != null && childSalesRecords.size() > 0;
    }

    /**
     * Toggles the expanded state of this SalesGroup Record.
     *
     * @param event
     */
    public void toggleSubGroupAction(ActionEvent event) {
        // toggle expanded state
        isExpanded = !isExpanded;

        // add sub elements to list
        if (isExpanded) {
            expandContractImage = contractImage;
            expandNodeAction();
        }
        // remove items from list
        else {
            expandContractImage = expandImage;
            contractNodeAction();
        }
    }

    /**
     * Adds a child sales record to this sales group.
     *
     * @param salesGroupRecord child sales record to add to this record.
     */
    public void addChildSalesGroupRecord(
            SalesGroupRecordBean salesGroupRecord) {
        if (this.childSalesRecords != null && salesGroupRecord != null) {
            this.childSalesRecords.add(salesGroupRecord);
            if (isExpanded) {
                // to keep elements in order, remove all
                contractNodeAction();
                // then add them again.
                expandNodeAction();
            }
        }
    }

    /**
     * Removes the specified child sales record from this sales group.
     *
     * @param salesGroupRecord child sales record to remove.
     */
    public void removeChildSalesGroupRecord(
            SalesGroupRecordBean salesGroupRecord) {
        if (this.childSalesRecords != null && salesGroupRecord != null) {
            if (isExpanded) {
                // remove all, make sure we are removing the specified one too.
                contractNodeAction();
            }
            // remove the current node
            this.childSalesRecords.remove(salesGroupRecord);
            // update the list if needed.
            if (isExpanded) {
                // to keep elements in order, remove all
                contractNodeAction();
                // then add them again.
                expandNodeAction();
            }
        }
    }

    /**
     * Utility method to add all child nodes to the parent dataTable list.
     */
    private void expandNodeAction() {
        if (childSalesRecords != null && childSalesRecords.size() > 0) {
            // get index of current node
            int index = parentInventoryList.indexOf(this);

            // add all items in childSalesRecords to the parent list
            parentInventoryList.addAll(index + 1, childSalesRecords);
        }

    }

    /**
     * Utility method to remove all child nodes from the parent dataTable list.
     */
    private void contractNodeAction() {
        if (childSalesRecords != null && childSalesRecords.size() > 0) {
            // add all items in childSalesRecords to the parent list
            parentInventoryList.removeAll(childSalesRecords);
        }
    }

    /**
     * Gets the style class name used to define the first column of a sales
     * record row.  This first column is where a expand/contract image is
     * placed.
     *
     * @return indent style class as defined in css file
     */
    public String getIndentStyleClass() {
        return indentStyleClass;
    }

    /**
     * Gets the style class name used to define all other columns in the sales
     * record row, except the first column.
     *
     * @return style class as defined in css file
     */
    public String getRowStyleClass() {
        return rowStyleClass;
    }

    /**
     * Gets the image which will represent either the expanded or contracted
     * state of the <code>SalesGroupRecordBean</code>.
     *
     * @return name of image to draw
     */
    public String getExpandContractImage() {
        return expandContractImage;
    }
}