
package com.icesoft.jsfmeta.util;

import java.net.URL;

public class GeneratorUtil {
    
    private static String WORKING_FOLDER;
    
    public GeneratorUtil() {
    }
    
    static{
        String result = ".";
        try {
            ClassLoader classLoader = Thread.currentThread()
            .getContextClassLoader();
            URL localUrl = classLoader.getResource(".");
            if(localUrl != null){
                result = localUrl.getPath();
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        WORKING_FOLDER = result;
    }
    
    public static String getWorkingFolder(){
        return WORKING_FOLDER;
    }
}