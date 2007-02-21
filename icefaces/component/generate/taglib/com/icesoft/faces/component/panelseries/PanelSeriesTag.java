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

package com.icesoft.faces.component.panelseries;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;

/**
 * <p>Auto-generated component tag class. Do <strong>NOT</strong> modify; all
 * changes <strong>will</strong> be lost!</p>
 */

public class PanelSeriesTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.PanelSeries";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.PanelSeriesRenderer";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        first = null;
        rows = null;
        style = null;
        styleClass = null;
        value = null;
        _var = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);
        if (first != null) {
            if (isValueReference(first)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(first);
                _component.setValueBinding("first", _vb);
            } else {
                _component.getAttributes().put("first", Integer.valueOf(first));
            }
        }
        if (rows != null) {
            if (isValueReference(rows)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(rows);
                _component.setValueBinding("rows", _vb);
            } else {
                _component.getAttributes().put("rows", Integer.valueOf(rows));
            }
        }
        if (style != null) {
            if (isValueReference(style)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(style);
                _component.setValueBinding("style", _vb);
            } else {
                _component.getAttributes().put("style", style);
            }
        }
        if (styleClass != null) {
            if (isValueReference(styleClass)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(styleClass);
                _component.setValueBinding("styleClass", _vb);
            } else {
                _component.getAttributes().put("styleClass", styleClass);
            }
        }
        if (value != null) {
            if (isValueReference(value)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(value);
                _component.setValueBinding("value", _vb);
            } else {
                _component.getAttributes().put("value", value);
            }
        }
        if (_var != null) {
            try {
                com.icesoft.faces.component.panelseries.UISeries panelseries =
                        (com.icesoft.faces.component.panelseries.UISeries) _component;
                panelseries.setVar(_var);
            } catch (ClassCastException cce) {
                throw new IllegalStateException(_component.toString() +
                                                " not expected type.  Expected: com.icesoft.faces.component.panelseries.UISeries.  Perhaps you're missing a tag?");
            }
        }
    }

    // first
    private String first = null;

    public void setFirst(String first) {
        this.first = first;
    }

    // rows
    private String rows = null;

    public void setRows(String rows) {
        this.rows = rows;
    }

    // style
    private String style = null;

    public void setStyle(String style) {
        this.style = style;
    }

    // styleClass
    private String styleClass = null;

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    // value
    private String value = null;

    public void setValue(String value) {
        this.value = value;
    }

    // var
    private String _var = null;

    public void setVar(String _var) {
        this._var = _var;
    }

    private static Class actionArgs[] = new Class[0];
    private static Class actionListenerArgs[] = {ActionEvent.class};
    private static Class validatorArgs[] =
            {FacesContext.class, UIComponent.class, Object.class};
    private static Class valueChangeListenerArgs[] = {ValueChangeEvent.class};

    //
    // Methods From TagSupport
    // 

    public int doStartTag() throws JspException {
        int rc = 0;
        try {
            rc = super.doStartTag();
        } catch (JspException e) {
            throw e;
        } catch (Throwable t) {
            throw new JspException(t);
        }
        return rc;
    }


    public int doEndTag() throws JspException {
        int rc = 0;
        try {
            rc = super.doEndTag();
        } catch (JspException e) {
            throw e;
        } catch (Throwable t) {
            throw new JspException(t);
        }
        return rc;
    }

}
