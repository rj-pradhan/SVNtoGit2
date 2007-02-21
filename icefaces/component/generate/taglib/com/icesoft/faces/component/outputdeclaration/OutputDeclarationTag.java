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

package com.icesoft.faces.component.outputdeclaration;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;

/**
 * <p>Auto-generated component tag class. Do <strong>NOT</strong> modify; all
 * changes <strong>will</strong> be lost!</p>
 */

public class OutputDeclarationTag extends UIComponentTag {
    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.OutputDeclaration";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.OutputDeclaration";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        doctypePublic = null;
        doctypeRoot = null;
        doctypeSystem = null;
        output = null;
        prettyPrinting = null;
        converter = null;
        value = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);
        if (doctypePublic != null) {
            if (isValueReference(doctypePublic)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(doctypePublic);
                _component.setValueBinding("doctypePublic", _vb);
            } else {
                _component.getAttributes().put("doctypePublic", doctypePublic);
            }
        }
        if (doctypeRoot != null) {
            if (isValueReference(doctypeRoot)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(doctypeRoot);
                _component.setValueBinding("doctypeRoot", _vb);
            } else {
                _component.getAttributes().put("doctypeRoot", doctypeRoot);
            }
        }
        if (doctypeSystem != null) {
            if (isValueReference(doctypeSystem)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(doctypeSystem);
                _component.setValueBinding("doctypeSystem", _vb);
            } else {
                _component.getAttributes().put("doctypeSystem", doctypeSystem);
            }
        }
        if (output != null) {
            if (isValueReference(output)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(output);
                _component.setValueBinding("output", _vb);
            } else {
                _component.getAttributes().put("output", output);
            }
        }
        if (prettyPrinting != null) {
            if (isValueReference(prettyPrinting)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(prettyPrinting);
                _component.setValueBinding("prettyPrinting", _vb);
            } else {
                _component.getAttributes()
                        .put("prettyPrinting", prettyPrinting);
            }
        }
        if (converter != null) {
            if (isValueReference(converter)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(converter);
                _component.setValueBinding("converter", _vb);
            } else {
                Converter _converter = FacesContext.getCurrentInstance().
                        getApplication().createConverter(converter);
                _component.getAttributes().put("converter", _converter);
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
    }

    // doctypePublic
    private String doctypePublic = null;

    public void setDoctypePublic(String doctypePublic) {
        this.doctypePublic = doctypePublic;
    }

    // doctypeRoot
    private String doctypeRoot = null;

    public void setDoctypeRoot(String doctypeRoot) {
        this.doctypeRoot = doctypeRoot;
    }

    // doctypeSystem
    private String doctypeSystem = null;

    public void setDoctypeSystem(String doctypeSystem) {
        this.doctypeSystem = doctypeSystem;
    }

    private String output;

    public void setOutput(String output) {
        this.output = output;
    }

    private String prettyPrinting;

    public void setPrettyPrinting(String prettyPrinting) {
        this.prettyPrinting = prettyPrinting;
    }

    // converter
    private String converter = null;

    public void setConverter(String converter) {
        this.converter = converter;
    }

    // value
    private String value = null;

    public void setValue(String value) {
        this.value = value;
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
