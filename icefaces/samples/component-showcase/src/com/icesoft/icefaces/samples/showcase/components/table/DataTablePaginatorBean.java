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

package com.icesoft.icefaces.samples.showcase.components.table;

import com.icesoft.icefaces.samples.showcase.common.Person;

import java.util.Arrays;
import java.util.Comparator;

/**
 * <p>The DataTablePaginatorBean Class is a backing bean for the
 * dataTablePaginator showcase demonstration and is used to store, add, or
 * remove data to the data table. </p>
 *
 * @since 0.3.0
 */
public class DataTablePaginatorBean extends SortableList {

    // table of person data


    public static Person[] buildPersonList() {
        Person[] personsList = new Person[]{
                new Person("Mary", "Smith", "555-2629",
                           "mary.smith@icesoft.com"),
                new Person("James", "Johnson", "555-3318",
                           "james.johnson@icesoft.com"),
                new Person("Patricia", "Williams", "555-3702",
                           "patricia.williams@icesoft.com"),
                new Person("John", "Jones", "555-6589",
                           "john.jones@icesoft.com"),
                new Person("Linda", "Brown", "555-4736",
                           "linda.brown@icesoft.com"),
                new Person("Robert", "Davis", "555-9732",
                           "robert.davis@icesoft.com"),
                new Person("Barbara", "Miller", "555-4660",
                           "barbara.miller@icesoft.com"),
                new Person("Michael", "Wilson", "555-1236",
                           "michael.wilson@icesoft.com"),
                new Person("Elizabeth", "Moore", "555-6653",
                           "elizabeth.moore@icesoft.com"),
                new Person("William", "Taylor", "555-1481",
                           "william.taylor@icesoft.com"),
                new Person("David", "Garcia", "555-1717",
                           "david.garcia@icesoft.com"),
                new Person("Maria", "Jackson", "555-8414",
                           "maria.jackson@icesoft.com"),
                new Person("Richard", "White", "555-1887",
                           "richard.white@icesoft.com"),
                new Person("Susan", "Harris", "555-9209",
                           "susan.harris@icesoft.com"),
                new Person("Charles", "Thompson", "555-2040",
                           "charles.thompson@icesoft.com"),
                new Person("Margaret", "Martinez", "555-9976",
                           "margaret.martinez@icesoft.com"),
                new Person("Edward", "Phillips", "555-1325",
                           "edward.phillips@icesoft.com"),
        };
        return personsList;
    }

    private Person[] persons = buildPersonList();

    private String paginatorLayout = "hor";

    /**
     *
     */
    public DataTablePaginatorBean() {
        super("lastName");
    }

    /**
     * Gets the data paginator layout.
     *
     * @return the data paginator layout
     */
    public String getPaginatorLayout() {
        return paginatorLayout;
    }

    /**
     * Sets the data paginator layout.
     *
     * @param paginatorLayout the data paginator layout
     */
    public void setPaginatorLayout(String paginatorLayout) {
        this.paginatorLayout = paginatorLayout;
    }

    /**
     * Determines if the data paginator layout is vertical.
     *
     * @return the status of the data paginator layout
     */
    public boolean isVertical() {
        return (paginatorLayout.equalsIgnoreCase("ver"));
    }

    /**
     * Gets the person data.
     *
     * @return table of person data
     */
    public Person[] getPersons() {
        return persons;
    }

    /**
     * Gets the sorted person data.
     *
     * @return table of sorted person data
     */
    public Person[] getSortedPersons() {
        sort(getSort(), isAscending());
        return persons;
    }

    /**
     * Determines the sort order.
     *
     * @param sortColumn to sort by.
     * @return whether sort order is ascending or descending.
     */
    protected boolean isDefaultAscending(String sortColumn) {
        return true;
    }

    /**
     * Sorts the list of person data.
     */
    protected void sort(final String column, final boolean ascending) {
        Comparator comparator = new Comparator() {
            public int compare(Object o1, Object o2) {
                Person c1 = (Person) o1;
                Person c2 = (Person) o2;
                if (column == null) {
                    return 0;
                }
                if (column.equals("firstName")) {
                    return ascending ?
                           c1.getFirstName().compareTo(c2.getFirstName()) :
                           c2.getFirstName().compareTo(c1.getFirstName());
                } else if (column.equals("lastName")) {
                    return ascending ?
                           c1.getLastName().compareTo(c2.getLastName()) :
                           c2.getLastName().compareTo(c1.getLastName());
                } else if (column.equals("phoneNo")) {
                    return ascending ?
                           c1.getPhoneNo().compareTo(c2.getPhoneNo()) :
                           c2.getPhoneNo().compareTo(c1.getPhoneNo());
                } else if (column.equals("email")) {
                    return ascending ? c1.getEmail().compareTo(c2.getEmail()) :
                           c2.getEmail().compareTo(c1.getEmail());
                } else return 0;
            }
        };
        Arrays.sort(persons, comparator);
    }

    /**
     * Dynamically adds data to the table.
     */
    public void addContent() {
        if (persons.length < 300) {
            int dataLength = persons.length;
            Person[] newData = new Person[persons.length + 5];
            for (int i = 0, j = 0; i < newData.length; i++, j++) {
                if (j >= dataLength) {
                    j = 0;
                }
                newData[i] = persons[j];
            }
            persons = newData;
        }
    }

    /**
     * Dynamically removes data from the table.
     */
    public void removeContent() {

        if (persons.length > 5) {
            int dataLength = persons.length;

            Person[] newData = new Person[persons.length - 5];
            // copy original data
            System.arraycopy(persons, 0, newData, 0, dataLength - 5);
            persons = newData;
        }
    }


}