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

import javax.faces.event.ActionEvent;

/**
 * <p>The Item class is used to create objects for use in the panelSeries
 * example.</p>
 */
public class Item {

    private String name;
    private String serialNo;
    private String category;
    private double unitPrice;
    private String quantity;
    private boolean discount;
    private String disValue;

    public Item(String n, String s, String c, double u, String q, boolean dis,
                String d) {
        name = n;
        serialNo = s;
        category = c;
        unitPrice = u;
        quantity = q;
        discount = dis;
        disValue = d;
    }

    public boolean isQuanMinusDisabled() {
        return (Integer.parseInt(this.quantity) <= 0);
    }

    public boolean isDisMinusDisabled() {
        return ((!discount) || (Integer.parseInt(this.disValue) <= 0));
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isDiscount() {
        return discount;
    }

    public void setDiscount(boolean discount) {
        this.discount = discount;
    }

    public String getDisValue() {
        return disValue;
    }

    public void setDisValue(String disValue) {
        this.disValue = disValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }


    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getSubtotal() {
        int quantity = Integer.parseInt(this.quantity);
        double dis = 0.00;
        if (discount) {
            dis = Double.parseDouble(this.disValue);

        }

        return unitPrice * quantity * (1 - dis / 100);

    }

    public void incQuan(ActionEvent event) {
        quantity = Integer.toString(Integer.parseInt(quantity) + 1);
    }

    public void decQuan(ActionEvent event) {
        quantity = Integer.toString(Integer.parseInt(quantity) - 1);
    }

    public void incDis(ActionEvent event) {
        disValue = Integer.toString(Integer.parseInt(disValue) + 1);
    }

    public void decDis(ActionEvent event) {
        disValue = Integer.toString(Integer.parseInt(disValue) - 1);
    }
}