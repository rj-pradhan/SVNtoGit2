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
/* Original copyright.
 * Copyright 2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.icesoft.faces.component.paneltabset;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.component.panelseries.UISeries;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;
import java.util.Iterator;
import java.util.List;

/**
 * <p>PanelTabSet is a JSF component class that represents an ICEfaces tab panel
 * container.</p>
 * <p/>
 * This component extends the ICEfaces UISeries component which is a modified
 * implementation of UIData. </p>
 * <p/>
 * By default the component is rendered by the "com.icesoft.faces.TabbedPane"
 * renderer type. </p>
 */
public class PanelTabSet
        extends UISeries {


    /**
     * The method binding for a TabChangeListener.
     */
    private MethodBinding _tabChangeListener = null;

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#decode(javax.faces.context.FacesContext)
     */
    public void decode(FacesContext context) {
        super.decode(context);
    }

    /**
     * @param context
     * @param phaseId
     */
    public void applyPhase(FacesContext context, PhaseId phaseId) {
        if (context == null) {
            throw new NullPointerException("Null context in PanelTabSet");
        }
//        if(phaseId ==  PhaseId.APPLY_REQUEST_VALUES)
//            decode(context);

        int tabIdx = 0;
        int selectedIndex = getSelectedIndex();

        if (super.getValue() != null) {
            int rowIndex = super.getFirst();
            setRowIndex(rowIndex);
            int rowsToBeDisplayed = getRows();
            int rowsDisplayed = 0;
            UIComponent child =
                    getUIComponent((UIComponent) getChildren().get(0));
            while (isRowAvailable()) {
                if (rowsToBeDisplayed > 0 &&
                    rowsDisplayed >= rowsToBeDisplayed) {
                    break;
                }

                if (child instanceof PanelTab) {
                    if (tabIdx == selectedIndex) {
                        applyPhase(context, child, phaseId);
                    }
                    tabIdx++;
                }
                rowsDisplayed++;
                rowIndex++;
                setRowIndex(rowIndex);
            }
            setRowIndex(-1);
        } else {

            Iterator it = getFacetsAndChildren();

            while (it.hasNext()) {
                UIComponent childOrFacet =
                        getUIComponent((UIComponent) it.next());
                if (childOrFacet instanceof PanelTab) {
                    if (tabIdx == selectedIndex) {
                        applyPhase(context, childOrFacet, phaseId);
                    }
                    tabIdx++;
                } else {
                    applyPhase(context, childOrFacet, phaseId);
                }
            }
        }

    }

    /**
     * @param context
     * @param component
     * @param phaseId
     */
    public void applyPhase(FacesContext context, UIComponent component,
                           PhaseId phaseId) {
        if (phaseId == PhaseId.APPLY_REQUEST_VALUES) {
            component.processDecodes(context);
        } else if (phaseId == PhaseId.PROCESS_VALIDATIONS) {
            component.processValidators(context);
        } else if (phaseId == PhaseId.UPDATE_MODEL_VALUES) {
            component.processUpdates(context);
        } else {
            throw new IllegalArgumentException();
        }
    }

    /* (non-Javadoc)
    * @see javax.faces.component.UIComponent#processDecodes(javax.faces.context.FacesContext)
    */
    public void processDecodes(javax.faces.context.FacesContext context) {
        if (context == null) {
            throw new NullPointerException("context");
        }

        if (!isRendered()) {
            return;
        }

        decode(context);
        applyPhase(context, PhaseId.APPLY_REQUEST_VALUES);
    }

    /* (non-Javadoc)
    * @see javax.faces.component.UIComponent#processValidators(javax.faces.context.FacesContext)
    */
    public void processValidators(FacesContext context) {

        if (context == null) {
            throw new NullPointerException();
        }
        if (!isRendered()) {
            return;
        }
        applyPhase(context, PhaseId.PROCESS_VALIDATIONS);
    }


    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#processUpdates(javax.faces.context.FacesContext)
     */
    public void processUpdates(FacesContext context) {

        if (context == null) {
            throw new NullPointerException();
        }
        if (!isRendered()) {
            return;
        }
        applyPhase(context, PhaseId.UPDATE_MODEL_VALUES);
    }

    private UIComponent getUIComponent(UIComponent uiComponent) {
        if (uiComponent instanceof UINamingContainer ||
            uiComponent instanceof UIForm) {
            List children = uiComponent.getChildren();
            for (int i = 0, len = children.size(); i < len; i++) {
                uiComponent = getUIComponent((UIComponent) children.get(i));
            }
        }
        return uiComponent;
    }

    /**
     * @param listener
     */
    public void addTabChangeListener(TabChangeListener listener) {
        addFacesListener(listener);
    }

    /**
     * @param listener
     */
    public void removeTabChangeListener(TabChangeListener listener) {
        removeFacesListener(listener);
    }

    /**
     * @return the tabChangeListener
     */
    public MethodBinding getTabChangeListener() {
        return _tabChangeListener;
    }

    /**
     * @param tabChangeListener
     */
    public void setTabChangeListener(MethodBinding tabChangeListener) {
        _tabChangeListener = tabChangeListener;
    }
    
    public void setValueBinding(String s, ValueBinding vb) {
        if (s != null && s.equals("tabChangeListener")) {
            MethodBinding mb =
                    getFacesContext().getApplication().createMethodBinding(
                            vb.getExpressionString(),
                            new Class[]{TabChangeEvent.class});
            setTabChangeListener( mb );
        } else {
            super.setValueBinding(s, vb);
        }
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#broadcast(javax.faces.event.FacesEvent)
     */
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        if (event instanceof TabChangeEvent) {
            TabChangeEvent tabChangeEvent = (TabChangeEvent) event;
            if (tabChangeEvent.getComponent() == this) {
                setSelectedIndex(tabChangeEvent.getNewTabIndex());
                //getFacesContext().renderResponse();
            }
        }
        
        super.broadcast(event);
        
        if (event instanceof TabChangeEvent) {
            TabChangeEvent tabChangeEvent = (TabChangeEvent) event;
            MethodBinding tabChangeListenerBinding = getTabChangeListener();
            if (tabChangeListenerBinding != null) {
                try {
                    tabChangeListenerBinding.invoke(
                            getFacesContext(), new Object[]{tabChangeEvent});
                }
                catch (EvaluationException e) {
                    Throwable cause = e.getCause();
                    if (cause != null &&
                            cause instanceof AbortProcessingException) {
                        throw(AbortProcessingException) cause;
                    } else {
                        throw e;
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
    * @see javax.faces.component.UIComponent#isRendered()
    */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }

    /**
     * The component type.
     */
    public static final String COMPONENT_TYPE = "com.icesoft.faces.PanelTabSet";
    /**
     * The component family.
     */
    public static final String COMPONENT_FAMILY = "javax.faces.Panel";
    /**
     * The default renderer type.
     */
    private static final String DEFAULT_RENDERER_TYPE =
            "com.icesoft.faces.TabbedPane";
    /**
     * The default selected index.
     */
    private static final int DEFAULT_SELECTEDINDEX = 0;
    /**
     * The default tab placement.
     */
    private final String DEFAULT_TABPLACEMENT =
            "Top"; // Top, Bottom, Left or Right

    // default styles

    /**
     * The default background color of the tab panels.
     */
    private static final String DEFAULT_BG_COLOR = "#FFFFFF";
    /**
     * The current selected tab index.
     */
    private Integer _selectedIndex = null;
    /**
     * The current tab placement. <p>At this time only "top" and "bottom" are
     * supported.
     */
    private String _tabPlacement = null;
    /**
     * The current background color.
     */
    private String _bgcolor = null;
    /**
     * The current style.
     */
    private String _style = null;
    /**
     * The current style class name.
     */
    private String _styleClass = null;

    /**
     * Creates an instance and sets the default renderer type to
     * "com.icesoft.faces.TabbedPane".
     */
    public PanelTabSet() {
        setRendererType(DEFAULT_RENDERER_TYPE);
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#getFamily()
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    /**
     * @param selectedIndex
     */
    void setSelectedIndex(Integer selectedIndex) {
        _selectedIndex = selectedIndex;
    }

    /**
     * @param selectedIndex
     */
    public void setSelectedIndex(int selectedIndex) {
        _selectedIndex = new Integer(selectedIndex);
    }

    /**
     * @return the value of selectedIndex
     */
    public int getSelectedIndex() {
        if (_selectedIndex != null) {
            return _selectedIndex.intValue();
        }
        ValueBinding vb = getValueBinding("selectedIndex");
        Number v = vb != null ? (Number) vb.getValue(getFacesContext()) : null;
        return v != null ? v.intValue() : DEFAULT_SELECTEDINDEX;
    }

    /**
     * @param bgcolor
     */
    public void setBgcolor(String bgcolor) {
        _bgcolor = bgcolor;
    }

    /**
     * @return the value of bgcolor
     */
    public String getBgcolor() {
        if (_bgcolor != null) {
            return _bgcolor;
        }
        ValueBinding vb = getValueBinding("bgcolor");
        return vb != null ? (String) vb.getValue(getFacesContext()) :
               DEFAULT_BG_COLOR;
    }

    private int border = 0;
    private boolean border_set = false;

    /**
     * <p>Return the value of the <code>border</code> property. Contents:</p><p>
     * Width (in pixels) of the border to be drawn around this table. </p>
     *
     * @return border
     */
    public int getBorder() {
        if (this.border_set) {
            return this.border;
        }
        ValueBinding _vb = getValueBinding("border");
        if (_vb != null) {
            Object _result = _vb.getValue(getFacesContext());
            if (_result == null) {
                return 0;
            } else {
                return ((Integer) _result).intValue();
            }
        } else {
            return this.border;
        }
    }

    /**
     * <p>Set the value of the <code>border</code> property.</p>
     *
     * @param border
     */
    public void setBorder(int border) {
        this.border = border;
        this.border_set = true;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlPanelGroup#setStyle(java.lang.String)
     */
    public void setStyle(String style) {
        _style = style;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlPanelGroup#getStyle()
     */
    public String getStyle() {
        if (_style != null) {
            return _style;
        }
        ValueBinding vb = getValueBinding("style");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlPanelGroup#setStyleClass(java.lang.String)
     */
    public void setStyleClass(String styleClass) {
        _styleClass = styleClass;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlPanelGroup#getStyleClass()
     */
    public String getStyleClass() {
        if (_styleClass != null) {
            return _styleClass;
        }
        ValueBinding vb = getValueBinding("styleClass");
        return vb != null ? (String) vb.getValue(getFacesContext()) :
               CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TAB_SET;
    }

    /**
     * @param tabPlacement
     */
    public void setTabPlacement(String tabPlacement) {
        _tabPlacement = tabPlacement;
    }

    /**
     * @return the value of tabPlacement, currently only "top" and "bottom" are
     *         supported
     */
    public String getTabPlacement() {
        if (_tabPlacement != null) {
            return _tabPlacement;
        }
        ValueBinding vb = getValueBinding("tabPlacement");
        return vb != null ? (String) vb.getValue(getFacesContext()) :
               this.DEFAULT_TABPLACEMENT;
    }

    /* (non-Javadoc)
    * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
    */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[29];
        values[0] = super.saveState(context);
        values[1] = _selectedIndex;
        values[2] = _bgcolor;
        values[3] = saveAttachedState(context, _tabChangeListener);
        values[4] = _style;
        values[5] = _styleClass;
        values[6] = _tabPlacement;
        values[7] = onclick;
        values[8] = ondblclick;
        values[9] = onmousedown;
        values[10] = onmouseup;
        values[11] = onmouseover;
        values[12] = onmousemove;
        values[13] = onmouseout;
        values[14] = onkeypress;
        values[15] = onkeydown;
        values[16] = onkeyup;
        values[17] = align;
        values[18] = new Integer(border);
        values[19] = cellpadding;
        values[20] = cellspacing;
        values[21] = frame;
        values[22] = rules;
        values[23] = summary;
        values[24] = height;
        values[25] = width;
        values[26] = dir;
        values[27] = lang;
        values[28] = title;
        return ((Object) (values));
    }

    /* (non-Javadoc)
     * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        _selectedIndex = (Integer) values[1];
        _bgcolor = (String) values[2];
        _tabChangeListener =
                (MethodBinding) restoreAttachedState(context, values[3]);
        _style = (String) values[4];
        _styleClass = (String) values[5];
        _tabPlacement = (String) values[6];
        onclick = (String) values[7];
        ondblclick = (String) values[8];
        onmousedown = (String) values[9];
        onmouseup = (String) values[10];
        onmouseover = (String) values[11];
        onmousemove = (String) values[12];
        onmouseout = (String) values[13];
        onkeypress = (String) values[14];
        onkeydown = (String) values[15];
        onkeyup = (String) values[16];
        align = (String) values[17];
        border = ((Integer) values[18]).intValue();
        cellpadding = (String) values[19];
        cellspacing = (String) values[20];
        frame = (String) values[21];
        rules = (String) values[22];
        summary = (String) values[23];
        height = (String) values[24];
        width = (String) values[25];
        dir = (String) values[26];
        lang = (String) values[27];
        title = (String) values[28];
    }

    private String onclick;
    private String ondblclick;
    private String onmousedown = null;
    private String onmouseup = null;
    private String onmouseover = null;
    private String onmousemove = null;
    private String onmouseout = null;
    private String onkeypress = null;
    private String onkeydown = null;
    private String onkeyup = null;
    private String align = null;
    private String cellpadding = null;
    private String cellspacing = null;
    private String frame = null;
    private String rules = null;
    private String summary = null;
    private String height = null;
    private String width = null;
    private String dir = null;
    private String lang = null;
    private String title = null;

    /**
     * @param align
     */
    public void setAlign(String align) {
        this.align = align;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setCellpadding(java.lang.String)
     */
    public void setCellpadding(String cellpadding) {
        this.cellpadding = cellpadding;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setCellspacing(java.lang.String)
     */
    public void setCellspacing(String cellspacing) {
        this.cellspacing = cellspacing;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setFrame(java.lang.String)
     */
    public void setFrame(String frame) {
        this.frame = frame;
    }

    /**
     * @param height
     */
    public void setHeight(String height) {
        this.height = height;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setOnclick(java.lang.String)
     */
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setOndblclick(java.lang.String)
     */
    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setOnkeydown(java.lang.String)
     */
    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setOnkeypress(java.lang.String)
     */
    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setOnkeyup(java.lang.String)
     */
    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setOnmousedown(java.lang.String)
     */
    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setOnmousemove(java.lang.String)
     */
    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setOnmouseout(java.lang.String)
     */
    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setOnmouseover(java.lang.String)
     */
    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setOnmouseup(java.lang.String)
     */
    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setRules(java.lang.String)
     */
    public void setRules(String rules) {
        this.rules = rules;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setSummary(java.lang.String)
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setWidth(java.lang.String)
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setDir(java.lang.String)
     */
    public void setDir(String dir) {
        this.dir = dir;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlDataTable#setLang(java.lang.String)
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    /* (non-Javadoc)
    * @see javax.faces.component.html.HtmlDataTable#setTitle(java.lang.String)
    */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the value of onclick property
     */
    public String getOnclick() {
        if (onclick != null) {
            return onclick;
        }
        ValueBinding vb = getValueBinding("onclick");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of ondblclick property
     */
    public String getOndblclick() {
        if (ondblclick != null) {
            return ondblclick;
        }
        ValueBinding vb = getValueBinding("ondblclick");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onmousedown property
     */
    public String getOnmousedown() {
        if (onmousedown != null) {
            return onmousedown;
        }
        ValueBinding vb = getValueBinding("onmousedown");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onmouseup property
     */
    public String getOnmouseup() {
        if (onmouseup != null) {
            return onmouseup;
        }
        ValueBinding vb = getValueBinding("onmouseup");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onmouseover property
     */
    public String getOnmouseover() {
        if (onmouseover != null) {
            return onmouseover;
        }
        ValueBinding vb = getValueBinding("onmouseover");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onmousemove property
     */
    public String getOnmousemove() {
        if (onmousemove != null) {
            return onmousemove;
        }
        ValueBinding vb = getValueBinding("onmousemove");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onmouseout property
     */
    public String getOnmouseout() {
        if (onmouseout != null) {
            return onmouseout;
        }
        ValueBinding vb = getValueBinding("onmouseout");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onkeypress property
     */
    public String getOnkeypress() {
        if (onkeypress != null) {
            return onkeypress;
        }
        ValueBinding vb = getValueBinding("onkeypress");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onkeydown property
     */
    public String getOnkeydown() {
        if (onkeydown != null) {
            return onkeydown;
        }
        ValueBinding vb = getValueBinding("onkeydown");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of onkeyup property
     */
    public String getOnkeyup() {
        if (onkeyup != null) {
            return onkeyup;
        }
        ValueBinding vb = getValueBinding("onkeyup");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of align property
     */
    public String getAlign() {
        if (align != null) {
            return align;
        }
        ValueBinding vb = getValueBinding("align");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }


    /**
     * @return the value of cellpadding property
     */
    public String getCellpadding() {
        if (cellpadding != null) {
            return cellpadding;
        }
        ValueBinding vb = getValueBinding("cellpadding");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of cellspacing property
     */
    public String getCellspacing() {
        if (cellspacing != null) {
            return cellspacing;
        }
        ValueBinding vb = getValueBinding("cellspacing");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of frame property
     */
    public String getFrame() {
        if (frame != null) {
            return frame;
        }
        ValueBinding vb = getValueBinding("frame");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of rules property
     */
    public String getRules() {
        if (rules != null) {
            return rules;
        }
        ValueBinding vb = getValueBinding("rules");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of summary property
     */
    public String getSummary() {
        if (summary != null) {
            return summary;
        }
        ValueBinding vb = getValueBinding("summary");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of height property
     */
    public String getHeight() {
        if (height != null) {
            return height;
        }
        ValueBinding vb = getValueBinding("height");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of width property
     */
    public String getWidth() {
        if (width != null) {
            return width;
        }
        ValueBinding vb = getValueBinding("width");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of dir property
     */
    public String getDir() {
        if (dir != null) {
            return dir;
        }
        ValueBinding vb = getValueBinding("dir");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of lang property
     */
    public String getLang() {
        if (lang != null) {
            return lang;
        }
        ValueBinding vb = getValueBinding("lang");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @return the value of title property
     */
    public String getTitle() {
        if (title != null) {
            return title;
        }
        ValueBinding vb = getValueBinding("title");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    private String renderedOnUserRole = null;

    /**
     * <p>Set the value of the <code>renderedOnUserRole</code> property.</p>
     *
     * @param renderedOnUserRole
     */
    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    /**
     * <p>Return the value of the <code>renderedOnUserRole</code> property.</p>
     *
     * @return renderedOnUserRole
     */
    public String getRenderedOnUserRole() {
        if (renderedOnUserRole != null) {
            return renderedOnUserRole;
        }
        ValueBinding vb = getValueBinding("renderedOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    private static final boolean DEFAULT_VISIBLE = true;
    private Boolean visible = null;

    /**
     * <p>Set the value of the <code>visible</code> property.</p>
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = Boolean.valueOf(visible);
    }

    /**
     * <p>Return the value of the <code>visible</code> property.</p>
     *
     * @return visible
     */
    public boolean isVisible() {
        if (visible != null) {
            return visible.booleanValue();
        }
        ValueBinding vb = getValueBinding("visible");
        Boolean boolVal =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return boolVal != null ? boolVal.booleanValue() : DEFAULT_VISIBLE;
    }

    String getTabClass() {
        if (getTabPlacement()
                .equalsIgnoreCase(CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_BOTTOM)) {
            return Util.appendNewStyleClass(
                    CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TAB_SET, getStyleClass(),
                    CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_BOTTOM);
        } else {
            return Util.appendNewStyleClass(
                    CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TAB_SET, getStyleClass(),
                    "");
        }
    }
}
