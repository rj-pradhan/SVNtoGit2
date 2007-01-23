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

package com.icesoft.icefaces.samples.showcase.components.selection;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

/**
 * <p>The SelectionTagsBean Class is the backing bean for the selection
 * components demonstration. It is used to store the options and selected values
 * of the various selection components.<p>
 */
public class SelectionTagsBean {

    /**
     * Available options for the various selection components.
     */
    private static final SelectItem[] drinkItems = new SelectItem[]{
            new SelectItem("Coke"),
            new SelectItem("Pepsi"),
            new SelectItem("Sprite"),
            new SelectItem("7Up"),
    };
    private static final SelectItem[] languageItems = new SelectItem[]{
            new SelectItem("Java"),
            new SelectItem("C#"),
            new SelectItem("C++"),
            new SelectItem("C"),
            new SelectItem("COBOL"),
    };
    private static final SelectItem[] countryItems = new SelectItem[]{
            new SelectItem("Canada"),
            new SelectItem("United States"),
            new SelectItem("China"),
            new SelectItem("United Kingdom"),
            new SelectItem("Russia"),
    };
    private static final SelectItem[] componentTypeItems = new SelectItem[]{
            new SelectItem("I/O"),
            new SelectItem("Command"),
            new SelectItem("Selection"),
    };
    private static final SelectItem[] carListItems = new SelectItem[]{
            new SelectItem("Batmobile"),
            new SelectItem("A-Team Van"),
            new SelectItem("BMW Z4"),
            new SelectItem("General Lee"),
            new SelectItem("El Chamino"),
    };
    

