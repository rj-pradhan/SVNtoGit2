package com.icesoft.faces.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import javax.faces.render.RenderKitFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.icesoft.jsfmeta.MetadataXmlParser;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;
import com.sun.rave.jsfmeta.beans.RenderKitBean;
import com.sun.rave.jsfmeta.beans.RendererBean;

public class TestValidMetadataXML{


	public static void main(String[] args) {

		TestValidMetadataXML test = new TestValidMetadataXML();
		test.setUp();
		test.testMetadata();
		test.testMetadataBeans();
	}


	private File getConfDir() {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		URL url = classLoader.getResource(".");

		File buildDir = new File(url.getPath());
		
		if(!buildDir.isDirectory()){
			System.out.println("test build directory does not exist"+url);
		}

		File confFile = new File(buildDir.getParent() + File.separatorChar
				+ "classes" + File.separator + "conf");

		return confFile;
	}

	protected void setUp() {

		File confFile = getConfDir();

		boolean isConfFile = confFile.isDirectory();
		if(!isConfFile){
			System.out.println("no conf directory in the build directory"+ confFile);
		}

		String xsltStreamSourceString = confFile.getPath() + File.separatorChar
				+ "xslt_conf" + File.separatorChar + "translate-conf.xsl";
		String outputStreamString = confFile.getPath() + File.separatorChar
				+ "sun-faces-config-verified.xml";
		String streamSourceString = confFile.getPath() + File.separatorChar
				+ "sun-faces-config.xml";

		try {

			TransformerFactory tFactory = TransformerFactory.newInstance();
			StreamSource xsltStreamSource = new StreamSource(
					xsltStreamSourceString);
			Transformer transformer = tFactory.newTransformer(xsltStreamSource);
			OutputStream outputStream = new FileOutputStream(outputStreamString);
			StreamResult streamResult = new StreamResult(outputStream);
			StreamSource streamSource = new StreamSource(streamSourceString);
			transformer.transform(streamSource, streamResult);

		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testMetadata() {

		File confDir = getConfDir();

		String outputStreamString = confDir.getPath() + File.separatorChar
				+ "sun-faces-config.xml";
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		documentBuilderFactory.setValidating(true);
		documentBuilderFactory.setNamespaceAware(true);

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
			Document document = documentBuilder.parse(new File(
					outputStreamString));

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	public void testMetadataBeans() {

		PropertyBean[] pb = null;
		String[] cb = null;
		MetadataXmlParser metadataParser = new MetadataXmlParser();
		metadataParser.setDesign(true);

		try {
			File confDir = getConfDir();

			String newPath = confDir.getPath()+ File.separator			
					+ "sun-faces-config-verified.xml";
			File file = new File(newPath);

			FacesConfigBean facesConfigBean = metadataParser.parse(file);

			RenderKitBean renderKitBean = facesConfigBean
					.getRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT);
			ComponentBean[] componentbeans = facesConfigBean.getComponents();

			cb = new String[componentbeans.length];

			for (int i = 0; i < componentbeans.length; i++) {
				cb[i] = componentbeans[i].getComponentClass();
				PropertyBean[] descriptions = componentbeans[i].getProperties();

				componentbeans[i].getBaseComponentType();

				RendererBean rendererBean = renderer(componentbeans[i],
						renderKitBean);
				String rendererType = rendererBean.getRendererType();
				String renderClassName = rendererBean.getRendererClass();

				String one = "";
				pb = descriptions;
				for (int j = 0; j < pb.length; j++) {
					one = one + "\n" + pb[j].getPropertyName();
				}

			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected RendererBean renderer(ComponentBean cb,
			RenderKitBean renderKitBean) {
		String rendererType = cb.getRendererType();
		if (rendererType == null)
			return null;
		String componentFamily = cb.getComponentFamily();
		if (componentFamily == null) {
			System.out.println("TODO: component family is null");
		}
		RendererBean rb = renderKitBean.getRenderer(componentFamily,
				rendererType);

		if (rb == null) {
			System.out.println("TODO: RenderBean componentFamily=" + componentFamily
					+ " rendererType=" + rendererType);
		}

		if (rb == null) {
			System.out.println("TODO: no renderer bean");
		}
		return rb;
	}

}
