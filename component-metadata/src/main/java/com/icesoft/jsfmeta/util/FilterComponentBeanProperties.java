package com.icesoft.jsfmeta.util;

import java.io.IOException;
import java.net.URL;

import org.xml.sax.SAXException;

import com.icesoft.jsfmeta.MetadataXmlParser;
import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.PropertyBean;

public class FilterComponentBeanProperties {

	
	public FilterComponentBeanProperties(){
		
	}
	
	public void init(){
		
	}
	
	public static void main(String[] args) {

		FilterComponentBeanProperties status = new FilterComponentBeanProperties();
		status.getFilterComponentProperty();
	}

	/*
	 * return Javascript attribute property for example: onclick
	 */
	public PropertyBean[] getFilterComponentProperty() {

		PropertyBean[] pb = null;
		String[] cb = null;
		MetadataXmlParser metadataParser = new MetadataXmlParser();
		metadataParser.setDesign(true);

		try {
			ClassLoader classLoader = Thread.currentThread()
					.getContextClassLoader();
			URL localUrl = classLoader.getResource(".");
			String newPath = "file:" + localUrl.getPath()
					+ "./../conf/filter.properties/filter-faces-config.xml";
			URL url = new URL(newPath);

			FacesConfigBean facesConfigBean = metadataParser.parse(url);
			ComponentBean[] componentbeans = facesConfigBean.getComponents();
			cb = new String[componentbeans.length];

			for (int i = 0; i < componentbeans.length; i++) {
				cb[i] = componentbeans[i].getComponentClass();
				PropertyBean[] descriptions = componentbeans[i].getProperties();

				String one = "";
				pb = descriptions;
				for (int j = 0; j < pb.length; j++) {

					one = one + "\n property name=" + pb[j].getPropertyName()+ " property category="+ pb[j].getCategory();
				}
				//System.out.println("" + one);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

		return pb;
	}

	
	private void filterCategory(PropertyBean propertyBean){
		
		PropertyBean[] filterPropertyBeans = getFilterComponentProperty();
		
		for(int i=0 ; i< filterPropertyBeans.length; i++){
			
			boolean condition = filterPropertyBeans[i].getPropertyName().equalsIgnoreCase(propertyBean.getPropertyName());
			if(condition){
				propertyBean.setCategory(filterPropertyBeans[i].getCategory());
			}
		}		
	}
	
	
	/*
	 * filter with category name (hiding JAVASCRIPT category related properties)getEffect
	 */
	public void filter(ComponentBean componentBean) {

		PropertyBean[] propertyBeans = null;

		propertyBeans = componentBean.getProperties();

		
		
		for (int j = 0; j < propertyBeans.length; j++) {
			
						
			String propertyName = propertyBeans[j].getPropertyName();
			String categoryName = propertyBeans[j].getCategory();

			//TODO:
			//filterCategory(propertyBeans[j]);
			
			// System.out.println("propertyName="+ propertyName+"
			// categoryName="+categoryName);
			if (categoryName != null
					&& categoryName.equalsIgnoreCase("javascript")) {
				// System.out.println("propertyName="+ propertyName+"
				// categoryName="+ categoryName);
				PropertyBean temp = propertyBeans[j];
				temp.setHidden(true);
			}
			
			
		}
	}

	/*
	 * filter 
	 */
	public void filter(ComponentBean[] componentBeans) {

		PropertyBean[] propertyBeans = null;

		for (int i = 0; i < componentBeans.length; i++) {

			componentBeans[i].getProperties();
			propertyBeans = componentBeans[i].getProperties();

			for (int j = 0; j < propertyBeans.length; j++) {
				String propertyName = propertyBeans[j].getPropertyName();
				String categoryName = propertyBeans[j].getCategory();

				System.out.println("propertyName=" + propertyName
						+ " categoryName=" + categoryName);
			}
		}
	}

	/*
	 * filter
	 */
	public void filter(ComponentBean[] componentBeans,
			PropertyBean[] propertyBeans) {

		for (int i = 0; i < componentBeans.length; i++) {
			componentBeans[i].getProperties();
		}

		for (int i = 0; i < propertyBeans.length; i++) {
			String propertyName = propertyBeans[i].getPropertyName();
			String categoryName = propertyBeans[i].getCategory();
		}
	}

}
