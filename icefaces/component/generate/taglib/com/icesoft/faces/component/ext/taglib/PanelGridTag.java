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

public class PanelGridTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.HtmlPanelGrid";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.Grid";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        columns = null;
        renderedOnUserRole = null;
        style = null;
        styleClass = null;
        visible = null;
        bgcolor = null;
        border = null;
        cellpadding = null;
        cellspacing = null;
        columnClasses = null;
        dir = null;
        footerClass = null;
        frame = null;
        headerClass = null;
        lang = null;
        onclick = null;
        ondblclick = null;
        onkeydown = null;
        onkeypress = null;
        onkeyup = null;
        onmousedown = null;
        onmousemove = null;
        onmouseout = null;
        onmouseover = null;
        onmouseup = null;
        rowClasses = null;
        rules = null;
        summary = null;
        title = null;
        width = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);
        if (columns != null) {
            if (isValueReference(columns)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(columns);
                _component.setValueBinding("columns", _vb);
            } else {
                _component.getAttributes()
                        .put("columns", Integer.valueOf(columns));
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
                _component.getAttributes()
                        .put("border", Integer.valueOf(border));
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
        if (columnClasses != null) {
            if (isValueReference(columnClasses)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(columnClasses);
                _component.setValueBinding("columnClasses", _vb);
            } else {
                _component.getAttributes().put("columnClasses", columnClasses);
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
        if (footerClass != null) {
            if (isValueReference(footerClass)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(footerClass);
                _component.setValueBinding("footerClass", _vb);
            } else {
                _component.getAttributes().put("footerClass", footerClass);
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
        if (headerClass != null) {
            if (isValueReference(headerClass)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(headerClass);
                _component.setValueBinding("headerClass", _vb);
            } else {
                _component.getAttributes().put("headerClass", headerClass);
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
        if (rowClasses != null) {
            if (isValueReference(rowClasses)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(rowClasses);
                _component.setValueBinding("rowClasses", _vb);
            } else {
                _component.getAttributes().put("rowClasses", rowClasses);
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

    // columns
    private String columns = null;

    public void setColumns(String columns) {
        this.columns = columns;
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

    // columnClasses
    private String columnClasses = null;

    public void setColumnClasses(String columnClasses) {
        this.columnClasses = columnClasses;
    }

    // dir
    private String dir = null;

    public void setDir(String dir) {
        this.dir = dir;
    }

    // footerClass
    private String footerClass = null;

    public void setFooterClass(String footerClass) {
        this.footerClass = footerClass;
    }

    // frame
    private String frame = null;

    public void setFrame(String frame) {
        this.frame = frame;
    }

    // headerClass
    private String headerClass = null;

    public void setHeaderClass(String headerClass) {
        this.headerClass = headerClass;
    }

    // lang
    private String lang = null;

    public void setLang(String lang) {
        this.lang = lang;
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

    // rowClasses
    private String rowClasses = null;

    public void setRowClasses(String rowClasses) {
        this.rowClasses = rowClasses;
    }

    // rules
    private String rules = null;

    public void setRules(String rules) {
        this.rules = rules;
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
