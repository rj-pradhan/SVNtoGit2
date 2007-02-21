package com.icesoft.jsfmeta.util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import javax.faces.render.RenderKitFactory;

import org.xml.sax.SAXException;

import com.icesoft.jsfmeta.MetadataXmlParser;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.NamedValueBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;
import com.sun.rave.jsfmeta.beans.RenderKitBean;
import com.sun.rave.jsfmeta.beans.RendererBean;

public class ExtendedPropertiesHelper {

	private ArrayList tagNames = new ArrayList();

	private ArrayList componentBeans = new ArrayList();

	public static void main(String[] args) {

		ExtendedPropertiesHelper extendedProperties = new ExtendedPropertiesHelper();
		extendedProperties.extractComponentBeanInfo();
	}

	public ExtendedPropertiesHelper() {
		extractComponentBeanInfo();
	}

	public ArrayList getComponentTagNames() {
		return tagNames;
	}

	public ArrayList getCompoentBeans() {
		return componentBeans;
	}

	public PropertyBean[] extractComponentBeanInfo() {

		PropertyBean[] pb = null;
		String[] cb = null;
		MetadataXmlParser metadataParser = new MetadataXmlParser();
		metadataParser.setDesign(true);

		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			URL localUrl = classLoader.getResource(".");
			String newPath = "file:" + localUrl.getPath()
					+ "./conf/sun-faces-config-verified.xml";
			URL url = new URL(newPath);

			FacesConfigBean facesConfigBean = metadataParser.parse(url);
			ComponentBean[] componentbeans = facesConfigBean.getComponents();
			RenderKitBean renderKitBean = facesConfigBean
					.getRenderKit(RenderKitFactory.HTML_BASIC_RENDER_KIT);

			cb = new String[componentbeans.length];

			for (int i = 0; i < componentbeans.length; i++) {
				cb[i] = componentbeans[i].getComponentClass();
				PropertyBean[] descriptions = componentbeans[i].getProperties();
				RendererBean rendererBean = renderer(componentbeans[i],
						renderKitBean);
				tagNames.add(rendererBean.getTagName());
				componentBeans.add(componentbeans[i]);

				String one = "";
				pb = descriptions;
				for (int j = 0; j < pb.length; j++) {

					one = one + "\n" + pb[j].getPropertyName();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return pb;
	}

	// TODO: debug info
	private void moreInfo(PropertyBean propertyBean, RendererBean rendererBean) {
		Map namedValues = propertyBean.getNamedValues();
		String key;
		String expression;
		String value;

		for (Iterator keys = namedValues.keySet().iterator(); keys.hasNext();) {

			key = (String) keys.next();
			NamedValueBean nvb = (NamedValueBean) namedValues.get(key);
			expression = nvb.getExpression();
			value = nvb.getValue();
			System.out.println("tag name=" + rendererBean.getTagName()
					+ " propert name=" + propertyBean.getPropertyName()
					+ " name=" + nvb.getName() + " expression=" + expression
					+ " value=" + value);
			System.out.println("" + rendererBean.getTagName());
		}
	}

	protected RendererBean renderer(ComponentBean cb,
			RenderKitBean renderKitBean) {
		String rendererType = cb.getRendererType();
		if (rendererType == null)
			return null;
		String componentFamily = cb.getComponentFamily();
		if (componentFamily == null) {
			System.out.println("TODO: component family");
		}
		RendererBean rb = renderKitBean.getRenderer(componentFamily,
				rendererType);

		if (rb == null) {
			System.out.println("TODO: RenderBean componentFamily="
					+ componentFamily + " rendererType=" + rendererType);
		}

		if (rb == null) {
			System.out.println("TODO: no renderer bean");
		}
		return rb;
	}

}
