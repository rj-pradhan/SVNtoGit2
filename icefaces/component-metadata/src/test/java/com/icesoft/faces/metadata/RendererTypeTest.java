package com.icesoft.faces.metadata;

import java.io.IOException;
import java.net.URL;

import javax.faces.component.UIComponentBase;
import javax.faces.render.RenderKitFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.SAXException;

import com.icesoft.jsfmeta.MetadataXmlParser;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.RendererBean;

public class RendererTypeTest extends TestCase {

	private ComponentBean[] componentBeans;

	private UIComponentBase[] uiComponentBases;

	private RendererBean[] rendererBeans;

	public static Test suite() {
		return new TestSuite(RendererTypeTest.class);
	}

	public static void main() {
		junit.textui.TestRunner.run(RendererTypeTest.suite());
	}

	protected void setUp() {

		if (componentBeans == null && uiComponentBases == null) {
			componentBeans = getComponentBeanInfo();
			rendererBeans = getRendererBean();
			uiComponentBases = new UIComponentBase[componentBeans.length];

			for (int j = 0; j < componentBeans.length; j++) {
				Object newObject = null;

				try {
					Class namedClass = Class.forName(componentBeans[j]
							.getComponentClass());
					newObject = namedClass.newInstance();

					if (newObject instanceof UIComponentBase) {
						uiComponentBases[j] = (UIComponentBase) newObject;

					}
				} catch (NullPointerException e) {
					e.printStackTrace();
					fail(e.getMessage());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					fail(e.getMessage());
				} catch (InstantiationException e) {
					e.printStackTrace();
					fail(e.getMessage());
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					fail(e.getMessage());
				}
			}
		}
	}

	public void testRendererType() {

		for (int i = 0; i < componentBeans.length; i++) {
			String renderTypeUIComponent = uiComponentBases[i]
					.getRendererType();
			String renderTypeComponentBean = componentBeans[i]
					.getRendererType();

			boolean notSameRenderType = renderTypeUIComponent != null
					&& renderTypeComponentBean != null
					&& !renderTypeUIComponent.trim().equalsIgnoreCase(
							renderTypeComponentBean.trim());

			String message = "RenderType not the same for Component Class="
					+ componentBeans[i].getComponentClass()
					+ "\n component renderType=" + renderTypeUIComponent
					+ "\n faces-config declared renderType="
					+ renderTypeComponentBean + "\n\n";
			assertFalse(message, notSameRenderType);
		}
	}

	public void testRendererClass() {

		for (int i = 0; i < rendererBeans.length; i++) {

			String message = "";
			try {
				String rendererClass = rendererBeans[i].getRendererClass();
				System.out.println("rendererClass=" + rendererClass);
				Class namedClass = Class.forName(rendererClass);
				String packageName = namedClass.getPackage().getName();

				message = "RenderType not the same for Component Class="
						+ componentBeans[i].getComponentClass()
						+ "\n component renderType="
						+ rendererBeans[i].getRendererType()
						+ "\n renderer class=" + rendererClass + "\n\n";
			} catch (Exception e) {
				fail(message);
			}
		}
	}

	public void testFamilyType() {

		for (int i = 0; i < componentBeans.length; i++) {
			String familyTypeUIComponent = uiComponentBases[i].getFamily();
			String familyTypeComponentBean = componentBeans[i]
					.getComponentFamily();

			boolean notSameRenderType = familyTypeUIComponent != null
					&& familyTypeComponentBean != null
					&& !familyTypeUIComponent.trim().equalsIgnoreCase(
							familyTypeComponentBean.trim());

			String message = "FamilyType not the same for Component Class="
					+ componentBeans[i].getComponentClass()
					+ "\n component familyType=" + familyTypeUIComponent
					+ "\n faces-config declared familyType="
					+ familyTypeComponentBean + "\n\n";
			assertFalse(message, notSameRenderType);
		}
	}
	
	//TODO: baseline Component

	private FacesConfigBean getFacesConfigBean() {

		FacesConfigBean facesConfigBean = null;
		MetadataXmlParser jsfMetaParser = new MetadataXmlParser();
		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			URL localUrl = classLoader.getResource(".");
			String newPath = "file:" + localUrl.getPath()
					+ "../../../component/conf/META-INF/faces-config.xml";
			URL url = new URL(newPath);

			facesConfigBean = jsfMetaParser.parse(url);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		return facesConfigBean;
	}

	private ComponentBean[] getComponentBeanInfo() {

		ComponentBean[] cb = getFacesConfigBean().getComponents();
		return cb;
	}

	private RendererBean[] getRendererBean() {

		RendererBean[] rb = getFacesConfigBean().getRenderKit(
				RenderKitFactory.HTML_BASIC_RENDER_KIT).getRenderers();
		return rb;
	}
}
