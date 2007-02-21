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

import com.icesoft.faces.component.IcePassThruAttributes;
import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.context.effects.LocalEffectEncoder;
import org.w3c.dom.Element;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class is responsible for the rendering of html pass thru attributes.
 */
public class PassThruAttributeRenderer {
    public static List passThruAttributeNames = new ArrayList();
    public static List booleanPassThruAttributeNames = new ArrayList();

    static {
        passThruAttributeNames.add("accept");
        passThruAttributeNames.add("accesskey");
        passThruAttributeNames.add("alt");
        passThruAttributeNames.add("bgcolor");
        passThruAttributeNames.add("border");
        passThruAttributeNames.add("cellpadding");
        passThruAttributeNames.add("cellspacing");
        passThruAttributeNames.add("charset");
        passThruAttributeNames.add("cols");
        passThruAttributeNames.add("coords");
        passThruAttributeNames.add("dir");
        passThruAttributeNames.add("enctype");
        passThruAttributeNames.add("frame");
        passThruAttributeNames.add("height");
        passThruAttributeNames.add("hreflang");
        passThruAttributeNames.add("lang");
        passThruAttributeNames.add("longdesc");
        passThruAttributeNames.add("maxlength");
        passThruAttributeNames.add("onblur");
        passThruAttributeNames.add("onchange");
        passThruAttributeNames.add("onclick");
        passThruAttributeNames.add("ondblclick");
        passThruAttributeNames.add("onfocus");
        passThruAttributeNames.add("onkeydown");
        passThruAttributeNames.add("onkeypress");
        passThruAttributeNames.add("onkeyup");
        passThruAttributeNames.add("onload");
        passThruAttributeNames.add("onmousedown");
        passThruAttributeNames.add("onmousemove");
        passThruAttributeNames.add("onmouseout");
        passThruAttributeNames.add("onmouseover");
        passThruAttributeNames.add("onmouseup");
        passThruAttributeNames.add("onreset");
        passThruAttributeNames.add("onselect");
        passThruAttributeNames.add("onsubmit");
        passThruAttributeNames.add("onunload");
        passThruAttributeNames.add("rel");
        passThruAttributeNames.add("rev");
        passThruAttributeNames.add("rows");
        passThruAttributeNames.add("rules");
        passThruAttributeNames.add("shape");
        passThruAttributeNames.add("size");
        passThruAttributeNames.add("style");
        passThruAttributeNames.add("summary");
        passThruAttributeNames.add("tabindex");
        passThruAttributeNames.add("target");
        passThruAttributeNames.add("title");
        passThruAttributeNames.add("usemap");
        passThruAttributeNames.add("width");
        passThruAttributeNames.add("width");
        passThruAttributeNames.add("onclickeffect");
        passThruAttributeNames.add("ondblclickeffect");
        passThruAttributeNames.add("onmousedowneffect");
        passThruAttributeNames.add("onmouseupeffect");
        passThruAttributeNames.add("onmousemoveeffect");
        passThruAttributeNames.add("onmouseovereffect");
        passThruAttributeNames.add("onmouseouteffect");
        passThruAttributeNames.add("onchangeeffect");
        passThruAttributeNames.add("onreseteffect");
        passThruAttributeNames.add("onsubmiteffect");
        passThruAttributeNames.add("onkeypresseffect");
        passThruAttributeNames.add("onkeydowneffect");
        passThruAttributeNames.add("onkeyupeffect");
        passThruAttributeNames.add("autocomplete");

        booleanPassThruAttributeNames.add("disabled");
        booleanPassThruAttributeNames.add("readonly");
        booleanPassThruAttributeNames.add("ismap");
    }

    /**
     * Render pass thru attributes to the root element of the DOMContext
     * associated with the UIComponent parameter. The excludedAttributes
     * argument is a String array of the names of attributes to omit. Do not
     * render attributes contained in the excludedAttributes argument.
     *
     * @param facesContext
     * @param uiComponent
     * @param excludedAttributes attributes to exclude
     */
    public static void renderAttributes(FacesContext facesContext,
                                        UIComponent uiComponent,
                                        String[] excludedAttributes) {
        renderNonBooleanAttributes(facesContext, uiComponent,
                                   excludedAttributes);
        renderBooleanAttributes(facesContext, uiComponent, excludedAttributes);
        CurrentStyle.apply(uiComponent, facesContext);
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        Element rootElement = (Element) domContext.getRootNode();
        LocalEffectEncoder
                .encodeLocalEffects(uiComponent, rootElement, facesContext);
        renderOnFocus(uiComponent, rootElement);
        renderOnBlur(rootElement);
    }

