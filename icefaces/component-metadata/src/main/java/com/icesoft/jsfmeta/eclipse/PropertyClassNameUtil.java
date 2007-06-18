/*
 * PropertyClassNameUtil to rematch CM model type
 */

package com.icesoft.jsfmeta.eclipse;

import com.icesoft.jsfmeta.util.ConfigStorage;
import com.icesoft.jsfmeta.util.GeneratorUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertyClassNameUtil {
    
    private Properties properties;
    private Map propertyNameToTypeMap = new HashMap();
    private Map propertyClassToTypeMap = new HashMap();
    private Map propertyCategoryToTypeMap = new HashMap();
    
    public PropertyClassNameUtil(){
        properties = loadProperties();
        initMapping();
    }
    
    
    public Properties loadProperties(){
        
        String filePath = GeneratorUtil.getWorkingFolder()+ "conf/eclipse.schema/cm_type.properties";
        Properties props = ConfigStorage.getInstance(filePath).loadProperties();
        for(Iterator iterator = props.keySet().iterator(); iterator.hasNext();){
            Object nextKey = iterator.next();
        }
        return props;
    }
    
    public void initMapping(){
        String[] propertyNames = new String[]{"style","styleClass","bgcolor","binding"};
        String[] propertyTypes = new String[]{"CSSSTYLE","CSSCLASS","COLOR","PROPERTYBINDING"};
        
        for(int i=0; i< propertyNames.length; i++){
            propertyNameToTypeMap.put(propertyNames[i], propertyTypes[i]);
        }
        
        String[] propertyClasses = new String[]{"com.icesoft.faces.context.effects.Effect","boolean", "java.lang.Object", "javax.faces.el.MethodBinding"};
        String[] propertyClassesToTypes = new String[]{"METHODBINDING","BOOLEAN", "METHODBINDING","METHODBINDING" };
        
        for(int i=0; i< propertyClasses.length; i++){
            propertyClassToTypeMap.put(propertyClasses[i], propertyClassesToTypes[i]);
        }
        
        String[] propertyCategories = new String[]{"javascript"};
        String[] propertyCategroiesToTypes = new String[]{"JAVASCRIPT"};
        
        for(int i=0; i< propertyCategories.length; i++){
            propertyClassToTypeMap.put(propertyCategories[i], propertyCategroiesToTypes[i]);
        }

    }
    
    //vwp metadata
    private final static Set propertyClassHashSet = new HashSet(Arrays.asList(
            new String[]{
        "boolean","com.icesoft.faces.context.effects.Effect","int","java.lang.Object",
        "java.lang.String","java.util.List","javax.faces.convert.Converter","javax.faces.el.MethodBinding"
    }));
    
    //eclipse cm model property type keywords
    private final static Set propertyTypeHashSet = new HashSet(Arrays.asList(
            new String[]{
        "BOOLEAN", "COLOR","CSSCLASS", "CSSSTYLE", "ENUMERATED",
        "JAVASCRIPT", "METHODBINDING", "NAMED-BOOLEAN", "PROPERTYBINDING", "RELATIVEPATH", "WEBPATH"
    }));
    
    
    public static boolean isPropertyType(String name){
        return propertyTypeHashSet.contains(name);
    }
    
    public static boolean isPropertyClass(String name){
        return propertyClassHashSet.contains(name);
    }
    
    //eclipse a separate marker for editor class yet, so type is associated with editor
    //
    public String getMatchedName(String propertyClassName, String propertyCategoryName, String propertyName, String propertyEditor){
        
        if(propertyClassName != null){                    
            propertyClassName = propertyClassName.trim();
        }
        
        if(propertyCategoryName != null){
            propertyCategoryName = propertyCategoryName.trim();
        }
        
        if(propertyName != null){
            propertyName = propertyName.trim();
        }
        
        if((String)propertyNameToTypeMap.get(propertyName)!= null){
            return (String)propertyNameToTypeMap.get(propertyName);
        }
        
        if((String)propertyClassToTypeMap.get(propertyClassName)!= null){
            return (String)propertyClassToTypeMap.get(propertyClassName);
        }
        
        if((String)propertyCategoryToTypeMap.get(propertyCategoryName)!= null){
            return (String)propertyCategoryToTypeMap.get(propertyCategoryName);
        }
        
        if(propertyEditor != null && (String)properties.get(propertyEditor) != null){
            return (String)properties.get(propertyEditor);
        }
     
        return null;
    }
    
}
