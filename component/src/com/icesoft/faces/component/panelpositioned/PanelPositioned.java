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

package com.icesoft.faces.component.panelpositioned;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.dragdrop.DndEvent;
import com.icesoft.faces.component.panelseries.UISeries;
import com.icesoft.faces.context.effects.JavascriptContext;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.FacesEvent;
import javax.faces.event.PhaseId;

/**
 * Panel Positioned Componenet class
 */
public class PanelPositioned extends UISeries {

    public static final String COMPONENET_TYPE =
            "com.icesoft.faces.dragdrop.PanelPositioned";
    public static final String DEFAULT_RENDERER_TYPE =
            "com.icesoft.faces.dragdrop.PanelPositionedRenderer";
    public static final String COMPONENT_FAMILY =
            "com.icesoft.faces.dragdrop.PanelPositionedFamily";

    private String styleClass;
    private String style;
    private MethodBinding listener;
    private String overlap;
    private String constraint;
    private String handle;
    private String hoverclass;
    


    public PanelPositioned() {
        super();
        setRendererType(DEFAULT_RENDERER_TYPE);
        JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS,
                                     FacesContext.getCurrentInstance());
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }


    public String getRendererType() {
        return DEFAULT_RENDERER_TYPE;
    }

    public String getStyleClass() {
        if (styleClass != null) {
            return styleClass;
        }
        ValueBinding vb = getValueBinding("styleClass");
        return vb != null ? (String) vb.getValue(getFacesContext()) :
               CSS_DEFAULT.POSITIONED_PANEL_DEFAULT_CLASS;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueBinding vb = getValueBinding("style");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public MethodBinding getListener() {
        return listener;
    }

    public void setListener(MethodBinding listener) {
        this.listener = listener;
    }

    public void setValueBinding(String s, ValueBinding vb) {
        if (s != null && s.equals("listener")) {
            MethodBinding mb =
                    getFacesContext().getApplication().createMethodBinding(
                            vb.getExpressionString(),
                            new Class[]{PanelPositionedEvent.class});
            setListener( mb );
        } else {
            super.setValueBinding(s, vb);
        }
    }


    public String getOverlap() {
        if (overlap != null) {
            return overlap;
        }
        ValueBinding vb = getValueBinding("overlap");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * Either 'vertical' or 'horizontal'. For floating sortables or horizontal
     * lists, choose 'horizontal'. Vertical lists should use 'vertical'.
     *
     * @param overlap
     */
    public void setOverlap(String overlap) {
        this.overlap = overlap;
    }

    public String getConstraint() {
        if (constraint != null) {
            return constraint;
        }
        ValueBinding vb = getValueBinding("constraint");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * If set to 'horizontal' or 'vertical' the drag will be constrained to take
     * place only horizontally or vertically.
     *
     * @param constraint
     */
    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public String getHandle() {
        if (handle != null) {
            return handle;
        }
        ValueBinding vb = getValueBinding("handle");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * The handle used to drag. The first child/grandchild/etc. element found
     * within the element that has this CSS class value will be used as the
     * handle.
     *
     * @param handle
     */
    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getHoverclass() {
        if (hoverclass != null) {
            return hoverclass;
        }
        ValueBinding vb = getValueBinding("hoverclass");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * If set, the Droppable will have this additional CSS class when an
     * accepted Draggable is hovered over it.
     *
     * @param hoverclass
     */
    public void setHoverclass(String hoverclass) {
        this.hoverclass = hoverclass;
    }


    public void broadcast(FacesEvent event)
            throws AbortProcessingException {


        if (event instanceof PanelPositionedEvent) {
            try {

                PanelPositionedEvent de = (PanelPositionedEvent) event;
                de.process(); // Copy over the list values now
                MethodBinding mb = de.getListener();
                Object[] oa = {de};
                mb.invoke(FacesContext.getCurrentInstance(), oa);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.broadcast(event);
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#queueEvent(javax.faces.event.FacesEvent)
     */
    public void queueEvent(FacesEvent event) {
        if (event instanceof DndEvent) {

            event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
        }
        super.queueEvent(event);
    }

    public Object saveState(FacesContext context) {
        Object[] values = new Object[4];
        values[0] = super.saveState(context);
        values[1] = styleClass;
        values[2] = style;
        values[3] = listener;
        return values;
    }

    public void restoreState(FacesContext context, Object stateIn) {
        Object[] state = (Object[]) stateIn;
        super.restoreState(context, state[0]);
        styleClass = (String) state[1];
        style = (String) state[2];
        listener = (MethodBinding) state[3];
    }


}
