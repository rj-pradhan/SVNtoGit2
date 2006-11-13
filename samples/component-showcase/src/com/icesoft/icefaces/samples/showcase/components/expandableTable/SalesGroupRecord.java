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

import java.util.ArrayList;

/**
 * <p>The <code>SalesGroupRecord</code> is responsible for storing a list of
 * child <code>SalesRecords</code>.  It is this list of child records which make
 * up an expandable node in the dataTable.  When a node is expanded its'
 * children are added to the dataTable.</p>
 */
public class SalesGroupRecord extends SalesRecord {

    // list of child SalesRecords.
    protected ArrayList childSalesRecords = new ArrayList(5);

    /**
     * Gets the list of child sales records.
     *
     * @return list of child sales records.
     */
    public ArrayList getChildSalesRecords() {
        return childSalesRecords;
    }

    /**
     * Return the calculated total of this sales group.  That is the sum of all
     * the child SalesGroup Records if any.
     *
     * @return total dollar amount of price * quantity.
     */
    public int getQuantity() {
        if (childSalesRecords != null && childSalesRecords.size() > 0) {
            // sum up the group items.
            int total = 0;
            SalesGroupRecord tmp;
            for (int i = 0; i < childSalesRecords.size(); i++) {
                tmp = (SalesGroupRecord) childSalesRecords.get(i);
                total += tmp.getQuantity();
            }
            return total;
        } else {
            return super.getQuantity();
        }
    }

    /**
     * Return the calculated total of this sales group.  That is the sum of all
     * the child SalesGroup Records if any.
     *
     * @return total dollar amount of price * quantity.
     */
    public double getTotal() {
        if (childSalesRecords != null && childSalesRecords.size() > 0) {
            // sum up the group itmes.
            double total = 0.0;
            SalesGroupRecord tmp;
            for (int i = 0; i < childSalesRecords.size(); i++) {
                tmp = (SalesGroupRecord) childSalesRecords.get(i);
                total += tmp.getTotal();
            }
            return total;
        } else {
            return super.getTotal();
        }
    }

    /**
     * Return sales group price.  If the group has no children then its price is
     * returned, otherwise the sum of all child prices is returned.
     *
     * @return total price of child records if any, unit sales price otherwise.
     */
    public double getPrice() {
        if (childSalesRecords != null && childSalesRecords.size() > 0) {
            // sum up the group itmes.
            return getTotal();
        } else {
            return super.getPrice();
        }
    }
}