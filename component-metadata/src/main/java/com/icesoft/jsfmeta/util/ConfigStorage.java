
package com.icesoft.jsfmeta.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * ConfigStorage pick up properties file
 */
public class ConfigStorage {
    
    private String fileName;
    
    private ConfigStorage(String fileName) {
        this.fileName = fileName;
    }
 
    public static ConfigStorage getInstance(String fileName){
        
        return new ConfigStorage(fileName);
    }
    
    public Properties loadProperties(){
        
        Properties properties = new Properties();
        try {
            properties.load(new BufferedInputStream(new FileInputStream(new File(fileName))));
        } catch (FileNotFoundException e){
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e){                        
            e.printStackTrace();
            System.exit(1);
        }
        
        return properties;
    }
    
    
    
}
