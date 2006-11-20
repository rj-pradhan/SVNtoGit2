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
	
	
	public FacesConfigParserHelper(){
		
	}
	
	public static void main(String[] args){
		
		FacesConfigParserHelper helper = new FacesConfigParserHelper();
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
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			URL localUrl = classLoader.getResource(".");			
		
			String newPath = localUrl+"./../../component/conf/webui-faces-config.xml";
			System.out.println("local url ="+ newPath);
			URL url = new URL(newPath);

			FacesConfigBean facesConfigBean = metadataParser.parse(url);
			RenderKitBean renderKitBean = facesConfigBean.getRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT);
			RendererBean[] rendererBeans = renderKitBean.getRenderers();
			
			rd = rendererBeans;

			for (int i = 0; i < rendererBeans.length; i++) {
				//System.out.println(" render bean= "+ rendererBeans[i].getRendererType());

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return rd;
	}

}
