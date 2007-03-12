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

import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.dragdrop.DragEvent;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import org.jboss.seam.annotations.Name;

/**
 * <p>The CartBean class is used as a bean for each user to maintain their store
 * and cart list and available items in the Drag and Drop store demo. It also
 * handles the store's drag and drop functionality via listeners.</p>
 */
@Name("cart")
public class CartBean {
    // store and cart data structures
    private ArrayList purchasedList = new ArrayList(0);
    private StoreTable storeTable = new StoreTable();

    // random prices
    private Random random = new Random(System.currentTimeMillis());

    // effects
    private Effect cartEffect;

    /**
     * Loads the default set of store objects with random prices.
     */
    public CartBean() {
        // instantiate the store objects
        loadDefaults();
    }

    /**
     * Gets a list representation of the Store.
     *
     * @return list representation of the store
     */
    public ArrayList getStoreTableAsList() {
        return (Collections.list(storeTable.elements()));
    }

    /**
     * Gets the list of purchases.
     *
     * @return the list of purchases
     */
    public ArrayList getPurchasedList() {
        return (purchasedList);
    }

    /**
     * Gets the store table.
     *
     * @return the store table
     */
    public Hashtable getStoreTable() {
        return (storeTable);
    }

    /**
     * Gets the total price formatted correctly as currency.
     *
     * @return the total price
     */
    public String getTotalPriceFormatted() {
        double price = 0.0;

        // Loop through the item list and calculate the total price 
        for (int i = 0; i < purchasedList.size(); i++) {
            price += ((CartItem) purchasedList.get(i)).getProductTotalDouble();
        }

        return (StoreTable.CURRENCY_FORMAT.format(price));
    }

    /**
     * Determines amount of purchased items.
     *
     * @return the number purchased.
     */
    public int getPurchasedCount() {
        Iterator i = purchasedList.iterator();
        int count = 0;

        while (i.hasNext()) {
            count += ((CartItem) i.next()).getPurchasedQuantity();
        }

        return (count);
    }

    /**
     * Determines if there are plural items.
     *
     * @return correct grammar for item/items
     */
    public String getItemString() {
        if (getPurchasedCount() == 1) {
            return ("item");
        }
        return ("items");
    }

    /**
     * Sets the list of purchases.
     *
     * @param purchasedList the list of purchases
     */
    public void setPurchasedList(ArrayList purchasedList) {
        this.purchasedList = purchasedList;
    }

    /**
     * Sets the Store data.
     *
     * @param storeTable the store data
     */
    public void setStoreTable(StoreTable storeTable) {
        this.storeTable = storeTable;
    }

    /**
     * Generates a list of default items in the store.
     */
    protected void loadDefaults() {
        storeTable.addItem(new CartItem(this, CartItem.ICE_BERG, Math.floor(
                (1 + random.nextDouble() * random.nextInt(19)) * 100) / 100, 1 +
                                                                             random.nextInt(
                                                                                     59)));
        storeTable.addItem(new CartItem(this, CartItem.ICE_SAILER, Math.floor(
                (1 + random.nextDouble() * random.nextInt(19)) * 100) / 100, 1 +
                                                                             random.nextInt(
                                                                                     59)));
        storeTable.addItem(new CartItem(this, CartItem.ICE_CASTLE, Math.floor(
                (1 + random.nextDouble() * random.nextInt(19)) * 100) / 100, 1 +
                                                                             random.nextInt(
                                                                                     59)));
        storeTable.addItem(new CartItem(this, CartItem.ICE_BREAKER, Math.floor(
                (1 + random.nextDouble() * random.nextInt(19)) * 100) / 100, 1 +
                                                                             random.nextInt(
                                                                                     59)));
    }

    /**
     * Method used to handle the drag and drop events from the page An item can
     * be dragged from the store to the cart to purchase it.
     *
     * @param DragEvent event fired
     * @return void
     */
    public void cartListener(DragEvent event) {
        if (event.getEventType() == DndEvent.HOVER_START) {
            String targetId = event.getTargetClientId();
            if ((targetId != null) &&
                (targetId.indexOf("cartDropTarget") != -1)) {
                cartEffect = new Highlight();

            }
        }
        // only deal with DROPPED event types
        if (event.getEventType() == DndEvent.DROPPED) {
            String targetId = event.getTargetClientId();
            if ((targetId != null) &&
                (targetId.indexOf("cartDropTarget") != -1)) {
                String value = ((HtmlPanelGroup) event.getComponent())
                        .getDragValue().toString();

                // get the actual dropped item from the store table
                CartItem dragged = storeTable
                        .getItem(value.substring(0, value.length() - 1));

                // ensure the dropped target was the cart
                if (targetId.endsWith("cartDropTarget")) {
                    // attempt to purchase the item (which checks quantity), and add it if valid
                    if (storeTable.purchaseItem(dragged)) {
                        addToPurchases(dragged);
                    }

                }
            }
        }
    }

    /**
     * Increases the purchased quantity of an item by 1. Adds the item to the
     * cart list if it was not already there.
     *
     * @param dragged
     */
    private void addToPurchases(CartItem dragged) {
        if (purchasedList.contains(dragged)) {
            //duplicate
            int i = purchasedList.indexOf(dragged);
            CartItem existing = (CartItem) purchasedList.get(i);
            existing.setEffect(new Highlight());
        } else {
            purchasedList.add(dragged);
        }
    }

    /**
     * Reduces the purchased quantity of an item by 1. Removes the item from the
     * cart list if the quantity reaches 0.
     *
     * @param dragged
     */
    public void removeFromPurchases(CartItem dragged) {
        dragged.increaseQuantity();

        // if it is the last one remaining, it is removed from the display
        if (dragged.getPurchasedQuantity() == 0) {
            purchasedList.remove(dragged);
        }
    }

    /**
     * Gets effect for shopping cart.
     *
     * @return the effect
     */
    public Effect getCartEffect() {
        return cartEffect;
    }

    /**
     * Sets effect for shopping cart.
     *
     * @param cartEffect the effect
     */
    public void setCartEffect(Effect cartEffect) {
        this.cartEffect = cartEffect;
    }
}