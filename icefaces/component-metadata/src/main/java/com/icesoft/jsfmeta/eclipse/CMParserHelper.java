/*
 * CM Parser Helper
 */

package com.icesoft.jsfmeta.eclipse;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class CMParserHelper {
    
    private TreeMap propertyTypeTreeMap = new TreeMap();
    
    public CMParserHelper() {
    }
    
    
    public void getInfo(){
        try {
            
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            
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
            
            Document document = documentBuilder.parse(new File("./src/main/resources/conf/eclipse.schema/jsfhtml.xml"));
            
            Element element = document.getDocumentElement();
            traversal(element, "\t");
            
            for(Iterator iterator = propertyTypeTreeMap.entrySet().iterator(); iterator.hasNext();){
                Map.Entry entry = (Map.Entry)iterator.next();
                System.out.println("key="+entry.getKey()+" value="+ entry.getValue());
            }
        } catch (SAXException ex) {
            ex.printStackTrace();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void traversal(Element element, String indentString){
        
        for(Node firstChild = element.getFirstChild();firstChild != null;
        firstChild = firstChild.getNextSibling()){
            
            int nodeType = firstChild.getNodeType();            
            switch(nodeType){
                case Node.ELEMENT_NODE:{
                    NamedNodeMap map = firstChild.getAttributes();
                    Object objectValue = map.getNamedItem("type");
                    if(objectValue != null && objectValue instanceof Node){
                        Node attrImpl = (Node)objectValue;
                        propertyTypeTreeMap.put(attrImpl.getNodeValue(), attrImpl.getNodeValue()+"_type");
                    }
                    traversal((Element)firstChild, indentString);
                }
                case Node.TEXT_NODE:{
                    
                }
            }
        }
    }
    
}
