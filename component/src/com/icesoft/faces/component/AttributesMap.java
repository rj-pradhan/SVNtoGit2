package com.icesoft.faces.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.icesoft.faces.renderkit.dom_html_basic.PassThruAttributeRenderer;


public class AttributesMap extends HashMap {

	private static final long serialVersionUID = 2138606391025947099L;
	private List passThruNonBooleanAttributeList;
	private List passThruBooleanAttributeList;
	private Map originalAttributeMap;
	
	public AttributesMap(Map originalAttributeMap){
		this.originalAttributeMap = originalAttributeMap;
		passThruNonBooleanAttributeList = new ArrayList();
		originalAttributeMap.put(IcePassThruAttributes.PASS_THRU_NON_BOOLEAN_ATT_LIST, passThruNonBooleanAttributeList);
		passThruBooleanAttributeList = new ArrayList();
		originalAttributeMap.put(IcePassThruAttributes.PASS_THRU_BOOLEAN_ATT_LIST, passThruBooleanAttributeList);
	}
	
    public Object put(Object key, Object value) {
    	if (PassThruAttributeRenderer.passThruAttributeNames.contains(key)) {
    		passThruNonBooleanAttributeList.add(key);
    	}else if (PassThruAttributeRenderer.booleanPassThruAttributeNames.contains(key)) {
    		passThruBooleanAttributeList.add(key);
    	}
    	return originalAttributeMap.put(key, value);
    }
    
    public Object remove(Object key) {
    	if (passThruNonBooleanAttributeList.contains(key)) {
    		passThruNonBooleanAttributeList.remove(key);
    	} else if (passThruBooleanAttributeList.contains(key)) {
    		passThruBooleanAttributeList.remove(key);
    	}
    	return originalAttributeMap.remove(key);
    }
    
    public boolean containsKey(Object key) {
    	return originalAttributeMap.containsKey(key);
    }

    public Object get(Object key) {
    	return originalAttributeMap.get(key);
    }

    public void putAll(Map map) {
    	originalAttributeMap.putAll(map);
    }
}
