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


import com.icesoft.faces.component.ext.taglib.Util;

import javax.faces.component.UIComponent;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

/**
 * Positioned Panel Tag, See TLD for additional docs.
 */
public class PanelPositionedTag extends UIComponentTag {


    private java.lang.String value;
    private java.lang.String var;
    private String style;
    private String styleClass;
    private String listener;
    private String overlap;
    private String constraint;
    private String handle;
    private String hoverclass;


    public PanelPositionedTag() {
        super();

    }

    public String getComponentType() {
        return PanelPositioned.COMPONENET_TYPE;
    }


    public String getRendererType() {
        return PanelPositioned.DEFAULT_RENDERER_TYPE;
    }


    public void setValue(java.lang.String value) {
        this.value = value;
    }

    public void setVar(java.lang.String var) {
        this.var = var;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public void setListener(String listener) {
        this.listener = listener;
    }

    public void setOverlap(String overlap) {
        this.overlap = overlap;
    }

    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public void setHoverclass(String hoverclass) {
        this.hoverclass = hoverclass;
    }

    protected void setProperties(UIComponent uiComponent) {
        try {
            super.setProperties(uiComponent);
            PanelPositioned series = (PanelPositioned) uiComponent;
            if (value != null) {
                if (isValueReference(value)) {
                    ValueBinding vb = Util.getValueBinding(value);
                    series.setValueBinding("value", vb);
                } else {
                    series.setValue(value);
                }
            }
            if (style != null) {
                if (isValueReference(style)) {
                    ValueBinding vb = Util.getValueBinding(style);
                    series.setValueBinding("style", vb);
                } else {
                    series.setStyle(style);
                }
            }
            if (styleClass != null) {
                if (isValueReference(styleClass)) {
                    ValueBinding vb = Util.getValueBinding(styleClass);
                    series.setValueBinding("styleClass", vb);
                } else {
                    series.setStyleClass(styleClass);
                }
            }

            if (overlap != null) {
                if (isValueReference(overlap)) {
                    ValueBinding vb = Util.getValueBinding(overlap);
                    series.setValueBinding("overlap", vb);
                } else {
                    series.setOverlap(overlap);
                }
            }
            if (constraint != null) {
                if (isValueReference(constraint)) {
                    ValueBinding vb = Util.getValueBinding(constraint);
                    series.setValueBinding("constraint", vb);
                } else {
                    series.setConstraint(constraint);
                }
            }
            if (handle != null) {
                if (isValueReference(handle)) {
                    ValueBinding vb = Util.getValueBinding(handle);
                    series.setValueBinding("handle", vb);
                } else {
                    series.setHandle(handle);
                }
            }
            if (hoverclass != null) {
                if (isValueReference(hoverclass)) {
                    ValueBinding vb = Util.getValueBinding(hoverclass);
                    series.setValueBinding("hoverclass", vb);
                } else {
                    series.setHoverclass(hoverclass);
                }
            }


            series.setVar(var);
            if (listener != null) {
                Class[] ca = ca = new Class[]{PanelPositionedEvent.class};
                MethodBinding mb = getFacesContext().getApplication()
                        .createMethodBinding(listener, ca);
                series.setListener(mb);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setStringProperty(UIComponent component, String property,
                                     String propertyName) {
        if (property != null) {
            if (isValueReference(property)) {
                ValueBinding vb = Util.getValueBinding(property);
                component.setValueBinding(propertyName, vb);
            } else {
                component.getAttributes().put(propertyName, property);
            }
        }
    }


}

