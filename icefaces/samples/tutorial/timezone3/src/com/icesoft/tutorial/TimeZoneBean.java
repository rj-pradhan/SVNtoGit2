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

import com.icesoft.faces.async.render.IntervalRenderer;
import com.icesoft.faces.async.render.RenderManager;
import com.icesoft.faces.async.render.Renderable;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.RenderingException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Bean backing the Time Zone application. This bean uses the RenderManager to
 * update state at a specified interval. Also controls time zone information
 * during the session.
 */

public class TimeZoneBean implements Renderable {
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
     * Time interval, in milliseconds, between renders.
     */
    private final int renderInterval = 1000;

    /**
     * The state associated with the current user that can be used for
     * server-initiated render calls.
     */
    private PersistentFacesState state;

    /**
     * A named render group that can be shared by all TimeZoneBeans for
     * server-initiated render calls.  Setting the interval determines the
     * frequency of the render call.
     */
    private IntervalRenderer clock;

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

        state = PersistentFacesState.getInstance();
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

    /**
     * Used to create, setup, and start an IntervalRenderer from the passed
     * renderManager This is used in conjunction with faces-config.xml to allow
     * the same single render manager to be set in all TimeZoneBeans
     *
     * @param renderManager RenderManager to get the IntervalRenderer from
     */
    public void setRenderManager(RenderManager renderManager) {
        clock = renderManager.getIntervalRenderer("clock");
        clock.setInterval(renderInterval);
        clock.add(this);
        clock.requestRender();
    }

    /**
     * Gets RenderManager
     *
     * @return RenderManager null
     */
    public RenderManager getRenderManager() {
        return null;
    }

    //
    // Renderable interface
    //

    /**
     * Gets the current instance of PersistentFacesState
     *
     * @return PersistentFacesState state
     */
    public PersistentFacesState getState() {
        return state;
    }

    /**
     * Callback to inform us that there was an Exception while rendering
     *
     * @param renderingException
     */
    public void renderingException(RenderingException renderingException) {
        if (clock != null) {
            clock.remove(this);
            clock = null;
        }
    }

    //
    // Implicit interfaces as defined by the callbacks in the web files
    //

    /**
     * Listens to client input from commandButtons in the UI map and sets the
     * selected time zone.
     *
     * @param event ActionEvent.
     */
    public void listen(ActionEvent event) {
        UIComponent comp = event.getComponent();
        FacesContext context = FacesContext.getCurrentInstance();
        String componentId = comp.getClientId(context);
        TimeZoneWrapper tzw = getTimeZoneWrapperByComponentId(componentId);
        if (tzw != null) {
            selectedTimeZone = TimeZone.getTimeZone(tzw.getId());
            selectedFormat = buildDateFormatForTimeZone(selectedTimeZone);
        }
    }
} // End of TimeZoneBean class
