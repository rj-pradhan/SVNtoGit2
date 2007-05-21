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

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The PieChartBean is responsible for holding all the backing information and
 * data for the pie chart
 *
 * @since 1.5
 */
public class PieChartBean extends Chart{
    
    //list of labels for the chart
    protected static List labels = new ArrayList();
    
    //list of the data used by the chart
    protected static List data = new ArrayList();
    
    //list of the colors used in the pie chart
    private List paints;
    
    //all sales
    private static List allSales = new ArrayList();
    
    //list of the saled data from the sales class
    private static final List sales = buildSales();
        
    //a map of the sales data
    private static Map salesMap;
    
    //
    private static String clickedAreaValue = ChartMediator.DEFAULT_STRING;
    
    //a temporary string for the current label
    private String label;
    
    private float value;
    
    //flag to determine if the chart is a 3D pie
    public static boolean is3D = false;
    
    //flag to determine if the graph needs rendering
    private boolean pieNeedsRendering = false;
    
    //the temporary value for the selected color
    private Color selectedColor;
    
    //the highlight effect for when the value selected is changed
    private static Effect effectOutputText = new Highlight("#ffff99");
    
    //index to delete from
    int deletInex = 0;
    
    //list of items to delete
    private List deleteList = new ArrayList();
    
    
    
    
    public PieChartBean(){
        super();
    }
    
    
    
    
    /**
     * Method to build the sales list and create the chart using the data from
     * the sales class
     *
     * @return list of sales items for charting.
     */
    public static List buildSales() {
        ArrayList salesTemp = new ArrayList();
        
        salesMap = Sales.getSales();
        
        Iterator it = salesMap.values().iterator();
        double price;
        String label;
        while (it.hasNext()) {
            
            Sales[] yearSale = (Sales[]) it.next();
            price = 0;
            label = "";
            for (int i = 0; i < yearSale.length; i++) {
                price += (yearSale[i]).getPrice();
                label = (yearSale[i]).getYear();
                salesTemp.add(yearSale[i]);
                allSales.add(yearSale[i]);
           
            }
            labels.add(label);
            data.add(new Double(price));
            
        }
        return salesTemp;
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
    
    
    
    
    
    public String getClickedAreaValue() {
        return clickedAreaValue;
    }
    
    public void setClickedAreaValue(String clickedAreaValue) {
        this.clickedAreaValue = clickedAreaValue;
    }
    
    
    public Effect getEffectOutputText() {
        return effectOutputText;
    }
    
    public void setEffectOutputText(Effect effectOutputText) {
        this.effectOutputText = effectOutputText;
    }
    
    
    
    /**
     *Method to set the displayed table data to that corresponding
     *with the year clicked
     *@param String year clicked
     */
    public static void setSalesForYear(String year) {
        sales.clear();
        Sales[] yearSales = (Sales[]) salesMap.get(year);
        for (int i = 0; i < yearSales.length; i++) {
            sales.add(yearSales[i]);
        }
    }
    
    public List getSales() {
       return type.equalsIgnoreCase("pie3d") ? allSales : sales;
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
