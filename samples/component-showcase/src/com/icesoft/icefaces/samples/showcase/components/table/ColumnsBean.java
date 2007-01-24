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

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import java.util.ArrayList;

/**
 * The ColumnsBean object generates ASCII data for the table columns example.
 *
 * @since 1.5
 */
public class ColumnsBean {
    // row columna data map
    private DataModel columnDataModel;
    private DataModel rowDataModel;

    // ascii index
    public static final int ASCII_START = 33;
    public static final int ASCII_END = 126;
    public static final int ASCII_RANGE = ASCII_END - ASCII_START;
    private static final AsciiData[] asciiData = new AsciiData[ASCII_RANGE];

    // we only need to initialized the ascii array once.
    private static boolean isInit;

    // default column and row values 
    private int columns = 5;
    private int rows = 0;  // ASCII_RANGE / 5 columns.
    
    //2D array to save the table values to.
    private String table[][];
    
    private static final int ROW_CONSTANT = 13;
    
    public ColumnsBean() {

        // calulate rows
        calculateRows();

        // generate some default data.
        init();
        updateTableColumns(null);
    }

    /**
     * Initialize an array of ASCII values which we will display differently
     * depending on the number of columns specified by the user.
     */
    private synchronized void init() {
        if (isInit) {
            return;
        }
        isInit = true;

        // build the asic data set
        AsciiData tmp;
        int index;
        for (int i = 0; i < ASCII_RANGE ; i++) {
            tmp = new AsciiData();
            index = ASCII_START + i;
            tmp.setIndex(index);
            tmp.setIndexChar((char) index);
            tmp.setIndexHex(Integer.toHexString(index));
            asciiData[i] = tmp;
        }
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public DataModel getRowDataModel() {
        return rowDataModel;
    }

    public DataModel getColumnDataModel() {
        return columnDataModel;
    }

    /**
     * Called by the table interator.  This method reads the column and row data
     * models and displays the correct cell value.
     *
     * @return data which should be displayed for the given model state.
     */
    public String getCellValue() {
        if (rowDataModel.isRowAvailable() &&
            columnDataModel.isRowAvailable()) {

            // get the index of the row and column that this method is being
            // called for
            int row = rowDataModel.getRowIndex();
            int col = columnDataModel.getRowIndex();
           
            //return the element at the specified column and row
            return table[col][row];
        }
        // empty field.
        return "-";
    }


    private void  calculateRows(){
        // calculate the number of columns.
        rows = ASCII_END / columns;

        // make an extra row if there is a modulus
        if ((ASCII_END % columns) != 0) {
            rows += 1;

        }
    }

    /**
     * Updates the table model data.
     *
     * @param event property change event which specifies whether or not the the
     *              column count has changed.
     */
    public void updateTableColumns(ValueChangeEvent event) {
        if (event != null && event.getNewValue() != null &&
            event.getNewValue() instanceof Integer) {
            // get the new column count
            columns = ((Integer) event.getNewValue()).intValue();
        }
        int numberOfRows = columns * ROW_CONSTANT;
              
        ArrayList columnList = new ArrayList();
        ArrayList rowList = new ArrayList();
        
        table = new String[columns][numberOfRows];
        String s;
        Integer index;
        
        for(int i=0;i<columns;i++)
        {
            for(int j=0;j<numberOfRows;j++)
            {
                s = getChar(j);
                table[i][j] = s;
                rowList.add(s);
             }
            index = new Integer(i);
            columnList.add(index);
        }
        
        rowDataModel = new ListDataModel(rowList);
        columnDataModel = new ListDataModel(columnList);
       
    }

   private String getChar(int i){
        i += 65;
        String r = "" + (char)i;
        return r;
    }

    /**
     * Utility class to store index, char and hex data.  Used to store
     * table cell data for this example. 
     */
    public class AsciiData {
        private int index;
        private char indexChar;
        private String indexHex = " ";


        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public char getIndexChar() {
            return indexChar;
        }

        public char getIndexCharString() {
            // only show ASCII values after char 32 (space).
            if (indexChar > 32)
                return indexChar;
            else {
                return ' ';
            }
        }

        public void setIndexChar(char indexChar) {
            this.indexChar = indexChar;
        }

        public String getIndexHex() {
            return indexHex;
        }

        public void setIndexHex(String indexHex) {
            this.indexHex = indexHex;
        }

        public String toString() {
            return index + "  " + indexChar + "  " + indexHex;
        }
    }

}

