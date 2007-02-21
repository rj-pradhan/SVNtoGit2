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

package com.icesoft.faces.component.inputfile;

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

public class InputFileTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.File";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.Upload";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        alt = null;
        buttonClass = null;
        disabled = null;
        enabledOnUserRole = null;
        file = null;
        filename = null;
        label = null;
        maxlength = null;
        mimeType = null;
        progressListener = null;
        readonly = null;
        renderedOnUserRole = null;
        size = null;
        style = null;
        styleClass = null;
        uniqueFolder = null;
        action = null;
        actionListener = null;
        immediate = null;
        value = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);
        if (alt != null) {
            if (isValueReference(alt)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(alt);
                _component.setValueBinding("alt", _vb);
            } else {
                _component.getAttributes().put("alt", alt);
            }
        }
        if (buttonClass != null) {
            if (isValueReference(buttonClass)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(buttonClass);
                _component.setValueBinding("buttonClass", _vb);
            } else {
                _component.getAttributes().put("buttonClass", buttonClass);
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
        if (file != null) {
            if (isValueReference(file)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(file);
                _component.setValueBinding("file", _vb);
            } else {
                _component.getAttributes().put("file", file);
            }
        }
        if (filename != null) {
            if (isValueReference(filename)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(filename);
                _component.setValueBinding("filename", _vb);
            } else {
                _component.getAttributes().put("filename", filename);
            }
        }
        if (label != null) {
            if (isValueReference(label)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(label);
                _component.setValueBinding("label", _vb);
            } else {
                _component.getAttributes().put("label", label);
            }
        }
        if (maxlength != null) {
            if (isValueReference(maxlength)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(maxlength);
                _component.setValueBinding("maxlength", _vb);
            } else {
                _component.getAttributes().put("maxlength", maxlength);
            }
        }
        if (mimeType != null) {
            if (isValueReference(mimeType)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(mimeType);
                _component.setValueBinding("mimeType", _vb);
            } else {
                _component.getAttributes().put("mimeType", mimeType);
            }
        }
        if (progressListener != null) {
            if (isValueReference(progressListener)) {
                Class progressListenerArgs[] = {java.util.EventObject.class};
                MethodBinding _mb = getFacesContext().getApplication()
                        .createMethodBinding(progressListener,
                                             progressListenerArgs);
                _component.getAttributes().put("progressListener", _mb);
            } else {
                throw new IllegalArgumentException(progressListener);
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
        if (size != null) {
            if (isValueReference(size)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(size);
                _component.setValueBinding("size", _vb);
            } else {
                _component.getAttributes().put("size", size);
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
        if (uniqueFolder != null) {
            if (isValueReference(uniqueFolder)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(uniqueFolder);
                _component.setValueBinding("uniqueFolder", _vb);
            } else {
                _component.getAttributes()
                        .put("uniqueFolder", Boolean.valueOf(uniqueFolder));
            }
        }
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

    // alt
    private String alt = null;

    public void setAlt(String alt) {
        this.alt = alt;
    }

    // buttonClass
    private String buttonClass = null;

    public void setButtonClass(String buttonClass) {
        this.buttonClass = buttonClass;
    }

    // disabled
    private String disabled = null;

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    // enabledOnUserRole
    private String enabledOnUserRole = null;

    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
    }

    // file
    private String file = null;

    public void setFile(String file) {
        this.file = file;
    }

    // filename
    private String filename = null;

    public void setFilename(String filename) {
        this.filename = filename;
    }

    // label
    private String label = null;

    public void setLabel(String label) {
        this.label = label;
    }

    // maxlength
    private String maxlength = null;

    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    // mimeType
    private String mimeType = null;

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    // progressListener
    private String progressListener = null;

    public void setProgressListener(String progressListener) {
        this.progressListener = progressListener;
    }

    // readonly
    private String readonly = null;

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    // renderedOnUserRole
    private String renderedOnUserRole = null;

    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    // size
    private String size = null;

    public void setSize(String size) {
        this.size = size;
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

    // uniqueFolder
    private String uniqueFolder = null;

    public void setUniqueFolder(String uniqueFolder) {
        this.uniqueFolder = uniqueFolder;
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

    // immediate
    private String immediate = null;

    public void setImmediate(String immediate) {
        this.immediate = immediate;
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
