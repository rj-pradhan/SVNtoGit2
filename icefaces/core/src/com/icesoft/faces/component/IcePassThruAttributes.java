package com.icesoft.faces.component;

import java.util.Map;

import javax.faces.el.ValueBinding;

public interface IcePassThruAttributes {
	public final static String ICE_ATTRIBUTE_MAP ="ICE_ATTRIBUTE_MAP"; 
	public final static String PASS_THRU_NON_BOOLEAN_ATT_LIST ="PASS_THRU_NON_BOOLEAN_ATT_LIST";
	public final static String PASS_THRU_BOOLEAN_ATT_LIST ="PASS_THRU_BOOLEAN_ATT_LIST";

	public Map getAttributes() ;
	
    public void setValueBinding(String s, ValueBinding vb) ;
}
