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
import com.icesoft.faces.context.effects.JavascriptContext;
import org.w3c.dom.Element;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.IOException;
import java.util.Map;

public class ButtonRenderer extends DomBasicRenderer {

    public void decode(FacesContext facesContext, UIComponent uiComponent) {
        validateParameters(facesContext, uiComponent, UICommand.class);
        if (isStatic(uiComponent)) {
            return;
        }
        boolean thisButtonInvokedSubmit =
                didThisButtonInvokeSubmit(facesContext, uiComponent);
        if (!thisButtonInvokedSubmit) {
            return;
        }
        String type = ((String) uiComponent.getAttributes().get("type"))
                .toLowerCase();
        if ((type != null) && (type.equals("reset"))) {
            return;
        }
        ActionEvent actionEvent = new ActionEvent(uiComponent);
        uiComponent.queueEvent(actionEvent);
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @return boolean
     */
    private boolean didThisButtonInvokeSubmit(
            FacesContext facesContext, UIComponent uiComponent) {
        boolean thisButtonInvokedSubmit = false;
        //find if the form submitted by a textField, workaround to deal with the default behaviour of the browser
        //(e.g.) if a form has a button on it, and enter key pressed on a text field, form submitted by the first intance of button
        if (!isTextFieldInvokedSubmit(facesContext)) {
            Map requestParameterMap =
                    facesContext.getExternalContext().getRequestParameterMap();
            String componentClientId = uiComponent.getClientId(facesContext);
            String clientIdInRequestMap =
                    (String) requestParameterMap.get(componentClientId);
            thisButtonInvokedSubmit = clientIdInRequestMap != null;
            if (!thisButtonInvokedSubmit) {
                // detect whether this button is an image button
                // that invoked the submit
                String clientIdXCoordinateInRequestMap =
                        componentClientId + ".x";
                String clientIdYCoordinateInRequestMap =
                        componentClientId + ".y";
                if (requestParameterMap.get(componentClientId + ".x") != null
                    || requestParameterMap.get(componentClientId + ".y") !=
                       null) {
                    thisButtonInvokedSubmit = true;
                }
            }
        }
        return thisButtonInvokedSubmit;
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, UICommand.class);

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);
        String clientId = uiComponent.getClientId(facesContext);

        if (!domContext.isInitialized()) {
            Element root = domContext.createElement("input");
            domContext.setRootNode(root);
            root.setAttribute("name", clientId);
        }
        Element root = (Element) domContext.getRootNode();

        setRootElementId(facesContext, root, uiComponent);

        // according to the Sun documentation for ButtonRenderer,
        // type: valid values are "submit" and "reset"; default to "submit".
        // But the docs for the image attribute declare that 'image' is also
        // a valid value for the type attribute. 
        // The logic here holds true when there is no image attribute
        // But suns JSF implementation allows for type button, we allow it as well.
        String typeAttribute =
                ((String) uiComponent.getAttributes().get("type"))
                        .toLowerCase();
        if (typeAttribute == null || (!typeAttribute.equals("reset") &&
                                      (!typeAttribute.equals("button")))) {
            typeAttribute = "submit";
        }
        uiComponent.getAttributes().put("type", typeAttribute);

        // If image attribute is specified, the type is "image"
        // otherwise take the type from the type attribute and set a label
        // with value specified by the value attribute
        String imageAttribute =
                (String) uiComponent.getAttributes().get("image");
        if (imageAttribute != null) {
            typeAttribute = "image";
            root.setAttribute("type", typeAttribute);
            root.setAttribute("src", imageAttribute);
            root.removeAttribute("value");
        } else {
            root.setAttribute("type", typeAttribute);
            String label = "";
            Object componentValue = ((UICommand) uiComponent).getValue();
            if (componentValue != null) {
                label = componentValue.toString();
            } else {
                label = "";
            }
            root.setAttribute("value", label);
            root.removeAttribute("src");
        }

        String styleClass =
                (String) uiComponent.getAttributes().get("styleClass");
        if (styleClass != null) {
            root.setAttribute("class", styleClass);
        }

        JavascriptContext.fireEffect(uiComponent, facesContext);

        PassThruAttributeRenderer
                .renderAttributes(facesContext, uiComponent, null);
        //add iceSubmit for image and submit button only
        if (typeAttribute.equals("submit") || typeAttribute.equals("image")) {
            renderOnClick(uiComponent, root);
        }

        domContext.stepOver();
        domContext.streamWrite(facesContext, uiComponent);
    }

    /**
     * @param uiComponent
     * @param root
     */
    protected void renderOnClick(UIComponent uiComponent, Element root) {
        String onclick = (String) uiComponent.getAttributes().get("onclick");
        String submitCode = this.ICESUBMIT + "return false;";
        if (onclick == null) {
            onclick = submitCode;
        } else {
            onclick += submitCode;
        }
        root.setAttribute("onclick", onclick);
    }

    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, UICommand.class);
    }

    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, UICommand.class);
    }

    private boolean isTextFieldInvokedSubmit(FacesContext facesContext) {
        Object textFieldfocus =
                facesContext.getExternalContext().getRequestParameterMap()
                        .get(FormRenderer.FOCUS_HIDDEN_FIELD);
        if (textFieldfocus == null) {
            return false;
        }

        if (textFieldfocus.toString().length() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
