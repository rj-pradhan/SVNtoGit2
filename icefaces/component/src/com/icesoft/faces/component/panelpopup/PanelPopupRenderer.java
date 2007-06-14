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

package com.icesoft.faces.component.panelpopup;

import com.icesoft.faces.component.ext.renderkit.GroupRenderer;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.component.util.CustomComponentUtils;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;
import com.icesoft.faces.util.CoreUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.io.IOException;

/**
 * <p>PanelPopupRenderer is an extension of ICEfaces D2D GroupRenderer
 * responsible for rendering the PanelPopup component.</p>
 */
public class PanelPopupRenderer extends GroupRenderer {
    private static Log log = LogFactory.getLog(PanelPopupRenderer.class);

    /* (non-Javadoc)
    * @see com.icesoft.faces.renderkit.dom_html_basic.GroupRenderer#getRendersChildren()
    */
    public boolean getRendersChildren() {
        return true;
    }

    /* (non-Javadoc)
     * @see com.icesoft.faces.component.ext.renderkit.GroupRenderer#encodeBegin(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, PanelPopup.class);

        String styleClass =
                (String) uiComponent.getAttributes().get("styleClass");
        String headerClass =
                (String) uiComponent.getAttributes().get("headerClass");
        String bodyClass =
                (String) uiComponent.getAttributes().get("bodyClass");
        Boolean resizable =
                null; // resizable functionality has not been implemented yet.
        Boolean modal = (Boolean) uiComponent.getAttributes().get("modal");
        if (log.isTraceEnabled()) {
            log.trace("Value of modal is [" + modal + "]");
        }
        Boolean visible = (Boolean) uiComponent.getAttributes().get("visible");

        String dndType = getDndType(uiComponent);

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        // initialize DOMContext
        PanelPopup panelPopup = (PanelPopup) uiComponent;

        String clientId = uiComponent.getClientId(facesContext);

        if (!domContext.isInitialized()) {
            Element rootDiv = domContext.createRootElement(HTML.DIV_ELEM);
            setRootElementId(facesContext, rootDiv, uiComponent);
            rootDiv.setAttribute(HTML.NAME_ATTR, clientId);
            Element table = domContext.createElement(HTML.TABLE_ELEM);
            table.setAttribute(HTML.CELLPADDING_ATTR, "0");
            table.setAttribute(HTML.CELLSPACING_ATTR, "0");
            table.setAttribute(HTML.WIDTH_ATTR, "100%");

            rootDiv.appendChild(table);
            if (modal != null && modal.booleanValue()) {
                dndType = null;
            }
            // extracted from GroupRenderer encodeBegin
            if (dndType != null) {
                // Drag an drop needs some hidden fields
                Element statusField = createHiddenField(domContext,
                                                        facesContext,
                                                        uiComponent, STATUS);
                rootDiv.appendChild(statusField);
                Element targetID = createHiddenField(domContext, facesContext,
                                                     uiComponent, DROP);
                rootDiv.appendChild(targetID);
            }
            // Write Modal Javascript so that on refresh it will still be modal.
            String script =
                    modalJavascript(modal, visible, facesContext, clientId);
            if (script != null) {
                Element scriptEle = domContext.createElement(HTML.SCRIPT_ELEM);
                scriptEle.setAttribute(HTML.SCRIPT_LANGUAGE_ATTR,
                                       HTML.SCRIPT_LANGUAGE_JAVASCRIPT);
                script = "window.onLoad(function(){" + script + "});";
                Node node = domContext.createTextNode(script);
                scriptEle.appendChild(node);
                rootDiv.appendChild(scriptEle);
            }
        }

        Element root = (Element) domContext.getRootNode();
        String style = ((PanelPopup) uiComponent).getStyle();
        if(style != null && style.length() > 0)
            root.setAttribute(HTML.STYLE_ATTR, style);
        else
            root.removeAttribute(HTML.STYLE_ATTR);
        try {
            root.setAttribute(HTML.CLASS_ATTR, styleClass);
            String script =
                    modalJavascript(modal, visible, facesContext, clientId);
            if (script != null) {
                JavascriptContext.addJavascriptCall(facesContext, script);
            }
        } catch (Exception e) {
            log.error("Error rendering Modal Panel Popup ", e);
        }
        // get tables , our table is the first and only one
        NodeList tables = root.getElementsByTagName(HTML.TABLE_ELEM);
        // assumption we want the first table in tables. there should only be one
        Element table = (Element) tables.item(0);
        // clean out child nodes and build a fresh selectinputdate
        DOMContext.removeChildrenByTagName(table, HTML.TR_ELEM);

        PassThruAttributeRenderer
                .renderAttributes(facesContext, uiComponent, null);
        String handleId = null;
        if (panelPopup.getHeader() != null) {
            Element headerTr = domContext.createElement(HTML.TR_ELEM);
            Element headerTd = domContext.createElement(HTML.TD_ELEM);
            headerTd.setAttribute(HTML.CLASS_ATTR, headerClass);
            handleId = uiComponent.getClientId(facesContext) + "Handle";
            headerTd.setAttribute(HTML.ID_ATTR, handleId);
            headerTr.appendChild(headerTd);
            // add header facet to header tr and add to table
            table.appendChild(headerTr);
            // set the cursor parent to the new table row Element
            // to the new table row Element
            domContext.setCursorParent(headerTd);

            UIComponent header = panelPopup.getHeader();

            domContext.streamWrite(facesContext, uiComponent,
                                   domContext.getRootNode(), headerTd);

            CustomComponentUtils.renderChild(facesContext, header);
        }

        if (panelPopup.getBody() != null) {

            Element bodyTr = domContext.createElement(HTML.TR_ELEM);
            Element bodyTd = domContext.createElement(HTML.TD_ELEM);


            bodyTd.setAttribute(HTML.CLASS_ATTR, bodyClass);
            bodyTr.appendChild(bodyTd);
            // add body facet to body tr then add to table
            table.appendChild(bodyTr);
            // set the cursor parent to the new table row Element
            // this will cause the renderChild method to append the child nodes
            // to the new table row Element
            domContext.setCursorParent(bodyTd);

            UIComponent body = panelPopup.getBody();

            domContext.streamWrite(facesContext, uiComponent,
                                   domContext.getRootNode(), bodyTd);

            CustomComponentUtils.renderChild(facesContext, body);
        }
        // if the popup is resizable render a resize handle
        if (resizable != null && resizable.booleanValue()) {
            Element footerTr = domContext.createElement(HTML.TR_ELEM);
            footerTr.setAttribute(HTML.HEIGHT_ATTR, "15px");
            footerTr.setAttribute(HTML.STYLE_ATTR,
                                  "text-align: right; float: right;");
            Element footerTd = domContext.createElement(HTML.TD_ELEM);
            footerTd.setAttribute(HTML.STYLE_CLASS_ATTR, "panelPopupFooter");
            Element img = domContext.createElement(HTML.IMG_ELEM);
            img.setAttribute(HTML.SRC_ATTR, CoreUtils
                    .resolveResourceURL(facesContext,
                    "/xmlhttp/css/xp/css-images/resize.gif"));
            img.setAttribute(HTML.STYLE_ATTR, "cursor: se-resize");
            footerTd.appendChild(img);
            footerTr.appendChild(footerTd);
            table.appendChild(footerTr);
        }

        domContext.stepOver();
        domContext.streamWrite(facesContext, uiComponent);
        CurrentStyle.apply(facesContext, uiComponent);
        // Rebroadcast Javascript to survive refresh
        if (dndType != null) {
            addJavascriptCalls(uiComponent, "DRAG", handleId, facesContext);
        }
    }

    private String modalJavascript(Boolean modal, Boolean visible,
                                   FacesContext facesContext, String clientId) {
        String call = null;
        if (modal != null) {
            if (modal.booleanValue() && visible.booleanValue()) {
                call = "Ice.modal.start('" + clientId + "');";
                if (log.isTraceEnabled()) {
                    log.trace("Starting Modal Function");
                }
            } else {
                call = "Ice.modal.stop('" + clientId + "');";
                if (log.isTraceEnabled()) {
                    log.trace("Stopping modal function");
                }
            }
        }
        return call;
    }

    /* (non-Javadoc)
     * @see com.icesoft.faces.renderkit.dom_html_basic.GroupRenderer#encodeChildren(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent) throws IOException {
    }

    /* (non-Javadoc)
     * @see com.icesoft.faces.component.ext.renderkit.GroupRenderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("Encode End Called");
        }
    }
}