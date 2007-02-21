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

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.ApplicationBaseLocator;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicInputRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;
import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;

public class InputFileRenderer extends DomBasicInputRenderer {

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, InputFile.class);
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        InputFile inputFile = (InputFile) uiComponent;
        if (domContext.isStreamWriting()) {
            writeStream(facesContext, uiComponent);
            return;
        }

        if (!domContext.isInitialized()) {
            Element table = domContext.createRootElement(HTML.TABLE_ELEM);
            //temp attribute
            table.setAttribute("style", "border:1px; width:400px");
            setRootElementId(facesContext, table, uiComponent);
            Element tbody = domContext.createElement(HTML.TBODY_ELEM);
            table.appendChild(tbody);
            inputFile.setFirstTd(
                    createRowColumn(domContext, "uploadTd", "height:60px;"));
            inputFile.setSecondTd(createRowColumn(domContext, "detailTd", ""));
            inputFile.getFirstTd().appendChild(
                    getIframe(facesContext, uiComponent, domContext));
        }
        DOMContext.removeChildren(inputFile.getFirstTd());
        inputFile.getFirstTd()
                .appendChild(getIframe(facesContext, uiComponent, domContext));

        PassThruAttributeRenderer
                .renderAttributes(facesContext, uiComponent, null);
        domContext.stepOver();
    }

    private Element createRowColumn(DOMContext domContext, String id,
                                    String style) {
        Element tr = domContext.createElement(HTML.TR_ELEM);
        Element td = domContext.createElement(HTML.TD_ELEM);
        td.setAttribute(HTML.ID_ATTR, id);
        td.setAttribute(HTML.STYLE_ATTR, style);
        tr.appendChild(td);
        domContext.getRootNode().getFirstChild().appendChild(tr);
        return td;
    }

    private Element getIframe(FacesContext facesContext,
                              UIComponent uiComponent, DOMContext domContext) {
        InputFile inputFile = (InputFile) uiComponent;
        Element iframe = domContext.createElement(HTML.IFRAME_ELEM);
        iframe.setAttribute(HTML.SRC_ATTR, ApplicationBaseLocator
                .locate(facesContext) + InputFile.ICE_UPLOAD_FILE +
                                      inputFile.getQueryString(facesContext));
        iframe.setAttribute(HTML.NAME_ATTR, InputFile.FILE_UPLOAD_PREFIX +
                                            uiComponent
                                                    .getClientId(facesContext));
        iframe.setAttribute(HTML.FRAMEBORDER_ATTR, "0");
        if (((InputFile) uiComponent).getStyle() != null) {
            iframe.setAttribute(HTML.STYLE_ATTR,
                                ((InputFile) uiComponent).getStyle());
        }
        iframe.setAttribute(HTML.CLASS_ATTR,
                            ((InputFile) uiComponent).getStyleClass());
        iframe.setAttribute(HTML.WIDTH_ATTR, "400");
        iframe.setAttribute(HTML.HEIGHT_ATTR, "60");
        return iframe;
    }

    void writeStream(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        InputFile inputFile = ((InputFile) uiComponent);
        Element root = domContext.createRootElement(HTML.DIV_ELEM);
        root.setAttribute(HTML.STYLE_ATTR, inputFile.getStyle());
        root.setAttribute(HTML.CLASS_ATTR, inputFile.getStyleClass());
        Element upload = domContext.createElement(HTML.INPUT_ELEM);
        upload.setAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_FILE);
        upload.setAttribute(HTML.CLASS_ATTR, inputFile.getInputTextClass());
        root.appendChild(upload);
        Element submit = domContext.createElement(HTML.INPUT_ELEM);
        submit.setAttribute(HTML.TYPE_ATTR, HTML.INPUT_TYPE_SUBMIT);
        submit.setAttribute(HTML.CLASS_ATTR, inputFile.getButtonClass());
        submit.setAttribute(HTML.VALUE_ATTR, inputFile.getLabel());
        root.appendChild(submit);
        domContext.streamWrite(facesContext, uiComponent);
    }
}