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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;
import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


/**
 * Application-scope bean used to store static lookup information for
 * AutoComplete (selectInputText) example. Statically referenced by
 * AutoCompleteBean as the dictionary is rather large.
 *
 * @see AutoCompleteBean
 */
public class AutoCompleteDictionary {

    private static Log log = LogFactory.getLog(AutoCompleteDictionary.class);

    // list of cities.
    private static List dictionary;

    public AutoCompleteDictionary() {
        // initialize the ditionary
        try {
            log.info("initializing dictionary");
            init();
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error initializtin sorting list");
            }
        }
    }

    /**
     * Comparator utility for sorting city names.
     */
    public static final Comparator LABEL_COMPARATOR = new Comparator() {
        String s1;
        String s2;

        // compare method for city entries.
        public int compare(Object o1, Object o2) {

            if (o1 instanceof SelectItem) {
                s1 = ((SelectItem) o1).getLabel();
            } else {
                s1 = o1.toString();
            }

            if (o2 instanceof SelectItem) {
                s2 = ((SelectItem) o2).getLabel();
            } else {
                s2 = o2.toString();
            }
            // compare ingnoring case, give the user a more automated feel when typing
            return s1.compareToIgnoreCase(s2);
        }
    };

    /**
     * Gets the dictionary of cities.
     *
     * @return dictionary list in sorted by city name, ascending.
     */
    public List getDictionary() {
        return dictionary;
    }

    private static void init() {
        // Raw list of xml cities.
        List cityList = null;

        // load the city dictionary from the compressed xml file.

        // get the path of the compressed file
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().
                getExternalContext().getSession(true);
        String basePath =
                session.getServletContext().getRealPath("/WEB-INF/resources");
        basePath += "/city.xml.zip";

        // extract the file
        ZipEntry zipEntry;
        ZipFile zipFile;
        try {
            zipFile = new ZipFile(basePath);
            zipEntry = zipFile.getEntry("city.xml");
        }
        catch (Exception e) {
            log.error("Error retrieving records", e);
            return;
        }

        // get the xml stream and decode it.
        if (zipFile.size() > 0 && zipEntry != null) {
            try {
                BufferedInputStream dictionaryStream =
                        new BufferedInputStream(
                                zipFile.getInputStream(zipEntry));
                XMLDecoder xDecoder = new XMLDecoder(dictionaryStream);
                // get the city list.
                cityList = (List) xDecoder.readObject();
                dictionaryStream.close();
                zipFile.close();
                xDecoder.close();
            } catch (ArrayIndexOutOfBoundsException e) {
                log.error("Error getting city list, not city objects", e);
                return;
            } catch (IOException e) {
                log.error("Error getting city list", e);
                return;
            }
        }

        // Finally load the object from the xml file.
        if (cityList != null) {
            dictionary = new ArrayList(cityList.size());
            City tmpCity;
            for (int i = 0, max = cityList.size(); i < max; i++) {
                tmpCity = (City) cityList.get(i);
                if (tmpCity != null && tmpCity.getCity() != null) {
                    dictionary.add(new SelectItem(tmpCity, tmpCity.getCity()));
                }
            }
            cityList.clear();
            // finally sort the list
            Collections.sort(dictionary, LABEL_COMPARATOR);
        }

    }
}