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

import com.icesoft.faces.component.UIXhtmlComponent;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.util.DOMUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;


public class TextRenderer extends DomBasicInputRenderer {

    public void encodeChildren(FacesContext facesContext,
                               UIComponent uiComponent) {
        validateParameters(facesContext, uiComponent, null);
    }

    protected void renderEnd(FacesContext facesContext, UIComponent uiComponent,
                             String currentValue)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
        if (uiComponent instanceof UIInput) {
            renderUIInput(facesContext, uiComponent, currentValue);
        } else if (uiComponent instanceof UIOutput) {
            renderUIOutput(facesContext, uiComponent, currentValue);
        }
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @param currentValue
     * @throws IOException
     */
    private void renderUIOutput(FacesContext facesContext,
                                UIComponent uiComponent,
                                String currentValue)
            throws IOException {

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        boolean requiresContainingSpan = requiresContainingSpan(uiComponent);

        if (requiresContainingSpan) {
            renderContainingSpan(facesContext, uiComponent, domContext);
        } else {
            renderTextNode(domContext);
        }

        if (currentValue == null) {
            // We need to set the data in the text node to the empty string.
            // The text node can be either the root node or it can be
            // the first child of the root node in the case where we
            // rendered a containing span
            Text textNode = null;
            if (requiresContainingSpan) {
                textNode = (Text) domContext.getRootNode()
                        .getFirstChild();
            } else {
                textNode = ((Text) domContext.getRootNode());
            }
            if (textNode != null) {
                textNode.setData("");
            }
        } else {
            renderCurrentValue(uiComponent, currentValue, domContext,
                               requiresContainingSpan);
        }
        domContext.streamWrite(facesContext, uiComponent);
    }

    /**
     * @param domContext
     */
    private void renderTextNode(DOMContext domContext) {
        if (!domContext.isInitialized()) {
            Node root = domContext.getDocument().createTextNode("");
            domContext.setRootNode(root);
        } else if (!(domContext.getRootNode() instanceof Text)) {
            // Need to switch from a root span to a root text node.
            // This type of change can occur when the binding attribute is defined
            // and the model has altered one of the attributes requiring a span 
            // from non-null to null.
            domContext.getRootNode().getParentNode()
                    .removeChild(domContext.getRootNode());
            domContext.setRootNode(domContext.getDocument().createTextNode(""));
        }
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @param domContext
     */
    private void renderContainingSpan(FacesContext facesContext,
                                      UIComponent uiComponent,
                                      DOMContext domContext) {
        if (!domContext.isInitialized()) {
            Node root = domContext.createElement(HTML.SPAN_ELEM);
            domContext.setRootNode(root);
        } else if (!(domContext.getRootNode() instanceof Element)) {
            // if we require a span but the existing root node is not a span then we 
            // need to switch from a text node at the root to a span at the root
            // This type of change can occur when the binding attribute is defined
            // and the model has altered one of the attributes requiring a span 
            // from null to some non-null value
            domContext.getRootNode().getParentNode()
                    .removeChild(domContext.getRootNode());
            domContext.setRootNode(domContext.createElement(HTML.SPAN_ELEM));
        }
        Element rootSpan = (Element) domContext.getRootNode();
        setRootElementId(facesContext, rootSpan, uiComponent);
        // render styleClass as the value of the class attribute;
        // leave the style to be passed through
        String styleClass =
                (String) uiComponent.getAttributes().get("styleClass");
        if (styleClass != null) {
            ((Element) rootSpan).setAttribute("class", styleClass);
        }
        PassThruAttributeRenderer
                .renderAttributes(facesContext, uiComponent, null);
    }

    /**
     * @param uiComponent
     * @param currentValue
     * @param domContext
     * @param requiresContainingSpan
     */
    private void renderCurrentValue(UIComponent uiComponent,
                                    String currentValue,
                                    DOMContext domContext,
                                    boolean requiresContainingSpan) {

        // escape 
        boolean valueTextRequiresEscape =
                DOMUtils.escapeIsRequired(uiComponent);
        if (valueTextRequiresEscape) {
            currentValue = DOMUtils.escapeAnsi(currentValue);
        }

        // Avoid severing and recreating the node
        Node rootNode = domContext.getRootNode();
        if (requiresContainingSpan) {
            domContext.setCursorParent(rootNode);
            if (rootNode.getFirstChild() != null
                && rootNode.getFirstChild() instanceof Text) {
                ((Text) rootNode.getFirstChild()).setData(currentValue);
            } else {
                Text text = domContext.getDocument()
                        .createTextNode(currentValue);
                rootNode.appendChild(text);
            }
        } else {
            ((Text) rootNode).setData(currentValue);
        }
    }

    /**
     * @param facesContext
     * @param uiComponent
     * @param currentValue
     * @throws IOException
     */
    private void renderUIInput(FacesContext facesContext,
                               UIComponent uiComponent,
                               String currentValue)
            throws IOException {

        DOMContext domContext =
                DOMContext.attachDOMContext(facesContext, uiComponent);

        if (!domContext.isInitialized()) {
            Element root = domContext.createElement("input");
            domContext.setRootNode(root);
            setRootElementId(facesContext, root, uiComponent);
            root.setAttribute("type", "text");
            root.setAttribute("name", uiComponent.getClientId(facesContext));
        }
        Element root = (Element) domContext.getRootNode();

        String bidi = (String) uiComponent.getAttributes().get("dir");
        if (bidi != null) {
            root.setAttribute("dir", bidi);
        }

        if (currentValue != null) {
            root.setAttribute("value", currentValue);
        } else {
            root.removeAttribute("value");
        }

        String styleClass =
                (String) uiComponent.getAttributes().get("styleClass");
        if (styleClass != null) {
            root.setAttribute("class", styleClass);
        }
        
        HashSet excludes = new HashSet();
        addJavaScript(facesContext, uiComponent, root, currentValue, excludes);
        PassThruAttributeRenderer
                .renderAttributes(facesContext, uiComponent, getExcludesArray(excludes));
        domContext.streamWrite(facesContext, uiComponent);

    }

    /**
     * We require a containing span if either styleClass or style attributes are
     * present, or there is an id to accommodate.
     *
     * @param uiComponent
     * @return boolean
     */
    private boolean requiresContainingSpan(UIComponent uiComponent) {

        // special case for title element
        UIComponent parent = uiComponent.getParent();
        if (parent != null && parent instanceof UIXhtmlComponent) {
            String tag = ((UIXhtmlComponent) parent).getTag();
            if (tag != null && tag.equalsIgnoreCase("title")) {
                return false;
            }
        }

        String style = (String) uiComponent.getAttributes().get("style");
        String styleClass =
                (String) uiComponent.getAttributes().get("styleClass");
        String title = (String) uiComponent.getAttributes().get("title");
        if (styleClass != null
            || style != null
            || title != null) {
            return true;
        }
        Map attributes = uiComponent.getAttributes();
        if (attributes.size() != 0) {
            if (PassThruAttributeRenderer
                    .passThruAttributeExists(uiComponent)) {
                return true;
            }
        }
        if (idNotNull(uiComponent) && !uiComponent.getId().startsWith("_")) {
            return true;
        }
        return false;
    }

    protected void addJavaScript(FacesContext facesContext,
                                 UIComponent uiComponent, Element root,
                                 String currentValue,
                                 HashSet excludes) {


    }
}

