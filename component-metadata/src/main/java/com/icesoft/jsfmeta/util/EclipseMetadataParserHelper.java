/*
 *  Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 *  "The contents of this file are subject to the Mozilla Public License
 *  Version 1.1 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *  http://www.mozilla.org/MPL/
 *
 *  Software distributed under the License is distributed on an "AS IS"
 *  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 *  License for the specific language governing rights and limitations under
 *  the License.
 *
 *  The Original Code is ICEfaces 1.5 open source software code, released
 *  November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 *  Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 *  2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 *  Contributor(s): _____________________.
 *
 *  Alternatively, the contents of this file may be used under the terms of
 *  the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 *  License), in which case the provisions of the LGPL License are
 *  applicable instead of those above. If you wish to allow use of your
 *  version of this file only under the terms of the LGPL License and not to
 *  allow others to use your version of this file under the MPL, indicate
 *  your decision by deleting the provisions above and replace them with
 *  the notice and other provisions required by the LGPL License. If you do
 *  not delete the provisions above, a recipient may use your version of
 *  this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.jsfmeta.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.rave.jsfmeta.beans.AttributeBean;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.NamedValueBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;
import com.sun.rave.jsfmeta.beans.RendererBean;

/*
 *
 * TODO:
 */


public class EclipseMetadataParserHelper {
    
    
    static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String GRAMMAR_ANNOTATIONSCHEMA = "http://org.eclipse.jst.jsf.contentmodel.annotations/grammarAnnotationSchema";
    static final String GRAMMAR_ANNOTATIONSCHEMA_LOCATION = "http://org.eclipse.jst.jsf.contentmodel.annotations/grammarAnnotationSchema ../../org.eclipse.jst.jsf.contentmodel.annotations/schema/grammar-annotations.xsd";
    static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    
    private ArrayList componentBeansArrayList = new ArrayList();
    
    public static void main(String[] args) {
        
        EclipseMetadataParserHelper metadataHelper = new EclipseMetadataParserHelper();
        metadataHelper.moreInfo();
    }
    
    public EclipseMetadataParserHelper() {
        
        extract();
    }
    
    public ArrayList getComponentBeans() {
        return componentBeansArrayList;
    }
    
    public ComponentBean getComponentBean(String name) {
        
        for (int i = 0; i < componentBeansArrayList.size(); i++) {
            
            ComponentBean componentBean = (ComponentBean) componentBeansArrayList
                    .get(i);
            if (componentBean.getAttributes()[0].getAttributeName()
            .equals(name)) {
                return componentBean;
            }
        }
        return null;
    }
    
    // TODO:
    private void extract() {
        
        ClassLoader classLoader = Thread.currentThread()
        .getContextClassLoader();
        URL localUrl = classLoader.getResource(".");
        String xmlFilePath = localUrl.getPath()
        + "./../conf/eclipse.schema/jsf_html.xml";
        String schemaSource = localUrl.getPath()
        + "./../conf/eclipse.schema/grammar-annotations.xsd";
        parse(xmlFilePath, schemaSource);
        
    }
    
    public void parse(String filePath, String schemaSource) {
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        documentBuilderFactory.setValidating(true);
        documentBuilderFactory.setNamespaceAware(true);
        try {
            documentBuilderFactory.setAttribute(JAXP_SCHEMA_LANGUAGE,
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
            documentBuilderFactory.setAttribute(JAXP_SCHEMA_SOURCE, new File(
                    schemaSource));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        
        documentBuilder.setErrorHandler(new ErrorHandler() {
            public void error(SAXParseException e) {
                e.printStackTrace();
            }
            
            public void fatalError(SAXParseException e) throws SAXException {
                e.printStackTrace();
            }
            
            public void warning(SAXParseException e) {
                e.printStackTrace();
            }
        });
        
        try {
            Document document = documentBuilder.parse(new File(filePath));
            
            traverse("\t", document.getDocumentElement());
        } catch (IOException e) {
            
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
            
        }
    }
    
    public void moreInfo() {
        
        for (int i = 0; i < componentBeansArrayList.size(); i++) {
            
            ComponentBean componentBean = (ComponentBean) componentBeansArrayList
                    .get(i);
            PropertyBean[] propertyBeans = componentBean.getProperties();
            
            System.out.println("component bean="
                    + componentBean.getAttributes()[0].getAttributeName());
            for (int j = 0; j < propertyBeans.length; j++) {
                Map namedValues = propertyBeans[j].getNamedValues();
                String key;
                String expression;
                String value;
                for (Iterator keys = namedValues.keySet().iterator(); keys
                        .hasNext();) {
                    
                    key = (String) keys.next();
                    NamedValueBean nvb = (NamedValueBean) namedValues.get(key);
                    expression = nvb.getExpression();
                    value = nvb.getValue();
                    System.out.println(" propert name="
                            + propertyBeans[j].getPropertyName() + " name="
                            + nvb.getName() + " expression=" + expression
                            + " value=" + value);
                }
            }
        }
    }
    
    // TODO:
    public void traverse(String indent, Element element) {
        
        String nameValue = element.getAttribute("name");
        if (element.getLocalName().equals("cm-element")) {
            
            RendererBean renderBean = new RendererBean();
            renderBean.setTagName(nameValue);
            
            ComponentBean componentBean = new ComponentBean();
            AttributeBean attributeBean = new AttributeBean();
            attributeBean.setAttributeName(nameValue);
            componentBean.addAttribute(attributeBean);
            
            NodeList attributeList = element.getChildNodes();
            for (int i = 0; i < attributeList.getLength(); i++) {
                
                Node attributeNode = attributeList.item(i);
                if (attributeNode instanceof Element) {
                    
                    Element attributeElement = (Element) attributeNode;
                    String propertyName = attributeElement.getAttribute("name");
                    
                    PropertyBean propertyBean = new PropertyBean();
                    propertyBean.setPropertyName(propertyName);
                    componentBean.addProperty(propertyBean);
                    
                    NodeList propertyList = attributeElement.getChildNodes();
                    for (int j = 0; j < propertyList.getLength(); j++) {
                        
                        Node propertyNode = propertyList.item(j);
                        
                        if (propertyNode instanceof Element) {
                            Element propertyElement = (Element) propertyNode;
                            NamedValueBean namedValueBean = new NamedValueBean();
                            
                            namedValueBean.setName(propertyElement
                                    .getAttribute("name"));
                            NodeList valueList = propertyElement
                                    .getChildNodes();
                            
                            for (int k = 0; k < valueList.getLength(); k++) {
                                
                                Node valueNode = valueList.item(k);
                                if (valueNode.getFirstChild() != null) {
                                    namedValueBean.setExpression(valueNode
                                            .getFirstChild().getNodeValue());
                                    namedValueBean.setValue(valueNode
                                            .getFirstChild().getNodeValue());
                                }
                            }
                            propertyBean.addNamedValue(namedValueBean);
                        }
                    }
                }
            }
            componentBeansArrayList.add(componentBean);
        }
        
        for (Node child = element.getFirstChild(); child != null; child = child
                .getNextSibling()) {
            
            switch (child.getNodeType()) {
                case Node.TEXT_NODE: {
                    break;
                }
                case Node.COMMENT_NODE: {
                    break;
                }
                case Node.CDATA_SECTION_NODE: {
                    break;
                }
                case Node.ELEMENT_NODE: {
                    traverse(indent + "  ", (Element) child);
                    break;
                }
            }
        }
    }
    
}
