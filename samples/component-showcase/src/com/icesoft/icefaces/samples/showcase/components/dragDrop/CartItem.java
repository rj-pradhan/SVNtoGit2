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

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;

import javax.faces.event.ActionEvent;
import org.jboss.seam.annotations.Name;

/**
 * <p>The CartItem class represents a single type of item for sale in the Drag
 * and Drop store demo. The item object maintains available and purchased
 * quantities; these are increased and decreased as they are added or removed
 * from the store.</p>
 */

public class CartItem {
    // item attributes
    private String key = null;
    private String name = "Unknown";
    private double price = 0.0;
    private boolean isPurchased = false;
    private int quantity = 0;
    private int purchasedQuantity = 0;

    // images for drag and drop
    private String image;
    private String imageSmall;

    // types of items
    public static final String ICE_SAILER = "Ice Sailer";
    public static final String ICE_CASTLE = "Ice Castle";
    public static final String ICE_BERG = "Ice Berg";
    public static final String ICE_BREAKER = "Ice Breaker";

    // store reference
    private CartBean cartBean;

    // effect used when an item is added to the cart
    private Effect effect = new Highlight();

    public CartItem() {
    }

    /**
     * Instatiates a new store item.
     *
     * @param cartBean reference to the store object
     * @param name     the name of the item
     * @param price    the price of the item
     * @param quantity the number of items available for purchase
     */
    public CartItem(CartBean cartBean, String name, double price,
                    int quantity) {
        this.cartBean = cartBean;
        this.name = name;
        this.price = price;
        this.quantity = quantity;

        // large image for the store, small image for the cart
        image = "./images/dragDrop/" + name.toLowerCase().replace(' ', '_');
        imageSmall = image + "_small.jpg";
        image += ".jpg";
    }

    /* Getters */
    public String getKey() {
        return (key);
    }

    public String getName() {
        return (name);
    }

    public String getImage() {
        return (image);
    }

    public String getImageSmall() {
        return (imageSmall);
    }

    public double getPrice() {
        return (price);
    }

    public boolean isPurchased() {
        return (isPurchased);
    }

    public int getQuantity() {
        return (quantity);
    }

    public int getPurchasedQuantity() {
        return (purchasedQuantity);
    }

    public double getProductTotalDouble() {
        return purchasedQuantity * price;
    }

    public String getProductTotal() {
        return StoreTable.CURRENCY_FORMAT.format(getProductTotalDouble());
    }

    public Effect getEffect() {
        return effect;
    }

    /* Setters */
    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setPurchased(boolean isPurchased) {
        this.isPurchased = isPurchased;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPurchasedQuantity(int purchasedQuantity) {
        this.purchasedQuantity = purchasedQuantity;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    /**
     * 'Moves' one of the items from the cart to the store. This is achieved by
     * incrementing and decrementing the quantity and purchasedQuantity values,
     * respectively. Used for "Return" button on cart list.
     *
     * @param e the event that fired
     */
    public void returnOne(ActionEvent e) {
        cartBean.removeFromPurchases(this);
    }

    /**
     * Convenience method to decrease the quantity of this item by 1.
     */
    public void decreaseQuantity() {
        if (quantity >= 1) {
            quantity--;
            purchasedQuantity++;
        }
    }

    /**
     * Convenience method to increase the quantity of this item by 1.
     */
    public void increaseQuantity() {
        quantity++;
        purchasedQuantity--;
    }

    /**
     * Method to return this item price in a currency formatted manner: Example:
     * "$4.25".
     *
     * @return String formatted price
     */
    public String getPriceFormatted() {
        return (StoreTable.CURRENCY_FORMAT.format(price));
    }
}
