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

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class SelectManyCheckboxListRenderer extends MenuRenderer {

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        validateParameters(facesContext, uiComponent, null);

        int counter = 0;

        boolean renderVertically = false;
        String layout = (String) uiComponent.getAttributes().get("layout");
        if (layout != null) {
            renderVertically =
                    layout.equalsIgnoreCase("pageDirection") ? true : false;
        }

        int border = getBorderSize(uiComponent);

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        Element rootTR = null;

        // remove all existing table rows from the root table
        if (domContext.isInitialized()) {
            DOMContext.removeChildrenByTagName(
                    (Element) domContext.getRootNode(), "tr");
        } else {
            Element root = domContext.createElement("table");
            domContext.setRootNode(root);
            if (idNotNull(uiComponent)) {
                setRootElementId(facesContext, root, uiComponent);
            }
        }

        Element rootTable = (Element) domContext.getRootNode();
        String styleClass =
                (String) uiComponent.getAttributes().get("styleClass");
        if (styleClass != null) {
            rootTable.setAttribute("class", styleClass);
        }
        String style = (String) uiComponent.getAttributes().get("style");
        if (style != null) {
            rootTable.setAttribute("style", style);
        }
        rootTable.setAttribute("border", new Integer(border).toString());

        if (!renderVertically) {
            rootTR = domContext.createElement("tr");
            rootTable.appendChild(rootTR);
        }

        Iterator options = getSelectItems(uiComponent);

        //We should call uiComponent.getValue() only once, becase if it binded with the bean,
        //The bean method would be called as many time as this method call.
        Object componentValue = ((UIInput) uiComponent).getValue();

        while (options.hasNext()) {
            SelectItem nextSelectItem = (SelectItem) options.next();

            counter++;

            // render a SelectItemGroup in a nested table
            if (nextSelectItem instanceof SelectItemGroup) {

                Element nextTR = null;
                Element nextTD = null;

                if (nextSelectItem.getLabel() != null) {
                    if (renderVertically) {
                        nextTR = domContext.createElement("tr");
                        rootTable.appendChild(nextTR);
                    }
                    nextTD = domContext.createElement("td");
                    nextTR.appendChild(nextTD);
                    Text label = domContext.getDocument()
                            .createTextNode(nextSelectItem.getLabel());
                    nextTD.appendChild(label);
                }
                if (renderVertically) {
                    nextTR = domContext.createElement("tr");
                    rootTable.appendChild(nextTR);
                }
                nextTD = domContext.createElement("td");
                nextTR.appendChild(nextTD);

                SelectItem[] selectItemsArray =
                        ((SelectItemGroup) nextSelectItem).getSelectItems();
                for (int i = 0; i < selectItemsArray.length; ++i) {
                    renderOption(facesContext, uiComponent, selectItemsArray[i],
                                 renderVertically, nextTR, counter,
                                 componentValue);
                }
            } else {
                renderOption(facesContext, uiComponent, nextSelectItem,
                             renderVertically, rootTR, counter,
                             componentValue);
            }
        }

        domContext.stepOver();
        domContext.streamWrite(facesContext, uiComponent);
    }

    private int getBorderSize(UIComponent uiComponent) {
        int border = 0;
        Object borderAttribute = uiComponent.getAttributes().get("border");
        if (borderAttribute instanceof Integer) {
            if (((Integer) borderAttribute).intValue() != Integer.MIN_VALUE) {
                border = ((Integer) borderAttribute).intValue();
            }
        } else {
            try {
                border = Integer.valueOf(borderAttribute.toString()).intValue();
            } catch (NumberFormatException nfe) {
                // couldn't parse it; stick with the default (initial) value of 0
            }
        }
        return border;
    }

    protected void renderOption(FacesContext facesContext,
                                UIComponent uiComponent,
                                SelectItem selectItem, boolean renderVertically,
                                Element rootTR, int counter,
                                Object componentValue)
            throws IOException {

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        Element rootTable = (Element) domContext.getRootNode();

        boolean disabled = false;
        if (uiComponent.getAttributes().get("disabled") != null) {
            if ((uiComponent.getAttributes().get("disabled"))
                    .equals(Boolean.TRUE)) {
                disabled = true;
            }
        }
        if (selectItem.isDisabled()) {
            disabled = true;
        }
        String labelClass =
                (String) uiComponent.getAttributes().get("styleClass");
        if (disabled) {
            labelClass += "-dis";
        }


        if (renderVertically) {
            rootTR = domContext.createElement("tr");
            rootTable.appendChild(rootTR);
        }
        Element td = domContext.createElement("td");
        rootTR.appendChild(td);

        Element label = domContext.createElement("label");
        td.appendChild(label);

        if (labelClass != null) {
            label.setAttribute("class", labelClass);
        }

        Element inputElement = domContext.createElement("input");
        inputElement
                .setAttribute("name", uiComponent.getClientId(facesContext));
        inputElement.setAttribute("id",
                                  uiComponent.getClientId(facesContext) + ":_" +
                                  counter);
        label.appendChild(inputElement);
        HashSet excludes = new HashSet();
        String accesskey =
                (String) uiComponent.getAttributes().get("accesskey");
        if (accesskey != null) {
            inputElement.setAttribute("accesskey", accesskey);
            excludes.add("accesskey");
        }

        String formattedOptionValue = formatComponentValue(
                facesContext,
                uiComponent,
                selectItem.getValue());
        inputElement.setAttribute("value", formattedOptionValue);
        inputElement.setAttribute("type", "checkbox");

        boolean isSelected;
        Object submittedValues[] =
                getSubmittedSelectedValues(uiComponent);
        if (submittedValues != null) {
            isSelected = isSelected(formattedOptionValue, submittedValues);
        } else {
            Object selectedValues =
                    getCurrentSelectedValues(uiComponent);
            isSelected = isSelected(selectItem.getValue(), selectedValues);
        }

        if (isSelected) {
            inputElement.setAttribute("checked", Boolean.TRUE.toString());
        }
        if (disabled) {
            inputElement.setAttribute("disabled", "disabled");
        }


        String selectItemLabel = selectItem.getLabel();
        if (selectItemLabel != null) {
            Text textNode =
                    domContext.getDocument().createTextNode(selectItemLabel);
            inputElement.appendChild(textNode);
        }
        addJavaScript(facesContext, uiComponent, inputElement, excludes);
        excludes.add("style");
        excludes.add("border");
        excludes.add("readonly");
        Iterator passTrhuAttributes = PassThruAttributeRenderer
                .getpassThruAttributeNames().iterator();
        while (passTrhuAttributes.hasNext()) {
            String passThruAttribute = passTrhuAttributes.next().toString();
            Object attOnComponent =
                    uiComponent.getAttributes().get(passThruAttribute);
            if (attOnComponent != null && !excludes.contains(passThruAttribute))
                inputElement.setAttribute(passThruAttribute,
                                          attOnComponent.toString());
        }
    }

    protected void addJavaScript(FacesContext facesContext,
                                 UIComponent uiComponent, Element root,
                                 Set excludes) {
    }
}