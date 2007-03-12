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

package com.icesoft.icefaces.samples.showcase.components.calendar;

import java.util.Date;

/**
 * <p>The SelectInputDateBean Class is used to store the selected dates from the
 * selectinputdate components.</p>
 */
public class SelectInputDateBean {
    /**
     * Variables to store the selected dates.
     */
    private Date date1 = null;
    private Date date2 = null;

    /**
     * Gets the first selected date.
     *
     * @return the first selected date
     */
    public Date getDate1() {
        return date1;
    }

    /**
     * Sets the first selected date.
     *
     * @param date the first selected date
     */
    public void setDate1(Date date) {
        date1 = date;
    }

    /**
     * Gets the 2nd selected date.
     *
     * @return the 2nd selected date
     */
    public Date getDate2() {
        return date2;
    }

    /**
     * Sets the 2nd selected date.
     *
     * @param date the 2nd selected date
     */
    public void setDate2(Date date) {
        date2 = date;
    }
}