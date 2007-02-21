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

package com.icesoft.faces.component.panelpopup;

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

public class PanelPopupTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.PanelPopup";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.PanelPopup";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        draggable = null;
        modal = null;
        renderedOnUserRole = null;
        resizable = null;
        style = null;
        styleClass = null;
        title = null;
        visible = null;
        dragListener = null;
        dragMask = null;
        dragOptions = null;
        dragValue = null;
        dropListener = null;
        dropMask = null;
        dropTarget = null;
        dropValue = null;
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
        onreseteffect = null;
        onsubmiteffect = null;
        renderedStyle = null;
        scrollHeight = null;
        scrollWidth = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);
        if (draggable != null) {
            if (isValueReference(draggable)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(draggable);
                _component.setValueBinding("draggable", _vb);
            } else {
                _component.getAttributes().put("draggable", draggable);
            }
        }
        if (modal != null) {
            if (isValueReference(modal)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(modal);
                _component.setValueBinding("modal", _vb);
            } else {
                _component.getAttributes().put("modal", Boolean.valueOf(modal));
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
        if (resizable != null) {
            if (isValueReference(resizable)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(resizable);
                _component.setValueBinding("resizable", _vb);
            } else {
                _component.getAttributes()
                        .put("resizable", Boolean.valueOf(resizable));
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
        if (title != null) {
            if (isValueReference(title)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(title);
                _component.setValueBinding("title", _vb);
            } else {
                _component.getAttributes().put("title", title);
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
        if (dragListener != null) {
            _component.getAttributes().put("dragListener", dragListener);
        }
        if (dragMask != null) {
            if (isValueReference(dragMask)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(dragMask);
                _component.setValueBinding("dragMask", _vb);
            } else {
                _component.getAttributes().put("dragMask", dragMask);
            }
        }
        if (dragOptions != null) {
            if (isValueReference(dragOptions)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(dragOptions);
                _component.setValueBinding("dragOptions", _vb);
            } else {
                _component.getAttributes().put("dragOptions", dragOptions);
            }
        }
        if (dragValue != null) {
            if (isValueReference(dragValue)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(dragValue);
                _component.setValueBinding("dragValue", _vb);
            } else {
                _component.getAttributes().put("dragValue", dragValue);
            }
        }
        if (dropListener != null) {
            _component.getAttributes().put("dropListener", dropListener);
        }
        if (dropMask != null) {
            if (isValueReference(dropMask)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(dropMask);
                _component.setValueBinding("dropMask", _vb);
            } else {
                _component.getAttributes().put("dropMask", dropMask);
            }
        }
        if (dropTarget != null) {
            if (isValueReference(dropTarget)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(dropTarget);
                _component.setValueBinding("dropTarget", _vb);
            } else {
                _component.getAttributes().put("dropTarget", dropTarget);
            }
        }
        if (dropValue != null) {
            if (isValueReference(dropValue)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(dropValue);
                _component.setValueBinding("dropValue", _vb);
            } else {
                _component.getAttributes().put("dropValue", dropValue);
            }
        }
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
        if (onreseteffect != null) {
            com.icesoft.faces.component.ext.taglib.Util
                    .addLocalEffect(onreseteffect, "onreseteffect", _component);
        }
        if (onsubmiteffect != null) {
            com.icesoft.faces.component.ext.taglib.Util.addLocalEffect(
                    onsubmiteffect, "onsubmiteffect", _component);
        }
        if (renderedStyle != null) {
            if (isValueReference(renderedStyle)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(renderedStyle);
                _component.setValueBinding("renderedStyle", _vb);
            } else {
                _component.getAttributes().put("renderedStyle", renderedStyle);
            }
        }
        if (scrollHeight != null) {
            if (isValueReference(scrollHeight)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(scrollHeight);
                _component.setValueBinding("scrollHeight", _vb);
            } else {
                _component.getAttributes().put("scrollHeight", scrollHeight);
            }
        }
        if (scrollWidth != null) {
            if (isValueReference(scrollWidth)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(scrollWidth);
                _component.setValueBinding("scrollWidth", _vb);
            } else {
                _component.getAttributes().put("scrollWidth", scrollWidth);
            }
        }
    }

    // draggable
    private String draggable = null;

    public void setDraggable(String draggable) {
        this.draggable = draggable;
    }

    // modal
    private String modal = null;

    public void setModal(String modal) {
        this.modal = modal;
    }

    // renderedOnUserRole
    private String renderedOnUserRole = null;

    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    // resizable
    private String resizable = null;

    public void setResizable(String resizable) {
        this.resizable = resizable;
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

    // title
    private String title = null;

    public void setTitle(String title) {
        this.title = title;
    }

    // visible
    private String visible = null;

    public void setVisible(String visible) {
        this.visible = visible;
    }

    // dragListener
    private String dragListener = null;

    public void setDragListener(String dragListener) {
        this.dragListener = dragListener;
    }

    // dragMask
    private String dragMask = null;

    public void setDragMask(String dragMask) {
        this.dragMask = dragMask;
    }

    // dragOptions
    private String dragOptions = null;

    public void setDragOptions(String dragOptions) {
        this.dragOptions = dragOptions;
    }

    // dragValue
    private String dragValue = null;

    public void setDragValue(String dragValue) {
        this.dragValue = dragValue;
    }

    // dropListener
    private String dropListener = null;

    public void setDropListener(String dropListener) {
        this.dropListener = dropListener;
    }

    // dropMask
    private String dropMask = null;

    public void setDropMask(String dropMask) {
        this.dropMask = dropMask;
    }

    // dropTarget
    private String dropTarget = null;

    public void setDropTarget(String dropTarget) {
        this.dropTarget = dropTarget;
    }

    // dropValue
    private String dropValue = null;

    public void setDropValue(String dropValue) {
        this.dropValue = dropValue;
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

    // onreseteffect
    private String onreseteffect = null;

    public void setOnreseteffect(String onreseteffect) {
        this.onreseteffect = onreseteffect;
    }

    // onsubmiteffect
    private String onsubmiteffect = null;

    public void setOnsubmiteffect(String onsubmiteffect) {
        this.onsubmiteffect = onsubmiteffect;
    }

    // renderedStyle
    private String renderedStyle = null;

    public void setRenderedStyle(String renderedStyle) {
        this.renderedStyle = renderedStyle;
    }

    // scrollHeight
    private String scrollHeight = null;

    public void setScrollHeight(String scrollHeight) {
        this.scrollHeight = scrollHeight;
    }

    // scrollWidth
    private String scrollWidth = null;

    public void setScrollWidth(String scrollWidth) {
        this.scrollWidth = scrollWidth;
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
