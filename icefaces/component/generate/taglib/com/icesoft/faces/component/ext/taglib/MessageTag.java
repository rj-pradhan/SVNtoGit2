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

package com.icesoft.faces.component.ext.taglib;

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

public class MessageTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.HtmlMessage";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.Message";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        effect = null;
        renderedOnUserRole = null;
        style = null;
        styleClass = null;
        visible = null;
        errorClass = null;
        errorStyle = null;
        fatalClass = null;
        fatalStyle = null;
        _for = null;
        infoClass = null;
        infoStyle = null;
        showDetail = null;
        showSummary = null;
        title = null;
        tooltip = null;
        warnClass = null;
        warnStyle = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);
        if (effect != null) {
            if (isValueReference(effect)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(effect);
                _component.setValueBinding("effect", _vb);
            } else {
                _component.getAttributes().put("effect", effect);
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
        if (visible != null) {
            if (isValueReference(visible)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(visible);
                _component.setValueBinding("visible", _vb);
            } else {
                _component.getAttributes()
                        .put("visible", Boolean.valueOf(visible));
            }
        }
        if (errorClass != null) {
            if (isValueReference(errorClass)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(errorClass);
                _component.setValueBinding("errorClass", _vb);
            } else {
                _component.getAttributes().put("errorClass", errorClass);
            }
        }
        if (errorStyle != null) {
            if (isValueReference(errorStyle)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(errorStyle);
                _component.setValueBinding("errorStyle", _vb);
            } else {
                _component.getAttributes().put("errorStyle", errorStyle);
            }
        }
        if (fatalClass != null) {
            if (isValueReference(fatalClass)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(fatalClass);
                _component.setValueBinding("fatalClass", _vb);
            } else {
                _component.getAttributes().put("fatalClass", fatalClass);
            }
        }
        if (fatalStyle != null) {
            if (isValueReference(fatalStyle)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(fatalStyle);
                _component.setValueBinding("fatalStyle", _vb);
            } else {
                _component.getAttributes().put("fatalStyle", fatalStyle);
            }
        }
        if (_for != null) {
            if (isValueReference(_for)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(_for);
                _component.setValueBinding("for", _vb);
            } else {
                _component.getAttributes().put("for", _for);
            }
        }
        if (infoClass != null) {
            if (isValueReference(infoClass)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(infoClass);
                _component.setValueBinding("infoClass", _vb);
            } else {
                _component.getAttributes().put("infoClass", infoClass);
            }
        }
        if (infoStyle != null) {
            if (isValueReference(infoStyle)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(infoStyle);
                _component.setValueBinding("infoStyle", _vb);
            } else {
                _component.getAttributes().put("infoStyle", infoStyle);
            }
        }
        if (showDetail != null) {
            if (isValueReference(showDetail)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(showDetail);
                _component.setValueBinding("showDetail", _vb);
            } else {
                _component.getAttributes()
                        .put("showDetail", Boolean.valueOf(showDetail));
            }
        }
        if (showSummary != null) {
            if (isValueReference(showSummary)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(showSummary);
                _component.setValueBinding("showSummary", _vb);
            } else {
                _component.getAttributes()
                        .put("showSummary", Boolean.valueOf(showSummary));
            }
        }
        if (title != null) {
            if (isValueReference(title)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(title);
                _component.setValueBinding("title", _vb);
            } else {
                _component.getAttributes().put("title", title);
            }
        }
        if (tooltip != null) {
            if (isValueReference(tooltip)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(tooltip);
                _component.setValueBinding("tooltip", _vb);
            } else {
                _component.getAttributes()
                        .put("tooltip", Boolean.valueOf(tooltip));
            }
        }
        if (warnClass != null) {
            if (isValueReference(warnClass)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(warnClass);
                _component.setValueBinding("warnClass", _vb);
            } else {
                _component.getAttributes().put("warnClass", warnClass);
            }
        }
        if (warnStyle != null) {
            if (isValueReference(warnStyle)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(warnStyle);
                _component.setValueBinding("warnStyle", _vb);
            } else {
                _component.getAttributes().put("warnStyle", warnStyle);
            }
        }
    }

    // effect
    private String effect = null;

    public void setEffect(String effect) {
        this.effect = effect;
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

    // visible
    private String visible = null;

    public void setVisible(String visible) {
        this.visible = visible;
    }

    // errorClass
    private String errorClass = null;

    public void setErrorClass(String errorClass) {
        this.errorClass = errorClass;
    }

    // errorStyle
    private String errorStyle = null;

    public void setErrorStyle(String errorStyle) {
        this.errorStyle = errorStyle;
    }

    // fatalClass
    private String fatalClass = null;

    public void setFatalClass(String fatalClass) {
        this.fatalClass = fatalClass;
    }

    // fatalStyle
    private String fatalStyle = null;

    public void setFatalStyle(String fatalStyle) {
        this.fatalStyle = fatalStyle;
    }

    // for
    private String _for = null;

    public void setFor(String _for) {
        this._for = _for;
    }

    // infoClass
    private String infoClass = null;

    public void setInfoClass(String infoClass) {
        this.infoClass = infoClass;
    }

    // infoStyle
    private String infoStyle = null;

    public void setInfoStyle(String infoStyle) {
        this.infoStyle = infoStyle;
    }

    // showDetail
    private String showDetail = null;

    public void setShowDetail(String showDetail) {
        this.showDetail = showDetail;
    }

    // showSummary
    private String showSummary = null;

    public void setShowSummary(String showSummary) {
        this.showSummary = showSummary;
    }

    // title
    private String title = null;

    public void setTitle(String title) {
        this.title = title;
    }

    // tooltip
    private String tooltip = null;

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    // warnClass
    private String warnClass = null;

    public void setWarnClass(String warnClass) {
        this.warnClass = warnClass;
    }

    // warnStyle
    private String warnStyle = null;

    public void setWarnStyle(String warnStyle) {
        this.warnStyle = warnStyle;
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
