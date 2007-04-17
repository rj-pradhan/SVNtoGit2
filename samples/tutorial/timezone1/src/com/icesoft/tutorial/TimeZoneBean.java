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

import java.awt.Polygon;
import java.util.Map;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Bean backing the Time Zone application. Also controls time zone information
 * during the session.
 */

public class TimeZoneBean {
    /**
     * The default {@link TimeZone} for this host server.
     */
    private TimeZone serverTimeZone;

    /**
     * {@link DateFormat} used to display the server time.
     */
    private DateFormat serverFormat;

    /**
     * Active {@link TimeZone} displayed at top of UI. Changes when a time zone
     * is selected by pressing one of six commandButtons in UI map.
     */
    private TimeZone selectedTimeZone;

    /**
     * {@link DateFormat} used to display the selected time.
     */
    private DateFormat selectedFormat;

    /**
     * List of all possible {@link TimeZoneWrapper} objects, which must mirror
     * the map UI.
     */
    private ArrayList allTimeZoneList;
    
    /**
     * Polygon objects that represent the Time Zone areas on the map.
     */
    private Polygon hawaii;
    private Polygon alaska;
    private Polygon pacific;
    private Polygon mountain;
    private Polygon central;
    private Polygon eastern;
    
    /**
     * Secondary polygon objects that represent the colored tabs on the map
     */
    private Polygon hawaiiTab;
    private Polygon alaskaTab;
    private Polygon pacificTab;
    private Polygon mountainTab;
    private Polygon centralTab;
    private Polygon easternTab;
            

    /**
     * Constructor initializes time zones.
     */
    public TimeZoneBean() {
        init();
    }

