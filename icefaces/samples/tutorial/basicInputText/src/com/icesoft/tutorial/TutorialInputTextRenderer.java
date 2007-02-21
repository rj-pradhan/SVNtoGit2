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

package com.icesoft.tutorial;

//w3c DOM

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicInputRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;
import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.Map;


public class TutorialInputTextRenderer extends
                                       DomBasicInputRenderer {

    public TutorialInputTextRenderer() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#decode(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void decode(FacesContext facesContext,
                       UIComponent uiComponent) {
        validateParameters(facesContext, uiComponent, null);
        // only need to decode input components
        if (!(uiComponent instanceof UIInput)) {
            return;
        }
        // only need to decode enabled, writable components
        if (isStatic(uiComponent)) {
            return;
        }
        // extract component value from the request map
        String clientId = uiComponent.getClientId(facesContext);
        if (clientId == null) {
            System.out.println("Client id is not defined for decoding");
        }
        Map requestMap =
                facesContext.getExternalContext().getRequestParameterMap();
        if (requestMap.containsKey(clientId)) {
            String decodedValue = (String) requestMap.get(clientId);
            // setSubmittedValue is a method in the superclass DomBasicInputRenderer
            setSubmittedValue(uiComponent, decodedValue);
        }
    }

    /* (non-Javadoc)
     * @see javax.faces.render.Renderer#encodeEnd(javax.faces.context.FacesContext, javax.faces.component.UIComponent)
     */
    public void encodeEnd(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        if (!domContext.isInitialized()) {
            Element root = domContext.createRootElement("input");
            setRootElementId(facesContext, root, uiComponent);
            root.setAttribute("type", "text");
            root.setAttribute("name", uiComponent.getClientId(facesContext));
        }

        Element root = (Element) domContext.getRootNode();

        root.setAttribute("onkeydown", this.ICESUBMIT);

        // create a new String array to hold attributes we will exclude for the PassThruAttributeRenderer
        String[] excludesArray = new String[1];
        excludesArray[0] = "onkeydown";

        // the renderAttributes method will not overwrite any attributes contained in the excludesArray
        PassThruAttributeRenderer
                .renderAttributes(facesContext, uiComponent, excludesArray);


    }

}