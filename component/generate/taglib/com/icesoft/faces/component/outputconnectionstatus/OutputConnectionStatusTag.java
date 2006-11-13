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

package com.icesoft.faces.component.outputconnectionstatus;

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

public class OutputConnectionStatusTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.OutputConnectionStatus";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.OutputConnectionStatusRenderer";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        activeLabel = null;
        cautionLabel = null;
        disconnectedLabel = null;
        inactiveLabel = null;
        renderedOnUserRole = null;
        style = null;
        styleClass = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);
        if (activeLabel != null) {
            if (isValueReference(activeLabel)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(activeLabel);
                _component.setValueBinding("activeLabel", _vb);
            } else {
                _component.getAttributes().put("activeLabel", activeLabel);
            }
        }
        if (cautionLabel != null) {
            if (isValueReference(cautionLabel)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(cautionLabel);
                _component.setValueBinding("cautionLabel", _vb);
            } else {
                _component.getAttributes().put("cautionLabel", cautionLabel);
            }
        }
        if (disconnectedLabel != null) {
            if (isValueReference(disconnectedLabel)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(disconnectedLabel);
                _component.setValueBinding("disconnectedLabel", _vb);
            } else {
                _component.getAttributes()
                        .put("disconnectedLabel", disconnectedLabel);
            }
        }
        if (inactiveLabel != null) {
            if (isValueReference(inactiveLabel)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(inactiveLabel);
                _component.setValueBinding("inactiveLabel", _vb);
            } else {
                _component.getAttributes().put("inactiveLabel", inactiveLabel);
            }
        }
        if (renderedOnUserRole != null) {
            if (isValueReference(renderedOnUserRole)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(renderedOnUserRole);
                _component.setValueBinding("renderedOnUserRole", _vb);
            } else {
                _component.getAttributes()
                        .put("renderedOnUserRole", renderedOnUserRole);
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
    }

    // activeLabel
    private String activeLabel = null;

    public void setActiveLabel(String activeLabel) {
        this.activeLabel = activeLabel;
    }

    // cautionLabel
    private String cautionLabel = null;

    public void setCautionLabel(String cautionLabel) {
        this.cautionLabel = cautionLabel;
    }

    // disconnectedLabel
    private String disconnectedLabel = null;

    public void setDisconnectedLabel(String disconnectedLabel) {
        this.disconnectedLabel = disconnectedLabel;
    }

    // inactiveLabel
    private String inactiveLabel = null;

    public void setInactiveLabel(String inactiveLabel) {
        this.inactiveLabel = inactiveLabel;
    }

    // renderedOnUserRole
    private String renderedOnUserRole = null;

    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
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
