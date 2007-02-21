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

package com.icesoft.icefaces.samples.showcase.layoutPanels.seriesPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>The Cashier Class is used to back the List (panelSeries) Component
 * example.</p>
 */
public class Cashier {

    // items to purchase
    private List itemTable;

    /**
     * Gets the list of purchases.
     *
     * @return the list of purchased items
     */
    public List getShoppingList() {
        List tmpList = new ArrayList();
        for (int i = 0; i < itemTable.size(); i++) {
            if (Integer.parseInt(((Item) itemTable.get(i)).getQuantity()) > 0)
                tmpList.add(itemTable.get(i));
        }
        return tmpList;
    }

    /**
     * Gets the total price (unformatted).
     *
     * @return the total price
     */
    public double getTotal() {
        double total = 0.00;
        for (int i = 0; i < itemTable.size(); i++) {

            total += ((Item) itemTable.get(i)).getSubtotal();
        }
        return total;
    }

    /**
     * Gets the item table.
     *
     * @return the item table.
     */
    public List getItemTable() {
        return itemTable;
    }

    /**
     * Sets the item table.
     *
     * @param itemTable the item table
     */
    public void setItemTable(List itemTable) {
        this.itemTable = itemTable;
    }

    /**
     * Starts initialization routine.
     */
    public Cashier() {
        init();
    }

    /**
     * Create the items to be used for the list.
     */
    private void init() {
        itemTable = new ArrayList();

        itemTable.add(
                new Item("Ice Berg", "B011", "Berg", 1.89, "0", false, "0"));
        itemTable.add(
                new Item("Ice Castle", "Z023", "Castle", 0.89, "0", false,
                         "0"));
        itemTable.add(
                new Item("Ice Sailer", "M001", "Sailer", 4.99, "0", false,
                         "0"));
        itemTable.add(
                new Item("Ice Breaker", "D023", "Breaker", 9.99, "0", true,
                         "0"));
    }
}