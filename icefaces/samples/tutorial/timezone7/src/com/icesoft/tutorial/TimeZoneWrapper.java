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

package com.icesoft.tutorial;

/**
 * Bean holding time zone specific information.
 * Checking a selectBooleanCheckbox in the UI will cause instances of this class to populate the checkedTimeZoneList ArrayList in <code>TimeZoneBean</code>.
 * That ArrayList is used to create a DataTable of checked time zones in the UI.
 */

import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class TimeZoneWrapper {
    /* Variables */
    /**
     * {@link TimeZone} id used to identify the time zone. This id can be passed
     * to <code>TimeZone.getTimeZone(String)</code>, to get the appropriate
     * {@link TimeZone} object.
     */
    private String id;

    /**
     * The component id of the commandButton, in the map UI, corresponding to
     * this time zone.
     */
    private String mapCommandButtonId;

    /**
     * The component id of the selectBooleanCheckbox, under the map UI,
     * corresponding to this time zone.
     */
    private String checkboxId;

    /**
     * Whether this {@link TimeZone} has been checked, and so should be
     * displayed in the bottom table UI.
     */
    private boolean currentlyShowing;

    /**
     * A cached {@link DateFormat} used to describe what the time is for this
     * {@link TimeZone}
     */
    private DateFormat dateFormat;
    
    /**
     * The abbreviaton for the {@link TimeZone}.
     */
    private String abbreviation;
    
    /**
     * The background color used for the UI label of each
     * selectBooleanCheckbox component
     */
    private String backgroundColor;

    /* Constructors */
    /**
     * @param id      id used to identify the time zone.
     * @param imgPath Path to the image to represent this time zone.
     * @param mapId   map button component id in web page
     * @param checkId checkbox component id in web page
     */
    public TimeZoneWrapper(String id, String mapId, String checkId, 
                                String abbreviation, String backgroundColor) {
        this.id = id;
        this.mapCommandButtonId = mapId;
        this.checkboxId = checkId;
        this.abbreviation = abbreviation;
        this.backgroundColor = backgroundColor;
        this.currentlyShowing = false;
        this.dateFormat = TimeZoneBean.buildDateFormatForTimeZone(
                TimeZone.getTimeZone(id));
    }

    /* Getters */
    /**
     * Gets the name of this time zone to be displayed in the UI.
     *
     * @return String
     */
    public String getDisplayName() {
        String displayName = null;
        TimeZone timeZone = TimeZone.getTimeZone(id);
        synchronized (TimeZone.class) {
            displayName = TimeZoneBean.displayNameTokenizer(
                    timeZone.getDisplayName());
        }
        return displayName;
    }

    /**
     * Gets the {@link TimeZone} id used to identify this time zone in the Java
     * code.
     *
     * @return String
     */
    public String getId() {
        return id;
    }

    /**
     * Gets the dynamic time through the <code>formatCurrentTime</code> method
     * in the <code>TimeZoneBean</code>.
     *
     * @return String
     */
    public String getTime() {
        return TimeZoneBean.formatCurrentTime(dateFormat);
    }

    /**
     * Gets whether or not this time zone uses DayLight time.
     *
     * @return Returns the useDaylightTime.
     */
    public String getUseDaylightTime() {
        TimeZone timeZone = TimeZone.getTimeZone(id);
        if (timeZone.useDaylightTime()) {
            return "Yes";
        }

        return "No";
    }

    /**
     * Gets the state of DayLight Time in this time zone.
     *
     * @return Returns the inDaylightTime.
     */
    public String getInDaylightTime() {
        TimeZone timeZone = TimeZone.getTimeZone(id);
        Calendar cal = Calendar.getInstance(timeZone);
        if (timeZone.inDaylightTime(cal.getTime())) {
            return "Yes";
        }

        return "No";
    }

    /**
     * Gets the {@link TimeZone} location used to identify this time zone.
     *
     * @return String
     */
    public String getLocation() {
        return id;
    }

    /**
     * Ascertains whether mapCommandButtonId or checkboxId are a part of
     * componentId. componentId might be a fully qualified id, with a prefix
     * corresponding to container component(s).
     *
     * @param componentId Id of some component that may be related to this time
     *                    zone
     */
    public boolean isRelevantComponentId(String componentId) {
        boolean relevant = (componentId.endsWith(mapCommandButtonId) ||
                            componentId.endsWith(checkboxId));
        return relevant;
    }

    /**
     * Gets the component id of the commandButton, in the map UI, corresponding
     * to this time zone.
     */
    public String getMapCommandButtonId() {
        return mapCommandButtonId;
    }

    /**
     * Gets the component id of the selectBooleanCheckbox, under the map UI,
     * corresponding to this time zone.
     */
    public String getCheckboxId() {
        return checkboxId;
    }

    /**
     * Gets whether this time zone has is checked, and so should be displayed in
     * the bottom table UI.
     *
     * @return boolean
     */
    public boolean getCurrentlyShowing() {
        return currentlyShowing;
    }

    /**
     * Sets whether this time zone has been checked, and so should be displayed
     * in the bottom table UI.
     *
     * @param showing If it has been checked, and so should be showed
     */
    public void setCurrentlyShowing(boolean showing) {
        currentlyShowing = showing;
    }
    
    /**
     * Gets the abbreviation for the TimeZone to be displayed in the UI.
     */
    public String getAbbreviation(){
        return abbreviation;
    }
    
    /**
     * Gets the backgroundColor for the corresponding time zone.
     */
    public String getBackgroundColor(){
        return backgroundColor;
    }
    
} // End of TimeZoneWrapper class
