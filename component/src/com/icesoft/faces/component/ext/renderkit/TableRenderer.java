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

package com.icesoft.faces.component.ext.renderkit;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.RowSelector;
import com.icesoft.faces.component.ext.UIColumns;
import com.icesoft.faces.component.panelseries.UISeries;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.faces.component.UIColumn;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.*;


public class TableRenderer
        extends com.icesoft.faces.renderkit.dom_html_basic.TableRenderer {

    private static final String SELECTED_ROWS = "sel_rows";

    public String getComponentStyleClass(UIComponent uiComponent) {
        String styleClass =
                (String) uiComponent.getAttributes().get("styleClass");
        if (styleClass == null) {
            styleClass = CSS_DEFAULT.TABLE_OUTLINE_CLASS;
        }
        return styleClass;
    }

    public String getHeaderClass(UIComponent component) {
        String headerClass =
                (String) component.getAttributes().get("headerClass");
        if (headerClass == null) {
            headerClass = CSS_DEFAULT.TABLE_HEADER_CLASS;
        }
        return headerClass;
    }

    public String getFooterClass(UIComponent component) {
        String footerClass =
                (String) component.getAttributes().get("footerClass");
        if (footerClass == null) {
            footerClass = CSS_DEFAULT.TABLE_FOOTER_CLASS;
        }
        return footerClass;
    }

    // row styles are returned by reference
    public String[] getRowStyles(UIComponent uiComponent) {
        if (((String[]) getRowStyleClasses(uiComponent)).length <= 0) {
            String[] rowStyles = new String[2];
            rowStyles[0] = CSS_DEFAULT.TABLE_ROW_CLASS1;
            rowStyles[1] = CSS_DEFAULT.TABLE_ROW_CLASS2;
            return rowStyles;
        } else {
            return getRowStyleClasses(uiComponent);
        }
    }

    public void writeColStyles(String[] columnStyles, int columnStylesMaxIndex,
                               int columnStyleIndex, Element td,
                               int colNumber) {
        if (columnStyles.length > 0) {
            if (columnStylesMaxIndex >= 0) {
                td.setAttribute("class", columnStyles[columnStyleIndex]);
            }
        } else {
            td.setAttribute("class",
                            CSS_DEFAULT.TABLE_COLUMN_CLASSES + colNumber++);
        }
    }

    protected void renderFacet(FacesContext facesContext,
                               UIComponent uiComponent,
                               DOMContext domContext, boolean header)
            throws IOException {
        String facet, tag, element, facetClass;
        if (header) {
            facet = "header";
            tag = HTML.THEAD_ELEM;
            element = HTML.TH_ELEM;
            facetClass = getHeaderClass(uiComponent);
        } else {
            facet = "footer";
            tag = HTML.TFOOT_ELEM;
            element = HTML.TD_ELEM;
            facetClass = getFooterClass(uiComponent);
        }
        UISeries uiData = (UISeries) uiComponent;
        uiData.setRowIndex(-1);
        Element root = (Element) domContext.getRootNode();
        if (isScrollable(uiComponent)) {
            
            if (header) {
                // First table in first div
                root = (Element) root.getFirstChild().getFirstChild();

            } else {
                // First table in second div
                root = (Element) root.getChildNodes().item(1).getFirstChild();
            }
        }
        UIComponent headerFacet = getFacetByName(uiData, facet);
        boolean childHeaderFacetExists =
                childColumnHasFacetWithName(uiData, facet);
        Element thead = null;
        if (headerFacet != null || childHeaderFacetExists) {
            thead = domContext.createElement(tag);
            root.appendChild(thead);
        }
        if (headerFacet != null && headerFacet.isRendered()) {
            resetFacetChildId(headerFacet);
            Element tr = domContext.createElement("tr");
            thead.appendChild(tr);
            Element th = domContext.createElement(element);
            tr.appendChild(th);
            if (facetClass != null) {
                th.setAttribute("class", facetClass);
            }
            th.setAttribute("colspan",
                            String.valueOf(getNumberOfChildColumns(uiData)));
            th.setAttribute("scope", "colgroup");
            domContext.setCursorParent(th);
            domContext.streamWrite(facesContext, uiComponent,
                                   domContext.getRootNode(), th);
            encodeParentAndChildren(facesContext, headerFacet);
            if (isScrollable(uiComponent)) {
                tr.appendChild(scrollBarSpacer(domContext, facesContext));
            }
        }
        StringTokenizer columnWitdths = getColumnWidths(uiData);
        if (childHeaderFacetExists) {
            Element tr = domContext.createElement("tr");
            thead.appendChild(tr);
            List childList = getRenderedChildColumnsList(uiData);
            Iterator childColumns = childList.iterator();
            String width = null;
            int columnIndex = 1;
            while (childColumns.hasNext()) {

                UIComponent nextColumn = (UIComponent) childColumns.next();
                if (columnWitdths != null && columnWitdths.hasMoreTokens()) {
                    width = columnWitdths.nextToken();
                } else {
                    width = null;
                }
                if (nextColumn instanceof UIColumn) {
                    processUIColumnHeader(facesContext, uiComponent,
                                          (UIColumn) nextColumn, tr, domContext,
                                          facet, element, facetClass, width,
                                          columnIndex);
                    columnIndex++;
                } else if (nextColumn instanceof UIColumns) {
                    columnIndex = processUIColumnsHeader(facesContext,
                                                         uiComponent,
                                                         (UIColumns) nextColumn,
                                                         tr, domContext, facet,
                                                         element, facetClass,
                                                         width, columnIndex);
                }
            }
            if (header && isScrollable(uiComponent)) {
                tr.appendChild(scrollBarSpacer(domContext, facesContext));
            }
        }

        domContext.setCursorParent(root);
    }

    private void processUIColumnHeader(FacesContext facesContext,
                                       UIComponent uiComponent,
                                       UIColumn nextColumn, Element tr,
                                       DOMContext domContext, String facet,
                                       String element, String facetClass,
                                       String width, int columnIndex)
            throws IOException {
        HtmlDataTable htmlDataTable = (HtmlDataTable) uiComponent;
        Element th = domContext.createElement(element);
        tr.appendChild(th);
        if (facet.equalsIgnoreCase("header")) {
            facetClass = facetClass + " " +
                         htmlDataTable.getHeaderClassAtIndex(columnIndex);
        }

        th.setAttribute("class", facetClass);
        if (width != null) {
            th.setAttribute("style", "width:" + width + ";overflow:hidden;");
        }
        th.setAttribute("colgroup", "col");
        UIComponent nextFacet = getFacetByName(nextColumn, facet);
        if (nextFacet != null) {
            resetFacetChildId(nextFacet);
            domContext.setCursorParent(th);
            domContext.streamWrite(facesContext, uiComponent,
                                   domContext.getRootNode(), th);
            encodeParentAndChildren(facesContext, nextFacet);
        }
    }

    private int processUIColumnsHeader(FacesContext facesContext,
                                       UIComponent uiComponent,
                                       UIColumns nextColumn, Element tr,
                                       DOMContext domContext, String facet,
                                       String element, String facetClass,
                                       String width, int columnIndex)
            throws IOException {
        HtmlDataTable htmlDataTable = (HtmlDataTable) uiComponent;
        int rowIndex = nextColumn.getFirst();
        nextColumn.setRowIndex(rowIndex);
        while (nextColumn.isRowAvailable()) {
            UIComponent headerFacet = getFacetByName(nextColumn, facet);

            if (headerFacet != null) {
                Node oldParent = domContext.getCursorParent();
                Element th = domContext.createElement(element);
                tr.appendChild(th);
                if (facet.equalsIgnoreCase("header")) {
                    th.setAttribute("class", facetClass + " " + htmlDataTable
                            .getHeaderClassAtIndex(columnIndex));
                } else {
                    th.setAttribute("class", facetClass);
                }
                if (width != null) {
                    th.setAttribute("style", "width:" + width + ";");
                }
                th.setAttribute("colgroup", "col");
                domContext.setCursorParent(th);
                domContext.streamWrite(facesContext, uiComponent,
                                       domContext.getRootNode(), th);

                encodeParentAndChildren(facesContext, headerFacet);
                domContext.setCursorParent(oldParent);
            }
            rowIndex++;
            columnIndex++;
            nextColumn.setRowIndex(rowIndex);
        }
        nextColumn.setRowIndex(-1);
        return columnIndex;
    }


    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent) throws IOException {
        validateParameters(facesContext, uiComponent, null);
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        Element root = (Element) domContext.getRootNode();


        if (isScrollable(uiComponent)) {
            root = (Element) root.getChildNodes().item(1).getFirstChild();
        }
        DOMContext.removeChildrenByTagName(root, HTML.TBODY_ELEM);
        Element tBody = (Element) domContext.createElement(HTML.TBODY_ELEM);
        root.appendChild(tBody);

        HtmlDataTable uiData = (HtmlDataTable) uiComponent;
        int rowIndex = uiData.getFirst();
        uiData.setRowIndex(rowIndex);
        int numberOfRowsToDisplay = uiData.getRows();
        int countOfRowsDisplayed = 0;
        String rowStyles[] = getRowStyles(uiComponent);
        int rowStyleIndex = 0;
        int rowStylesMaxIndex = rowStyles.length - 1;

        RowSelector rowSelector = getRowSelector(uiComponent);
        boolean rowSelectorFound = rowSelector != null;
        String rowSelectionFunctionName = null;

        if (rowSelectorFound) {
            Element rowSelectedField =
                    domContext.createElement(HTML.INPUT_ELEM);
            String tableId = uiComponent.getClientId(facesContext);
            String id = getSelectedRowParameterName(tableId);
            rowSelectedField.setAttribute(HTML.ID_ATTR, id);
            rowSelectedField.setAttribute(HTML.NAME_ATTR, id);
            rowSelectedField.setAttribute(HTML.TYPE_ATTR, "hidden");
            root.appendChild(rowSelectedField);
            rowSelectionFunctionName = "ice_tableRowClicked" + rowSelectorNumber(facesContext); 
            String scriptSrc = "this['" + rowSelectionFunctionName + "'] = function (id){\n";
            scriptSrc += " var fld = $('" + id +
                         "');fld.value = id;var nothingEvent = new Object();\n";
            scriptSrc +=
                    " var form = Ice.util.findForm(fld);iceSubmit(null,fld,nothingEvent);};";
            JavascriptContext.addJavascriptCall(facesContext, scriptSrc);
            Element scriptNode = domContext.createElement(HTML.SCRIPT_ELEM);
            scriptNode.setAttribute("language", "javascript");
            scriptNode.appendChild(domContext.createTextNode(scriptSrc));
            root.appendChild(scriptNode);

        }

        String columnStyles[] = getColumnStyleClasses(uiComponent);
        int columnStyleIndex = 0;
        int columnStylesMaxIndex = columnStyles.length - 1;
        while (uiData.isRowAvailable()) {

           String selectedClass =null;
            Iterator childs = uiData.getChildren().iterator();
            Element tr = (Element) domContext.createElement(HTML.TR_ELEM);
            if (rowSelectorFound) {
                tr.setAttribute("onclick", rowSelectionFunctionName + "('" +
                                           uiData.getRowIndex() + "');");
            }
            String id = uiComponent.getClientId(facesContext);
            tr.setAttribute(HTML.ID_ATTR, id);
            if (rowSelectorFound) {
                if (Boolean.TRUE.equals(rowSelector.getValue())){

                    if(rowSelector.getSelectedClass() != null){
                        selectedClass  = rowSelector.getSelectedClass();
                    }else{
                        selectedClass = "iceRowSelSelected";    
                    }
                }
                String mouseOverClass = "iceRowSelMouseOver";
                if(rowSelector.getMouseOverClass() != null)
                    mouseOverClass = rowSelector.getMouseOverClass();

                StringTokenizer st = new StringTokenizer(mouseOverClass);
                StringBuffer omov = new StringBuffer();
                StringBuffer omot = new StringBuffer();
                while(st.hasMoreTokens()){
                    String t = st.nextToken();
                    omov.append("Element.addClassName($('" + id + "'), '" +
                        t + "');");
                    omot.append("Element.removeClassName($('" + id + "'), '" +
                        t + "');");
                }
                tr.setAttribute(HTML.ONMOUSEOVER_ATTR, omov.toString());
                tr.setAttribute(HTML.ONMOUSEOUT_ATTR, omot.toString());

            }
            domContext.setCursorParent(tBody);
            tBody.appendChild(tr);

            if (selectedClass != null) {
                tr.setAttribute(HTML.CLASS_ATTR, selectedClass);

            } else if (rowStylesMaxIndex >= 0) {
                // if row styles exist, then render the appropriate one
                tr.setAttribute(HTML.CLASS_ATTR, rowStyles[rowStyleIndex]);
            }

            if(rowStylesMaxIndex >= 0){ // Thanks denis tsyplakov
               if (++rowStyleIndex > rowStylesMaxIndex) {
                    rowStyleIndex = 0;
               }
            }
            int colNumber = 1;
            StringTokenizer columnWitdths =
                    getColumnWidths(uiData);
            while (childs.hasNext()) {
                UIComponent nextChild = (UIComponent) childs.next();
                if (nextChild.isRendered()) {
                    if (nextChild instanceof UIColumn) {
                        Element td = domContext.createElement(HTML.TD_ELEM);
                        writeColStyles(columnStyles, columnStylesMaxIndex,
                                       columnStyleIndex, td, colNumber++);
                        if (isScrollable(uiComponent) &&
                            columnWitdths != null &&
                            columnWitdths.hasMoreTokens()) {
                            String width = columnWitdths.nextToken();

                            td.setAttribute("style", "width:" + width +
                                                     ";overflow:hidden;");

                        }
                        tr.appendChild(td);
                        // if column styles exist, then apply the appropriate one

                        if (++columnStyleIndex > columnStylesMaxIndex) {
                            columnStyleIndex = 0;
                        }

                        Node oldCursorParent = domContext.getCursorParent();
                        domContext.setCursorParent(td);
                        domContext.streamWrite(facesContext, uiComponent,
                                               domContext.getRootNode(), td);
                        
                        
                        encodeParentAndChildren(facesContext, nextChild);
                        domContext.setCursorParent(oldCursorParent);

                    } else if (nextChild instanceof UIColumns) {
                        String width = null;
                        if (isScrollable(uiComponent) &&
                            columnWitdths != null &&
                            columnWitdths.hasMoreTokens()) {
                            width = columnWitdths.nextToken();

                        }
                        encodeColumns(facesContext, nextChild, domContext, tr,
                                      columnStyles, columnStylesMaxIndex,
                                      columnStyleIndex, colNumber, width);
                        colNumber = uiData.getColNumber();
                    }
                }

            }
            rowIndex++;
            countOfRowsDisplayed++;
            if (numberOfRowsToDisplay > 0 &&
                    countOfRowsDisplayed >= numberOfRowsToDisplay) {
                    break;
                }            
            uiData.setRowIndex(rowIndex);
        }
        uiData.setRowIndex(-1);
        domContext.stepOver();
        domContext.streamWrite(facesContext, uiComponent);
    }

    private void encodeColumns(FacesContext facesContext, UIComponent columns,
                               DOMContext domContext, Node tr,
                               String[] columnStyles, int columnStylesMaxIndex,
                               int columnStyleIndex, int colNumber,
                               String width) throws IOException {
        UIColumns uiList = (UIColumns) columns;
        int rowIndex = uiList.getFirst();
        uiList.setRowIndex(rowIndex);
        int numberOfRowsToDisplay = uiList.getRows();
        int countOfRowsDisplayed = 0;
        domContext.setCursorParent(tr);
        Node oldCursorParent = domContext.getCursorParent();
        while (uiList.isRowAvailable()) {
            if ((numberOfRowsToDisplay > 0) &&
                (countOfRowsDisplayed >= numberOfRowsToDisplay)) {
                break;
            }
            Iterator childs;
            childs = columns.getChildren().iterator();
            Element td = domContext.createElement(HTML.TD_ELEM);
            if (width != null) {

                td.setAttribute("style",
                                "width:" + width + ";overfolw:hidden;");
            }
            domContext.setCursorParent(oldCursorParent);
            tr.appendChild(td);
            while (childs.hasNext()) {
                UIComponent nextChild = (UIComponent) childs.next();
                if (nextChild.isRendered()) {
                    domContext.setCursorParent(td);
                    writeColStyles(columnStyles, columnStylesMaxIndex,
                                   columnStyleIndex, td, colNumber++);
                    if (++columnStyleIndex > columnStylesMaxIndex) {
                        columnStyleIndex = 0;
                    }
                    encodeParentAndChildren(facesContext, nextChild);
                    domContext.setCursorParent(oldCursorParent);
                }
            }
            rowIndex++;
            countOfRowsDisplayed++;
            uiList.setRowIndex(rowIndex);
        }
        ((HtmlDataTable) uiList.getParent()).setColNumber(colNumber);
        uiList.setRowIndex(-1);
    }

    protected List getRenderedChildColumnsList(UIComponent component) {
        List results = new ArrayList();
        Iterator kids = component.getChildren().iterator();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            if (((kid instanceof UIColumn) && kid.isRendered()) ||
                kid instanceof UIColumns) {
                results.add(kid);
            }
        }
        return results;
    }

    protected boolean childColumnHasFacetWithName(UIComponent component,
                                                  String facetName) {
        Iterator childColumns = getRenderedChildColumnsIterator(component);
        while (childColumns.hasNext()) {
            UIComponent nextChildColumn = (UIComponent) childColumns.next();
            if (getFacetByName(nextChildColumn, facetName) != null) {
                return true;
            }
        }
        return false;
    }

    public static String getSelectedRowParameterName(String dataTableId) {
        // strip the last ':' because the Datatables client Id changes for each iterator
        int i = dataTableId.lastIndexOf(":");
        dataTableId = dataTableId.substring(0, i);
        return dataTableId + SELECTED_ROWS;
    }


    public static RowSelector getRowSelector(UIComponent comp) {
        if (comp instanceof RowSelector) {
            return (RowSelector) comp;
        }
        Iterator iter = comp.getChildren().iterator();
        while (iter.hasNext()) {
            UIComponent kid = (UIComponent) iter.next();
            if (kid instanceof HtmlDataTable){
                // Nested HtmlDataTable might be a peer of
                //  a later, valid RowSelector, so don't
                //  traverse in, but keep looking
                continue;
            }
            RowSelector rs = getRowSelector(kid);
            if (rs != null) {
                return rs;
            }
        }
        return null;
    }

    private int rowSelectorNumber(FacesContext context){
        Map m = context.getExternalContext().getRequestMap();
        String key = RowSelector.class.getName() + "-Selector";
        Integer I = (Integer)m.get(key);
        int i = 0;
        if(I != null){
            i = I.intValue();
            i++;
        }

        I = new Integer(i);
        m.put(key, I);
        return i;
    }

    protected int getNumberOfChildColumns(UIComponent component) {
        int size = getRenderedChildColumnsList(component).size();
        Iterator it = getRenderedChildColumnsList(component).iterator();
        while (it.hasNext()) {
        	UIComponent uiComponent = (UIComponent)it.next(); 
        	if (uiComponent instanceof UIColumns) {
        		size +=((UIColumns)uiComponent).getRowCount();
        	}
        }
        return size;
    }
}
