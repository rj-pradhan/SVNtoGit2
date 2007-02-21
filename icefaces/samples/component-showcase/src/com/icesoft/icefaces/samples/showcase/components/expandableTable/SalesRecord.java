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

import java.util.GregorianCalendar;

/**
 * <p>The <code>SalesRecord</code> class contains the base information for an
 * inventory entry in a data table.  This class is meant to represent a model
 * and should only contain base inventory data</p>
 * <p/>
 * <p>The class instance variables are a direct map from the original ascii
 * expandable table specification. </p>
 */
public class SalesRecord {

    // simple list of sales records.
    protected String description = "";
    protected GregorianCalendar date;
    protected int quantity;
    protected double price;

    /**
     * Gets the description of the record.
     *
     * @return description of the record
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the record.
     *
     * @param description of the record
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the calendar date.
     *
     * @return the calendar date
     */
    public GregorianCalendar getDate() {
        return date;
    }

    /**
     * Returns the sales record date in dd/mm/yyy format.
     *
     * @return sales record date
     */
    public String getDateString() {
        return date.get(GregorianCalendar.DAY_OF_MONTH) + "/" +
               date.get(GregorianCalendar.MONTH) + "/" +
               date.get(GregorianCalendar.YEAR);
    }

    /**
     * Sets the date of the Calendar.
     *
     * @param date
     */
    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    /**
     * Gets the quantity.
     *
     * @return the quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity.
     *
     * @param quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Returns the unit price of this sales record.
     *
     * @return unit price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the unit price of this sales record.
     *
     * @param price unit price
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Return the calculated total of this sales record, price * quantity.
     *
     * @return total dollar amount of price * quantity.
     */
    public double getTotal() {
        return price * quantity;
    }
}