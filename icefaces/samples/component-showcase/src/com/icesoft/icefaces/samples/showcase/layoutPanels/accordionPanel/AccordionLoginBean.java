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

import javax.faces.event.ActionEvent;

/**
 * This class controls the ability of a user to login or register
 * on the page. Also demos the ablity to add custom components to the
 * accordion panel component.
 *
 *@since May 18 2007
 * @author dnorthcott
 */
public class AccordionLoginBean {
    // boolean to determine if the user has logged in or registered
    private boolean isLoggedIn;
    // status of the user
    private String status;
    //username
    private String name;
    //password
    private String password;
    
        
    public void setIsLoggedIn(boolean bool){
        isLoggedIn = bool;
    }
    public boolean getIsLoggedIn(){
        return isLoggedIn;
    }
    /**
     * Method to listen for any action from the login area of the jspx page
     *@param event jsp event listener
     */
    public void loginActionListener(ActionEvent event){
        setIsLoggedIn(true);
        setStatus("Logged In");
        
        setPassword("");
    }
    /**
     * Method to listen for any action from the register area of the jspx page
     *@param event jsp event listener
     */
    public void registerActionListener(ActionEvent event){
        setIsLoggedIn(true);
        setStatus("Registered");
        
        setPassword("");
    }

     public void setStatus(String status){
        this.status = status;
    }
     
     public String getStatus(){
         return status;
     }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
