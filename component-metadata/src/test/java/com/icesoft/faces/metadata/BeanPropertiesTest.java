package com.icesoft.faces.metadata;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.io.IOException;
import java.net.URL;

import org.xml.sax.SAXException;
import com.sun.rave.jsfmeta.JsfMetaParser;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BeanPropertiesTest extends TestCase {

	public static Test suite() {
		return new TestSuite(BeanPropertiesTest.class);
	}

	public static void main() {
		junit.textui.TestRunner.run(BeanPropertiesTest.suite());
	}

	public void testComponentProperties() {

		String[] components = getComponentBeanInfo();
		for (int j = 0; j < components.length; j++) {

			try {
				Class beanInfoClass = Class.forName(components[j] + "BeanInfo");
				Object object = beanInfoClass.newInstance();
				if (object instanceof SimpleBeanInfo) {
					SimpleBeanInfo new_name = (SimpleBeanInfo) object;
					testSimpleBeanInfo(new_name);
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
			} catch (Exception e){
				e.printStackTrace();
				fail(e.getMessage());
			}

		}
	}

	public String[] getComponentBeanInfo() {

		String[] cb = null;
		JsfMetaParser jsfMetaParser = new JsfMetaParser();

		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			URL localUrl = classLoader.getResource(".");
			String newPath = "file:" + localUrl.getPath()
					+ "../../../component/conf/META-INF/faces-config.xml";
			URL url = new URL(newPath);

			FacesConfigBean facesConfigBean = jsfMetaParser.parse(url);
			ComponentBean[] componentbeans = facesConfigBean.getComponents();
			cb = new String[componentbeans.length];

			for (int i = 0; i < componentbeans.length; i++) {
				cb[i] = componentbeans[i].getComponentClass();
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		} catch (SAXException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		return cb;
	}

	public void testSimpleBeanInfo(SimpleBeanInfo simpleBeanInfo) {

		PropertyDescriptor[] pds = null;
		Object newObject = null;
		try {
			pds = simpleBeanInfo.getPropertyDescriptors();
			String className = simpleBeanInfo.getClass().getName();
			
			Class namedClass = Class.forName(className.substring(0, className
					.indexOf("BeanInfo")));
			newObject = namedClass.newInstance();
			
			
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
		} catch (Exception e){
			e.printStackTrace();
			fail(e.getMessage());
		}
		
		
		String[] names = getMethodArray(newObject);
		for (int i = 0; i < pds.length; i++) {
			String propertyName = pds[i].getName();

			String tmp = "\tmethod name=" + pds[i].getReadMethod().getName()
					+ " type" + pds[i].getPropertyType();
			boolean methodIndexBoolean = getMethodIndex(pds[i].getReadMethod()
					.getName(), names) == -1;
			String message = " failed class name= "
					+ newObject.getClass().getName() + " method name= "
					+ propertyName +"\n"+ tmp;
			assertEquals(" " + message + "", false, methodIndexBoolean);
		}
	}

	private int getMethodIndex(String methodName, String[] names) {

		int index = -1;
		for (int j = 0; j < names.length; j++) {
			if (methodName.equalsIgnoreCase(names[j])) {
				index = j;
				return index;
			}
		}
		return index;
	}

	private String[] getMethodArray(Object object) {

		String[] methodArray = null;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
			MethodDescriptor[] mds = beanInfo.getMethodDescriptors();
			methodArray = new String[mds.length];
			for (int j = 0; j < mds.length; j++) {
				methodArray[j] = mds[j].getMethod().getName();
			}
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		return methodArray;
	}
}
