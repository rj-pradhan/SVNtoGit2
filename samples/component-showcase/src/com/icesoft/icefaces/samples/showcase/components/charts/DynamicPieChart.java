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

package com.icesoft.icefaces.samples.showcase.components.charts;

import com.icesoft.faces.component.outputchart.AbstractChart;
import com.icesoft.faces.component.outputchart.OutputChart;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;
import java.util.Arrays;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The DynamicPieChartBean is responsible for holding all the backing information and
 * data for the dynamic pie chart
 *
 * @since 1.5
 */
public class DynamicPieChart{
    
    //list of labels for the chart
    public static List labels = new ArrayList();
    
    //list of the data used by the chart
    public static List data = new ArrayList();
    
    
    //list of the colors used in the pie chart
    private static List paints = new ArrayList();
    
    
    //a map of the sales data
    private static Map salesMap;
    
    
    //a temporary string for the current label
    private String label;
    
    private float value;
    
    //flag to determine if the chart is a 3D pie
    public static boolean is3D = false;
    
    //flag to determine if the graph needs rendering
    private boolean pieNeedsRendering = false;
    
    //the temporary value for the selected color
    private Color selectedColor;
    
    
    
    //index to delete from
    int deletInex = 0;
    
    //list of items to delete
    private List deleteList = new ArrayList();
    
    
    //array of the available paints used in the chart
    public static final SelectItem[] availablePaints = new SelectItem[]{
        
        
        new SelectItem("black", "Black"),
        new SelectItem("blue", "Blue"),
        new SelectItem("cyan", "Cyan"),
        new SelectItem("darkGray", "Dark Gray"),
        new SelectItem("gray", "Gray"),
        new SelectItem("green", "Green"),
        new SelectItem("red", "Red"),
        new SelectItem("lightGray", "Light Gray"),
        new SelectItem("magenta", "Magenta"),
        new SelectItem("orange", "Orange"),
        new SelectItem("pink", "Pink"),
        new SelectItem("red", "Red"),
        new SelectItem("white", "White"),
        new SelectItem("yellow", "Yellow") };
    
    
    
    /**
     * Method to build the sales list and create the chart using the data from
     * the sales class
     *
     * @return list of sales items for charting.
     */
    public DynamicPieChart() {
        
        
        salesMap = Sales.getSales();
        Iterator it = salesMap.values().iterator();
        double price;
        String label;
        int r = 3;
        while (it.hasNext()) {
            
            Sales[] yearSale = (Sales[]) it.next();
            price = 0;
            label = "";
            for (int i = 0; i < yearSale.length; i++) {
                price += (yearSale[i]).getPrice();
                label = (yearSale[i]).getYear();
                
            }
            labels.add(label);
            data.add(new Double(price));
            //adds paint from availablePaints list
            paints.add(AbstractChart.getColor((String)availablePaints[r].getValue()));
            r++;
        }
        
    }
    
    
    /**
     * Method to call the rendering of the chart based on the pieNeedsRendering
     * flag
     *
     * @param component chart component which will be rendered.
     *
     * @return boolean true if OutputChart should be re-rendered; otherwise, false.
     */
    public boolean renderOnSubmit(OutputChart component) {
        if (pieNeedsRendering) {
            pieNeedsRendering = false;
            return true;
        } else {
            return false;
        }
    }
    
    public static void setIs3D(boolean i3D) {
        is3D = i3D;
    }
    
    /**
     * Method to return whether chart is 3D or not
     *
     * @return boolean
     */
    public static boolean is3D() {
        return is3D;
    }
    
    
    public SelectItem[] getAvailablePaints() {
        return availablePaints;
    }
    
    
    /**
     * Mehtod to listen for the change in color in the graph
     *
     * @param event JSF value changed event
     */
    public void paintChangeListener(ValueChangeEvent event) {
        if (event.getNewValue() != null) {
            selectedColor =
                    AbstractChart.getColor(event.getNewValue().toString());
        }
    }
    
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        if (null == label || label.length() < 1) {
            label = " ";
        }
        this.label = label;
    }
    
    public float getValue() {
        return value;
    }
    
    public void setValue(float value) {
        this.value = value;
    }
    
    /**
     * Method to add a value and a color to the chart
     *
     * @param event JSF action event.
     */
    public void addToChart(ActionEvent event) {
        
        
        paints.add(selectedColor);
        
        labels.add(label);
        
        data.add(new Double(value));
        
        Sales[] newEntry = {new Sales(1, "New Product", label)};
        
        salesMap.put(label, newEntry);
        
        pieNeedsRendering = true;
        
    }
    
    
    public List getDeleteList() {
        deleteList.clear();
        deleteList.add(new SelectItem("-1", "Select..."));
        for (int i = 0; i < labels.size(); i++) {
            deleteList.add(new SelectItem("" + i, "" + labels.get(i)));
        }
        return deleteList;
    }
    
    public void setDeleteList(List deleteList) {
        this.deleteList = deleteList;
    }
    
    
    /**
     * Method to listen for an action to delete from the chart
     *
     * @param event JSF value changed event
     */
    public void deleteListValueChangeListener(ValueChangeEvent event) {
        if (event.getNewValue() != null) {
            deletInex = Integer.parseInt(event.getNewValue().toString());
        }
    }
    
    /**
     * Method to delete an item from the chart
     *
     * @param event JSF action event
     */
    public void deleteChart(ActionEvent event) {
        if (deletInex >= 0 && labels.size() > 1) {
            labels.remove(deletInex);
            data.remove(deletInex);
            paints.remove(deletInex);
            pieNeedsRendering = true;
        }
    }
    
    
    public List getData() {
        return data;
    }
    
    public List getLabels() {
        
        return labels;
    }
    
    public List getPaints() {
        
        return paints;
    }
    
    public void setPaints(List paints) {
       
        
        this.paints = paints;
       
    }
}

