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

package com.icesoft.faces.component.paneltabset;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.component.panelseries.UISeries;
import com.icesoft.faces.component.util.CustomComponentUtils;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.FormRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.util.DOMUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.faces.FacesException;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UINamingContainer;
import javax.faces.component.UIParameter;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import java.beans.Beans;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>PanelTabSetRenderer extends DomBasicRenderer and is responsible for
 * rendering PanelTabSet and PanelTab components.</p>
 */
public class PanelTabSetRenderer
        extends DomBasicRenderer

{
    // hidden field for the tab links
    private static final String HIDDEN_FIELD_NAME = "cl";

    private static final Log log = LogFactory.getLog(PanelTabSetRenderer.class);
    private static final String SPACER_IMG =
            "xmlhttp/css/xp/css-images/spacer.gif";

    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
    }

    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#getRendersChildren()
     */
    public boolean getRendersChildren() {
        return true;
    }

    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#encodeChildren(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeChildren(FacesContext facescontext,
                               UIComponent uicomponent) throws IOException {
    }

    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, PanelTabSet.class);
        // get DOMContext using DOMContext static method getDOMContext
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        // if the domContext has not been initialized
        // initialize it, create the Root Element
        if (!domContext.isInitialized()) {
            Element table = domContext.createRootElement(HTML.TABLE_ELEM);
            table.setAttribute(HTML.CELLPADDING_ATTR, "0");
            table.setAttribute(HTML.CELLSPACING_ATTR, "0");
            setRootElementId(facesContext, table, uiComponent);
        }

        FormRenderer.addHiddenField(facesContext,
                                    deriveCommonHiddenFieldName(
                                            facesContext, uiComponent));

        PanelTabSet tabSet = (PanelTabSet) uiComponent;
        String baseClass = tabSet.getStyleClass();

        int selectedIndex = tabSet.getSelectedIndex();

        // get the parentForm
        UIComponent parentForm = findForm(tabSet);
        // if there is no parent form - ERROR
        if (parentForm == null) {
            if (log.isErrorEnabled()) {
                log.error(" TabbedPane::must be in a FORM");
            }
            return;
        }

        if (tabSet.getValue() != null) {
            if (tabSet.getChildCount() < 1) {
                if (log.isErrorEnabled()) {
                    log.error(" TabbedPane:a panelTab element is required");
                }
                return;
            }

            if (tabSet.getChildCount() > 1 ||
                !(tabSet.getChildren().get(0) instanceof PanelTab)) {
                if (log.isErrorEnabled()) {
                    log.error(
                            " TabbedPane::only one panelTab element allowed when using value atttibute");
                }
                return;
            }
        }

        // get table
        Element table = (Element) domContext.getRootNode();
        // render table pass thru attributes
        for (int i = 0; i < HTML.TABLE_PASSTHROUGH_ATTRIBUTES.length; i++) {
            if (HTML.TABLE_PASSTHROUGH_ATTRIBUTES[i]
                    .equalsIgnoreCase("styleClass")) {
                renderAttribute(tabSet, table,
                                HTML.TABLE_PASSTHROUGH_ATTRIBUTES[i],
                                HTML.CLASS_ATTR);
            } else {
                renderAttribute(tabSet, table,
                                HTML.TABLE_PASSTHROUGH_ATTRIBUTES[i],
                                HTML.TABLE_PASSTHROUGH_ATTRIBUTES[i]);
            }
        }

        table.removeAttribute(HTML.BGCOLOR_ATTR);

        // clean out children
        DOMContext.removeChildrenByTagName(table, HTML.TR_ELEM);
        // create a new table row for the Tab Header Cell
        Element headerRow = domContext.createElement(HTML.TR_ELEM);

        // Create Tab headers
        int tabIdx = 0;
        int visibleTabCount = 0;
        List children = tabSet.getChildren();

        // check tabPlacement
        // default is Top
        // if tabPlacement is Bottom
        // render Tab Cell first then Tab Header
        String tabPlacement = tabSet.getTabPlacement();
        List tabList = null;
        if (tabSet.getValue() != null) {
            tabList = (List) tabSet.getValue();
        }
        if (tabPlacement
                .equalsIgnoreCase(CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_BOTTOM)) {

            if (tabList != null) {
                visibleTabCount = tabList.size();
            } else {
                // determine visibleTabCount
                for (int i = 0, len = children.size(); i < len; i++) {
                    UIComponent child =
                            getUIComponent((UIComponent) children.get(i));
                    if (child instanceof PanelTab) {
                        if (child.isRendered()) {
                            visibleTabCount++;
                        }
                    }
                }
            }
            // Create Tab Cells
            // create a new table row Element for the Tab Content Cells
            Element contentRow = domContext.createElement(HTML.TR_ELEM);
            // append the tab content table row Element to the table
            table.appendChild(contentRow);

            contentRow.setAttribute(HTML.HEIGHT_ATTR, "100%");
            // call the writeTabCell method
            // pass in the new table row Element tr3
            writeTabCell(domContext,
                         facesContext,
                         tabSet,
                         visibleTabCount,
                         selectedIndex,
                         contentRow,
                         tabSet);
            if (tabSet.getValue() != null) {
                int rowIndex = tabSet.getFirst();
                int rowsToBeDisplayed = tabSet.getRows();
                int rowsDisplayed = 0;
                tabSet.setRowIndex(rowIndex);

                while (tabSet.isRowAvailable()) {
                    if (rowsToBeDisplayed > 0 &&
                        rowsDisplayed >= rowsToBeDisplayed) {
                        break;
                    }

                    UIComponent child = getUIComponent(
                            (UIComponent) tabSet.getChildren().get(0));
                    if (child instanceof PanelTab) {
                        if (child.isRendered()) {
                            // append the header table row Element to the table
                            table.appendChild(headerRow);

                            // call the writeHeaderCell method
                            // pass in the new header table row Element
                            writeHeaderCell(domContext,
                                            facesContext,
                                            tabSet,
                                            (PanelTab) child,
                                            tabIdx,
                                            tabIdx == selectedIndex,
                                            ((PanelTab) child).isDisabled(),
                                            headerRow);
                            visibleTabCount++;
                        }
                        tabIdx++;
                    }
                    rowsDisplayed++;
                    rowIndex++;
                    tabSet.setRowIndex(rowIndex);
                }
                tabSet.setRowIndex(-1);
            } else {

                for (int i = 0, len = children.size(); i < len; i++) {
                    UIComponent child =
                            getUIComponent((UIComponent) children.get(i));
                    if (child instanceof PanelTab) {
                        if (child.isRendered()) {
                            // append the header table row Element to the table
                            table.appendChild(headerRow);

                            // call the writeHeaderCell method
                            // pass in the new header table row Element
                            writeHeaderCell(domContext,
                                            facesContext,
                                            tabSet,
                                            (PanelTab) child,
                                            tabIdx,
                                            tabIdx == selectedIndex,
                                            ((PanelTab) child).isDisabled(),
                                            headerRow);
                            visibleTabCount++;
                        }
                        tabIdx++;
                    }
                }
            }
            // Empty tab cell on the right for better look
            // create a new table data for the empty TextNode
            Element td = domContext.createElement(HTML.TD_ELEM);
            String className = Util.appendNewStyleClass(
                    CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TAB_SET, baseClass,
                    CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TABSPACER + CSS_DEFAULT
                            .PANEL_TAB_SET_DEFAULT_BOTTOM);
            td.setAttribute(HTML.CLASS_ATTR, className);
            Text text = null;
            if (domContext.isStreamWriting()) {
                text = domContext.createTextNode(" ");
            } else {
                text = domContext.createTextNode("&#160;");
            }
            td.appendChild(text);
            // append the empty TextNode table data to our table row Element tr1
            headerRow.appendChild(td);

            domContext.streamWrite(facesContext, tabSet);
            // steps to the position where the next sibling should be rendered
            domContext.stepOver();

        } else { // for now it's either Top or Bottom

            if (tabSet.getValue() != null) {
                int rowIndex = tabSet.getFirst();
                int rowsToBeDisplayed = tabSet.getRows();
                int rowsDisplayed = 0;
                tabSet.setRowIndex(rowIndex);

                while (tabSet.isRowAvailable()) {
                    if (rowsToBeDisplayed > 0 &&
                        rowsDisplayed >= rowsToBeDisplayed) {
                        break;
                    }

                    UIComponent child = getUIComponent(
                            (UIComponent) tabSet.getChildren().get(0));
                    if (child instanceof PanelTab) {
                        if (child.isRendered()) {
                            // append the header table row Element to the table
                            table.appendChild(headerRow);

                            // call the writeHeaderCell method
                            // pass in the new header table row Element
                            writeHeaderCell(domContext,
                                            facesContext,
                                            tabSet,
                                            (PanelTab) child,
                                            tabIdx,
                                            tabIdx == selectedIndex,
                                            ((PanelTab) child).isDisabled(),
                                            headerRow);
                            visibleTabCount++;
                        }
                        tabIdx++;
                    }
                    rowsDisplayed++;
                    rowIndex++;
                    tabSet.setRowIndex(rowIndex);
                }
                tabSet.setRowIndex(-1);
            } else {
                for (int i = 0, len = children.size(); i < len; i++) {
                    UIComponent child =
                            getUIComponent((UIComponent) children.get(i));
                    if (child instanceof PanelTab) {
                        if (child.isRendered()) {
                            // append the header table row Element to the table
                            table.appendChild(headerRow);

                            // call the writeHeaderCell method
                            // pass in the new header table row Element
                            writeHeaderCell(domContext,
                                            facesContext,
                                            tabSet,
                                            (PanelTab) child,
                                            tabIdx,
                                            tabIdx == selectedIndex,
                                            ((PanelTab) child).isDisabled(),
                                            headerRow);
                            visibleTabCount++;
                        }
                        tabIdx++;
                    }
                }
            }
            // Empty tab cell on the right for better look
            // create a new table data for the empty TextNode
            Element td = domContext.createElement(HTML.TD_ELEM);

            String className = Util.appendNewStyleClass(
                    CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TAB_SET, baseClass,
                    CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TABSPACER);
            td.setAttribute(HTML.CLASS_ATTR, className);
            Text text;
            if (domContext.isStreamWriting()) {
                text = domContext.createTextNode(" ");
            } else {
                text = domContext.createTextNode("&#160;");
            }

            td.appendChild(text);
            // append the empty TextNode table data to our table row Element tr1
            headerRow.appendChild(td);

            // Create Tab Cells
            // create a new table row Element for the Tab Cells
            Element contentRow = domContext.createElement(HTML.TR_ELEM);
            contentRow.setAttribute(HTML.HEIGHT_ATTR, "100%");
            // append the table row Element to the table
            table.appendChild(contentRow);
            // call the writeTabCell method
            // pass in the new table row Element tr3
            writeTabCell(domContext,
                         facesContext,
                         tabSet,
                         visibleTabCount,
                         selectedIndex,
                         contentRow,
                         tabSet);

            // steps to the position where the next sibling should be rendered
            domContext.stepOver();
            domContext.streamWrite(facesContext, tabSet);

        }
    }


    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        validateParameters(facesContext, uiComponent, PanelTabSet.class);

        PanelTabSet tabSet = (PanelTabSet) uiComponent;
        Map paramMap =
                facesContext.getExternalContext().getRequestParameterMap();
        int tabIdx = 0;
        String paramName;
        String paramValue;
        if (tabSet.getValue() != null) {
            int rowIndex = tabSet.getFirst();
            tabSet.setRowIndex(rowIndex);
            int rowsToBeDisplayed = tabSet.getRows();
            int rowsDisplayed = 0;
            UIComponent child =
                    getUIComponent((UIComponent) tabSet.getChildren().get(0));
            while (tabSet.isRowAvailable()) {
                if (rowsToBeDisplayed > 0 &&
                    rowsDisplayed >= rowsToBeDisplayed) {
                    break;
                }
                if (child instanceof PanelTab) {
                    paramName = tabSet.getClientId(facesContext) + "." + tabIdx;
                    paramValue = (String) paramMap.get(paramName);
                    if (paramValue != null && paramValue.length() > 0) {
                        tabSet.queueEvent(new TabChangeEvent(tabSet,
                                                             tabSet.getSelectedIndex(),
                                                             tabIdx));
                        return;
                    }
                    tabIdx++;
                }
                rowsDisplayed++;
                rowIndex++;
                tabSet.setRowIndex(rowIndex);
            }
            tabSet.setRowIndex(-1);
        } else {
            List children = tabSet.getChildren();
            for (int i = 0, len = children.size(); i < len; i++) {
                UIComponent child =
                        getUIComponent((UIComponent) children.get(i));
                if (child instanceof PanelTab) {
                    paramName = tabSet.getClientId(facesContext) + "." + tabIdx;
                    paramValue = (String) paramMap.get(paramName);
                    if (paramValue != null && paramValue.length() > 0) {
                        tabSet.queueEvent(new TabChangeEvent(tabSet,
                                                             tabSet.getSelectedIndex(),
                                                             tabIdx));
                        return;
                    }
                    tabIdx++;
                }
            }
        }
    }

    /**
     * writeHeaderCell is a DOM-enabled version of the MyFaces writeHeaderCell
     * implementation. Calls to the ResponseWriter have been substituted with
     * DOMContext and w3c DOM API calls.
     *
     * @param domContext
     * @param facesContext
     * @param tabSet
     * @param tab
     * @param tabIndex
     * @param active
     * @param disabled
     * @param tr
     * @throws IOException
     */
    protected void writeHeaderCell(DOMContext domContext,
                                   FacesContext facesContext,
                                   PanelTabSet tabSet,
                                   PanelTab tab,
                                   int tabIndex,
                                   boolean active,
                                   boolean disabled,
                                   Element tr)
            throws IOException {
        String baseClass = tabSet.getStyleClass();
        // create a new table data Element using the DOMContext API
        Element td = domContext.createElement(HTML.TD_ELEM);
        td.setAttribute(HTML.ID_ATTR,
                        tabSet.getClientId(facesContext) + "ht" + tabIndex);
        // append the td to the tr
        tr.appendChild(td);

        String styleClass = Util.appendNewStyleClass(
                CSS_DEFAULT.PANEL_TAB_DEFAULT_STYLECLASS, tab.getStyleClass(),
                "");

        String label = tab.getLabel();

        if (label == null || label.length() == 0) {
            label = "Tab " + tabIndex;
        }
        
        label = DOMUtils.escapeAnsi(label);
 
        String tabPlacement = "";
        if (tabSet.getTabPlacement()
                .equalsIgnoreCase(CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_BOTTOM)) {
            tabPlacement = CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_BOTTOM;
        }

        // create a table for the tab
        Element table = domContext.createElement(HTML.TABLE_ELEM);
        // append the table to the td
        td.appendChild(table);

        table.setAttribute(HTML.CELLPADDING_ATTR, "0");
        table.setAttribute(HTML.CELLSPACING_ATTR, "0");

        // table will have 3 rows
        Element tr_top = domContext.createElement(HTML.TR_ELEM);
        Element tr_mid = domContext.createElement(HTML.TR_ELEM);
        Element tr_bot = domContext.createElement(HTML.TR_ELEM);

        // each row will have 3 columns
        Element td_top_left = domContext.createElement(HTML.TD_ELEM);
        Element td_top_mid = domContext.createElement(HTML.TD_ELEM);
        Element td_top_right = domContext.createElement(HTML.TD_ELEM);
        this.renderSpacerImage(domContext, td_top_left);
        this.renderSpacerImage(domContext, td_top_mid);
        this.renderSpacerImage(domContext, td_top_right);

        Element td_mid_left = domContext.createElement(HTML.TD_ELEM);
        // the command link will go in this td
        Element td_mid_mid = domContext.createElement(HTML.TD_ELEM);
        Element td_mid_right = domContext.createElement(HTML.TD_ELEM);
        this.renderSpacerImage(domContext, td_mid_left);
        this.renderSpacerImage(domContext, td_mid_right);

        Element td_bot_left = domContext.createElement(HTML.TD_ELEM);
        Element td_bot_mid = domContext.createElement(HTML.TD_ELEM);
        Element td_bot_right = domContext.createElement(HTML.TD_ELEM);
        this.renderSpacerImage(domContext, td_bot_left);
        this.renderSpacerImage(domContext, td_bot_mid);
        this.renderSpacerImage(domContext, td_bot_right);

        String disableStyleClassSuffix;

        if (disabled) {
            disableStyleClassSuffix = "-dis";
            Text text = domContext.createTextNode(label);
            td_mid_mid.appendChild(text);
        } else {
            disableStyleClassSuffix = "";
            // Build a command link
            Element link = domContext.createElement(HTML.ANCHOR_ELEM);
            link.setAttribute(HTML.NAME_ATTR, tabSet.getClientId(facesContext) +
                                              "." + tabIndex);
            link.setAttribute(HTML.ID_ATTR, tabSet.getClientId(facesContext) +
                                            "." + tabIndex);
            link.setAttribute(HTML.HREF_ATTR, "#");
            // set focus handler
            link.setAttribute(HTML.ONFOCUS_ATTR, "setFocus(this.id);");
            link.setAttribute(HTML.ONBLUR_ATTR, "setFocus('');");
            td_mid_mid.appendChild(link);
            renderLinkText(label, domContext, link, tab, tabSet);

            Map parameterMap = getParameterMap(facesContext, tab);
            renderOnClick(facesContext, tabSet, link, parameterMap);

            Iterator parameterKeys = parameterMap.keySet().iterator();
            String nextKey;
            while (parameterKeys.hasNext()) {
                nextKey = (String) parameterKeys.next();
                FormRenderer.addHiddenField(facesContext, nextKey);
            }

        }
        // set style class attributes
        if (active) {
            String className = Util.appendNewStyleClass(
                    CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TAB_SET,
                    baseClass,
                    CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TABONCLASS +
                    tabPlacement);
            table.setAttribute(HTML.CLASS_ATTR, className);
        } else // inactive style with mouse over and out
        {
            String className = Util.appendNewStyleClass(
                    CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TAB_SET,
                    baseClass,
                    CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TABOFFCLASS +
                    tabPlacement);
            table.setAttribute(HTML.CLASS_ATTR, className);

            if (!disabled) {
                className = Util.appendNewStyleClass(
                        CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TAB_SET,
                        baseClass,
                        CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TABOVERCLASS +
                        tabPlacement);
                table.setAttribute(HTML.ONMOUSEOVER_ATTR,
                                   "this.className='" + className + "';");
                className = Util.appendNewStyleClass(
                        CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TAB_SET,
                        baseClass,
                        CSS_DEFAULT.PANEL_TAB_SET_DEFAULT_TABOFFCLASS +
                        tabPlacement);
                table.setAttribute(HTML.ONMOUSEOUT_ATTR,
                                   "this.className='" + className + "';");
            } else {
                table.removeAttribute(HTML.ONMOUSEOVER_ATTR);
                table.removeAttribute(HTML.ONMOUSEOUT_ATTR);
            }
        }

        td_top_left.setAttribute(HTML.CLASS_ATTR, CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_LEFT + CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_TOP + disableStyleClassSuffix);
        td_top_mid.setAttribute(HTML.CLASS_ATTR, CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_MIDDLE + CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_TOP + disableStyleClassSuffix);
        td_top_right.setAttribute(HTML.CLASS_ATTR, CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_RIGHT + CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_TOP + disableStyleClassSuffix);

        td_mid_left.setAttribute(HTML.CLASS_ATTR, CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_LEFT + CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_MIDDLE + disableStyleClassSuffix);
        td_mid_mid.setAttribute(HTML.CLASS_ATTR, CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_MIDDLE + CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_MIDDLE + disableStyleClassSuffix);
        td_mid_right.setAttribute(HTML.CLASS_ATTR, CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_RIGHT + CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_MIDDLE + disableStyleClassSuffix);

        td_bot_left.setAttribute(HTML.CLASS_ATTR, CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_LEFT + CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_BOTTOM + disableStyleClassSuffix);
        td_bot_mid.setAttribute(HTML.CLASS_ATTR, CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_MIDDLE + CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_BOTTOM + disableStyleClassSuffix);
        td_bot_right.setAttribute(HTML.CLASS_ATTR, CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_RIGHT + CSS_DEFAULT
                .PANEL_TAB_SET_DEFAULT_BOTTOM + disableStyleClassSuffix);

        tr_top.appendChild(td_top_left);
        tr_top.appendChild(td_top_mid);
        tr_top.appendChild(td_top_right);
        table.appendChild(tr_top);

        tr_mid.appendChild(td_mid_left);
        tr_mid.appendChild(td_mid_mid);
        tr_mid.appendChild(td_mid_right);
        table.appendChild(tr_mid);

        tr_bot.appendChild(td_bot_left);
        tr_bot.appendChild(td_bot_mid);
        tr_bot.appendChild(td_bot_right);
        table.appendChild(tr_bot);

        // TODO: test passthru attributes
        //PassThruAttributeRenderer.renderAttributes(facesContext, tab, new String[] { "onclick" });

        // append Elements using the w3c DOM API appendChild
        if (styleClass != null) {
            td.setAttribute(HTML.CLASS_ATTR, styleClass);
        }

        if (tab.getTitle() != null) {
            td.setAttribute(HTML.TITLE_ATTR, tab.getTitle());
        }
    }


    protected static String deriveCommonHiddenFieldName(
            FacesContext facesContext,
            UIComponent uiComponent) {
        if (Beans.isDesignTime()) {
            return "";
        }
        try {
            UIComponent parentNamingContainer =
                    findNamingContainer(uiComponent);
            String parentClientId =
                    parentNamingContainer.getClientId(facesContext);
            return parentClientId
                   + NamingContainer.SEPARATOR_CHAR
                   + UIViewRoot.UNIQUE_ID_PREFIX
                   + HIDDEN_FIELD_NAME;            
        } catch (NullPointerException e) {
            throw new RuntimeException("Panel Tab Set must be in a <ice:form>",
                                       e);
        }
    }

    /**
     * @param linkText
     * @param domContext
     * @param link
     * @param tab
     * @param tabSet
     */
    private void renderLinkText(String linkText, DOMContext
            domContext, Element link, PanelTab tab, PanelTabSet tabSet) {

        // create a new or update the old text node for the label
        if (linkText != null && linkText.length() != 0) {
            Text labelNode = (Text) link.getFirstChild();
            if (labelNode == null) {
                labelNode = domContext.getDocument().createTextNode(linkText);
                tab.addHeaderText(domContext, link, labelNode, tabSet);
            } else {
                labelNode.setData(linkText);
            }
        }
    }

    /**
     * This method is defined in the DomBasicRenderer with a default package
     * visiblity. TODO: modify visiblity to public in DomBasicRenderer and
     * remove from PanelTabSetRenderer Due to the behaviour of the UIParameter
     * class, the names in the name-value pairs of the Map returned by this
     * method are guaranteed to be Strings
     *
     * @param facesContext
     * @param uiComponent
     * @return map
     */
    static Map getParameterMap(FacesContext facesContext,
                               UIComponent uiComponent) {
        Map parameterMap = new HashMap();
        Iterator children = uiComponent.getChildren().iterator();
        UIComponent nextChild;
        UIParameter uiParam;
        while (children.hasNext()) {
            nextChild = (UIComponent) children.next();
            if (nextChild instanceof UIParameter) {
                uiParam = (UIParameter) nextChild;
                parameterMap.put(uiParam.getName(), uiParam.getValue());
            }
        }
        return parameterMap;
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @param root
     * @param parameters
     */
    private void renderOnClick(FacesContext facesContext,
                               UIComponent uiComponent,
                               Element root, Map parameters) {
        UIComponent uiForm = findForm(uiComponent);
        if (uiForm == null) {
            throw new FacesException("CommandLink must be contained in a form");
        }
        String uiFormClientId = uiForm.getClientId(facesContext);
        root.setAttribute("onclick", getJavaScriptOnClickString(facesContext,
                                                                uiComponent,
                                                                uiFormClientId,
                                                                parameters)); // replaced command w/component
    }

    private String getJavaScriptOnClickString(FacesContext facesContext,
                                              UIComponent uiComponent,
                                              String formClientId,
                                              Map parameters) {
        return getJavascriptHiddenFieldSetters(facesContext,
                                               uiComponent, formClientId,
                                               parameters)
               + "iceSubmitPartial("
               + " document.forms['" + formClientId + "'],"
               + " this,event); "
               + "return false;";
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @param formClientId
     * @param parameters
     * @return string representing hidden field setters for this tabset
     */
    private String getJavascriptHiddenFieldSetters(FacesContext facesContext,
                                                   UIComponent uiComponent,
                                                   String formClientId,
                                                   Map parameters) {
        StringBuffer buffer;
        buffer = new StringBuffer("document.forms['" + formClientId + "']['");
        buffer.append(deriveCommonHiddenFieldName(facesContext, uiComponent));
        buffer.append("'].value='");
        buffer.append(uiComponent.getClientId(facesContext));
        buffer.append("';");
        Iterator parameterKeys = parameters.keySet().iterator();
        String nextParamName;
        Object nextParamValue;
        while (parameterKeys.hasNext()) {
            nextParamName = (String) parameterKeys.next();
            nextParamValue = parameters.get(nextParamName);
            buffer.append("document.forms['");
            buffer.append(formClientId);
            buffer.append("']['");
            buffer.append(nextParamName);
            buffer.append("'].value='");
            buffer.append((String) nextParamValue);
            buffer.append("';");
        }
        return buffer.toString();
    }

    /**
     * @param domContext
     * @param td
     */
    private void renderSpacerImage(DOMContext domContext, Element td) {

        // create a dummy image to load into given td
        Element img = domContext.createElement(HTML.IMG_ELEM);
        img.setAttribute(HTML.SRC_ATTR, Util.getApplicationBase(
                FacesContext.getCurrentInstance()) + SPACER_IMG);
        img.setAttribute(HTML.HEIGHT_ATTR, "1");
        img.setAttribute(HTML.WIDTH_ATTR, "4");
        img.setAttribute(HTML.ALT_ATTR, "");

        td.appendChild(img);
    }

    /**
     * writeTabCell is a DOM-enabled version of the MyFaces writeTabCell
     * implementation. Calls to the ResponseWriter have been substituted with
     * DOMContext and w3c DOM API calls.
     *
     * @param domContext
     * @param facesContext
     * @param tabSet
     * @param tabCount
     * @param selectedIndex
     * @param tr
     * @param uiList
     * @throws IOException
     */
    protected void writeTabCell(DOMContext domContext,
                                FacesContext facesContext,
                                PanelTabSet tabSet,
                                int tabCount,
                                int selectedIndex,
                                Element tr,
                                UISeries uiList)
            throws IOException {
        // create a new table data Element
        Element td = domContext.createElement(HTML.TD_ELEM);
        td.setAttribute(HTML.ID_ATTR,
                        tabSet.getClientId(facesContext) + "td" + tabCount);
        // append the new table data Element to the table row
        tr.appendChild(td);

        // set the table data attributes
        td.setAttribute(HTML.COLSPAN_ATTR, Integer.toString(tabCount + 1));
        td.setAttribute(HTML.CLASS_ATTR, tabSet.getTabClass());

        // set the cursor parent to the new table data Element
        // this will cause the renderChild method to append the child nodes
        // to the new table data Element
        domContext.setCursorParent(td);
        domContext.streamWrite(facesContext, tabSet, domContext.getRootNode(),
                               td);
        int tabIdx = 0;
        if (uiList.getValue() != null) {
            int rowIndex = uiList.getFirst();
            uiList.setRowIndex(rowIndex);
            int rowsToBeDisplayed = uiList.getRows();
            int rowsDisplayed = 0;
            UIComponent child =
                    getUIComponent((UIComponent) tabSet.getChildren().get(0));
            while (uiList.isRowAvailable()) {
                if (rowsToBeDisplayed > 0 &&
                    rowsDisplayed >= rowsToBeDisplayed) {
                    break;
                }
                if (child instanceof PanelTab) {
                    if (tabIdx == selectedIndex) {
                        CustomComponentUtils.renderChild(facesContext, child);
                    }
                    tabIdx++;
                }
                rowsDisplayed++;
                rowIndex++;
                uiList.setRowIndex(rowIndex);
            }
            uiList.setRowIndex(-1);
        } else {
            List children = tabSet.getChildren();
            UIComponent child;
            for (int i = 0, len = children.size(); i < len; i++) {
                child = getUIComponent((UIComponent) children.get(i));
                if (child instanceof PanelTab) {
                    if (tabIdx == selectedIndex) {
                        CustomComponentUtils.renderChild(facesContext, child);
                    }
                    tabIdx++;
                } else {
                    CustomComponentUtils.renderChild(facesContext, child);
                }
            }
        }
    }

    private UIComponent getUIComponent(UIComponent uiComponent) {
        if (uiComponent instanceof UIForm ||
            uiComponent instanceof UINamingContainer) {
            List children = uiComponent.getChildren();
            for (int i = 0, len = children.size(); i < len; i++) {
                uiComponent = getUIComponent((UIComponent) children.get(i));
            }
        }
        return uiComponent;
    }
}
