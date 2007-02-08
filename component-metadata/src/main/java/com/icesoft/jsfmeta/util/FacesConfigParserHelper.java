/*
 * faces-Config.xml Parser Helper
 *
 */

package com.icesoft.jsfmeta.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.faces.render.RenderKitFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.icesoft.jsfmeta.MetadataXmlParser;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.RenderKitBean;
import com.sun.rave.jsfmeta.beans.RendererBean;

public class FacesConfigParserHelper {
    
    private String fileName;
    
    public FacesConfigParserHelper(String file){
        fileName = file;
    }
    
    public static void main(String[] args){
        String tmp = "./src/main/resources/conf/webui-faces-config.xml";
        FacesConfigParserHelper helper = new FacesConfigParserHelper(tmp);
        helper.getRendererBeans();
        
    }
    
    public static void validate(String filePath) {
        
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        documentBuilderFactory.setValidating(true);
        
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
            documentBuilder.parse(new File(filePath));
        } catch (IOException e) {
            
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
            
        }
    }
    
    
    
        /*
         * return Javascript attribute property for example: onclick
         */
    public RendererBean[] getRendererBeans() {
        
        RendererBean[] rd = null;
        MetadataXmlParser metadataParser = new MetadataXmlParser();
        metadataParser.setDesign(false);
        
        try {
            
            File file = new File(fileName);
            FacesConfigBean facesConfigBean = metadataParser.parse(file);
            RenderKitBean renderKitBean = facesConfigBean.getRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT);
            rd = renderKitBean.getRenderers();
            
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        
        return rd;
    }
    
}
