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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The PieChartBean is responsible for
 *
 * @since 1.5
 */
public class PieChartBean {

    private List labels = new ArrayList();
    private List data = new ArrayList();
    private List paints = new ArrayList();

    private List sales;
    private Map salesMap;
    
    public static boolean is3D = false;

    private static final String DEFAULT_STRING =
            "Click on the image map below to display a chart value: ";

    public PieChartBean() {

        availablePaints[0] = new SelectItem("black", "Black");
        availablePaints[1] = new SelectItem("blue", "Blue");
        availablePaints[2] = new SelectItem("cyan", "Cyan");
        availablePaints[3] = new SelectItem("darkGray", "Dark Gray");
        availablePaints[4] = new SelectItem("gray", "Gray");
        availablePaints[5] = new SelectItem("green", "Green");
        availablePaints[6] = new SelectItem("red", "Red");
        availablePaints[7] = new SelectItem("lightGray", "Light Gray");
        availablePaints[8] = new SelectItem("magenta", "Magenta");
        availablePaints[9] = new SelectItem("orange", "Orange");
        availablePaints[10] = new SelectItem("pink", "Pink");
        availablePaints[11] = new SelectItem("red", "Red");
        availablePaints[12] = new SelectItem("white", "White");
        availablePaints[13] = new SelectItem("yellow", "Yellow");

        paints.add(Color.YELLOW);
        paints.add(Color.RED);
        paints.add(Color.BLUE);
        paints.add(Color.PINK);

        sales = new ArrayList();
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
                sales.add(yearSale[i]);
            }
            labels.add(label);
            data.add(new Double(price));
        }
    }

    private boolean pieNeedsRendering = false;

    public boolean renderOnSubmit(OutputChart component) {
        if (pieNeedsRendering) {
            pieNeedsRendering = false;
            return true;
        } else {
            return false;
        }
    }
    
    public static void setIs3D(boolean i3D)
    {
        is3D = i3D;
    }
    
    public static boolean is3D()
    {
        return is3D;
    }
    

    private SelectItem[] availablePaints = new SelectItem[14];

    public SelectItem[] getAvailablePaints() {
        return availablePaints;
    }

    public void setAvailablePaints(SelectItem[] availablePaints) {
        this.availablePaints = availablePaints;
    }

    private Color selectedColor;

    public void paintChangeListener(ValueChangeEvent event) {
        if (event.getNewValue() != null) {
            selectedColor =
                    AbstractChart.getColor(event.getNewValue().toString());
        }
    }

    private String label;
    private float value;

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

    public void addChart(ActionEvent event) {
        paints.add(selectedColor);
        labels.add(label);
        data.add(new Double(value));
        Sales[] newEntry = {new Sales(1, "New Product", label)};
        salesMap.put(label, newEntry);
        pieNeedsRendering = true;
    }

    private List deleteList = new ArrayList();

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

    int deletInex = 0;

    public void deleteListValueChangeListener(ValueChangeEvent event) {
        if (event.getNewValue() != null) {
            deletInex = Integer.parseInt(event.getNewValue().toString());
        }
    }

    public void deleteChart(ActionEvent event) {
        if (deletInex >= 0 && labels.size() > 1) {
            labels.remove(deletInex);
            data.remove(deletInex);
            paints.remove(deletInex);
            pieNeedsRendering = true;
        }
    }

    private String clickedAreaValue = DEFAULT_STRING;

    public String getClickedAreaValue() {
        return clickedAreaValue;
    }

    public void setClickedAreaValue(String clickedAreaValue) {
        this.clickedAreaValue = clickedAreaValue;
    }

    private Effect effectOutputText;

    public Effect getEffectOutputText() {
        return effectOutputText;
    }

    public void setEffectOutputText(Effect effectOutputText) {
        this.effectOutputText = effectOutputText;
    }

    public void action(ActionEvent event) {
        if (event.getSource() instanceof OutputChart) {
            OutputChart chart = (OutputChart) event.getSource();
            if (chart.getClickedImageMapArea().getLengendLabel() != null) {
                setClickedAreaValue(DEFAULT_STRING + chart
                        .getClickedImageMapArea().getLengendLabel() + " : " +
                                                                    chart.getClickedImageMapArea()
                                                                            .getValue());
                setSalesForYear(
                        chart.getClickedImageMapArea().getLengendLabel());
                effectOutputText = new Highlight("#ffff99");
            }
        }
    }

    private void setSalesForYear(String year) {
        sales.clear();
        Sales[] yearSales = (Sales[]) salesMap.get(year);
        for (int i = 0; i < yearSales.length; i++) {
            sales.add(yearSales[i]);
        }
    }

    public List getSales() {
        return sales;
    }


    public void setSales(List sales) {
        this.sales = sales;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    public List getLabels() {
        return labels;
    }

    public void setLabels(List labels) {
        this.labels = labels;
    }

    public List getPaints() {
        return paints;
    }

    public void setPaints(List paints) {
        this.paints = paints;
    }
}
