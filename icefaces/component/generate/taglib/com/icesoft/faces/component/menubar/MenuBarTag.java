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

package com.icesoft.faces.component.menubar;

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

public class MenuBarTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.Menu";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.Menu";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        action = null;
        actionListener = null;
        imageDir = null;
        immediate = null;
        orientation = null;
        style = null;
        value = null;
        noIcons = null;
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
                MethodBinding _mb =
                        new com.icesoft.faces.component.ext.taglib.MethodBindingString(
                                action);
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
        if (imageDir != null) {
            if (isValueReference(imageDir)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(imageDir);
                _component.setValueBinding("imageDir", _vb);
            } else {
                _component.getAttributes().put("imageDir", imageDir);
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
        if (orientation != null) {
            if (isValueReference(orientation)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(orientation);
                _component.setValueBinding("orientation", _vb);
            } else {
                _component.getAttributes().put("orientation", orientation);
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
        if (value != null) {
            if (isValueReference(value)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(value);
                _component.setValueBinding("value", _vb);
            } else {
                _component.getAttributes().put("value", value);
            }
        }
        if (noIcons != null) {
            if (isValueReference(noIcons)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(noIcons);
                _component.setValueBinding("noIcons", _vb);
            } else {
                _component.getAttributes().put("noIcons", noIcons);
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

    // imageDir
    private String imageDir = null;

    public void setImageDir(String imageDir) {
        this.imageDir = imageDir;
    }

    // immediate
    private String immediate = null;

    public void setImmediate(String immediate) {
        this.immediate = immediate;
    }

    // orientation
    private String orientation = null;

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

    // style
    private String style = null;

    public void setStyle(String style) {
        this.style = style;
    }

    // value
    private String value = null;

    public void setValue(String value) {
        this.value = value;
    }

    // noIcons
    private String noIcons = null;

    public void setNoIcons(String noIcons) {
        this.noIcons = noIcons;
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
