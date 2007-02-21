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

import com.icesoft.faces.component.ext.HtmlDataTable;
import com.icesoft.faces.component.ext.RowSelector;
import com.icesoft.faces.component.ext.RowSelectorEvent;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA. User: rmayhew Date: Aug 28, 2006 Time: 12:48:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class RowSelectorRenderer extends DomBasicRenderer {

    // Decode Method
    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        super.decode(facesContext, uiComponent);

        // Check for row selection in its parent table hidden field
        HtmlDataTable dataTable = getParentDataTable(uiComponent);

        String dataTableId = dataTable.getClientId(facesContext);
        String selectedRowsParameter =
                TableRenderer.getSelectedRowParameterName(dataTableId);
        String selectedRows = (String) facesContext.getExternalContext()
                .getRequestParameterMap().get(selectedRowsParameter);


        if (selectedRows == null || selectedRows.trim().length() == 0) {           
           // selectedRows = (String)facesContext.getExternalContext().getRequestMap().get(RowSelectorRenderer.class.getName());
           // if(selectedRows == null || selectedRows.trim().length() == 0)
                return;
        }else{
            //Myfaces 1.1.0 Work around
            facesContext.getExternalContext().getRequestMap().put(RowSelectorRenderer.class.getName(), selectedRows);
        }
        // What row number am I, was I clicked?
        int rowIndex = dataTable.getRowIndex();
        StringTokenizer st = new StringTokenizer(selectedRows, ",");
        boolean rowClicked = false;
        while (st.hasMoreTokens()) {
            int row = Integer.parseInt(st.nextToken());
            if (row == rowIndex) {
                rowClicked = true;
                break;
            }
        }
        RowSelector rowSelector = (RowSelector) uiComponent;

        try {
            if (rowClicked) {
                // Toggle the row selection if multiple
                boolean b = rowSelector.getValue().booleanValue();
                b = !b;
                rowSelector.setValue(new Boolean(b));

                if (rowSelector.getSelectionListener() != null) {
                    RowSelectorEvent evt =
                            new RowSelectorEvent(rowSelector, rowIndex, b);
                    evt.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);

                    rowSelector.queueEvent(evt);
                }

            } else {
                if (Boolean.FALSE.equals(rowSelector.getMultiple())) {
                    // Clear all other selections
                    rowSelector.setValue(Boolean.FALSE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        super.encodeEnd(facesContext, uiComponent);

        // Nothing is rendered
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        super.encodeBegin(facesContext, uiComponent);
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        if (!domContext.isInitialized()) {
            Element root = domContext.createRootElement(HTML.DIV_ELEM);


        }
        uiComponent.setRendered(true);
        // Mothing is rendered
    }

    private static HtmlDataTable getParentDataTable(UIComponent uiComponenent) {
        UIComponent parentComp = uiComponenent.getParent();
        if (parentComp == null) {
            throw new RuntimeException(
                    "RowSelectorRenderer: decode. Could not find an Ice:dataTable as a parent componenent");
        }
        if (parentComp instanceof com.icesoft.faces.component.ext.HtmlDataTable) {
            return (HtmlDataTable) parentComp;
        }
        return getParentDataTable(parentComp);
    }

}
