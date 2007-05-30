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

import com.icesoft.icefaces.samples.showcase.util.StyleBean;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * <p>The <code>SalesRecordsManager</code> class is responsible for constructing
 * the list of <code>SalesGroupRecordBean</code> beans which will be bound to a
 * ice:dataTable JSF component.  This construction process is currently static
 * but could easily be configured to work with Hibernate or DAO implementation.
 * </p>
 * <p/>
 * <p>Large data sets could be handle by adding a ice:dataPaginator.
 * Alternatively the dataTable could also be hidden and the dataTable could be
 * added to scrollable ice:panelGroup. </p>
 * <p/>
 * <p>All default style allocation can be changed in this class.  The
 * application /lib/sytle.css file can be edited to change the style properties
 * of the table.</p>
 */
public class SalesRecordsManager {

    private ArrayList inventoryGroupItemBeans;

    private boolean isInit;

    // css style related constants
    public static final String GROUP_INDENT_STYLE_CLASS = "groupRowIndentStyle";
    public static final String GROUP_ROW_STYLE_CLASS = "groupRowStyle";
    public static final String CHILD_INDENT_STYLE_CLASS = "childRowIndentStyle";
    public static final String CHILD_ROW_STYLE_CLASS = "childRowStyle";
    // toggle for expand contract
    public static final String CONTRACT_IMAGE = "tree_nav_top_close_no_siblings.gif";
    public static final String EXPAND_IMAGE = "tree_nav_top_open_no_siblings.gif";

    public SalesRecordsManager() {
        init();
    }

    private void init() {

        // check if manager has been initiated
        if (isInit) {
            return;
        }
        isInit = true;

        // initiate the list
        if (inventoryGroupItemBeans != null) {
            inventoryGroupItemBeans.clear();
        } else {
            inventoryGroupItemBeans = new ArrayList(10);
        }
        
        Application application =
                FacesContext.getCurrentInstance().getApplication();
        StyleBean styleBean =
                ((StyleBean) application.createValueBinding("#{styleBean}").
                        getValue(FacesContext.getCurrentInstance()));

        /**
         * Build the array list group items.  Currently static but could be easily
         * bound to a database.
         */

        // Tuesday's group
        SalesGroupRecordBean salesRecordGroup =
                new SalesGroupRecordBean(GROUP_INDENT_STYLE_CLASS,
                                         GROUP_ROW_STYLE_CLASS,
                                         styleBean,
                                         EXPAND_IMAGE, CONTRACT_IMAGE,
                                         inventoryGroupItemBeans, false);
        salesRecordGroup.setDescription("Tuesday's Items");
        salesRecordGroup.setDate(new GregorianCalendar(2006, 5, 2));

        // add Tuesday's children
        SalesGroupRecordBean childSalesGroup =
                new SalesGroupRecordBean(CHILD_INDENT_STYLE_CLASS,
                                         CHILD_ROW_STYLE_CLASS);
        childSalesGroup.setDescription("2mm Torx screws");
        childSalesGroup.setDate(new GregorianCalendar(2006, 6, 3));
        childSalesGroup.setQuantity(6);
        childSalesGroup.setPrice(0.25);
        salesRecordGroup.addChildSalesGroupRecord(childSalesGroup);
        childSalesGroup =
                new SalesGroupRecordBean(CHILD_INDENT_STYLE_CLASS,
                                         CHILD_ROW_STYLE_CLASS);
        childSalesGroup.setDescription("5mm Torx screws");
        childSalesGroup.setDate(new GregorianCalendar(2006, 7, 2));
        childSalesGroup.setQuantity(6);
        childSalesGroup.setPrice(0.25);
        salesRecordGroup.addChildSalesGroupRecord(childSalesGroup);

        // Wednesday's group
        salesRecordGroup =
                new SalesGroupRecordBean(GROUP_INDENT_STYLE_CLASS,
                                         GROUP_ROW_STYLE_CLASS,
                                         styleBean,
                                         EXPAND_IMAGE, CONTRACT_IMAGE,
                                         inventoryGroupItemBeans, true);
        salesRecordGroup.setDescription("Wednesday's Items");
        salesRecordGroup.setDate(new GregorianCalendar(2006, 6, 2));

        // add Wednesday's children
        childSalesGroup =
                new SalesGroupRecordBean(CHILD_INDENT_STYLE_CLASS,
                                         CHILD_ROW_STYLE_CLASS);
        childSalesGroup.setDescription("Steel Hammer");
        childSalesGroup.setDate(new GregorianCalendar(2006, 6, 6));
        childSalesGroup.setQuantity(1);
        childSalesGroup.setPrice(35.0);
        salesRecordGroup.addChildSalesGroupRecord(childSalesGroup);
        childSalesGroup =
                new SalesGroupRecordBean(CHILD_INDENT_STYLE_CLASS,
                                         CHILD_ROW_STYLE_CLASS);
        childSalesGroup.setDescription("Bag of 10# nails");
        childSalesGroup.setDate(new GregorianCalendar(2006, 6, 6));
        childSalesGroup.setQuantity(2);
        childSalesGroup.setPrice(15.0);
        salesRecordGroup.addChildSalesGroupRecord(childSalesGroup);
        childSalesGroup =
                new SalesGroupRecordBean(CHILD_INDENT_STYLE_CLASS,
                                         CHILD_ROW_STYLE_CLASS);
        childSalesGroup.setDescription("Bag of 15# nails");
        childSalesGroup.setDate(new GregorianCalendar(2006, 6, 6));
        childSalesGroup.setQuantity(5);
        childSalesGroup.setPrice(15.0);
        salesRecordGroup.addChildSalesGroupRecord(childSalesGroup);

        // Thursday's group
        salesRecordGroup =
                new SalesGroupRecordBean(GROUP_INDENT_STYLE_CLASS,
                                         GROUP_ROW_STYLE_CLASS,
                                         styleBean,
                                         EXPAND_IMAGE, CONTRACT_IMAGE,
                                         inventoryGroupItemBeans, false);
        salesRecordGroup.setDescription("Thursday's Items");
        salesRecordGroup.setDate(new GregorianCalendar(2006, 7, 2));

        // add Thursday's children
        childSalesGroup =
                new SalesGroupRecordBean(CHILD_INDENT_STYLE_CLASS,
                                         CHILD_ROW_STYLE_CLASS);
        childSalesGroup.setDescription("B&D Table Saw");
        childSalesGroup.setDate(new GregorianCalendar(2006, 6, 15));
        childSalesGroup.setQuantity(1);
        childSalesGroup.setPrice(310.0);
        salesRecordGroup.addChildSalesGroupRecord(childSalesGroup);

    }

    /**
     * Cleans up the resources used by this class.  This method could be called
     * when a session destroyed event is called.
     */
    public void dispose() {
        isInit = false;
        // clean up the array list
        if (inventoryGroupItemBeans != null) {
            SalesGroupRecordBean tmp;
            ArrayList tmpList;
            for (int i = 0; i < inventoryGroupItemBeans.size(); i++) {
                tmp = (SalesGroupRecordBean) inventoryGroupItemBeans.get(i);
                tmpList = tmp.getChildSalesRecords();
                if (tmpList != null) {
                    tmpList.clear();
                }
            }
            inventoryGroupItemBeans.clear();
        }
    }

    /**
     * Gets the list of SalesGroupRecordBean which will be used by the
     * ice:dataTable component.
     *
     * @return array list of parent SalesGroupRecordBeans
     */
    public ArrayList getSalesGroupRecordBeans() {
        return inventoryGroupItemBeans;
    }
}