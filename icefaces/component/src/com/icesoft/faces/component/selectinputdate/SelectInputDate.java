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

package com.icesoft.faces.component.selectinputdate;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.DOMResponseWriter;
import com.icesoft.faces.util.CoreUtils;

import org.krysalis.jcharts.properties.LegendProperties;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SelectInputDate is a JSF component class that represents an ICEfaces input
 * date selector.
 * <p/>
 * The component extends the ICEfaces extended HTMLPanelGroup.
 * <p/>
 * By default the component is rendered by the "com.icesoft.faces.Calendar"
 * renderer type.
 *
 * @author Greg McCleary
 * @version 1.1
 */
public class SelectInputDate
        extends HtmlInputText {
    /**
     * The component type.
     */
    public static final String COMPONENT_TYPE =
            "com.icesoft.faces.SelectInputDate";
    /**
     * The component family.
     */
    public static final String COMPONENT_FAMILY = "javax.faces.Input";
    /**
     * The default renderer type.
     */
    private static final String DEFAULT_RENDERER_TYPE =
            "com.icesoft.faces.Calendar";
    /**
     * The default date format for the popup input text child component.
     */
    public static final String DEFAULT_POPUP_DATE_FORMAT = "MM/dd/yyyy";

    // style
    private String style = null;

    private String styleClass = null;

    /**
     * The current renderAsPopup state.
     */
    private Boolean _renderAsPopup = null;
    /**
     * The current directory path of the images used by the component.
     */
    private String _imageDir = null;
    /**
     * The current name of the move next image
     */
    private String _moveNextImage = null;
    /**
     * The current name of the move previous image.
     */
    private String _movePreviousImage = null;
    /**
     * The current name of the open popup image.
     */
    private String _openPopupImage = null;
    /**
     * The current name of the close popup image.
     */
    private String _closePopupImage = null;

    /**
     * The current date format used for the input text child of the component.
     * <p>Only applies when component is used in popup mode.
     */
    private String _popupDateFormat = null;

    /**
     * The current popup state.
     */
    private List showPopup = new ArrayList();
    /**
     * The current navigation event state.
     */
    private boolean navEvent = false;
    /**
     * The current navigation date of the component.
     */
    private Date navDate = null;

    // declare default style classes
    /**
     * The default directory where the images used by this component can be
     * found. This directory and its contents are included in the icefaces.jar
     * file.
     */
    private final String DEFAULT_IMAGEDIR = "/xmlhttp/css/xp/css-images/";
    /**
     * The default move next image name.
     */
    private final String DEFAULT_MOVENEXT = "cal_arrow_right.gif";
    /**
     * The default move previous image name.
     */
    private final String DEFAULT_MOVEPREV = "cal_arrow_left.gif";
    /**
     * The default open popup image name.
     */
    private final String DEFAULT_OPENPOPUP = "cal_button.gif";
    /**
     * The default close popup image name.
     */
    private final String DEFAULT_CLOSEPOPUP = "cal_off.gif";
    /**
     * The default date format used by this component.
     */
    private DateFormat myDateFormat =
            new SimpleDateFormat(DEFAULT_POPUP_DATE_FORMAT);

    /**
     * Creates an instance and sets renderer type to "com.icesoft.faces.Calendar".
     */
    public SelectInputDate() {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }

    public void encodeBegin(FacesContext context) throws IOException {
        super.encodeBegin(context);
        buildHeighLightMap();
    }

    /**
     * <p/>
     * CSS style attribute. </p>
     *
     * @return style
     */

    public String getStyle() {
        if (this.style != null) {
            return this.style;
        }
        ValueBinding _vb = getValueBinding("style");


        if (_vb != null) {
            return (String) _vb.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * <p/>
     * CSS style attribute. </p>
     *
     * @param style
     * @see #getStyle()
     */
    public void setStyle(String style) {
        this.style = style;
    }


    /**
     * Formats the given date using the default date format MM/dd/yyyy.
     *
     * @param date
     * @return the formatted date as a String.
     */
    public String formatDate(Date date) {
        if (date != null) {
            return myDateFormat.format(date);
        } else {
            return "";
        }
    }

    /**
     * Sets the boolean navEvent attribute.
     *
     * @param navEvent a value of true indicates that a navigation event has
     *                 occured.
     */
    public void setNavEvent(boolean navEvent) {
        this.navEvent = navEvent;
    }

    /**
     * A navEvent value of true indicates that a navEvent has occured.
     *
     * @return a value of true if a navigation event caused that render.
     */
    public boolean isNavEvent() {
        return this.navEvent;
    }

    /**
     * Set the date value of the navDate. The navDate is used to render a
     * calendar when the user is navigating from month to month or year to
     * year.
     *
     * @param navDate a Date assigned to the navDate.
     */
    public void setNavDate(Date navDate) {
        this.navDate = navDate;
    }

    /**
     * Get the navDate to render a calendar on a navigation event.
     *
     * @return the navDate as a Date
     */
    public Date getNavDate() {
        return this.navDate;
    }

    /**
     * Setting the showPopup attribute to true will render the SelectInputDate
     * popup calendar.
     *
     * @param showPopup a value of true will cause the popup calendar to be
     *                  rendered
     */
    public void setShowPopup(boolean showPopup) {
        if (showPopup) {
            this.showPopup.add(getClientId(FacesContext.getCurrentInstance()));
        } else {
            this.showPopup
                    .remove(getClientId(FacesContext.getCurrentInstance()));
        }
    }

    /**
     * A showPopup value of true indicates the SelectInputText popup be
     * rendered.
     *
     * @return the current value showPopup
     */
    public boolean isShowPopup() {
        if (showPopup
                .contains(getClientId(FacesContext.getCurrentInstance()))) {
            return true;
        } else {
            return false;
        }
    }

    /* (non-Javadoc)
    * @see javax.faces.component.UIComponent#getFamily()
    */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }


    /**
     * Returns the style class name used for the row containing the month and
     * Year. The style class is defined in an external style sheet.
     *
     * @return the style class name applied to the monthYearRow. If a
     *         monthYearRowClass attribute has not been set the default will be
     *         used.
     */
    public String getMonthYearRowClass() {

        String result = CSS_DEFAULT.DEFAULT_YEARMONTHHEADER_CLASS;
        if (isDisabled()) {
            result += "-dis";
        }
        return Util.appendNewStyleClass(CSS_DEFAULT.DEFAULT_CALENDAR,
                                        styleClass, result);
    }


    /**
     * Returns the style class name of the weekRowClass The style class is
     * defined in an external style sheet.
     *
     * @return the style class name applied to the weekRow. If a weekRowClass
     *         attribute has not been set the default will be used.
     */
    public String getWeekRowClass() {

        String result = CSS_DEFAULT.DEFAULT_WEEKHEADER_CLASS;
        if (isDisabled()) {
            result += "-dis";
        }
        return Util.appendNewStyleClass(CSS_DEFAULT.DEFAULT_CALENDAR,
                                        styleClass, result);
    }

    /**
     * @return the style class name used for the input text of the calendar.
     */
    public String getCalendarInputClass() {
        return Util.appendNewStyleClass(CSS_DEFAULT.DEFAULT_CALENDAR,
                                        styleClass,
                                        CSS_DEFAULT.DEFAULT_CALENDARINPUT_CLASS);
    }

    /**
     * Returns the style class name applied to the day cells in the
     * SelectInputDate calendar.
     *
     * @return the style class name that is applied to the SelectInputDate day
     *         cells
     */
    public String getDayCellClass() {
        String result = CSS_DEFAULT.DEFAULT_DAYCELL_CLASS;
        if (isDisabled()) {
            result += "-dis";
        }
        return Util.appendNewStyleClass(CSS_DEFAULT.DEFAULT_CALENDAR,
                                        styleClass, result);
    }

    /* (non-Javadoc)
     * @see com.icesoft.faces.component.ext.HtmlInputText#setStyleClass(java.lang.String)
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     *
     * @return styleClass
     */
    public String getStyleClass() {
        String s = Util.getDisaledOREnabledClass(this,
                                                 isDisabled(),
                                                 styleClass,
                                                 "styleClass",
                                                 CSS_DEFAULT.DEFAULT_CALENDAR);
        return s;
    }

    /**
     * Returns the currentDayCell style class name.
     *
     * @return style class name used for the current day cell
     */
    public String getCurrentDayCellClass() {
        String result = CSS_DEFAULT.DEFAULT_CURRENTDAYCELL_CLASS;
        if (isDisabled()) {
            result += "-dis";
        }
        return Util.appendNewStyleClass(CSS_DEFAULT.DEFAULT_CALENDAR,
                                        styleClass, result);
    }


    /**
     * @return the value of the renderAsPopup indicator.
     */
    public boolean isRenderAsPopup() {
        if (_renderAsPopup != null) {
            return _renderAsPopup.booleanValue();
        }
        ValueBinding vb = getValueBinding("renderAsPopup");
        Boolean v =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : false;
    }

    /**
     * @param b
     */
    public void setRenderAsPopup(boolean b) {
        _renderAsPopup = new Boolean(b);
    }


    /**
     * @return the style class name used for the outline of the calendar.
     */
    public String getCalendarOutlineClass() {
        String result = CSS_DEFAULT.DEFAULT_CALENDAROUTLINE_CLASS;
        if (isDisabled()) {
            result += "-dis";
        }
        return Util.appendNewStyleClass(CSS_DEFAULT.DEFAULT_CALENDAR,
                                        styleClass, result);
    }

    /**
     * Sets the directory where the images used by this component are located.
     *
     * @param imageDir the directory where the images used by this component are
     *                 located
     */
    public void setImageDir(String imageDir) {
        _imageDir = imageDir;
    }

    /**
     * @return the directory name where the images used by this component are
     *         located.
     */
    public String getImageDir() {
        if (_imageDir != null) {
            return _imageDir;
        }

        ValueBinding vb = getValueBinding("imageDir");
        if (vb != null) {
            _imageDir = (String) vb.getValue(getFacesContext());
        } else {
            _imageDir = DEFAULT_IMAGEDIR;
        }
        return _imageDir;
    }

    /**
     * @return the name of the move next image.
     */
    public String getMoveNextImage() {
        return this.DEFAULT_MOVENEXT;
    }

    /**
     * Returns the name of the move previous image.
     *
     * @return DEFAULT_MOVEPREV
     */
    public String getMovePreviousImage() {
        return this.DEFAULT_MOVEPREV;
    }

    /**
     * returns the name of the open popup image.
     *
     * @return DEFAULT_OPENPOPUP
     */
    public String getOpenPopupImage() {
        return this.DEFAULT_OPENPOPUP;
    }

    /**
     * Returns the name of the close popup image.
     *
     * @return DEFAULT_CLOSEPOPUP
     */
    public String getClosePopupImage() {
        return this.DEFAULT_CLOSEPOPUP;
    }

    /**
     * Sets the date format of the input text child component when the component
     * is in popup mode.
     *
     * @param popupDateFormat
     */
    public void setPopupDateFormat(String popupDateFormat) {
        _popupDateFormat = popupDateFormat;
    }

    /**
     * Returns the date format string of the input text child componenet.
     *
     * @return _popupDateFormat
     */
    public String getPopupDateFormat() {
        if (_popupDateFormat != null) {
            myDateFormat = new SimpleDateFormat(_popupDateFormat);
            return _popupDateFormat;
        }
        ValueBinding vb = getValueBinding("popupDateFormat");
        if (vb != null ) {
            myDateFormat = new SimpleDateFormat((String) vb.getValue(getFacesContext()));
            return (String) vb.getValue(getFacesContext());
        } else {
            return DEFAULT_POPUP_DATE_FORMAT;
        }
    }

    /* (non-Javadoc)
     * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[10];
        values[0] = super.saveState(context);
        values[1] = _renderAsPopup;
        values[2] = _popupDateFormat;
        values[3] = _imageDir;
        values[4] = _moveNextImage;
        values[5] = _movePreviousImage;
        values[6] = _openPopupImage;
        values[7] = _closePopupImage;
        values[8] = new Boolean(navEvent);
        values[9] = navDate;
        return ((Object) (values));
    }

    /* (non-Javadoc)
     * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _renderAsPopup = (Boolean) values[1];
        _popupDateFormat = (String) values[2];
        _imageDir = (String) values[3];
        _moveNextImage = (String) values[4];
        _movePreviousImage = (String) values[5];
        _openPopupImage = (String) values[6];
        _closePopupImage = (String) values[7];
        navEvent = ((Boolean) values[8]).booleanValue();
        navDate = (Date) values[9];
    }

    private Map linkMap = new HashMap();

    /**
     * @return linkMap
     */
    public Map getLinkMap() {
        return linkMap;
    }

    /**
     * @param linkMap
     */
    public void setLinkMap(Map linkMap) {
        this.linkMap = linkMap;
    }

    private String selectedDayLink;

    /**
     * @return selectedDayLink
     */
    public String getSelectedDayLink() {
        return selectedDayLink;
    }

    /**
     * @param selectedDayLink
     */
    public void setSelectedDayLink(String selectedDayLink) {
        this.selectedDayLink = selectedDayLink;
    }

    //this component was throwing an exception in popup mode, if the component has no binding for value attribute
    //so here we returning current date in this case.
    /* (non-Javadoc)
     * @see javax.faces.component.ValueHolder#getValue()
     */
    public Object getValue() {
        if (super.getValue() == null) {
            if (DOMResponseWriter.isStreamWriting()) {
                return new Date();
            }
            return null;
        } else {
            return super.getValue();
        }
    }

    private String highlightClass;
    /**
     * <p>Set the value of the <code>highlightClass</code> property.</p>
     *
     * @param highlightClass
     */
    public void setHighlightClass(String highlightClass) {
        this.highlightClass = highlightClass;
    }

    /**
     * <p>Return the value of the <code>highlightClass</code> property.</p>
     *
     * @return String highlightClass, if never set returns a blank string not 
     * null
     */
    public String getHighlightClass() {
        if (highlightClass != null) {
            return highlightClass;
        }
        ValueBinding vb = getValueBinding("highlightClass");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "";
    }
    
    private String highlightUnit;
    /**
     * <p>Set the value of the <code>highlightUnit</code> property.</p>
     *
     * @param highlightClass
     */
    public void setHighlightUnit(String highlightUnit) {
        this.highlightUnit = highlightUnit;
    }

    /**
     * <p>Return the value of the <code>highlightUnit</code> property.</p>
     *
     * @return String highlightUnit, if never set returns a blank string not 
     * null
     */
    public String getHighlightUnit() {
        if (highlightUnit != null) {
            return highlightUnit;
        }
        ValueBinding vb = getValueBinding("highlightUnit");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "";
    }  
    
    private String highlightValue;
    /**
     * <p>Set the value of the <code>highlightValue</code> property.</p>
     *
     * @param highlightValue
     */
    public void setHighlightValue(String highlightValue) {
        this.highlightValue = highlightValue;
    }

    /**
     * <p>Return the value of the <code>highlightValue</code> property.</p>
     *
     * @return String highlightValue. if never set returns blank a string not 
     * null
     */
    public String getHighlightValue() {
        if (highlightValue != null) {
            return highlightValue;
        }
        ValueBinding vb = getValueBinding("highlightValue");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "";
    }     
    
    private Map hightlightRules = new HashMap(); 
    private Map unitMap = new UnitMap();
    private void buildHeighLightMap() {
        validateHighlight();
        resetHighlightClasses(Calendar.YEAR); 
    }

    private boolean validateHighlight() {
        hightlightRules.clear();
        String highlightClassArray[] = getHighlightClass().split(":");
        String highlightUnitArray[] = getHighlightUnit().split(":");
        String highlightValueArray[] = getHighlightValue().split(":");
        if ((highlightClassArray.length  < 1 ) ||
                highlightClassArray[0].equals("") ||
                highlightUnitArray[0].equals("") ||
                highlightValueArray[0].equals("")) {
            return false;
        }
        if (!(highlightClassArray.length == highlightUnitArray.length) ||
                !(highlightUnitArray.length == highlightValueArray.length)) {
            System.out.println("\n[SelectInputDate] The following attributes does not have corresponding values:" +
                    "\n-highlightClass \n-highlightUnit \n-highlightValue \n" +
                    "Note: When highlighting required, all above attributes " +
                    "need to be used together and should have corresponding values.\n" +
                    "Each entity can be separated using the : colon, e.g. \n" +
                    "highlightClass=\"weekend: newyear\" \n" +
                    "highlightUnit=\"DAY_OF_WEEK: DAY_OF_YEAR\" \n"+
                    "highlightValue=\"1, 7: 1\" "
                    );
            return false;
        }
        
        for(int i = 0; i < highlightUnitArray.length; i++) {
            try {
                int option = Integer.parseInt(highlightUnitArray[i].trim());
                if (option <1 || option > 8) {
                    System.out.println("[SelectInputDate:highlightUnit] \""+ highlightUnitArray[i].trim() +"\" " +
                        "s not a valid unit value. Valid values are between 1 to 8");
                    return false;
                }
            } catch (NumberFormatException exception) {
                if (unitMap.containsKey(highlightUnitArray[i].trim())) {
                    highlightUnitArray[i] = String.valueOf(unitMap.get(
                            highlightUnitArray[i].trim()));
                } else {
                    System.out.println("[SelectInputDate:highlightUnit] \""+ highlightUnitArray[i] +"\" is " +
                            "not a valid unit value, String representation " +
                            "of unit must match with java.util.Calendar contants (e.g.)" +
                            "\nYEAR, MONTH, WEEK_OF_YEAR, WEEK_OF_MONTH, DATE, DAY_OF_YEAR, " +
                            "DAY_OF_WEEK and DAY_OF_WEEK_IN_MONTH");
                    return false;
                }
            }
            String[] value = highlightValueArray[i].replaceAll(" ", "").trim().split(",");
            for (int j=0; j <value.length; j++ ) {
                hightlightRules.put(highlightUnitArray[i].trim() + "$"+ value[j] , highlightClassArray[i]);
            }
        }

        
        return true;
    }

    Map getHightlightRules() {
        return hightlightRules;
    }

    void setHightlightRules(Map hightlightRules) {
        this.hightlightRules = hightlightRules;
    }
    
    private String highlightYearClass = "";
    private String highlightMonthClass ="";
    private String highlightWeekClass =""; 
    private String highlightDayClass ="";   
    
    String getHighlightDayCellClass() {
        return highlightYearClass  + 
        highlightMonthClass + 
        highlightWeekClass + 
        highlightDayClass;
    }

    String getHighlightMonthClass() {
        return highlightMonthClass;
    }

    void setHighlightMonthClass(String highlightMonthClass) {
        this.highlightMonthClass = highlightMonthClass;
    }

    String getHighlightYearClass() {
        return highlightYearClass;
    }

    void setHighlightYearClass(String highlightYearClass) {
        this.highlightYearClass = highlightYearClass;
    }

    String getHighlightWeekClass() {
        return highlightWeekClass;
    }

    void addHighlightWeekClass(String highlightWeekClass) {
        if (this.highlightWeekClass.indexOf(highlightWeekClass) == -1) {
            this.highlightWeekClass += (highlightWeekClass + " ");
        }
    }

    void addHighlightDayClass(String highlightDayClass) {
        if (this.highlightDayClass.indexOf(highlightDayClass) == -1) {
            this.highlightDayClass += (highlightDayClass + " ");
        }
    }

    void resetHighlightClasses(int level)  {
        if (level <= Calendar.MONTH) {
            this.highlightMonthClass = "";
            this.highlightYearClass = "";
            this.highlightDayClass = "";
        }
        this.highlightDayClass = ""; 
        this.highlightWeekClass = "";
    }
}

class UnitMap extends HashMap {
    public UnitMap() {
        this.put("YEAR", new Integer(Calendar.YEAR));
        this.put("MONTH", new Integer(Calendar.MONTH));
        this.put("WEEK_OF_YEAR", new Integer(Calendar.WEEK_OF_YEAR));
        this.put("WEEK_OF_MONTH", new Integer(Calendar.WEEK_OF_MONTH));
        this.put("DATE", new Integer(Calendar.DATE));
        this.put("DAY_OF_YEAR", new Integer(Calendar.DAY_OF_YEAR));
        this.put("DAY_OF_WEEK", new Integer(Calendar.DAY_OF_WEEK));
        this.put("DAY_OF_WEEK_IN_MONTH", new Integer(Calendar.DAY_OF_WEEK_IN_MONTH));
    }
    
    public int getConstant(String key) {
        if (!super.containsKey(key)) {
            return 0;
        }
        return Integer.parseInt(super.get(key).toString());
    }
}

