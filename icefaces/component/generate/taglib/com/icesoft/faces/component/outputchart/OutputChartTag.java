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

package com.icesoft.faces.component.outputchart;

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

public class OutputChartTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.OutputChart";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.OutputChartRenderer";
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
        onreseteffect = null;
        onsubmiteffect = null;
        renderedOnUserRole = null;
        style = null;
        styleClass = null;
        url = null;
        visible = null;
        alt = null;
        dir = null;
        height = null;
        ismap = null;
        lang = null;
        longdesc = null;
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
        usemap = null;
        value = null;
        width = null;
        labels = null;
        legendLabel = null;
        action = null;
        actionListener = null;
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
        if (onreseteffect != null) {
            com.icesoft.faces.component.ext.taglib.Util
                    .addLocalEffect(onreseteffect, "onreseteffect", _component);
        }
        if (onsubmiteffect != null) {
            com.icesoft.faces.component.ext.taglib.Util.addLocalEffect(
                    onsubmiteffect, "onsubmiteffect", _component);
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
        if (url != null) {
            if (isValueReference(url)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(url);
                _component.setValueBinding("url", _vb);
            } else {
                _component.getAttributes().put("url", url);
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
        if (alt != null) {
            if (isValueReference(alt)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(alt);
                _component.setValueBinding("alt", _vb);
            } else {
                _component.getAttributes().put("alt", alt);
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
        if (height != null) {
            if (isValueReference(height)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(height);
                _component.setValueBinding("height", _vb);
            } else {
                _component.getAttributes().put("height", height);
            }
        }
        if (ismap != null) {
            if (isValueReference(ismap)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(ismap);
                _component.setValueBinding("ismap", _vb);
            } else {
                _component.getAttributes().put("ismap", Boolean.valueOf(ismap));
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
        if (longdesc != null) {
            if (isValueReference(longdesc)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(longdesc);
                _component.setValueBinding("longdesc", _vb);
            } else {
                _component.getAttributes().put("longdesc", longdesc);
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
        if (usemap != null) {
            if (isValueReference(usemap)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(usemap);
                _component.setValueBinding("usemap", _vb);
            } else {
                _component.getAttributes().put("usemap", usemap);
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
        if (width != null) {
            if (isValueReference(width)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(width);
                _component.setValueBinding("width", _vb);
            } else {
                _component.getAttributes().put("width", width);
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
        if (data != null) {
            if (isValueReference(data)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(data);
                _component.setValueBinding("data", _vb);
            } else {
                _component.getAttributes().put("data", data);
            }
        }
        if (labels != null) {
            if (isValueReference(labels)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(labels);
                _component.setValueBinding("labels", _vb);
            } else {
                _component.getAttributes().put("labels", labels);
            }
        }
        if (colors != null) {
            if (isValueReference(colors)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(colors);
                _component.setValueBinding("colors", _vb);
            } else {
                _component.getAttributes().put("colors", colors);
            }
        }
        if (shapes != null) {
            if (isValueReference(shapes)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(shapes);
                _component.setValueBinding("shapes", _vb);
            } else {
                _component.getAttributes().put("shapes", shapes);
            }
        }
        if (chartTitle != null) {
            if (isValueReference(chartTitle)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(chartTitle);
                _component.setValueBinding("chartTitle", _vb);
            } else {
                _component.getAttributes().put("chartTitle", chartTitle);
            }
        }
        if (xaxisTitle != null) {
            if (isValueReference(xaxisTitle)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(xaxisTitle);
                _component.setValueBinding("xaxisTitle", _vb);
            } else {
                _component.getAttributes().put("xaxisTitle", xaxisTitle);
            }
        }
        if (yaxisTitle != null) {
            if (isValueReference(yaxisTitle)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(yaxisTitle);
                _component.setValueBinding("yaxisTitle", _vb);
            } else {
                _component.getAttributes().put("yaxisTitle", yaxisTitle);
            }
        }
        if (xaxisLabels != null) {
            if (isValueReference(xaxisLabels)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(xaxisLabels);
                _component.setValueBinding("xaxisLabels", _vb);
            } else {
                _component.getAttributes().put("xaxisLabels", xaxisLabels);
            }
        }
        if (renderOnSubmit != null) {
            if (isValueReference(renderOnSubmit)) {
                MethodBinding _mb = getFacesContext().getApplication()
                        .createMethodBinding(renderOnSubmit,
                                             rendereOnSubmitArgs);
                _component.getAttributes().put("renderOnSubmit", _mb);
            } else {
                _component.getAttributes()
                        .put("renderOnSubmit", Boolean.valueOf(renderOnSubmit));
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
        if (legendLabel != null) {
            if (isValueReference(legendLabel)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(legendLabel);
                _component.setValueBinding("legendLabel", _vb);
            } else {
                _component.getAttributes().put("legendLabel", legendLabel);
            }
        }

    }

    // labels
    private String labels = null;

    public void setLabels(String labels) {
        this.labels = labels;
    }

    // data
    private String data = null;

    public void setData(String data) {
        this.data = data;
    }

    // colors
    private String colors = null;

    public void setColors(String colors) {
        this.colors = colors;
    }

    // chartTitle
    private String chartTitle = null;

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

//  xAxisTitle
    private String xaxisTitle = null;

    public void setXaxisTitle(String xaxisTitle) {
        this.xaxisTitle = xaxisTitle;
    }

//  yAxisTitle
    private String yaxisTitle = null;

    public void setYaxisTitle(String yaxisTitle) {
        this.yaxisTitle = yaxisTitle;
    }

//  xaxisLabels
    private String xaxisLabels = null;

    public void setXaxisLabels(String xaxisLabels) {
        this.xaxisLabels = xaxisLabels;
    }

    // paints
    private String renderOnSubmit = null;

    public void setRenderOnSubmit(String renderOnSubmit) {
        this.renderOnSubmit = renderOnSubmit;
    }

    // paints
    private String shapes = null;

    public void setShapes(String shapes) {
        this.shapes = shapes;
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

    // legendLabels
    private String legendLabel = null;

    public void setLegendLabel(String legendLabel) {
        this.legendLabel = legendLabel;
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

    // url
    private String url = null;

    public void setUrl(String url) {
        this.url = url;
    }

    // visible
    private String visible = null;

    public void setVisible(String visible) {
        this.visible = visible;
    }

    // alt
    private String alt = null;

    public void setAlt(String alt) {
        this.alt = alt;
    }

    // dir
    private String dir = null;

    public void setDir(String dir) {
        this.dir = dir;
    }

    // height
    private String height = null;

    public void setHeight(String height) {
        this.height = height;
    }

    // ismap
    private String ismap = null;

    public void setIsmap(String ismap) {
        this.ismap = ismap;
    }

    // lang
    private String lang = null;

    public void setLang(String lang) {
        this.lang = lang;
    }

    // longdesc
    private String longdesc = null;

    public void setLongdesc(String longdesc) {
        this.longdesc = longdesc;
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

    // usemap
    private String usemap = null;

    public void setUsemap(String usemap) {
        this.usemap = usemap;
    }

    // value
    private String value = null;

    public void setValue(String value) {
        this.value = value;
    }

    // width
    private String width = null;

    public void setWidth(String width) {
        this.width = width;
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
    private static Class rendereOnSubmitArgs[] = {OutputChart.class};

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
