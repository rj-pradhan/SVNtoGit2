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

package com.icesoft.faces.renderkit.dom_html_basic;

import com.icesoft.faces.context.DOMContext;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIMessages;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Iterator;


public class MessagesRenderer extends DomBasicRenderer {

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, UIMessages.class);
    }

    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent) throws IOException {
        validateParameters(facesContext, uiComponent, UIMessages.class);
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, UIMessages.class);

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        if (domContext.isStreamWriting()) {
            writeStream(facesContext, uiComponent);
            return;
        }

        // retrieve the messages
        Iterator messagesIterator = null;
        boolean retrieveGlobalMessagesOnly =
                ((UIMessages) uiComponent).isGlobalOnly();
        if (retrieveGlobalMessagesOnly) {
            messagesIterator = facesContext.getMessages(null);
        } else {
            messagesIterator = facesContext.getMessages();
        }

        // layout
        boolean tableLayout = false; // default layout is list
        String layout = (String) uiComponent.getAttributes().get("layout");
        if (layout != null && layout.toLowerCase().equals("table")) {
            tableLayout = true;
        }

        // the target element to which messages are appended; either td or span
        Element parentTarget = null;
        if (tableLayout) {
            if (!domContext.isInitialized()) {
                parentTarget = domContext.createElement("table");
                domContext.setRootNode(parentTarget);
                setRootElementId(facesContext, parentTarget, uiComponent);
            } else {
                // remove previous messages
                parentTarget = (Element) domContext.getRootNode();
                DOMContext.removeChildrenByTagName(parentTarget, "tr");
            }
        } else {
            if (!domContext.isInitialized()) {
                Element list = domContext.createElement(HTML.UL_ELEM);
                domContext.setRootNode(list);
                parentTarget = list;
                setRootElementId(facesContext, list, uiComponent);
            } else {
                // remove previous messages
                parentTarget = (Element) domContext.getRootNode();
                DOMContext.removeChildrenByTagName(parentTarget, HTML.LI_ELEM);
            }
        }

        FacesMessage nextFacesMessage = null;
        while (messagesIterator.hasNext()) {

            nextFacesMessage = (FacesMessage) messagesIterator.next();

            String[] styleAndStyleClass =
                    getStyleAndStyleClass(uiComponent, nextFacesMessage);
            String style = styleAndStyleClass[0];
            String styleClass = styleAndStyleClass[1];

            String[] summaryAndDetail = getSummaryAndDetail(nextFacesMessage);
            String summary = summaryAndDetail[0];
            String detail = summaryAndDetail[1];

            boolean showSummary = ((UIMessages) uiComponent).isShowSummary();
            boolean showDetail = ((UIMessages) uiComponent).isShowDetail();

            Element nextTableData = null;
            if (tableLayout) {
                Element tr = domContext.createElement("tr");
                Element td = domContext.createElement("td");
                tr.appendChild(td);
                parentTarget.appendChild(tr);
                nextTableData = td;
            }

            Element nextMessageSpan = domContext.createElement(HTML.SPAN_ELEM);
            if (tableLayout) {
                nextTableData.appendChild(nextMessageSpan);
            } else {
                Element li = domContext.createElement(HTML.LI_ELEM);
                parentTarget.appendChild(li);
                li.appendChild(nextMessageSpan);
            }

            if (null != styleClass) {
                nextMessageSpan.setAttribute("class", styleClass);
            }
            if (style != null && style.length() > 0) {
                nextMessageSpan.setAttribute("style", style);
            }
            else {
                nextMessageSpan.removeAttribute("style");
            }

            boolean tooltip = getToolTipAttribute(uiComponent);

            if (showSummary && showDetail && tooltip) {
                // show summary as tooltip, detail as child span
                nextMessageSpan.setAttribute("title", summary);
                Text textNode = domContext.getDocument().createTextNode(detail);
                nextMessageSpan.appendChild(textNode);
            } else {
                if (showSummary) {
                    Text textNode =
                            domContext.getDocument().createTextNode(summary);
                    nextMessageSpan.appendChild(textNode);
                }
                if (showDetail) {
                    Text textNode =
                            domContext.getDocument().createTextNode(detail);
                    nextMessageSpan.appendChild(textNode);
                }
            }

        }
        domContext.stepOver();
    }

    private void writeStream(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        Element root = domContext.createRootElement(HTML.SPAN_ELEM);
        Text text = domContext.createTextNode("List of Messages");
        Object style = uiComponent.getAttributes().get("style");
        String sstyle = ( (style == null) ? null : style.toString() );
        if (sstyle != null && sstyle.length() > 0) {
            root.setAttribute(HTML.STYLE_ATTR, sstyle);
        }
        else {
            root.removeAttribute(HTML.STYLE_ATTR);
        }
        root.appendChild(text);
        domContext.streamWrite(facesContext, uiComponent);
        domContext.stepOver();
    }

}

