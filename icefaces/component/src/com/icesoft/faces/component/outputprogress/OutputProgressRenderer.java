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

package com.icesoft.faces.component.outputprogress;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicInputRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;

public class OutputProgressRenderer extends DomBasicInputRenderer {


    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {


        validateParameters(facesContext, uiComponent, OutputProgress.class);
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        if (!domContext.isInitialized()) {
            Element table = domContext.createRootElement(HTML.TABLE_ELEM);
            setRootElementId(facesContext, table, uiComponent);
            table.setAttribute(HTML.CELLPADDING_ATTR, "0");
            table.setAttribute(HTML.CELLSPACING_ATTR, "0");
            table.setAttribute(HTML.BORDER_ATTR, "0");
        }
        Element table = (Element) domContext.getRootNode();
        table.setAttribute(HTML.STYLE_ATTR,
                           ((OutputProgress) uiComponent).getStyle());
        //In order to fix IRAPtor Bug 291, we took out buildLayout() from the intialized block, 
        //Because of variouse text position, layout could have different combination of tr and td
        //therefore we are storing nodes to the component itself.
        buildLayout(table, uiComponent, domContext);

        setPercentage(facesContext, uiComponent);
        if (PassThruAttributeRenderer.passThruAttributeExists(uiComponent)) {
            PassThruAttributeRenderer
                    .renderAttributes(facesContext, uiComponent, null);
        }

        domContext.stepOver();
        domContext.streamWrite(facesContext, uiComponent);
    }


    private void setPercentage(FacesContext facesContext,
                               UIComponent uiComponent) {
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        String space = "&nbsp;";
        if (domContext.isStreamWriting()) {
            space = "";
        }

        OutputProgress progressBar = (OutputProgress) uiComponent;
        String base = progressBar.getStyleClass();
        if (base == null) {
            base = CSS_DEFAULT.OUTPUT_PROGRESS_BASE_CLASS;
        }

        Text percentageText = progressBar.getTextNode();
        Element bgBar = progressBar.getBarNode();
        Element fillBar = (Element) bgBar.getFirstChild();

        String progressLabel = progressBar.getProgressLabel();
        int percentValue = progressBar.getValue();
        if (percentValue > 100) {
            percentValue = 100;
        }
        if (percentValue < 0) {
            percentValue = 0;
        }
        //update percent value in determinate mode only
        if (progressBar.getIndeterminate() == false) {
            percentageText.setData(percentValue + " %");
        }

        if (percentValue < 100) {

            if (progressLabel != null && progressLabel.length() > 0) {
                percentageText.setData(progressLabel);
            }
            //following if block is for Indeterminate mode only
            if (progressBar.getIndeterminate()) {
                if (percentValue < 1) {
                    String className = Util.appendNewStyleClass(
                            CSS_DEFAULT.OUTPUT_PROGRESS_BASE_CLASS,
                            base,
                            CSS_DEFAULT.OUTPUT_PROGRESS_INDEREMINATE_INACTIVE_CLASS);
                    fillBar.setAttribute(HTML.CLASS_ATTR, className);
                    percentageText.setData(space);
                } else {
                    String className = Util.appendNewStyleClass(
                            CSS_DEFAULT.OUTPUT_PROGRESS_BASE_CLASS,
                            base,
                            CSS_DEFAULT.OUTPUT_PROGRESS_INDERERMINATE_ACTIVE_CLASS);
                    fillBar.setAttribute(HTML.CLASS_ATTR, className);
                    fillBar.setAttribute(HTML.STYLE_ATTR,
                                         "position:absolute;width:100%");

                    if (progressLabel != null && progressLabel.length() > 0) {
                        percentageText.setData(progressLabel);
                    } else {
                        percentageText.setData(space);
                    }
                }
            }

        } else {
            if (progressBar.getIndeterminate()) {
                String className = Util.appendNewStyleClass(
                        CSS_DEFAULT.OUTPUT_PROGRESS_BASE_CLASS,
                        base,
                        CSS_DEFAULT.OUTPUT_PROGRESS_INDEREMINATE_INACTIVE_CLASS);
                fillBar.setAttribute(HTML.CLASS_ATTR, className);
                fillBar.setAttribute(HTML.STYLE_ATTR,
                                     "position:absolute;width:100%;");
            }
            String progressCompleteLabel = progressBar.getProgressLabelComplete();
            if (progressCompleteLabel != null && progressCompleteLabel.length() > 0) {
                percentageText.setData(progressCompleteLabel);
            }
        }

        // The following fix is required for determinate mode only
        if (progressBar.getIndeterminate() == false) {
            // This code it to fix IE renderering. If a nbsp is present and the value is zero
            // then a tiny bit of the bar is rendered. However if this value is missing then
            // firefox will not render the bar. Therefore we don't add the nbsp until we
            // need to render the bar        
            Node node = fillBar.getFirstChild();
            if (node instanceof Text) {
                if (percentValue <= 0) {
                    fillBar.removeChild(node);
                }
            } else if (node == null) {
                if (percentValue > 0) {
                    Text nbsp4opera = domContext.createTextNode("&nbsp;");
                    fillBar.appendChild(nbsp4opera);
                }
            }
        }
        //set the percent value for determinate mode only
        if (progressBar.getIndeterminate() == false) {
            fillBar.setAttribute(HTML.STYLE_ATTR, "position:absolute;width:" +
                                                  percentValue + "%;");
        }
    }

