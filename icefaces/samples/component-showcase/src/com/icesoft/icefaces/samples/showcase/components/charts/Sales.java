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

package com.icesoft.icefaces.samples.showcase.components.charts;

import java.util.HashMap;
import java.util.Map;

public class Sales {

    private String year;
    private String product;
    private int price;

    public Sales(int price, String product, String year) {
        this.price = price;
        this.product = product;
        this.year = year;
    }

    private static Map sales;

    public static Map getSales() {
        sales = new HashMap();
        Sales[] sales2001 = {new Sales(15, "Ice Sailor", "2001"),
                             new Sales(15, "Ice Sailor", "2001"),
                             new Sales(15, "Ice Sailor", "2001"),
                             new Sales(22, "Ice Skate", "2001")};

        Sales[] sales2002 = {new Sales(79, "Ice Car", "2002"),
                             new Sales(63, "Icebreaker", "2002"),
                             new Sales(22, "Ice Skate", "2002"),
                             new Sales(22, "Ice Skate", "2002"),
                             new Sales(22, "Ice Skate", "2002")};

        Sales[] sales2003 = {new Sales(22, "Ice Skate", "2003"),
                             new Sales(15, "Ice Sailor", "2003")};

        Sales[] sales2004 = {new Sales(79, "Ice Car", "2004"),
                             new Sales(22, "Ice Skate", "2004"),
                             new Sales(22, "Ice Skate", "2004"),
                             new Sales(15, "Ice Sailor", "2004")};

        sales.put("2001", sales2001);
        sales.put("2002", sales2002);
        sales.put("2003", sales2003);
        sales.put("2004", sales2004);

        return sales;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
