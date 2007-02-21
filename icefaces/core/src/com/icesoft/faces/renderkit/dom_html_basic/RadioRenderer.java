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
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class RadioRenderer extends SelectManyCheckboxListRenderer {

    protected void renderOption(FacesContext facesContext,
                                UIComponent uiComponent,
                                SelectItem selectItem, boolean renderVertically,
                                Element rootTR, int counter,
                                Object componentValue)
            throws IOException {
        UISelectOne uiSelectOne = (UISelectOne) uiComponent;
        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiSelectOne);
        Element rootTable = (Element) domContext.getRootNode();

        Object submittedValue = uiSelectOne.getSubmittedValue();
        if (submittedValue == null) {
            submittedValue = componentValue;
        }
        if (renderVertically) {
            rootTR = domContext.createElement("tr");
            rootTable.appendChild(rootTR);
        }
        String labelClass = null;
        boolean disabled = false;
        if (uiSelectOne.getAttributes().get("disabled") != null &&
            uiSelectOne.getAttributes().get("disabled")
                    .equals(Boolean.TRUE)) {
            disabled = true;

        }
        if (selectItem.isDisabled()) {
            disabled = true;
        }
        labelClass = (String) uiSelectOne.getAttributes().get("styleClass");
        if (labelClass != null && disabled) {
            labelClass += "-dis";
        }


        Element td = domContext.createElement("td");
        rootTR.appendChild(td);

        Element input = domContext.createElement("input");
        td.appendChild(input);
        input.setAttribute("type", "radio");

        if (disabled) {
            input.setAttribute("disabled", "disabled");
        }

        HashSet excludes = new HashSet();
        String accesskey =
                (String) uiComponent.getAttributes().get("accesskey");
        if (accesskey != null) {
            input.setAttribute("accesskey", accesskey);
            excludes.add("accesskey");
        }

        Object selectItemValue = selectItem.getValue();  
        
        if (selectItemValue != null && String.valueOf(selectItemValue).equals(String.valueOf(componentValue))) {
            input.setAttribute("checked", "checked");
        } else {
            input.removeAttribute("checked");
        }

        input.setAttribute("name", uiSelectOne.getClientId(facesContext));
        input.setAttribute("id",
                           uiComponent.getClientId(facesContext) + ":_" +
                           counter);
        input.setAttribute("value", (formatComponentValue(facesContext,
                                                          uiSelectOne,
                                                          selectItem.getValue())));

        addJavaScript(facesContext, uiSelectOne, input, excludes);
        // style is rendered on containing table
        excludes.add("style");
        excludes.add("readonly");
        excludes.add("disabled");

        PassThruAttributeRenderer.
                renderAttributes(
                        facesContext, uiSelectOne, getExcludesArray(excludes));

        Element label = domContext.createElement("label");
        td.appendChild(label);

        if (labelClass != null) {
            label.setAttribute("class", labelClass);
        }
        String itemLabel = selectItem.getLabel();
        if (itemLabel != null) {
            Text labelText = domContext.getDocument().createTextNode(itemLabel);
            label.appendChild(labelText);
        }
    }

    protected void addJavaScript(FacesContext facesContext,
                                 UIComponent uiSelectOne, Element input,
                                 Set excludes) {
    }
}