    /**
     * Render the icefaces onfocus handler to the root element. This should be
     * restricted to input type elements and commandlinks.
     *
     * @param uiComponent
     * @param root
     */
    public static void renderOnFocus(UIComponent uiComponent, Element root) {
        // check the type of the root node
        String nodeName = root.getNodeName();

        if (nodeName.equalsIgnoreCase(HTML.ANCHOR_ELEM) ||
            nodeName.equalsIgnoreCase(HTML.INPUT_ELEM) ||
            nodeName.equalsIgnoreCase(HTML.SELECT_ELEM)) {
            String original =
                    (String) uiComponent.getAttributes().get("onfocus");
            String onfocus = "setFocus(this.id);";

            if (original == null) {
                original = "";
            }
            root.setAttribute(HTML.ONFOCUS_ATTR, onfocus + original);
        }
    }

    /**
     * Render the icefaces onblur handler to the root element. This should be
     * restricted to input type elements and commandlinks.
     *
     * @param root
     */
    public static void renderOnBlur(Element root) {
        // check the type of the root node
        // onblur will clear focus id
        String nodeName = root.getNodeName();

        if (nodeName.equalsIgnoreCase(HTML.ANCHOR_ELEM) ||
            nodeName.equalsIgnoreCase(HTML.INPUT_ELEM) ||
            nodeName.equalsIgnoreCase(HTML.SELECT_ELEM)) {
            String original = root.getAttribute("onblur");
            String onblur = "setFocus('');";

            if (original == null) {
                original = "";
            }
            root.setAttribute(HTML.ONBLUR_ATTR, onblur + original);
        }
    }

    private static void renderBooleanAttributes(
            FacesContext facesContext, UIComponent uiComponent,
            String[] excludedAttributes) {

        if (facesContext == null) {
            throw new FacesException("Null pointer exception");
        }
        if (uiComponent == null) {
            throw new FacesException("Null pointer exception");
        }

        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        Element rootElement = (Element) domContext.getRootNode();
        if (rootElement == null) {
            throw new FacesException("DOMContext is null");
        }

        List excludedAttributesList = null;
        if (excludedAttributes != null && excludedAttributes.length > 0) {
            excludedAttributesList = Arrays.asList(excludedAttributes);
        }
        
        if (uiComponent instanceof IcePassThruAttributes &&
        		uiComponent.getAttributes().get(IcePassThruAttributes.ICE_ATTRIBUTE_MAP) != null) {
        	Iterator passThruAttOnComponent = ((List)((Map)uiComponent.getAttributes()
        			.get(IcePassThruAttributes.ICE_ATTRIBUTE_MAP))
        			.get(IcePassThruAttributes.PASS_THRU_BOOLEAN_ATT_LIST)).iterator();
        	while (passThruAttOnComponent.hasNext()) {
        		Object attribute = passThruAttOnComponent.next();
        		Object value = uiComponent.getAttributes().get(attribute);
        		if (excludedAttributesList != null && 
        				excludedAttributesList.contains(attribute)) {
        			continue;
        		}
	                renderBooleanAttribute(attribute, 
	                		value, rootElement);
        	}
        	return;
        }
        
        Object nextPassThruAttributeName;
        Object nextPassThruAttributeValue = null;
        Iterator passThruNameIterator =
                booleanPassThruAttributeNames.iterator();

        while (passThruNameIterator.hasNext()) {
            nextPassThruAttributeName = (passThruNameIterator.next());
            if (excludedAttributesList != null) {
                if (excludedAttributesList
                        .contains(nextPassThruAttributeName)) {
                    continue;
                }
            }
            nextPassThruAttributeValue = uiComponent.getAttributes().get(
                    nextPassThruAttributeName);
            renderBooleanAttribute(nextPassThruAttributeName, 
            		nextPassThruAttributeValue, rootElement);
        }
    }

    private static void renderBooleanAttribute(Object attribute, Object value, Element rootElement) {
        boolean primitiveAttributeValue;
        if (value != null) {
            if (value instanceof Boolean) {
                primitiveAttributeValue = ((Boolean)
                        value).booleanValue();
            } else {
                if (!(value instanceof String)) {
                    value =
                            value.toString();
                }
                primitiveAttributeValue = (new Boolean((String)
                        value)).booleanValue();
            }
            if (primitiveAttributeValue) {
                rootElement
                        .setAttribute(attribute.toString(),
                                      attribute.toString());
            } else {
                rootElement.removeAttribute(
                        attribute.toString());
            }

        } else {
            rootElement
                    .removeAttribute(attribute.toString());
        }
    }
    
    private static void renderNonBooleanAttributes(
            FacesContext facesContext, UIComponent uiComponent,
            String[] excludedAttributes) {

        if (uiComponent == null) {
            throw new FacesException("Component instance is null");
        }

        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);

        Element rootElement = (Element) domContext.getRootNode();
        if (rootElement == null) {
            throw new FacesException("DOMContext is not initialized");
        }

        List excludedAttributesList = null;
        if (excludedAttributes != null && excludedAttributes.length > 0) {
            excludedAttributesList = Arrays.asList(excludedAttributes);
        }
        
