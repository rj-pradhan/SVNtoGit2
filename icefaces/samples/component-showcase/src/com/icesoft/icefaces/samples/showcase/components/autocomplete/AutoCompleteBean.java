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

package com.icesoft.icefaces.samples.showcase.components.autocomplete;

import com.icesoft.faces.component.selectinputtext.SelectInputText;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;

/**
 * Stores the values picked from the AutoCompleteDictionary (different scope to
 * avoid memory hole). The showcase stores one static copy of the actual
 * dictionary information.
 *
 * @see AutoCompleteDictionary
 */
@Name("autoCompleteBean")
public class AutoCompleteBean implements AutoCompleteLocal{
    
    @In
    private AutoCompleteDictionary acd;
    
    
        
    
    private static Log log = LogFactory.getLog(AutoCompleteBean.class);

    // list of cities, used for auto complete list.
    private static List dictionary = null;


    // default city, no value.
    private City currentCity = new City("", "", "", "", "", "", 0);

    // list of possible matches.
    private List matchesList = new ArrayList();

    /**
     * Called when a user has modifed the SelectInputText value.  This method
     * call causes the match list to be updated.
     *
     * @param event
     */
    public void updateList(ValueChangeEvent event) {

        // get a new list of matches.
        setMatches(event);

        // Get the auto complete component from the event and assing
        if (event.getComponent() instanceof SelectInputText) {
            SelectInputText autoComplete =
                    (SelectInputText) event.getComponent();
            // if no selected item then return the previously selected item.
            if (autoComplete.getSelectedItem() != null) {
                currentCity = (City) autoComplete.getSelectedItem().getValue();
            }
            // otherwise if there is a selected item get the value from the match list
            else {
                City tempCity = getMatch(autoComplete.getValue().toString());
                if (tempCity != null) {
                    currentCity = tempCity;
                }
            }
        }
    }

    /**
     * Gets the currently selected city.
     *
     * @return selected city.
     */
    public City getCurrentCity() {
        System.out.println("----- Getting current city");
        return currentCity;
    }

    /**
     * The list of possible matches for the given SelectInputText value
     *
     * @return list of possible matches.
     */
    public List getList() {
        System.out.println(acd);
        
        if(dictionary == null)
        {
            
            dictionary = acd.getDictionary();
            System.out.println("---- Dictionary size: " + dictionary.size());
        }
        return matchesList;
    }

    private City getMatch(String value) {
        City result = null;
        if (matchesList != null) {
            SelectItem si;
            Iterator iter = matchesList.iterator();
            while (iter.hasNext()) {
                si = (SelectItem) iter.next();
                if (value.equals(si.getLabel())) {
                    result = (City) si.getValue();
                }
            }
        }
        return result;
    }


//    public List getDictionary() {
//        return dictionary;
//    }
//
//    public void setDictionary(List dictionary) {
//        AutoCompleteBean.dictionary = dictionary;
//    }

    /**
     * Utility method for building the match list given the current value of the
     * SelectInputText component.
     *
     * @param event
     */
    private void setMatches(ValueChangeEvent event) {

        Object searchWord = event.getNewValue();
        int maxMatches = ((SelectInputText) event.getComponent()).getRows();
        List matchList = new ArrayList(maxMatches);

        try {

            int insert = Collections.binarySearch(dictionary, searchWord,
                                                  AutoCompleteDictionary.LABEL_COMPARATOR);

            // less then zero if wer have a partial match
            if (insert < 0) {
                insert = Math.abs(insert) - 1;
            }

            for (int i = 0; i < maxMatches; i++) {
                // quit the match list creation if the index is larger then
                // max entries in the dictionary if we have added maxMatches.
                if ((insert + i) >= dictionary.size() ||
                    i >= maxMatches) {
                    break;
                }
                matchList.add(dictionary.get(insert + i));
            }
        } catch (Throwable e) {
            log.error("Erorr finding autocomplete matches", e);
        }
        // assign new matchList
        if (this.matchesList != null) {
            this.matchesList.clear();
            this.matchesList = null;
        }
        this.matchesList = matchList;
    }
}