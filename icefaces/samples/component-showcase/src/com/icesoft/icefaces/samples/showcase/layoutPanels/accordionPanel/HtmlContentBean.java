/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.icefaces.samples.showcase.layoutPanels.accordionPanel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;

/**
 * This class handles the inlining of the html content into the different
 * pages in the panelStack. Reads in the html from the sources.
 */
public class HtmlContentBean {
    
    private String apples;
    private String oranges;
    private String bananas;
    private String citrus;
    private String grapes;
    private String cucumbers;
    private String lettuce;
    private String tomatoes;
    private String peppers;
    private String carrots;
        
    /**
     * Creates a new instance of HtmlContentBean and loads in the pages
     */
    public HtmlContentBean() {
        
         this.apples = loadHtml("apples.html");
        this.oranges = loadHtml("oranges.html");
        this.bananas = loadHtml("bananas.html");
        this.citrus = loadHtml("citrus.html");
        this.grapes = loadHtml("grapes.html");
        this.cucumbers = loadHtml("cucumber.html");
        this.carrots = loadHtml("carrots.html");
        this.lettuce = loadHtml("lettuce.html");
        this.peppers = loadHtml("peppers.html");
        this.tomatoes = loadHtml("tomatoes.html");
    }
    
    /**
     * Method to load in the html pages based on the file name and return the page
     * as a string.
     *
     *@param htmlFileName the name of the html file without the extention.
     *@return String the html page as a string
     */
    private String loadHtml(String htmlFileName){
        String htmlText = "";
        String path = "com/icesoft/icefaces/samples/showcase/resources/accordionContent/"+htmlFileName;
        
        try {
            System.out.println(this.getClass().getClassLoader());
            
            Reader readmeReader = new InputStreamReader(
                    this.getClass()
                    .getClassLoader()
                    .getResourceAsStream(path));
            StringWriter readmeWriter = new StringWriter();
            
            char[] buf = new char[2000];
            int len;
            try {
                while ((len = readmeReader.read(buf)) > -1) {
                    readmeWriter.write(buf, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            htmlText = readmeWriter.toString();
            readmeReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return htmlText;
    }
    
   
    /**
     *@return the html page in String format
     */
    public String getApples(){
        
        return apples;
    }
    
    /**
     *@return the html page in String format
     */
    public String getOranges(){
       return oranges;
    }
    
    /**
     *@return the html page in String format
     */
    public String getGrapes(){
       return grapes;
    }
    
    /**
     *@return the html page in String format
     */
    public String getCitrus(){
        return citrus;
    }
    
    /**
     *@return the html page in String format
     */
    public String getBananas(){
       return bananas;
    }
    
    /**
     *@return the html page in String format
     */
    public String getLettuce(){
       return lettuce;
    }
    
    /**
     *@return the html page in String format
     */
    public String getCarrots(){
       return carrots;
    }
    
    /**
     *@return the html page in String format
     */
    public String getTomatoes(){
       return tomatoes;
    }
    
    /**
     *@return the html page in String format
     */
    public String getCucumbers(){
       return cucumbers;
    }
    
    /**
     *@return the html page in String format
     */
    public String getPeppers(){
        return peppers;
    }
}
