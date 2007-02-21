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
import java.io.IOException;
import java.util.HashSet;

public class TextareaRenderer extends DomBasicInputRenderer {

    public void encodeBegin(FacesContext context, UIComponent component)
            throws IOException {
        validateParameters(context, component, UIInput.class);
    }

    public void encodeChildren(FacesContext context, UIComponent component) {
        validateParameters(context, component, UIInput.class);
    }

    protected void renderEnd(FacesContext facesContext, UIComponent component,
                             String currentValue)
            throws IOException {

        validateParameters(facesContext, component, UIInput.class);

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, component);

        if (!domContext.isInitialized()) {
            Element root = domContext.createElement("textarea");
            domContext.setRootNode(root);
            setRootElementId(facesContext, root, component);
            root.setAttribute("name", component.getClientId(facesContext));
            Text valueNode = domContext.getDocument().createTextNode("");
            root.appendChild(valueNode);
        }
        Element root = (Element) domContext.getRootNode();

        String styleClass =
                (String) component.getAttributes().get("styleClass");
        if (styleClass != null) {
            root.setAttribute("class", styleClass);
        }
        PassThruAttributeRenderer
                .renderAttributes(facesContext, component, null);

        String dir = (String) component.getAttributes().get("dir");
        if (dir != null) {
            root.setAttribute("dir", dir);
        }

        Text valueNode = (Text) root.getFirstChild();
        if (currentValue != null) {
            valueNode.setData(currentValue);
        } else {
            // this is necessary due to a restriction on the 
            // structure of the textarea element in the DOM
            valueNode.setData("");
        }
        HashSet excludes = new HashSet();
        addJavaScript(facesContext, component, root, currentValue, excludes);
        domContext.stepOver();
        domContext.streamWrite(facesContext, component);
    }

    protected void addJavaScript(FacesContext facesContext,
                                 UIComponent uiComponent, Element root,
                                 String currentValue,
                                 HashSet excludes) {
    }

}
