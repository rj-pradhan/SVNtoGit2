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

import com.icesoft.faces.component.ext.HtmlDataTable;

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

public class DataTableTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.HtmlDataTable";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.Table";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        border = null;
        cellpadding = null;
        cellspacing = null;
        columnClasses = null;
        first = null;
        footerClass = null;
        frame = null;
        headerClass = null;
        headerClasses = null;
        renderedOnUserRole = null;
        rowClasses = null;
        rows = null;
        rules = null;
        sortAscending = null;
        sortColumn = null;
        style = null;
        styleClass = null;
        summary = null;
        value = null;
        _var = null;
        width = null;
        bgcolor = null;
        dir = null;
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
        title = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);

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
        if (first != null) {
            if (isValueReference(first)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(first);
                _component.setValueBinding("first", _vb);
            } else {
                _component.getAttributes().put("first", Integer.valueOf(first));
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
        if (headerClasses != null) {
            if (isValueReference(headerClasses)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(headerClasses);
                _component.setValueBinding("headerClasses", _vb);
            } else {
                _component.getAttributes().put("headerClasses", headerClasses);
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
        if (rowClasses != null) {
            if (isValueReference(rowClasses)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(rowClasses);
                _component.setValueBinding("rowClasses", _vb);
            } else {
                _component.getAttributes().put("rowClasses", rowClasses);
            }
        }
        if (rows != null) {
            if (isValueReference(rows)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(rows);
                _component.setValueBinding("rows", _vb);
            } else {
                _component.getAttributes().put("rows", Integer.valueOf(rows));
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
        if (sortAscending != null) {
            if (isValueReference(sortAscending)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(sortAscending);
                _component.setValueBinding("sortAscending", _vb);
            } else {
                _component.getAttributes()
                        .put("sortAscending", Boolean.valueOf(sortAscending));
            }
        }
        if (sortColumn != null) {
            if (isValueReference(sortColumn)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(sortColumn);
                _component.setValueBinding("sortColumn", _vb);
            } else {
                _component.getAttributes().put("sortColumn", sortColumn);
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
        if (value != null) {
            if (isValueReference(value)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(value);
                _component.setValueBinding("value", _vb);
            } else {
                _component.getAttributes().put("value", value);
            }
        }
        if (_var != null) {
            try {
                javax.faces.component.UIData htmldatatable =
                        (javax.faces.component.UIData) _component;
                htmldatatable.setVar(_var);
            } catch (ClassCastException cce) {
                throw new IllegalStateException(_component.toString() +
                                                " not expected type.  Expected: javax.faces.component.UIData.  Perhaps you're missing a tag?");
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
        if (bgcolor != null) {
            if (isValueReference(bgcolor)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(bgcolor);
                _component.setValueBinding("bgcolor", _vb);
            } else {
                _component.getAttributes().put("bgcolor", bgcolor);
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
        if (title != null) {
            if (isValueReference(title)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(title);
                _component.setValueBinding("title", _vb);
            } else {
                _component.getAttributes().put("title", title);
            }
        }
        if (scrollable != null) {

            if (isValueReference(scrollable)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(scrollable);
                _component.setValueBinding("scrollable", _vb);
            } else {


                ((HtmlDataTable) _component)
                        .setScrollable(new Boolean(scrollable));

            }
        }
        if (columnWidths != null) {

            if (isValueReference(columnWidths)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(columnWidths);
                _component.setValueBinding("columnWidths", _vb);
            } else {

                ((HtmlDataTable) _component).setColumnWidths(columnWidths);
            }
        }

        if (scrollHeight != null) {

            if (isValueReference(scrollHeight)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(scrollHeight);
                _component.setValueBinding("scrollHeight", _vb);
            } else {

                ((HtmlDataTable) _component).setScrollHeight(scrollHeight);
            }
        }
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

    // first
    private String first = null;

    public void setFirst(String first) {
        this.first = first;
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

    // headerClasses
    private String headerClasses = null;

    public void setHeaderClasses(String headerClasses) {
        this.headerClasses = headerClasses;
    }

    // renderedOnUserRole
    private String renderedOnUserRole = null;

    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    // rowClasses
    private String rowClasses = null;

    public void setRowClasses(String rowClasses) {
        this.rowClasses = rowClasses;
    }

    // rows
    private String rows = null;

    public void setRows(String rows) {
        this.rows = rows;
    }

    // rules
    private String rules = null;

    public void setRules(String rules) {
        this.rules = rules;
    }

    // sortAscending
    private String sortAscending = null;

    public void setSortAscending(String sortAscending) {
        this.sortAscending = sortAscending;
    }

    // sortColumn
    private String sortColumn = null;

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
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

    // value
    private String value = null;

    public void setValue(String value) {
        this.value = value;
    }

    // var
    private String _var = null;

    public void setVar(String _var) {
        this._var = _var;
    }

    // width
    private String width = null;

    public void setWidth(String width) {
        this.width = width;
    }

    // bgcolor
    private String bgcolor = null;

    public void setBgcolor(String bgcolor) {
        this.bgcolor = bgcolor;
    }

    // dir
    private String dir = null;

    public void setDir(String dir) {
        this.dir = dir;
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

    // title
    private String title = null;

    public void setTitle(String title) {
        this.title = title;
    }

    private String scrollable = null;

    public void setScrollable(String s) {
        this.scrollable = s;
    }

    private String columnWidths = null;

    public void setColumnWidths(String s) {

        this.columnWidths = s;
    }

    private String scrollHeight = null;

    public void setScrollHeight(String s) {

        this.scrollHeight = s;
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
            t.printStackTrace();
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