        if (uiComponent instanceof IcePassThruAttributes && 
        		uiComponent.getAttributes().get(
        				IcePassThruAttributes.ICE_ATTRIBUTE_MAP) != null) {
        	Iterator passThruAttOnComponent = ((List)((Map)	uiComponent.getAttributes().
        			get(IcePassThruAttributes.ICE_ATTRIBUTE_MAP))
        			.get(IcePassThruAttributes.PASS_THRU_NON_BOOLEAN_ATT_LIST)).iterator();
        	
        	while (passThruAttOnComponent.hasNext()) {
        		Object attribute = passThruAttOnComponent.next();
        		Object value = uiComponent.getAttributes().get(attribute);
        		if (excludedAttributesList != null && 
        				excludedAttributesList.contains(attribute)) {
        			continue;
        		}
    	       if (value != null &&
    	                !attributeValueIsSentinel(value)) {
    	                rootElement.setAttribute(
    	                        attribute.toString(),
    	                        value.toString());
    	       } else {
    	                rootElement
    	                        .removeAttribute(attribute.toString());
    	       }
        		
        	}
        	return;
        }
        
        Object nextPassThruAttributeName = null;
        Object nextPassThruAttributeValue = null;
        Iterator passThruNameIterator = passThruAttributeNames.iterator();
        while (passThruNameIterator.hasNext()) {
            nextPassThruAttributeName = (passThruNameIterator.next());
            if (excludedAttributesList != null) {
                if (excludedAttributesList
                        .contains(nextPassThruAttributeName)) {
                    continue;
                }
            }
            nextPassThruAttributeValue =
                    uiComponent.getAttributes().get(nextPassThruAttributeName);
            // Only render non-null attributes.
            // Some components have attribute values
            // set to the Wrapper classes' minimum value - don't render 
            // an attribute with this sentinel value.
            if (nextPassThruAttributeValue != null &&
                !attributeValueIsSentinel(nextPassThruAttributeValue)) {
                rootElement.setAttribute(
                        nextPassThruAttributeName.toString(),
                        nextPassThruAttributeValue.toString());
            } else {
                rootElement
                        .removeAttribute(nextPassThruAttributeName.toString());
            }
        }
    }

    /**
     * Determine whether any of the attributes defined for the UIComponent
     * instance are pass thru attributes.
     *
     * @param uiComponent
     * @return true if the UIComponent parameter has one or more attributes
     *         defined that are pass thru attributes
     */
    public static boolean passThruAttributeExists(UIComponent uiComponent) {
        if (uiComponent == null) {
            return false;
        }
        Map componentAttributes = uiComponent.getAttributes();
        if (componentAttributes.size() <= 0) {
            return false;
        }
        if (componentAttributesIncludePassThruAttribute(componentAttributes,
                                                        passThruAttributeNames)) {
            return true;
        }
        if (componentAttributesIncludePassThruAttribute(componentAttributes,
                                                        booleanPassThruAttributeNames)) {
            return true;
        }
        return false;
    }

    private static boolean attributeValueIsSentinel(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            if (((Boolean) value).booleanValue() == false) {
                return true;
            }
            return false;
        }
        if (value instanceof Number) {
            if (value instanceof Integer) {
                if (((Integer) value).intValue() == Integer.MIN_VALUE) {
                    return true;
                }
                return false;
            }
            if (value instanceof Long) {
                if (((Long) value).longValue() == Long.MIN_VALUE) {
                    return true;
                }
                return false;
            }
            if (value instanceof Short) {
                if (((Short) value).shortValue() == Short.MIN_VALUE) {
                    return true;
                }
                return false;
            }
            if (value instanceof Float) {
                if (((Float) value).floatValue() == Float.MIN_VALUE) {
                    return true;
                }
                return false;
            }
            if (value instanceof Double) {
                if (((Double) value).doubleValue() == Double.MIN_VALUE) {
                    return true;
                }
                return false;
            }
            if (value instanceof Byte) {
                if (((Byte) value).byteValue() == Byte.MIN_VALUE) {
                    return true;
                }
                return false;
            }
        }
        if (value instanceof Character) {
            if (((Character) value).charValue() == Character.MIN_VALUE) {
                return true;
            }
            return false;
        }
        return false;
    }

    private static boolean componentAttributesIncludePassThruAttribute(
            Map componentAttributes, List passThru) {
        Object componentAttributeKey;
        Object componentAttributeValue;
        Iterator attributeKeys = componentAttributes.keySet().iterator();
        while (attributeKeys.hasNext()) {
            componentAttributeKey = attributeKeys.next();
            if (passThru.contains(componentAttributeKey)) {
                componentAttributeValue =
                        componentAttributes.get(componentAttributeKey);
                if ((componentAttributeValue != null) &&
                    (componentAttributeValue != "")) {
                    return true;
                }
            }
        }
        return false;
    }

    static final List getpassThruAttributeNames() {
        return passThruAttributeNames;
    }
}
