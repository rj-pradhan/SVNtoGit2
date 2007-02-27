/*
 * InternalConfig.java
 *
 * Created on February 27, 2007, 8:37 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.icesoft.jsfmeta.util;

import com.icesoft.jsfmeta.MetadataXmlParser;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author frank
 */
public class InternalConfig {
    
        
    private Properties properties;

    
    private static String WORKING_FOLDER;
    
    /** Creates a new instance of InternalConfig */
    public InternalConfig(Properties properties) {
        this.properties = properties;
    }
 
    public String getProperty(String keyValue){
        
        return properties.getProperty(keyValue);
    }
    
        

    
    public void clear(){
        
    }
}