    /**
     * Initializes this TimeZoneBean's properties.
     */
    private void init() {
        serverTimeZone = TimeZone.getDefault();
        serverFormat = buildDateFormatForTimeZone(serverTimeZone);
        selectedTimeZone = TimeZone.getTimeZone(
                "Etc/GMT+0"); // selected time zone set to UTC as default
        selectedFormat = buildDateFormatForTimeZone(selectedTimeZone);

        // Entries in this list are hardcoded to match entries in
        //  the timezone web file, so no parameters can be changed.
        allTimeZoneList = new ArrayList(6);
        allTimeZoneList
                .add(new TimeZoneWrapper("Pacific/Honolulu", "GMTminus10"));
        allTimeZoneList
                .add(new TimeZoneWrapper("America/Anchorage", "GMTminus9"));
        allTimeZoneList
                .add(new TimeZoneWrapper("America/Los_Angeles", "GMTminus8"));
        allTimeZoneList
                .add(new TimeZoneWrapper("America/Phoenix", "GMTminus7"));
        allTimeZoneList
                .add(new TimeZoneWrapper("America/Chicago", "GMTminus6"));
        allTimeZoneList
                .add(new TimeZoneWrapper("America/New_York", "GMTminus5"));
        //Create main polygon objects
        int[] hawaiiXCoords = {0,29,54,58,58,61,61,0};
        int[] hawaiiYCoords = {186,194,208,215,223,243,254,254};
        hawaii = new Polygon(hawaiiXCoords, hawaiiYCoords, hawaiiXCoords.length);
        int[] alaskaXCoords = {117,118,125,132,135,138,141,146,147,157,164,165,
                                        162,156,144,120,75,72,60,45,1,0,0,14};
        int[] alaskaYCoords = {0,4,5,12,12,8,7,14,14,28,31,37,38,41,41,16,16,25,
                                        35,38,55,55,1,0};
        alaska = new Polygon(alaskaXCoords, alaskaYCoords, alaskaXCoords.length);
        int[] pacificXCoords = {176,176,187,187,181,185,191,192,207,207,214,214,
                                        218,222,222,221,221,222,224,230,229,225,
                                        222,219,220,218,214,214,219,107,219,232,
                                        231,230,228,228,229,228,226,226,229,231,
                                        238,233,226,217,205,198,195,197,194,187,
                                        188,189,190,186,169,152,145,158,164,164,
                                        155,141,136,134,132,125,118,118};
        int[] pacificYCoords = {0,3,3,7,7,20,19,25,32,43,47,50,54,59,64,68,67,
                                        71,71,80,86,90,92,89,90,93,95,97,106,
                                        107,112,112,137,139,140,148,149,157,158,
                                        162,163,171,179,179,171,154,148,138,133,
                                        130,130,118,114,103,88,77,61,54,41,41,
                                        37,32,25,7,9,11,11,4,4,0};
        pacific = new Polygon(pacificXCoords, pacificYCoords, pacificXCoords.length);
        int[] mountainXCoords = {177,287,287,268,268,258,259,249,249,254,254,
                                        250,253,250,253,254,250,250,277,277,284,
                                        289,288,290,290,285,286,281,281,272,270,
                                        265,258,256,256,264,263,268,269,275,276,
                                        272,244,216,217,231,218,226,232,239,230,
                                        228,230,230,228,229,231,233,220,220,215,
                                        215,221,220,223,225,231,231,225,222,222,
                                        215,215,208,208,193,192,185,182,182,189,
                                        189};
        int[] mountainYCoords = {0,0,8,8,45,45,41,40,48,48,52,52,63,63,63,69,69,
                                        75,75,80,81,86,98,101,110,116,137,138,
                                        160,160,164,161,161,165,181,190,194,198,
                                        201,204,210,214,233,231,216,208,176,174,
                                        179,179,163,158,153,147,144,140,139,109,
                                        110,106,104,98,93,89,91,93,88,84,77,70,
                                        61,53,48,44,41,30,24,16,17,8,7,2};
        mountain = new Polygon(mountainXCoords, mountainYCoords, mountainXCoords.length);
        int[] centralXCoords = {288,317,314,314,321,325,330,340,336,336,338,346,
                                        348,349,350,351,347,347,357,356,358,357,
                                        352,378,380,381,291,291,269,277,276,269,
                                        264,264,257,257,260,267,270,273,283,282,
                                        287,286,292,291,289,290,284,277,277,252,
                                        252,255,255,252,255,255,251,251,256,256,
                                        270,270,289};
        int[] centralYCoords = {0,0,9,15,15,26,25,30,35,74,86,89,94,111,113,115,
                                        118,129,137,145,155,170,210,242,243,252,
                                        252,241,217,210,203,198,193,189,179,164,
                                        162,163,165,163,162,139,138,116,109,98,
                                        97,85,78,78,72,73,69,69,62,54,52,47,46,
                                        41,43,47,47,9,9};
        central = new Polygon(centralXCoords, centralYCoords, centralXCoords.length);
        int[] easternXCoords = {388,417,446,446,449,447,448,447,450,449,442,438,
                                        431,437,437,446,447,449,449,450,451,441,
                                        429,430,433,433,435,424,419,415,415,463,
                                        464,382,381,353,360,357,359,348,348,353,
                                        349,348,340,337,337,341,373};
        int[] easternYCoords = {0,0,5,10,16,19,21,28,36,41,40,38,44,52,57,57,51,
                                        50,54,66,74,78,81,83,84,91,96,209,215,
                                        216,226,242,255,253,241,208,154,144,136,
                                        127,118,114,110,92,84,78,35,29,9};
        eastern = new Polygon(easternXCoords, easternYCoords, easternXCoords.length);
        //Create secondary polygon objects for the bottom tabs
        int[] hawaiiTabX = {0,61,61,0};
        int[] hawaiiTabY = {242,242,254,254};
        hawaiiTab = new Polygon(hawaiiTabX, hawaiiTabY, hawaiiTabX.length);
        int[] alaskaTabX = {61,131,131,61};
        int[] alaskaTabY = {242,242,254,254};
        alaskaTab = new Polygon(alaskaTabX, alaskaTabY, alaskaTabX.length);
        int[] pacificTabX = {131,210,210,131};
        int[] pacificTabY = {242,242,254,254};
        pacificTab = new Polygon(pacificTabX, pacificTabY, pacificTabX.length);
        int[] mountainTabX = {210,292,292,210};
        int[] mountainTabY = {242,242,254,254};
        mountainTab = new Polygon(mountainTabX, mountainTabY, mountainTabX.length);
        int[] centralTabX = {292,381,381,292};
        int[] centralTabY = {242,242,254,254};
        centralTab = new Polygon(centralTabX, centralTabY, centralTabX.length);
        int[] easternTabX = {381,464,464,381};
        int[] easternTabY = {242,242,254,254};
        easternTab = new Polygon(easternTabX, easternTabY, easternTabX.length);
    }

    /**
     * Gets server time.
     *
     * @return Server time.
     */
    public String getServerTime() {
        return formatCurrentTime(serverFormat);
    }

    /**
     * Gets server time zone display name.
     *
     * @return Server time zone display name.
     */
    public String getServerTimeZoneName() {
        return displayNameTokenizer(serverTimeZone.getDisplayName());
    }

    /**
     * Gets selected time zone time. This is the time zone selected by one of
     * six commandButtons from the map in the UI.
     *
     * @return selectedTimeZone time.
     */
    public String getSelectedTime() {
        return formatCurrentTime(selectedFormat);
    }

