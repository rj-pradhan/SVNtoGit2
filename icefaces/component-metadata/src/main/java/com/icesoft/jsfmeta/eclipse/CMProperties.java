/*
 * CM Model Properties
 */

package com.icesoft.jsfmeta.eclipse;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

public class CMProperties extends Properties{
    
    //work around for the order of properties keys
    public Enumeration keys() {
        Enumeration keys = super.keys();
        Vector sortedVector = new Vector();
        while(keys.hasMoreElements()){
            sortedVector.add(keys.nextElement());
        }
        Collections.sort(sortedVector);
        
        return sortedVector.elements();
    }
    
}
