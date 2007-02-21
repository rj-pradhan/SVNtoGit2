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
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;

/**
 * <p>Auto-generated component tag class. Do <strong>NOT</strong> modify; all
 * changes <strong>will</strong> be lost!</p>
 */

public class SelectOneRadioTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.HtmlSelectOneRadio";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.Radio";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        autocomplete = null;
        disabled = null;
        disabledClass = null;
        enabledClass = null;
        enabledOnUserRole = null;
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
        partialSubmit = null;
        renderedOnUserRole = null;
        style = null;
        styleClass = null;
        value = null;
        valueChangeListener = null;
        visible = null;
        accesskey = null;
        border = null;
        converter = null;
        dir = null;
        immediate = null;
        lang = null;
        layout = null;
        onblur = null;
        onchange = null;
        onclick = null;
        ondblclick = null;
        onfocus = null;
        onkeydown = null;
        onkeypress = null;
        onkeyup = null;
        onmousedown = null;
        onmousemove = null;
        onmouseout = null;
        onmouseover = null;
        onmouseup = null;
        onselect = null;
        readonly = null;
        required = null;
        tabindex = null;
        title = null;
        validator = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);
        if (autocomplete != null) {
            if (isValueReference(autocomplete)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(autocomplete);
                _component.setValueBinding("autocomplete", _vb);
            } else {
                _component.getAttributes().put("autocomplete", autocomplete);
            }
        }
        if (disabled != null) {
            if (isValueReference(disabled)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(disabled);
                _component.setValueBinding("disabled", _vb);
            } else {
                _component.getAttributes()
                        .put("disabled", Boolean.valueOf(disabled));
            }
        }
        if (disabledClass != null) {
            if (isValueReference(disabledClass)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(disabledClass);
                _component.setValueBinding("disabledClass", _vb);
            } else {
                _component.getAttributes().put("disabledClass", disabledClass);
            }
        }
        if (enabledClass != null) {
            if (isValueReference(enabledClass)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(enabledClass);
                _component.setValueBinding("enabledClass", _vb);
            } else {
                _component.getAttributes().put("enabledClass", enabledClass);
            }
        }
        if (enabledOnUserRole != null) {
            if (isValueReference(enabledOnUserRole)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(enabledOnUserRole);
                _component.setValueBinding("enabledOnUserRole", _vb);
            } else {
                _component.getAttributes()
                        .put("enabledOnUserRole", enabledOnUserRole);
            }
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
        if (partialSubmit != null) {
            if (isValueReference(partialSubmit)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(partialSubmit);
                _component.setValueBinding("partialSubmit", _vb);
            } else {
                _component.getAttributes()
                        .put("partialSubmit", Boolean.valueOf(partialSubmit));
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
        if (value != null) {
            if (isValueReference(value)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(value);
                _component.setValueBinding("value", _vb);
            } else {
                _component.getAttributes().put("value", value);
            }
        }
        if (valueChangeListener != null) {
            if (isValueReference(valueChangeListener)) {
                MethodBinding _mb = getFacesContext().getApplication()
                        .createMethodBinding(valueChangeListener,
                                             valueChangeListenerArgs);
                _component.getAttributes().put("valueChangeListener", _mb);
            } else {
                throw new IllegalArgumentException(valueChangeListener);
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
        if (accesskey != null) {
            if (isValueReference(accesskey)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(accesskey);
                _component.setValueBinding("accesskey", _vb);
            } else {
                _component.getAttributes().put("accesskey", accesskey);
            }
        }
        if (border != null) {
            if (isValueReference(border)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(border);
                _component.setValueBinding("border", _vb);
            } else {
                _component.getAttributes()
                        .put("border", Integer.valueOf(border));
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
        if (dir != null) {
            if (isValueReference(dir)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(dir);
                _component.setValueBinding("dir", _vb);
            } else {
                _component.getAttributes().put("dir", dir);
            }
        }
        if (immediate != null) {
            if (isValueReference(immediate)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(immediate);
                _component.setValueBinding("immediate", _vb);
            } else {
                _component.getAttributes()
                        .put("immediate", Boolean.valueOf(immediate));
            }
        }
        if (lang != null) {
            if (isValueReference(lang)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(lang);
                _component.setValueBinding("lang", _vb);
            } else {
                _component.getAttributes().put("lang", lang);
            }
        }
        if (layout != null) {
            if (isValueReference(layout)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(layout);
                _component.setValueBinding("layout", _vb);
            } else {
                _component.getAttributes().put("layout", layout);
            }
        }
        if (onblur != null) {
            if (isValueReference(onblur)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onblur);
                _component.setValueBinding("onblur", _vb);
            } else {
                _component.getAttributes().put("onblur", onblur);
            }
        }
        if (onchange != null) {
            if (isValueReference(onchange)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onchange);
                _component.setValueBinding("onchange", _vb);
            } else {
                _component.getAttributes().put("onchange", onchange);
            }
        }
        if (onclick != null) {
            if (isValueReference(onclick)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onclick);
                _component.setValueBinding("onclick", _vb);
            } else {
                _component.getAttributes().put("onclick", onclick);
            }
        }
        if (ondblclick != null) {
            if (isValueReference(ondblclick)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(ondblclick);
                _component.setValueBinding("ondblclick", _vb);
            } else {
                _component.getAttributes().put("ondblclick", ondblclick);
            }
        }
        if (onfocus != null) {
            if (isValueReference(onfocus)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onfocus);
                _component.setValueBinding("onfocus", _vb);
            } else {
                _component.getAttributes().put("onfocus", onfocus);
            }
        }
        if (onkeydown != null) {
            if (isValueReference(onkeydown)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onkeydown);
                _component.setValueBinding("onkeydown", _vb);
            } else {
                _component.getAttributes().put("onkeydown", onkeydown);
            }
        }
        if (onkeypress != null) {
            if (isValueReference(onkeypress)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onkeypress);
                _component.setValueBinding("onkeypress", _vb);
            } else {
                _component.getAttributes().put("onkeypress", onkeypress);
            }
        }
        if (onkeyup != null) {
            if (isValueReference(onkeyup)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onkeyup);
                _component.setValueBinding("onkeyup", _vb);
            } else {
                _component.getAttributes().put("onkeyup", onkeyup);
            }
        }
        if (onmousedown != null) {
            if (isValueReference(onmousedown)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onmousedown);
                _component.setValueBinding("onmousedown", _vb);
            } else {
                _component.getAttributes().put("onmousedown", onmousedown);
            }
        }
        if (onmousemove != null) {
            if (isValueReference(onmousemove)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onmousemove);
                _component.setValueBinding("onmousemove", _vb);
            } else {
                _component.getAttributes().put("onmousemove", onmousemove);
            }
        }
        if (onmouseout != null) {
            if (isValueReference(onmouseout)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onmouseout);
                _component.setValueBinding("onmouseout", _vb);
            } else {
                _component.getAttributes().put("onmouseout", onmouseout);
            }
        }
        if (onmouseover != null) {
            if (isValueReference(onmouseover)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onmouseover);
                _component.setValueBinding("onmouseover", _vb);
            } else {
                _component.getAttributes().put("onmouseover", onmouseover);
            }
        }
        if (onmouseup != null) {
            if (isValueReference(onmouseup)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onmouseup);
                _component.setValueBinding("onmouseup", _vb);
            } else {
                _component.getAttributes().put("onmouseup", onmouseup);
            }
        }
        if (onselect != null) {
            if (isValueReference(onselect)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onselect);
                _component.setValueBinding("onselect", _vb);
            } else {
                _component.getAttributes().put("onselect", onselect);
            }
        }
        if (readonly != null) {
            if (isValueReference(readonly)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(readonly);
                _component.setValueBinding("readonly", _vb);
            } else {
                _component.getAttributes()
                        .put("readonly", Boolean.valueOf(readonly));
            }
        }
        if (required != null) {
            if (isValueReference(required)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(required);
                _component.setValueBinding("required", _vb);
            } else {
                _component.getAttributes()
                        .put("required", Boolean.valueOf(required));
            }
        }
        if (tabindex != null) {
            if (isValueReference(tabindex)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(tabindex);
                _component.setValueBinding("tabindex", _vb);
            } else {
                _component.getAttributes().put("tabindex", tabindex);
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
        if (validator != null) {
            if (isValueReference(validator)) {
                MethodBinding _mb = getFacesContext().getApplication()
                        .createMethodBinding(validator, validatorArgs);
                _component.getAttributes().put("validator", _mb);
            } else {
                throw new IllegalArgumentException(validator);
            }
        }
    }

    // autocomplete
    private String autocomplete = null;

    public void setAutocomplete(String autocomplete) {
        this.autocomplete = autocomplete;
    }

    // disabled
    private String disabled = null;

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    // disabledClass
    private String disabledClass = null;

    public void setDisabledClass(String disabledClass) {
        this.disabledClass = disabledClass;
    }

    // enabledClass
    private String enabledClass = null;

    public void setEnabledClass(String enabledClass) {
        this.enabledClass = enabledClass;
    }

    // enabledOnUserRole
    private String enabledOnUserRole = null;

    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
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

    // partialSubmit
    private String partialSubmit = null;

    public void setPartialSubmit(String partialSubmit) {
        this.partialSubmit = partialSubmit;
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

    // valueChangeListener
    private String valueChangeListener = null;

    public void setValueChangeListener(String valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }

    // visible
    private String visible = null;

    public void setVisible(String visible) {
        this.visible = visible;
    }

    // accesskey
    private String accesskey = null;

    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    // border
    private String border = null;

    public void setBorder(String border) {
        this.border = border;
    }

    // converter
    private String converter = null;

    public void setConverter(String converter) {
        this.converter = converter;
    }

    // dir
    private String dir = null;

    public void setDir(String dir) {
        this.dir = dir;
    }

    // immediate
    private String immediate = null;

    public void setImmediate(String immediate) {
        this.immediate = immediate;
    }

    // lang
    private String lang = null;

    public void setLang(String lang) {
        this.lang = lang;
    }

    // layout
    private String layout = null;

    public void setLayout(String layout) {
        this.layout = layout;
    }

    // onblur
    private String onblur = null;

    public void setOnblur(String onblur) {
        this.onblur = onblur;
    }

    // onchange
    private String onchange = null;

    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    // onclick
    private String onclick = null;

    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    // ondblclick
    private String ondblclick = null;

    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }

    // onfocus
    private String onfocus = null;

    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }

    // onkeydown
    private String onkeydown = null;

    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }

    // onkeypress
    private String onkeypress = null;

    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    // onkeyup
    private String onkeyup = null;

    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    // onmousedown
    private String onmousedown = null;

    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }

    // onmousemove
    private String onmousemove = null;

    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }

    // onmouseout
    private String onmouseout = null;

    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }

    // onmouseover
    private String onmouseover = null;

    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }

    // onmouseup
    private String onmouseup = null;

    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }

    // onselect
    private String onselect = null;

    public void setOnselect(String onselect) {
        this.onselect = onselect;
    }

    // readonly
    private String readonly = null;

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    // required
    private String required = null;

    public void setRequired(String required) {
        this.required = required;
    }

    // tabindex
    private String tabindex = null;

    public void setTabindex(String tabindex) {
        this.tabindex = tabindex;
    }

    // title
    private String title = null;

    public void setTitle(String title) {
        this.title = title;
    }

    // validator
    private String validator = null;

    public void setValidator(String validator) {
        this.validator = validator;
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
