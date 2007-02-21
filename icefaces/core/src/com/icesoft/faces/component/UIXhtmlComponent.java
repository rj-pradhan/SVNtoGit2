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

package com.icesoft.faces.component;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UIXhtmlComponent extends UIComponentBase {
    public static final String COMPONENT_FAMILY =
            "com.icesoft.faces.XhtmlComponent";
    public static final String RENDERER_TYPE =
            "com.icesoft.domXhtml";
    
    private static final Attributes EMPTY_ATTRIBUTES = new AttributesImpl();
    private static final Log log = LogFactory.getLog(UIXhtmlComponent.class);
    private static Method getELContextMethod;
    private static Method getValueMethod;

    private String tag;
    private Attributes xmlAttributes = EMPTY_ATTRIBUTES;
    private Map standardAttributes = Collections.EMPTY_MAP;
    private Map elValueExpressions = Collections.EMPTY_MAP;
    private boolean createdByFacelets = false;

    static {
        try {
            Class ELAdaptorClass =
                    Class.forName("com.sun.facelets.el.ELAdaptor");
            getELContextMethod = ELAdaptorClass
                    .getMethod("getELContext", new Class[]{FacesContext.class});
            Class ValueExpressionClass =
                    Class.forName("javax.el.ValueExpression");
            Class ELContextClass = Class.forName("javax.el.ELContext");
            getValueMethod = ValueExpressionClass
                    .getMethod("getValue", new Class[]{ELContextClass});
        }
        catch (Throwable e) {
            //EL libraries not available, which either means that we're
            //not using Facelets, or someone forgot to include a JAR
            if (log.isDebugEnabled()) {
                log.debug(
                        "EL libraries not detected; Facelets are not supported by this configuration: " +
                        e.getMessage());
            }
        }
    }

    public UIXhtmlComponent() {
        setRendererType( RENDERER_TYPE );
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getTag() {
        return tag;
    }

    public Map getTagAttributes() {
        Map allAttributes = new HashMap();
        int length = xmlAttributes.getLength();
        for (int i = 0; i < length; i++) {
            allAttributes
                    .put(xmlAttributes.getQName(i), xmlAttributes.getValue(i));
        }

        // Straight text attributes from Facelets
        Iterator attributeIterator = standardAttributes.entrySet().iterator();
        while (attributeIterator.hasNext()) {
            Map.Entry attribute = (Map.Entry) attributeIterator.next();
            allAttributes.put(attribute.getKey().toString(),
                              attribute.getValue().toString());
        }

        // EL expression attributes from Facelets
        if (getELContextMethod != null && getValueMethod != null) {
            try {
                Object elContext = getELContextMethod.invoke(null,
                                                             new Object[]{
                                                                     FacesContext.getCurrentInstance()});

                if (elContext != null) {
                    Iterator elAttributeIterator =
                            elValueExpressions.entrySet().iterator();
                    while (elAttributeIterator.hasNext()) {
                        Map.Entry attribute =
                                (Map.Entry) elAttributeIterator.next();
                        Object name = attribute.getKey();
                        Object value = attribute.getValue();
                        if (value != null) {
                            Object evaluatedValue = getValueMethod
                                    .invoke(value, new Object[]{elContext});
                            if (evaluatedValue != null) {
                                allAttributes.put(name.toString(),
                                                  evaluatedValue.toString());
                            }
                        }
                    }
                }
            }
            catch (IllegalAccessException iae) {
                // It shouldn't be possible for these reflection exceptions to happen
                throw new RuntimeException(iae);
            }
            catch (InvocationTargetException ite) {
                // It shouldn't be possible for these reflection exceptions to happen
                throw new RuntimeException(ite);
            }
        }

        return allAttributes;
    }

    public boolean isCreatedByFacelets() {
        return createdByFacelets;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setXmlAttributes(Attributes attr) {
        xmlAttributes = attr == null ? xmlAttributes : attr;
    }

    public void addStandardAttribute(String key, Object value) {
        if (standardAttributes == Collections.EMPTY_MAP)
            standardAttributes = new HashMap();
        standardAttributes.put(key, value);
    }

    /**
     * Since we might not always include the EL jars, we can't refer to those
     * classes in our method signatures, so the second param to this method has
     * to be "Object", even though it must specifically take a ValueExpression
     *
     * @param key
     * @param valueExpression Must be a javax.el.ValueExpression
     */
    public void addELValueExpression(String key, Object valueExpression) {
        if (elValueExpressions == Collections.EMPTY_MAP)
            elValueExpressions = new HashMap();
        elValueExpressions.put(key, valueExpression);
    }

    public void setCreatedByFacelets() {
        createdByFacelets = true;
    }

    public String toString() {
        return this.getClass() + "@" + this.hashCode() + ":tag=[" +
               this.getTag() + "]";
    }
}
