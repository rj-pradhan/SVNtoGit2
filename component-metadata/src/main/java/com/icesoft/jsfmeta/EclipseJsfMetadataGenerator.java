package com.icesoft.jsfmeta;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.icesoft.jsfmeta.util.EclipseMetadataParserHelper;
import com.icesoft.jsfmeta.util.ExtendedPropertiesHelper;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.NamedValueBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;

public class EclipseJsfMetadataGenerator {

	public static final String WORKING_FOLDER;

	static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	static final String GRAMMAR_ANNOTATIONSCHEMA = "http://org.eclipse.jst.jsf.contentmodel.annotations/grammarAnnotationSchema";

	static final String GRAMMAR_ANNOTATIONSCHEMA_LOCATION = "http://org.eclipse.jst.jsf.contentmodel.annotations/grammarAnnotationSchema ../../org.eclipse.jst.jsf.contentmodel.annotations/schema/grammar-annotations.xsd";

	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	static {
		String result = ".";
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			URL localUrl = classLoader.getResource(".");
			result = localUrl.getPath() + File.separator+ "conf";

		} catch (Exception exception) {
		}
		WORKING_FOLDER = result;
	}

	public static void main(String[] args) {

		EclipseJsfMetadataGenerator generator = new EclipseJsfMetadataGenerator();

		String xmlPath = "icefaces-eclipse-jsf-metadata.xml";
		String schemaLocation = WORKING_FOLDER
				+ "/eclipse.schema/grammar-annotations.xsd";
		generator.createEclipseJsfMetadata(xmlPath, schemaLocation);
	}

	/**
	 * TODO: data mapping
	 */
	public void createEclipseJsfMetadata(String xmlFileName,
			String schemaLocation) {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		documentBuilderFactory.setNamespaceAware(true);

		DocumentBuilder documentBuilder = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document document = documentBuilder.newDocument();

		Element rootElement = document.createElementNS(
				GRAMMAR_ANNOTATIONSCHEMA, "p" + ":grammar-annotations");

		document.appendChild(rootElement);

		rootElement.setAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
				"xsi:schemaLocation", GRAMMAR_ANNOTATIONSCHEMA_LOCATION);

		initDom(document);

		// closing root
		rootElement.appendChild(document.createTextNode("\n"));

		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();

		validate(document, schemaLocation);

		try {
			Transformer transformer = transformerFactory.newTransformer();
			transformer.transform(new DOMSource(document), new StreamResult(
					new FileOutputStream(WORKING_FOLDER + "/" + xmlFileName)));
			// transformer.transform(new DOMSource(document), new StreamResult(
			// System.out));
			// System.out.println();

		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// TODO:
	private void initDom(Document document) {
		ExtendedPropertiesHelper extendedPropertiesHelper = new ExtendedPropertiesHelper();
		ArrayList tagNames = extendedPropertiesHelper.getComponentTagNames();

		Element rootElement = document.getDocumentElement();

		for (int i = 0; i < tagNames.size(); i++) {

			if (tagNames.get(i) != null) {
				// component element
				Element cmElement = document.createElement("cm-element");
				rootElement.appendChild(document.createTextNode("\n"));
				rootElement.appendChild(cmElement);
				cmElement.setAttributeNS(null, "name", tagNames.get(i)
						.toString());
				// closing component
				cmElement.appendChild(document.createTextNode("\n"));
			}
		}

		emfGeneric(document);

	}

	// TODO:
	private void emfGeneric(Document document) {

		EclipseMetadataParserHelper eclipseMetadataParserHelper = new EclipseMetadataParserHelper();
		ComponentBean componentBean = eclipseMetadataParserHelper
				.getComponentBean("*");
		PropertyBean[] propertBeans = componentBean.getProperties();

		Element rootElement = document.getDocumentElement();

		// component element
		Element cmElement = document.createElement("cm-element");
		rootElement.appendChild(document.createTextNode("\n"));
		rootElement.appendChild(cmElement);
		cmElement.setAttributeNS(null, "name", "*");

		// TODO:
		for (int i = 0; i < propertBeans.length; i++) {

			// component attribute
			Element cmAttribute = document.createElement("cm-attribute");
			cmElement.appendChild(document.createTextNode("\n\t"));
			cmElement.appendChild(cmAttribute);
			cmAttribute.setAttributeNS(null, "name", propertBeans[i]
					.getPropertyName());

			Map namedValues = propertBeans[i].getNamedValues();

			String key;
			for (Iterator keys = namedValues.keySet().iterator(); keys
					.hasNext();) {

				key = (String) keys.next();
				NamedValueBean nvb = (NamedValueBean) namedValues.get(key);
				// component attribute property
				Element cmAttributeProperty = document
						.createElement("property");
				cmAttribute.appendChild(document.createTextNode("\n\t\t"));
				cmAttributeProperty.setAttributeNS(null, "name", nvb.getName());
				cmAttribute.appendChild(cmAttributeProperty);

				// // component attribute property value
				Element cmAttributePropertyValue = document
						.createElement("value");
				cmAttributeProperty.appendChild(document
						.createTextNode("\n\t\t\t"));
				cmAttributePropertyValue.appendChild(document
						.createTextNode(nvb.getValue()));
				cmAttributeProperty.appendChild(cmAttributePropertyValue);

				// closing component attribute property value
				cmAttributePropertyValue.appendChild(document
						.createTextNode("\n\t\t\t"));

				// closing component attribute property
				cmAttributeProperty.appendChild(document
						.createTextNode("\n\t\t"));

			}
			// closing component attribute
			cmAttribute.appendChild(document.createTextNode("\n\t"));

		}

		// closing component
		cmElement.appendChild(document.createTextNode("\n"));
	}

	public void validate(String filePath, String schemaSource) {

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
			documentBuilder.parse(new File(filePath));
		} catch (IOException e) {

			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();

		}
	}

	public void validate(Document document, String schemaSource) {

		try {
			SchemaFactory factory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new File(schemaSource));
			Validator validator = schema.newValidator();
			validator.validate(new DOMSource(document));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