    private void buildLayout(Element table, UIComponent uiComponent,
                             DOMContext domContext) {
        String space = "&nbsp;";
        if (domContext.isStreamWriting()) {
            space = "";
        }
        Node node = table.getFirstChild();
        Element tbody = domContext.createElement(HTML.TBODY_ELEM);
        if (node != null) {
            table.replaceChild(tbody, node);
        } else {
            table.appendChild(tbody);
        }


        OutputProgress progressBar = (OutputProgress) uiComponent;
        String base = progressBar.getStyleClass();
        if (base == null) {
            base = CSS_DEFAULT.OUTPUT_PROGRESS_BASE_CLASS;
        }
        table.setAttribute(HTML.CLASS_ATTR, progressBar.getStyleClass());

        String textStyleClass =
                Util.appendNewStyleClass(CSS_DEFAULT.OUTPUT_PROGRESS_BASE_CLASS,
                                         base,
                                         CSS_DEFAULT.OUTPUT_PROGRESS_TEXT_STYLE_CLASS);
        String bgStyleClass =
                Util.appendNewStyleClass(CSS_DEFAULT.OUTPUT_PROGRESS_BASE_CLASS,
                                         base,
                                         CSS_DEFAULT.OUTPUT_PROGRESS_BG_STYLE_CLASS);
        String fillStyleClass =
                Util.appendNewStyleClass(CSS_DEFAULT.OUTPUT_PROGRESS_BASE_CLASS,
                                         base,
                                         CSS_DEFAULT.OUTPUT_PROGRESS_FILL_STYLE_CLASS);
        Element row = domContext.createElement(HTML.TR_ELEM);
        Element textTd = domContext.createElement(HTML.TD_ELEM);
        textTd.setAttribute(HTML.CLASS_ATTR, textStyleClass);

        Element barTd = domContext.createElement(HTML.TD_ELEM);
        tbody.appendChild(row);
        Text percentageText = null;
        if (progressBar.getProgressLabel() != null) {
            //add the blank label initially
            percentageText = domContext.createTextNode(space);
        } else {
            percentageText = domContext.createTextNode("0 %");
        }

        textTd.appendChild(percentageText);
        textTd.setAttribute("id", uiComponent
                .getClientId(FacesContext.getCurrentInstance()) +
                                                                "percentageText");

        Element bgBar = domContext.createElement(HTML.DIV_ELEM);
        bgBar.setAttribute(HTML.CLASS_ATTR, bgStyleClass);
        bgBar.setAttribute(HTML.STYLE_ATTR, "position:relative;");

        Element fillBar = domContext.createElement(HTML.DIV_ELEM);
        fillBar.setAttribute(HTML.ID_ATTR, uiComponent
                .getClientId(FacesContext.getCurrentInstance()) + "bar");

        if (progressBar.getIndeterminate() == false) { //determinate mode
            fillBar.setAttribute(HTML.CLASS_ATTR, fillStyleClass);
            fillBar.setAttribute(HTML.STYLE_ATTR, "position:absolute;width:0%");
        } else {// indeterminate mode
            fillBar.setAttribute(HTML.CLASS_ATTR,
                                 Util.appendNewStyleClass(
                                         CSS_DEFAULT.OUTPUT_PROGRESS_BASE_CLASS,
                                         base,
                                         CSS_DEFAULT.OUTPUT_PROGRESS_INDEREMINATE_INACTIVE_CLASS
                                 )
            );
            fillBar.setAttribute(HTML.STYLE_ATTR,
                                 "position:absolute;width:100%;");
        }


        bgBar.appendChild(fillBar);
        Text nbsp4mozila = domContext.createTextNode(space);

        barTd.appendChild(bgBar);

        progressBar.setTextNode(percentageText);
        progressBar.setBarNode(bgBar);

        String textPosition = progressBar.getLabelPosition();

        if (!isValidTextPosition(textPosition.toString().toLowerCase())) {
            throw new FacesException(
                    "Please define valid textPosition [top|bottom|left|right|topcenter|bottomcenter|topright|bottomright|embed]");
        }

        if (textPosition.toString().equalsIgnoreCase("left")) {
            textTd.setAttribute("style", "vertical-align: middle;");
            row.appendChild(textTd);
            row.appendChild(barTd);
        }
        if (textPosition.toString().equalsIgnoreCase("right")) {
            textTd.setAttribute("style", "vertical-align: middle;");
            row.appendChild(barTd);
            row.appendChild(textTd);
        }

        if (textPosition.toString().toLowerCase().startsWith("top")) {
            Element row2 = domContext.createElement(HTML.TR_ELEM);
            row.appendChild(textTd);
            row2.appendChild(barTd);
            tbody.appendChild(row2);
            if (textPosition.toString().equalsIgnoreCase("topcenter")) {
                textTd.setAttribute("align", "center");
            }
            if (textPosition.toString().equalsIgnoreCase("topright")) {
                textTd.setAttribute("align", "right");
            }
        }

        if (textPosition.toString().toLowerCase().startsWith("bottom")) {
            Element row2 = domContext.createElement(HTML.TR_ELEM);
            row.appendChild(barTd);
            row2.appendChild(textTd);
            tbody.appendChild(row2);
            if (textPosition.toString().equalsIgnoreCase("bottomcenter")) {
                textTd.setAttribute("align", "center");
            }
            if (textPosition.toString().equalsIgnoreCase("bottomright")) {
                textTd.setAttribute("align", "right");
            }
        }

        if (textPosition.toString().equalsIgnoreCase("embed")) {
            Element embedDiv = domContext.createElement(HTML.DIV_ELEM);
            embedDiv.setAttribute(HTML.CLASS_ATTR, textStyleClass);
            embedDiv.setAttribute(HTML.STYLE_ATTR,
                                  "text-align:center;position:relative;background-color:transparent;width:100%;z-index:1;");
            embedDiv.appendChild(percentageText);

            if (progressBar.getIndeterminate() == false) {//determinate mode
                bgBar.appendChild(embedDiv);
            } else {//indeterminate mode
                fillBar.appendChild(embedDiv);
            }
            row.appendChild(barTd);
        } else {
            //&nbsp fix for mozila
            Text nbsp4opera = domContext.createTextNode(space);
            //&nbsp fix for opera
            fillBar.appendChild(nbsp4opera);
            bgBar.appendChild(nbsp4mozila);
        }
    }

    private boolean isValidTextPosition(String textPosition) {
        String[] validPosition = {"top", "bottom", "left", "right", "topcenter",
                                  "bottomcenter", "topright", "bottomright",
                                  "embed"};
        for (int i = 0; i < validPosition.length; i++) {
            if (validPosition[i].equals(textPosition)) {
                return true;
            }
        }
        return false;
    }
}
