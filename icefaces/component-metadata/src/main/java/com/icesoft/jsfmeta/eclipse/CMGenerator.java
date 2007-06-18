/*
 * CM Model Generator
 */

package com.icesoft.jsfmeta.eclipse;

import com.icesoft.jsfmeta.MetadataXmlParser;
import com.icesoft.jsfmeta.util.GeneratorUtil;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;
import com.sun.rave.jsfmeta.beans.RenderKitBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.render.RenderKitFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class CMGenerator {
    
    private static Logger log = Logger.getLogger(CMGenerator.class .getName());
    private Document cmDoc;
    private Map propertyDisplayLabelMap = new HashMap();
    private String cmOutputFolder;
    
    public CMGenerator() {
    }
    
    public void traversal(RenderKitBean renderKitBean, FacesConfigBean facesConfigBean){
        
        ComponentBean[] componentBeans = facesConfigBean.getComponents();
        try {
            Element rootElement = cmDoc.createElement("taglib");
            rootElement.setAttribute("uri", "http://www.icesoft.com/icefaces/component");
            cmDoc.appendChild(rootElement);
            rootElement.appendChild(cmDoc.createTextNode("\n"));
            
            for(int i = 0; i< componentBeans.length; i++){
                
                RendererBean rendererBean = renderKitBean.getRenderer(componentBeans[i].getComponentFamily(), componentBeans[i].getRendererType());
                if(rendererBean == null){
                    continue;
                }
                
                String tagName = rendererBean.getTagName();
                if(tagName == null || tagName.length() < 1){
                    continue;
                }
                
                Element tagElement = cmDoc.createElement("tag");
                tagElement.appendChild(cmDoc.createTextNode("\n"));
                tagElement.setAttribute("name", tagName);
                rootElement.appendChild(tagElement);
                rootElement.appendChild(cmDoc.createTextNode("\n"));
                
                baselineAttribute(componentBeans[i], tagElement, facesConfigBean);
            }
        } catch (DOMException ex) {
            ex.printStackTrace();
        }
    }
    
    private void baselineAttribute(ComponentBean componentBean, Element tagElement, FacesConfigBean facesConfigBean){
        
        PropertyBean[] propertyBeans =  componentBean.getProperties();
        for(int j=0; j< propertyBeans.length; j++){
            
            if(propertyBeans[j].isSuppressed() || !propertyBeans[j].isTagAttribute()
            || propertyBeans[j].getPropertyName().length() < 1
                    ||  propertyBeans[j].getCategory() == null
                    || propertyBeans[j].getCategory().length() < 1){
                continue;
            }
            
            if(getElementByAttributeName(tagElement, "attribute",propertyBeans[j].getPropertyName()) !=  null){
                continue;
            }
            
            addCategory(tagElement, propertyBeans[j], createAttributeElement(propertyBeans[j]));
        }
        
        if(componentBean.getBaseComponentType() != null
                && facesConfigBean.getComponent(componentBean.getBaseComponentType()) != null){
            baselineAttribute(facesConfigBean.getComponent(componentBean.getBaseComponentType()), tagElement, facesConfigBean);
        }
    }
    
    
    private Element createAttributeElement(PropertyBean propertyBean){
        
        String suggestedValue = propertyBean.getSuggestedValue();
        String defaultValue = propertyBean.getDefaultValue();
        String propertyName = propertyBean.getPropertyName();
        String propertyClass = propertyBean.getPropertyClass();
        String category = propertyBean.getCategory();
        String propertyEditor = propertyBean.getEditorClass();
        
        if(log.isLoggable(Level.FINEST)){
            log.log(Level.FINEST, " suggestedValue="+suggestedValue+" defaultValue="+defaultValue+
                    " propertyName="+propertyName+" propertyClass=" +propertyClass+" category="+category+" editor="+propertyEditor);
        }
        
        Element attributeElement = cmDoc.createElement("attribute");
        attributeElement.setAttribute("name", propertyName);
        attributeElement.setAttribute("displaylabel", "%Attribute.Label."+ propertyName);
        
        propertyDisplayLabelMap.put("Attribute.Label."+propertyName, propertyBean.getPropertyName());
        PropertyClassNameUtil util = new PropertyClassNameUtil();
        String propertyTypeName = util.getMatchedName(propertyClass, category, propertyName, propertyEditor);
        
        if(propertyTypeName != null && propertyTypeName.length() > 0){
            attributeElement.setAttribute("type", propertyTypeName);
        }
        
        return attributeElement;
    }
    
    private void addCategory(Element tagElement, PropertyBean propertyBean, Element attributeElement){
        
        Element categoryElement = null;
        Node testElement = getElementByCategoryName(tagElement, "category",propertyBean.getCategory());
        if(testElement != null){
            return;
        }else{
            categoryElement = cmDoc.createElement("category");
            categoryElement.setAttribute("displaylabel", "%Category.Label."+propertyBean.getCategory());
            categoryElement.setAttribute("name",propertyBean.getCategory());
            tagElement.appendChild(categoryElement);
            tagElement.appendChild(cmDoc.createTextNode("\n"));
            
            //TODO: fix metadata related to category.
            if(propertyBean != null && propertyBean.getCategory() != null){
                propertyDisplayLabelMap.put("Category.Label."+propertyBean.getCategory(), propertyBean.getCategory());
            }
        }
        
        if(categoryElement != null){
            categoryElement.appendChild(attributeElement);
            categoryElement.appendChild(cmDoc.createTextNode("\n"));
        }
    }
    
    
    public static void main(String[] args){
        
        CMGenerator generator = new CMGenerator();
        generator.generate(args);
    }
    
    private void generate(String[] args){
        
        //TODO:
        //for(int i=0; i<args.length; i++){        
        
        try {
            
            CMGenerator generator = new CMGenerator();
            Document document = generator.initDoc();
            generator.init();
            generator.serialize(document);
            
            //TODO:
//            CMParserHelper cmParser = new CMParserHelper();
//            cmParser.getInfo();
            
            CMProperties printProperties = new CMProperties();
            printProperties.putAll(generator.propertyDisplayLabelMap);
            
            File propFile = new File(generator.cmOutputFolder, "ICEfaces_component_cm.properties");
            generator.saveProperties(propFile.getPath(), printProperties);
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        } catch(TransformerConfigurationException ex){
            ex.printStackTrace();
        } catch(TransformerException ex){
            ex.printStackTrace();
        }
    }
    
    private void saveProperties(String filePath, Properties properties){
        
        File outputFile = new File(filePath);
        try {
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            properties.store(outputStream, null);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void setOutputFolder(){
        
        try {
            //File folder = GeneratorUtil.getDestFolder("/home/frank/eclipse_ide/com.icesoft.ide.eclipse.designtime/metadata");
            File folder = GeneratorUtil.getDestFolder(GeneratorUtil.getWorkingFolder()+"../generated-sources/eclipse/metadata");
            cmOutputFolder = folder.getPath();
            File outputFile = new File(folder, "ICEfaces_component_cm.xml");
            outputFile.mkdirs();
            outputFile.delete();
            
            File propFile = new File(folder, "ICEfaces_component_cm.properties");
            propFile.delete();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    public void init() {
        
        setOutputFolder();
        String fileName = "./src/main/resources/conf/extended-faces-config.xml";
        String standard_html_renderkit = "jar:file:"+GeneratorUtil.getWorkingFolder()+"jsfmeta-resources.jar!/com/sun/faces/standard-html-renderkit.xml";
        String standard_html_renderkit_overlay = "jar:file:"+GeneratorUtil.getWorkingFolder()+"jsfmeta-resources.jar!/com/sun/rave/jsfmeta/standard-html-renderkit-overlay.xml";
        String standard_html_renderkit_fixup = "jar:file:"+GeneratorUtil.getWorkingFolder()+"jsfmeta-resources.jar!/META-INF/standard-html-renderkit-fixups.xml";
        
        String[] baseUrlList = new String[]{standard_html_renderkit, standard_html_renderkit_overlay, standard_html_renderkit_fixup};
        
        MetadataXmlParser metadataParser = new MetadataXmlParser();
        metadataParser.setDesign(false);
        
        try {
            
            File file = new File(fileName);
            FacesConfigBean facesConfigBean = metadataParser.parse(file);       
            for(int i=0; i< baseUrlList.length; i++){
                metadataParser.parse(new URL(baseUrlList[i]), facesConfigBean);
            }                        
            RenderKitBean renderKitBean = facesConfigBean.getRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT);
            traversal(renderKitBean, facesConfigBean);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
    
    /*
     * @return category node by given child tag name
     */
    private Node getElementByCategoryName(Element tagElement, String childTagName, String categoryName){
        
        NodeList nodeList = tagElement.getElementsByTagName(childTagName);
        for(int i=0; i < nodeList.getLength(); i++){
            Node node = nodeList.item(i);
            NamedNodeMap namedNodeMap = node.getAttributes();
            for(int j=0; j< namedNodeMap.getLength();j++){
                Node childNode = namedNodeMap.item(j);
                if(childNode.getNodeValue().equalsIgnoreCase(categoryName)){
                    return node;
                }
            }
        }
        return null;
        
    }
    
    /*
     * @return child node by given child value
     */
    private Node getElementByAttributeName(Element tagElement, String childTagName, String value){
        
        NodeList nodeList = tagElement.getElementsByTagName(childTagName);
        for(int i=0; i < nodeList.getLength(); i++){
            Node node = nodeList.item(i);
            NamedNodeMap namedNodeMap = node.getAttributes();
            for(int j=0; j< namedNodeMap.getLength();j++){
                Node childNode = namedNodeMap.item(j);
                if(childNode.getNodeValue().equalsIgnoreCase(value)){
                    return childNode;
                }
            }
        }
        return null;
    }
    
    
    private Node getChildNode(Node node, String namedValue){
        
        NodeList nodeList = node.getChildNodes();
        for(int i =0; i< nodeList.getLength(); i++){
            Node child = nodeList.item(i);
            if(child.getNodeName().equalsIgnoreCase(namedValue)){
                return child;
            }
        }
        return null;
    }
    
    public Document initDoc() throws ParserConfigurationException{
        if(cmDoc == null){
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            cmDoc = builder.newDocument();
        }
        return cmDoc;
    }
    
    public void serialize(Document document) throws TransformerConfigurationException, TransformerException{
        try {
            
            File outputXmlFile = new File(cmOutputFolder, "ICEfaces_component_cm.xml");
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.transform(new DOMSource(document),new StreamResult( new FileOutputStream(outputXmlFile.getPath())));
        } catch (TransformerConfigurationException ex) {
            ex.printStackTrace();
        } catch (TransformerException ex) {
            ex.printStackTrace();
        } catch (TransformerFactoryConfigurationError ex) {
            ex.printStackTrace();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
}
