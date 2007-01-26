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
    private static final SelectItem[] DRINK_ITEMS = new SelectItem[]{
            new SelectItem("Coke"),
            new SelectItem("Pepsi"),
            new SelectItem("Sprite"),
            new SelectItem("7Up"),
    };
    private static final SelectItem[] LANGUAGE_ITEMS = new SelectItem[]{
            new SelectItem("Java"),
            new SelectItem("C#"),
            new SelectItem("C++"),
            new SelectItem("C"),
            new SelectItem("COBOL"),
    };
    private static final SelectItem[] COMPONENT_ITEMS = new SelectItem[]{
            new SelectItem("I/O"),
            new SelectItem("Command"),
            new SelectItem("Selection"),
    };
    private static final SelectItem[] CARS_ITEMS = new SelectItem[]{
            new SelectItem("Batmobile"),
            new SelectItem("A-Team Van"),
            new SelectItem("BMW Z4"),
            new SelectItem("General Lee"),
            new SelectItem("El Chamino"),
    };

    /**
     * Countries and Cities declorations
     */

    private SelectItem[] cityItems;

    private static final String COUNTRY_CANADA = "Canada";
    private static final String COUNTRY_USA = "United States";
    private static final String COUNTRY_CHINA = "China";
    private static final String COUNTRY_UK = "United Kingdom";
    private static final String COUNTRY_RUSSIA = "Russia";

    private static final SelectItem[] COUNTRY_ITEMS = new SelectItem[]{
            new SelectItem(COUNTRY_CANADA),
            new SelectItem(COUNTRY_USA),
            new SelectItem(COUNTRY_CHINA),
            new SelectItem(COUNTRY_UK),
            new SelectItem(COUNTRY_RUSSIA),
    };

    private static final SelectItem[] CITIES_CANADA = new SelectItem[]{
            new SelectItem("Calgary"),
            new SelectItem("Vancouver"),
            new SelectItem("Toronto"),
            new SelectItem("Montreal"),
            new SelectItem("Ottawa"),
            new SelectItem("Sudbury")};

    private static final SelectItem[] CITIES_USA = new SelectItem[]{
            new SelectItem("Seattle"),
            new SelectItem("San Francisco"),
            new SelectItem("Los Angeles"),
            new SelectItem("New York"),
            new SelectItem("Chicago")};

    private static final SelectItem[] CITIES_CHINA = new SelectItem[]{
            new SelectItem("Beijing"),
            new SelectItem("Shanghai"),
            new SelectItem("Canton"),
            new SelectItem("Shenzhen"),
            new SelectItem("Hong Kong")};

    private static final SelectItem[] CITIES_UK = new SelectItem[]{
            new SelectItem("London"),
            new SelectItem("Birmingham"),
            new SelectItem("Edinburgh"),
            new SelectItem("Liverpool"),
            new SelectItem("Cardiff")};

    private static final SelectItem[] CITIES_RUSSIA = new SelectItem[]{
            new SelectItem("Moscow"),
            new SelectItem("St. Petersburgh"),
            new SelectItem("Kaliningrad"),
            new SelectItem("Vladivostok"),
            new SelectItem("Volgograd")};

    // selectOneListbox example value
    private String selectedCountry;
    private boolean countryChange;
    // selectManyListbox example value
    private String[] selectedCities;
    // check box example value
    private boolean newUser;
    // radio button example
    private String selectedDrink;
    // checkbox multiselect languanges example
    private String[] selectedLanguages;
    // selectManyMenu cars values
    private String[] selectedCars;
    // selectOneMenu for components
    private String selectedComponent;


    /**
     * Value change listener for the country change event. Sets up the cities
     * listbox according to the country.
     *
     * @param event value change event
     */
    public void countryChanged(ValueChangeEvent event) {

        // get new city value and assign it. 
        String newCountry = (String) event.getNewValue();

        if (newCountry.equals(COUNTRY_CANADA)) {
            cityItems = CITIES_CANADA;
        } else if (newCountry.equals(COUNTRY_USA)) {
            cityItems = CITIES_USA;
        } else if (newCountry.equals(COUNTRY_CHINA)) {
            cityItems = CITIES_CHINA;
        } else if (newCountry.equals(COUNTRY_UK)) {
            cityItems = CITIES_UK;
        } else if (newCountry.equals(COUNTRY_RUSSIA)) {
            cityItems = CITIES_RUSSIA;
        } else {
            cityItems = null;
        }
        
        // check to see if the country has changed if clear the selected cities
        selectedCities = new String[]{};
        countryChange = true;
        
    }

    public void cityChanged(ValueChangeEvent event) {

    }

    public void carChanged(ValueChangeEvent event) {

    }

    /**
     * Gets the option items for drinks.
     *
     * @return array of drink items
     */
    public SelectItem[] getDrinkItems() {
        return DRINK_ITEMS;
    }

    /**
     * Gets the option items for languages.
     *
     * @return array of language items
     */
    public SelectItem[] getLanguageItems() {
        return LANGUAGE_ITEMS;
    }

    /**
     * Gets the option items for countries.
     *
     * @return array of country items
     */
    public SelectItem[] getCountryItems() {
        return COUNTRY_ITEMS;
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
    public SelectItem[] getComponentItems() {
        return COMPONENT_ITEMS;
    }


    /**
     * returns the list of available cars to select
     *
     * @return carlist
     */
    public SelectItem[] getCarListItems() {
        return CARS_ITEMS;
    }

    /**
     * Gets the newUser property.
     *
     * @return true or false
     */
    public boolean isNewUser() {
        return newUser;
    }

    /**
     * Sets the newUser property.
     *
     * @param newValue true of false
     */
    public void setNewUser(boolean newValue) {
        newUser = newValue;
    }

    /**
     * Gets the selected drink.
     *
     * @return the selected drink
     */
    public String getSelectedDrink() {
        return selectedDrink;
    }

    /**
     * Gets the selected languages.
     *
     * @return the array of selected languages
     */
    public String[] getSelectedLanguages() {
        return selectedLanguages;
    }

    /**
     * Gets the array of selected cars.
     * @return the array of selected cars
     */
    public String[] getSelectedCars() {
        return selectedCars;
    }

    /**
     * Returns the selectedCities array a comma seperated list
     * @return comma seperated list of selected cities.
     */
    public String getSelectedCarsStrings() {
        return convertToString(selectedCars);
    }

    /**
     * Returns the selectedLangues array a comma seperated list
     * @return comma seperated list of selected languages.
     */
    public String getSelectedLanguagesStrings() {
        return convertToString(selectedLanguages);
    }
    
    /**
     * Gets the selected country.
     *
     * @return the selected country
     */
    public String getSelectedCountry() {
        return selectedCountry;
    }

    /**
     * Gets the selected component.
     *
     * @return the selected component
     */
    public String getSelectedComponent() {
        return selectedComponent;
    }

    /**
     * Gets the selected cities.
     *
     * @return array of selected cities
     */
    public String[] getSelectedCities() {
        return selectedCities;
    }

    /**
     * Returns the selectedCities array a comma seperated list
     * @return comma seperated list of selected cities.
     */
    public String getSelectedCitiesStrings() {
        // if the changeEventListener fired then we want to clear the cities list
        if (countryChange){
            countryChange = false;
            return "";
        }
        return convertToString(selectedCities);
    }

    public void setCityItems(SelectItem[] cityItems) {
        this.cityItems = cityItems;
    }

    public void setSelectedCountry(String selectedCountry) {
        this.selectedCountry = selectedCountry;
    }

    public void setSelectedCities(String[] selectedCities) {
        this.selectedCities = selectedCities;
    }

    public void setSelectedDrink(String selectedDrink) {
        this.selectedDrink = selectedDrink;
    }

    public void setSelectedLanguages(String[] selectedLanguages) {
        this.selectedLanguages = selectedLanguages;
    }

    public void setSelectedCars(String[] selectedCars) {
        this.selectedCars = selectedCars;
    }

    public void setSelectedComponent(String selectedComponent) {
        this.selectedComponent = selectedComponent;
    }

    /**
     * Converts string arrays for displays.
     *
     * @param stringArray string array to convert
     * @return a string concatenating the elements of the string array
     */
    private static String convertToString(String[] stringArray) {
        if (stringArray == null) {
            return "";
        }
        StringBuffer itemBuffer = new StringBuffer();
        for (int i = 0, max = stringArray.length; i < max; i++) {
            if (i > 0) {
                itemBuffer.append(" , ");
            }
            itemBuffer.append(stringArray[i]);
        }
        return itemBuffer.toString();
    }
}