    /**
     * Gets selected time zone display name.
     *
     * @return selectedTimeZone display name.
     */
    public String getSelectedTimeZoneName() {
        synchronized (TimeZone.class) {
            return displayNameTokenizer(selectedTimeZone.getDisplayName());
        }
    }

    /**
     * Extracts the first word from a TimeZone displayName.
     *
     * @param displayName A TimeZone displayName.
     * @return String The first word from the TimeZone displayName.
     */
    public static String displayNameTokenizer(String displayName) {
        if (displayName == null) {
            displayName = "";
        } else {
            int firstSpace = displayName.indexOf(' ');
            if (firstSpace != -1) {
                displayName = displayName.substring(0, firstSpace);
            }
        }
        return displayName;
    }

    public static DateFormat buildDateFormatForTimeZone(TimeZone timeZone) {
        SimpleDateFormat currentFormat = new SimpleDateFormat("EEE, HH:mm:ss");
        Calendar currentZoneCal = Calendar.getInstance(timeZone);
        currentFormat.setCalendar(currentZoneCal);
        currentFormat.setTimeZone(timeZone);
        return currentFormat;
    }

    public static String formatCurrentTime(DateFormat dateFormat) {
        Calendar cal = dateFormat.getCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        return dateFormat.format(cal.getTime());
    }

    /**
     * Each TimeZoneWrapper has an id of a component in the UI that corresponds
     * to its time zone.  By this, if an event comes from a component in the web
     * page, then this will return the relevant TimeZoneWrapper.
     *
     * @param componentId Id of component in UI
     * @return TimeZoneWrapper
     */
    private TimeZoneWrapper getTimeZoneWrapperByComponentId(
            String componentId) {
        for (int i = 0; i < allTimeZoneList.size(); i++) {
            TimeZoneWrapper tzw = (TimeZoneWrapper) allTimeZoneList.get(i);
            if (tzw.isRelevantComponentId(componentId)) {
                return tzw;
            }
        }
        return null;
    }

    //
    // Implicit interfaces as defined by the callbacks in the web files
    //

    /**
     * Listens to client input from the commandButton in the UI map and sets the
     * selected time zone.
     *
     * @param event ActionEvent.
     */
    public void listen(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        String clientId = event.getComponent().getClientId(context);
        Map requestParams = context.getExternalContext().getRequestParameterMap();
        int x = new Integer((String)requestParams.get(clientId + ".x")).intValue();
        int y = new Integer((String)requestParams.get(clientId + ".y")).intValue();
        if((hawaii.contains(x,y)) || (hawaiiTab.contains(x,y))){
            TimeZoneWrapper tzw = getTimeZoneWrapperByComponentId("GMTminus10");
            selectedTimeZone = TimeZone.getTimeZone(tzw.getId());
            selectedFormat = buildDateFormatForTimeZone(selectedTimeZone);
        }
        else if((alaska.contains(x,y)) || (alaskaTab.contains(x,y))){
            TimeZoneWrapper tzw = getTimeZoneWrapperByComponentId("GMTminus9");
            selectedTimeZone = TimeZone.getTimeZone(tzw.getId());
            selectedFormat = buildDateFormatForTimeZone(selectedTimeZone);
        }
        else if((pacific.contains(x,y))||(pacificTab.contains(x,y))){
            TimeZoneWrapper tzw = getTimeZoneWrapperByComponentId("GMTminus8");
            selectedTimeZone = TimeZone.getTimeZone(tzw.getId());
            selectedFormat = buildDateFormatForTimeZone(selectedTimeZone);
        }
        else if((mountain.contains(x,y))||(mountainTab.contains(x,y))){
            TimeZoneWrapper tzw = getTimeZoneWrapperByComponentId("GMTminus7");
            selectedTimeZone = TimeZone.getTimeZone(tzw.getId());
            selectedFormat = buildDateFormatForTimeZone(selectedTimeZone);
        }
        else if((central.contains(x,y))||(centralTab.contains(x,y))){
            TimeZoneWrapper tzw = getTimeZoneWrapperByComponentId("GMTminus6");
            selectedTimeZone = TimeZone.getTimeZone(tzw.getId());
            selectedFormat = buildDateFormatForTimeZone(selectedTimeZone);
        }
        else if((eastern.contains(x,y)) || (easternTab.contains(x,y))){
            TimeZoneWrapper tzw = getTimeZoneWrapperByComponentId("GMTminus5");
            selectedTimeZone = TimeZone.getTimeZone(tzw.getId());
            selectedFormat = buildDateFormatForTimeZone(selectedTimeZone);
        }
        else{
            //Do nothing
        }
        
        
    }
} // End of TimeZoneBean class