    /**
     * Converts string arrays for displays.
     *
     * @param stringArray string array to convert
     * @return a string concatenating the elements of the string array
     */
    private static String convertToString(String[] stringArray) {
        if (stringArray == null) return "";
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < stringArray.length; i++) {
            if (i > 0) b.append(" , ");
            b.append(stringArray[i]);
        }
        return b.toString();
    }

    /**
     * Variables for storing the selected options.
     */
    private SelectItem[] cityItems;
    private boolean isNewUser;
    private String drink;
    private String[] languages;
    private String country;
    private String[] cities;
    private String componentType = "I/O";
    private String[] cars;

    /**
     * Gets the option items for drinks.
     *
     * @return array of drink items
     */
    public SelectItem[] getDrinkItems() {
        return drinkItems;
    }

    /**
     * Gets the option items for languages.
     *
     * @return array of language items
     */
    public SelectItem[] getLanguageItems() {
        return languageItems;
    }

    /**
     * Gets the option items for countries.
     *
     * @return array of country items
     */
    public SelectItem[] getCountryItems() {
        return countryItems;
    }

    /**
     * Gets the option items of cities.
     *
     * @return array of city items
     */
    public SelectItem[] getCityItems() {
        return cityItems;
    }

    /**
     * Gets the option items for component types.
     *
     * @return array of component type items
     */
    public SelectItem[] getComponentTypeItems() {
        return componentTypeItems;
    }
    
    
    /**
     *returns the list of available cars to select
     *
     *@return carlist
     */
    public SelectItem[] getCarListItems()
    {
        return carListItems;
    }

    /**
     * Gets the newUser property.
     *
     * @return true or false
     */
    public boolean isNewUser() {
        return isNewUser;
    }

    /**
     * Sets the newUser property.
     *
     * @param newValue true of false
     */
    public void setNewUser(boolean newValue) {
        isNewUser = newValue;
    }

    /**
     * Gets the selected drink.
     *
     * @return the selected drink
     */
    public String getDrink() {
        return drink;
    }

    /**
     * Sets the selected drink.
     *
     * @param newValue the new selected drink
     */
    public void setDrink(String newValue) {
        drink = newValue;
    }

    /**
     * Gets the selected languages.
     *
     * @return the array of selected languages
     */
    public String[] getLanguages() {
        return languages;
    }

    /**
     * Sets the selected languages.
     *
     * @param newValue the array of newly selected languages
     */
    public void setLanguages(String[] newValue) {
        languages = newValue;
    }

    /**
     * Converts array of languages to a string.
     *
     * @return string of languages
     */
    public String getLanguagesString() {
        return convertToString(languages);
    }

    /**
     * Gets the selected country.
     *
     * @return the selected country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the selected country.
     *
     * @param newValue the new selected country
     */
    public void setCountry(String newValue) {
        country = newValue;
    }

    /**
     * Gets the selected cities.
     *
     * @return array of selected cities
     */
    public String[] getCities() {
        return cities;
    }

    /**
     * Sets the selected cities.
     *
     * @param newValue array of newly selected cities
     */
    public void setCities(String[] newValue) {
        if (FacesContext.getCurrentInstance().getExternalContext()
                .getRequestMap().get("CITY_HACK") !=
                                                  null)//Country changed. Don't set cities
            return;
        cities = newValue;
    }
   
    /**
     *sets the selected cars
     *
     *@param newValue array of newly selected cars
     */
    public void setCars(String[] newValue) {
        if (FacesContext.getCurrentInstance().getExternalContext()
                .getRequestMap().get("CITY_HACK") !=
                                                  null)//Country changed. Don't set cities
            return;
        cars = newValue;
    }

    /**
     * Converts array of cities to a string.
     *
     * @return string of cities
     */
    public String getCitiesString() {
        if (cities == null) return "";
        StringBuffer b = new StringBuffer();
        try {
            for (int i = 0; i < cities.length; i++) {
                b.append(cities[i]);
                if (i < cities.length - 1) b.append(',');
            }
            return b.toString();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }

    /**
     * Gets the selected component type.
     *
     * @return the selected component type
     */
    public String getComponentType() {
        return componentType;
    }
    
   /**
    *returns the String array of selected cars
    *
    *@return String[] of selected cars
    */
    public String[] getCars(){
        return cars;
    }
   
    
    
    /**
     *returns the car list as a String
     *
     *@return String the selected car list 
     */
    public String getCarListString() {
        if (cars == null) return "";
        StringBuffer b = new StringBuffer();
        try {
            for (int i = 0; i < cars.length; i++) {
                b.append(cars[i]);
                if (i < cars.length - 1) b.append(',');
            }
            return b.toString();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }
    }

    /**
     * Sets the selected component type.
     *
     * @param newValue the new selected component type
     */
    public void setComponentType(String newValue) {
        componentType = newValue;
    }
    

    public void cityChanged(ValueChangeEvent event) {

    }
    public void carChanged(ValueChangeEvent event){
        
    }

    /**
     * Value change listener for the country change event. Sets up the cities
     * listbox according to the country.
     *
     * @param event value change event
     */
    public void countryChanged(ValueChangeEvent event) {
        FacesContext.getCurrentInstance().getExternalContext().getRequestMap()
                .put("CITY_HACK", new Boolean(
                        true));//Citys can still be set. DON'T Let this happen

        cities = null;


        String newCountry = (String) event.getNewValue();
        if (newCountry.equals("Canada")) {
            cityItems = new SelectItem[]{
                    new SelectItem("Calgary"),
                    new SelectItem("Vancouver"),
                    new SelectItem("Toronto"),
                    new SelectItem("Montreal"),
                    new SelectItem("Ottawa"),
            };
        } else if (newCountry.equals("United States")) {
            cityItems = new SelectItem[]{
                    new SelectItem("Seattle"),
                    new SelectItem("San Francisco"),
                    new SelectItem("Los Angeles"),
                    new SelectItem("New York"),
                    new SelectItem("Chicago"),
            };
        } else if (newCountry.equals("China")) {
            cityItems = new SelectItem[]{
                    new SelectItem("Beijing"),
                    new SelectItem("Shanghai"),
                    new SelectItem("Canton"),
                    new SelectItem("Shenzhen"),
                    new SelectItem("Hong Kong"),
            };
        } else if (newCountry.equals("United Kingdom")) {
            cityItems = new SelectItem[]{
                    new SelectItem("London"),
                    new SelectItem("Birmingham"),
                    new SelectItem("Edinburgh"),
                    new SelectItem("Liverpool"),
                    new SelectItem("Cardiff"),
            };
        } else if (newCountry.equals("Russia")) {
            cityItems = new SelectItem[]{
                    new SelectItem("Moscow"),
                    new SelectItem("St. Petersburgh"),
                    new SelectItem("Kaliningrad"),
                    new SelectItem("Vladivostok"),
                    new SelectItem("Volgograd"),
            };
        } else {
            cityItems = null;
        }
    }
}
