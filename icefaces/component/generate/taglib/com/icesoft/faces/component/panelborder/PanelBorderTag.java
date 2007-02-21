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

package com.icesoft.faces.component.panelborder;

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

public class PanelBorderTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.BorderLayout";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.BorderLayout";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        align = null;
        bgcolor = null;
        border = null;
        cellpadding = null;
        cellspacing = null;
        dir = null;
        frame = null;
        height = null;
        lang = null;
        layout = null;
        renderCenter = null;
        renderEast = null;
        renderNorth = null;
        renderSouth = null;
        renderWest = null;
        renderedOnUserRole = null;
        rules = null;
        style = null;
        styleClass = null;
        summary = null;
        title = null;
        width = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);
        if (align != null) {
            if (isValueReference(align)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(align);
                _component.setValueBinding("align", _vb);
            } else {
                _component.getAttributes().put("align", align);
            }
        }
        if (bgcolor != null) {
            if (isValueReference(bgcolor)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(bgcolor);
                _component.setValueBinding("bgcolor", _vb);
            } else {
                _component.getAttributes().put("bgcolor", bgcolor);
            }
        }
        if (border != null) {
            if (isValueReference(border)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(border);
                _component.setValueBinding("border", _vb);
            } else {
                _component.getAttributes().put("border", border);
            }
        }
        if (cellpadding != null) {
            if (isValueReference(cellpadding)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(cellpadding);
                _component.setValueBinding("cellpadding", _vb);
            } else {
                _component.getAttributes().put("cellpadding", cellpadding);
            }
        }
        if (cellspacing != null) {
            if (isValueReference(cellspacing)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(cellspacing);
                _component.setValueBinding("cellspacing", _vb);
            } else {
                _component.getAttributes().put("cellspacing", cellspacing);
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
        if (frame != null) {
            if (isValueReference(frame)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(frame);
                _component.setValueBinding("frame", _vb);
            } else {
                _component.getAttributes().put("frame", frame);
            }
        }
        if (height != null) {
            if (isValueReference(height)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(height);
                _component.setValueBinding("height", _vb);
            } else {
                _component.getAttributes().put("height", height);
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
        if (renderCenter != null) {
            if (isValueReference(renderCenter)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(renderCenter);
                _component.setValueBinding("renderCenter", _vb);
            } else {
                _component.getAttributes()
                        .put("renderCenter", Boolean.valueOf(renderCenter));
            }
        }
        if (renderEast != null) {
            if (isValueReference(renderEast)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(renderEast);
                _component.setValueBinding("renderEast", _vb);
            } else {
                _component.getAttributes()
                        .put("renderEast", Boolean.valueOf(renderEast));
            }
        }
        if (renderNorth != null) {
            if (isValueReference(renderNorth)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(renderNorth);
                _component.setValueBinding("renderNorth", _vb);
            } else {
                _component.getAttributes()
                        .put("renderNorth", Boolean.valueOf(renderNorth));
            }
        }
        if (renderSouth != null) {
            if (isValueReference(renderSouth)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(renderSouth);
                _component.setValueBinding("renderSouth", _vb);
            } else {
                _component.getAttributes()
                        .put("renderSouth", Boolean.valueOf(renderSouth));
            }
        }
        if (renderWest != null) {
            if (isValueReference(renderWest)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(renderWest);
                _component.setValueBinding("renderWest", _vb);
            } else {
                _component.getAttributes()
                        .put("renderWest", Boolean.valueOf(renderWest));
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
        if (rules != null) {
            if (isValueReference(rules)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(rules);
                _component.setValueBinding("rules", _vb);
            } else {
                _component.getAttributes().put("rules", rules);
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
        if (summary != null) {
            if (isValueReference(summary)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(summary);
                _component.setValueBinding("summary", _vb);
            } else {
                _component.getAttributes().put("summary", summary);
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
        if (width != null) {
            if (isValueReference(width)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(width);
                _component.setValueBinding("width", _vb);
            } else {
                _component.getAttributes().put("width", width);
            }
        }
    }

    // align
    private String align = null;

    public void setAlign(String align) {
        this.align = align;
    }

    // bgcolor
    private String bgcolor = null;

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    // border
    private String border = null;

    public void setBorder(String border) {
        this.border = border;
    }

    // cellpadding
    private String cellpadding = null;

    public void setCellpadding(String cellpadding) {
        this.cellpadding = cellpadding;
    }

    // cellspacing
    private String cellspacing = null;

    public void setCellspacing(String cellspacing) {
        this.cellspacing = cellspacing;
    }

    // dir
    private String dir = null;

    public void setDir(String dir) {
        this.dir = dir;
    }

    // frame
    private String frame = null;

    public void setFrame(String frame) {
        this.frame = frame;
    }

    // height
    private String height = null;

    public void setHeight(String height) {
        this.height = height;
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

    // renderCenter
    private String renderCenter = null;

    public void setRenderCenter(String renderCenter) {
        this.renderCenter = renderCenter;
    }

    // renderEast
    private String renderEast = null;

    public void setRenderEast(String renderEast) {
        this.renderEast = renderEast;
    }

    // renderNorth
    private String renderNorth = null;

    public void setRenderNorth(String renderNorth) {
        this.renderNorth = renderNorth;
    }

    // renderSouth
    private String renderSouth = null;

    public void setRenderSouth(String renderSouth) {
        this.renderSouth = renderSouth;
    }

    // renderWest
    private String renderWest = null;

    public void setRenderWest(String renderWest) {
        this.renderWest = renderWest;
    }

    // renderedOnUserRole
    private String renderedOnUserRole = null;

    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    // rules
    private String rules = null;

    public void setRules(String rules) {
        this.rules = rules;
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

    // summary
    private String summary = null;

    public void setSummary(String summary) {
        this.summary = summary;
    }

    // title
    private String title = null;

    public void setTitle(String title) {
        this.title = title;
    }

    // width
    private String width = null;

    public void setWidth(String width) {
        this.width = width;
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
