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

package com.icesoft.icefaces.samples.showcase.components.dragDrop;

import java.text.NumberFormat;
import java.util.Hashtable;

/**
 * <p>The StoreTable class is used to represent the available items for sale in
 * the Drag & Drop demo. This class also handles adding and removing items.</p>
 *
 * @see com.icesoft.icefaces.samples.showcase.components.dragDrop.CartBean ,
 *      CartItem
 */
public class StoreTable extends Hashtable {
    // default size
    private static final int DEFAULT_SIZE = 4;

    // currency formatting
    public static final NumberFormat CURRENCY_FORMAT =
            NumberFormat.getCurrencyInstance();

    // store attributes
    private int id = 0;
    private String skuBase = "#skuITEM";

    /**
     * New store table with default initial capacity.
     */
    public StoreTable() {
        super(DEFAULT_SIZE);
    }

    /**
     * New store table with specified initial capacity.
     *
     * @param initialCapacity the initial capacity
     */
    public StoreTable(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Method to add the passed item to this hashtable. A new key is generated
     * and assigned internally.
     *
     * @param CartItem to add
     * @return String resulting key
     */
    public String addItem(CartItem toAdd) {
        toAdd.setKey(getNextKey());
        put(toAdd.getKey(), toAdd);

        return (toAdd.getKey());
    }

    /**
     * Method to purchase the passed item. The quantity of the item will be
     * decreased (if possible).
     *
     * @param CartItem to purchase
     * @return boolean true if the item can be purchased
     */
    public boolean purchaseItem(CartItem item) {
        // Check for valid quantity before purchasing
        if (item.getQuantity() >= 1) {
            item.decreaseQuantity();
            return (true);
        }

        return (false);
    }

    /**
     * Method to "return" the passed item to the store. The quantity of the item
     * will be increased.
     *
     * @param CartItem to return
     * @return void
     */
    public void returnItem(CartItem item) {
        item.increaseQuantity();
    }

    /**
     * Convenience method to get a specified CartItem based on the passed key.
     *
     * @param String key of desired item
     * @return CartItem from store table (or null if no matches are found)
     */
    public CartItem getItem(String key) {
        return ((CartItem) get(key));
    }

    /**
     * Method to generate and return the next item key
     *
     * @param void
     * @return String generated key
     */
    private String getNextKey() {
        id++;
        return (skuBase + id);
    }
}