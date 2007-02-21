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

public class OutputTextTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.HtmlOutputText";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.Text";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        effect = null;
        onchangeeffect = null;
        onclickeffect = null;
        ondblclickeffect = null;
        onkeydowneffect = null;
        onkeypresseffect = null;
        onkeyupeffect = null;
        onmousedowneffect = null;
        onmousemoveeffect = null;
        onmouseouteffect = null;
        onmouseovereffect = null;
        onmouseupeffect = null;
        renderedOnUserRole = null;
        style = null;
        styleClass = null;
        value = null;
        visible = null;
        converter = null;
        escape = null;
        title = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);
        if (effect != null) {
            com.icesoft.faces.component.ext.taglib.Util
                    .addEffect(effect, _component);
        }
        if (onchangeeffect != null) {
            com.icesoft.faces.component.ext.taglib.Util.addLocalEffect(
                    onchangeeffect, "onchangeeffect", _component);
        }
        if (onclickeffect != null) {
            com.icesoft.faces.component.ext.taglib.Util
                    .addLocalEffect(onclickeffect, "onclickeffect", _component);
        }
        if (ondblclickeffect != null) {
            com.icesoft.faces.component.ext.taglib.Util.addLocalEffect(
                    ondblclickeffect, "ondblclickeffect", _component);
        }
        if (onkeydowneffect != null) {
            com.icesoft.faces.component.ext.taglib.Util.addLocalEffect(
                    onkeydowneffect, "onkeydowneffect", _component);
        }
        if (onkeypresseffect != null) {
            com.icesoft.faces.component.ext.taglib.Util.addLocalEffect(
                    onkeypresseffect, "onkeypresseffect", _component);
        }
        if (onkeyupeffect != null) {
            com.icesoft.faces.component.ext.taglib.Util
                    .addLocalEffect(onkeyupeffect, "onkeyupeffect", _component);
        }
        if (onmousedowneffect != null) {
            com.icesoft.faces.component.ext.taglib.Util.addLocalEffect(
                    onmousedowneffect, "onmousedowneffect", _component);
        }
        if (onmousemoveeffect != null) {
            com.icesoft.faces.component.ext.taglib.Util.addLocalEffect(
                    onmousemoveeffect, "onmousemoveeffect", _component);
        }
        if (onmouseouteffect != null) {
            com.icesoft.faces.component.ext.taglib.Util.addLocalEffect(
                    onmouseouteffect, "onmouseouteffect", _component);
        }
        if (onmouseovereffect != null) {
            com.icesoft.faces.component.ext.taglib.Util.addLocalEffect(
                    onmouseovereffect, "onmouseovereffect", _component);
        }
        if (onmouseupeffect != null) {
            com.icesoft.faces.component.ext.taglib.Util.addLocalEffect(
                    onmouseupeffect, "onmouseupeffect", _component);
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
        if (value != null) {
            if (isValueReference(value)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(value);
                _component.setValueBinding("value", _vb);
            } else {
                _component.getAttributes().put("value", value);
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
        if (escape != null) {
            if (isValueReference(escape)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(escape);
                _component.setValueBinding("escape", _vb);
            } else {
                _component.getAttributes()
                        .put("escape", Boolean.valueOf(escape));
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
    }

    // effect
    private String effect = null;

    public void setEffect(String effect) {
        this.effect = effect;
    }

    // onchangeeffect
    private String onchangeeffect = null;

    public void setOnchangeeffect(String onchangeeffect) {
        this.onchangeeffect = onchangeeffect;
    }

    // onclickeffect
    private String onclickeffect = null;

    public void setOnclickeffect(String onclickeffect) {
        this.onclickeffect = onclickeffect;
    }

    // ondblclickeffect
    private String ondblclickeffect = null;

    public void setOndblclickeffect(String ondblclickeffect) {
        this.ondblclickeffect = ondblclickeffect;
    }

    // onkeydowneffect
    private String onkeydowneffect = null;

    public void setOnkeydowneffect(String onkeydowneffect) {
        this.onkeydowneffect = onkeydowneffect;
    }

    // onkeypresseffect
    private String onkeypresseffect = null;

    public void setOnkeypresseffect(String onkeypresseffect) {
        this.onkeypresseffect = onkeypresseffect;
    }

    // onkeyupeffect
    private String onkeyupeffect = null;

    public void setOnkeyupeffect(String onkeyupeffect) {
        this.onkeyupeffect = onkeyupeffect;
    }

    // onmousedowneffect
    private String onmousedowneffect = null;

    public void setOnmousedowneffect(String onmousedowneffect) {
        this.onmousedowneffect = onmousedowneffect;
    }

    // onmousemoveeffect
    private String onmousemoveeffect = null;

    public void setOnmousemoveeffect(String onmousemoveeffect) {
        this.onmousemoveeffect = onmousemoveeffect;
    }

    // onmouseouteffect
    private String onmouseouteffect = null;

    public void setOnmouseouteffect(String onmouseouteffect) {
        this.onmouseouteffect = onmouseouteffect;
    }

    // onmouseovereffect
    private String onmouseovereffect = null;

    public void setOnmouseovereffect(String onmouseovereffect) {
        this.onmouseovereffect = onmouseovereffect;
    }

    // onmouseupeffect
    private String onmouseupeffect = null;

    public void setOnmouseupeffect(String onmouseupeffect) {
        this.onmouseupeffect = onmouseupeffect;
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

    // value
    private String value = null;

    public void setValue(String value) {
        this.value = value;
    }

    // visible
    private String visible = null;

    public void setVisible(String visible) {
        this.visible = visible;
    }

    // converter
    private String converter = null;

    public void setConverter(String converter) {
        this.converter = converter;
    }

    // escape
    private String escape = null;

    public void setEscape(String escape) {
        this.escape = escape;
    }

    // title
    private String title = null;

    public void setTitle(String title) {
        this.title = title;
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
