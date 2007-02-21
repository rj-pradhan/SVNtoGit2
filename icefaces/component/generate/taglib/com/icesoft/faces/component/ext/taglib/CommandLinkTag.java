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

public class CommandLinkTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.HtmlCommandLink";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.Link";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        action = null;
        actionListener = null;
        charset = null;
        disabled = null;
        effect = null;
        enabledOnUserRole = null;
        immediate = null;

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
        visible = null;
        accesskey = null;
        coords = null;
        dir = null;
        hreflang = null;
        lang = null;
        onblur = null;
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
        rel = null;
        rev = null;
        shape = null;
        tabindex = null;
        target = null;
        title = null;
        type = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);
        if (action != null) {
            if (isValueReference(action)) {
                MethodBinding _mb = getFacesContext().getApplication()
                        .createMethodBinding(action, actionArgs);
                _component.getAttributes().put("action", _mb);
            } else {
                MethodBinding _mb = new MethodBindingString(action);
                _component.getAttributes().put("action", _mb);
            }
        }
        if (actionListener != null) {
            if (isValueReference(actionListener)) {
                MethodBinding _mb = getFacesContext().getApplication()
                        .createMethodBinding(actionListener,
                                             actionListenerArgs);
                _component.getAttributes().put("actionListener", _mb);
            } else {
                throw new IllegalArgumentException(actionListener);
            }
        }
        if (charset != null) {
            if (isValueReference(charset)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(charset);
                _component.setValueBinding("charset", _vb);
            } else {
                _component.getAttributes().put("charset", charset);
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
        if (effect != null) {
            com.icesoft.faces.component.ext.taglib.Util
                    .addEffect(effect, _component);
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
        if (coords != null) {
            if (isValueReference(coords)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(coords);
                _component.setValueBinding("coords", _vb);
            } else {
                _component.getAttributes().put("coords", coords);
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
        if (hreflang != null) {
            if (isValueReference(hreflang)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(hreflang);
                _component.setValueBinding("hreflang", _vb);
            } else {
                _component.getAttributes().put("hreflang", hreflang);
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
        if (onblur != null) {
            if (isValueReference(onblur)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(onblur);
                _component.setValueBinding("onblur", _vb);
            } else {
                _component.getAttributes().put("onblur", onblur);
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
        if (rel != null) {
            if (isValueReference(rel)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(rel);
                _component.setValueBinding("rel", _vb);
            } else {
                _component.getAttributes().put("rel", rel);
            }
        }
        if (rev != null) {
            if (isValueReference(rev)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(rev);
                _component.setValueBinding("rev", _vb);
            } else {
                _component.getAttributes().put("rev", rev);
            }
        }
        if (shape != null) {
            if (isValueReference(shape)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(shape);
                _component.setValueBinding("shape", _vb);
            } else {
                _component.getAttributes().put("shape", shape);
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
        if (target != null) {
            if (isValueReference(target)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(target);
                _component.setValueBinding("target", _vb);
            } else {
                _component.getAttributes().put("target", target);
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
        if (type != null) {
            if (isValueReference(type)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(type);
                _component.setValueBinding("type", _vb);
            } else {
                _component.getAttributes().put("type", type);
            }
        }
    }

    // action
    private String action = null;

    public void setAction(String action) {
        this.action = action;
    }

    // actionListener
    private String actionListener = null;

    public void setActionListener(String actionListener) {
        this.actionListener = actionListener;
    }

    // charset
    private String charset = null;

    public void setCharset(String charset) {
        this.charset = charset;
    }

    // disabled
    private String disabled = null;

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    // effect
    private String effect = null;

    public void setEffect(String effect) {
        this.effect = effect;
    }

    // enabledOnUserRole
    private String enabledOnUserRole = null;

    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
    }

    // immediate
    private String immediate = null;

    public void setImmediate(String immediate) {
        this.immediate = immediate;
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

    // coords
    private String coords = null;

    public void setCoords(String coords) {
        this.coords = coords;
    }

    // dir
    private String dir = null;

    public void setDir(String dir) {
        this.dir = dir;
    }

    // hreflang
    private String hreflang = null;

    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    // lang
    private String lang = null;

    public void setLang(String lang) {
        this.lang = lang;
    }

    // onblur
    private String onblur = null;

    public void setOnblur(String onblur) {
        this.onblur = onblur;
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

    // rel
    private String rel = null;

    public void setRel(String rel) {
        this.rel = rel;
    }

    // rev
    private String rev = null;

    public void setRev(String rev) {
        this.rev = rev;
    }

    // shape
    private String shape = null;

    public void setShape(String shape) {
        this.shape = shape;
    }

    // tabindex
    private String tabindex = null;

    public void setTabindex(String tabindex) {
        this.tabindex = tabindex;
    }

    // target
    private String target = null;

    public void setTarget(String target) {
        this.target = target;
    }

    // title
    private String title = null;

    public void setTitle(String title) {
        this.title = title;
    }

    // type
    private String type = null;

    public void setType(String type) {
        this.type = type;
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
