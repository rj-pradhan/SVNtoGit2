/*
 * ChartFactory.java
 *
 * Created on January 24, 2007, 10:13 AM
 *
 * This class is the governing class for the chart component in the Component Showcase.
 *It is used to set up the types of charts, create the charts, and manage the actions of the different charts.
 * 
 */

package com.icesoft.icefaces.samples.showcase.components.charts;

import com.icesoft.faces.component.outputchart.AxisChart;
import com.icesoft.faces.component.outputchart.OutputChart;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

/**
 *
 * @author dnorthcott
 *@version 1.0
 */
public class ChartFactory {
    
    private SelectItem[] chartList = null;
    private boolean pie = false;
    private boolean axis = true;
    public boolean pie3D = false;
    private boolean chartChangedFlag = true;
    
    private String wasChanged;
    
    private static final String DEFAULT_STRING =
            "Click on the image map below to display a chart value: ";
    
    private Highlight effectOutputText;
    
    private String chart = OutputChart.BAR_CHART_TYPE;
   
    private String clickedValue = DEFAULT_STRING;
    
    /*
     *Sets the list of charts to choose from
    */
    
    public SelectItem[] getChartList() {
        if (chartList == null) {
            chartList = new SelectItem[9];
            chartList[0] = new SelectItem(OutputChart.AREA_CHART_TYPE);
            chartList[1] = new SelectItem(OutputChart.AREA_STACKED_CHART_TYPE);
            chartList[2] = new SelectItem(OutputChart.BAR_CHART_TYPE);
            chartList[3] = new SelectItem(OutputChart.BAR_CLUSTERED_CHART_TYPE);
            chartList[4] = new SelectItem(OutputChart.BAR_STACKED_CHART_TYPE);
            chartList[5] = new SelectItem(OutputChart.LINE_CHART_TYPE);
            chartList[6] = new SelectItem(OutputChart.POINT_CHART_TYPE);
            chartList[7] = new SelectItem(OutputChart.PIE2D_CHART_TYPE);
            chartList[8] = new SelectItem(OutputChart.PIE3D_CHART_TYPE);
        }
        return chartList;
    }
    
    /*
     *sets the chartList
     */
    public void setChartList(SelectItem[] items) {
        this.chartList = items;
    }
    
    /*
     *sets the isAxis Boolean
     *@param boolean isAxis
     */
    public boolean isAxis() {
        return axis;
    }
    
    /*
     *returns whether the grap is a 3D Pie
     *@return boolean
     */
    public boolean isPie3D() {
        return pie3D;
    }
    
    /*
     *sets the graph to a 3D pie
     *@param boolean pie3D
     */
    public void setPie3D(boolean pie3D) {
        this.pie3D = pie3D;
    }
    
    /*
     *returns whether the graph is a pie chart(2d)
     *@return boolean
     */
    public boolean isPie() {
        return pie;
    }
    
    /*
     *sets the graph to an axis type
     *@param boolean axis
     */
    public void setAxis(boolean axis) {
        this.axis = axis;
    }
    
    /*
     *sets the graph to a 2dPie type
     *@param boolean pie
     */
    public void setPie(boolean pie) {
        this.pie = pie;
    }
    
    
    
    /*
     *returns the chart String
     *@return String chart
     */
    public String getChart() {
       
        return chart;
    }
    
    /*
     *sets the chart String
     *determines what type of graph it is based on the parameter passed in
     *@param String chart
     */
    public void setChart(String chart) {
        
       
        if(!wasChanged.equals(null)) {
            this.chart = wasChanged;
            wasChanged = null;
        } else {
            this.chart = chart;
        }
        
        if (!chart.equals("pie3D")) {
            pie3D = false;
        }
        if (chart.equals("pie2D") || chart.equals("pie3D")) {
            axis = false;
            if (chart.equals("pie3D")) {
                pie3D = true;
            }
        } else {
            axis = true;
        }
    }
    
    /*
     * determines whether or not the application should render based on status change
     *@return boolean
     */
    public boolean allCharts(OutputChart component) {
        
        
        if (chartChangedFlag) {
            System.out.println(chartChangedFlag);
            chartChangedFlag = false;
            return true;
        } else {
            System.out.println(chartChangedFlag);
            return chartChangedFlag;
        }
    }
    
    
    /*
     *determines whether the chart was changed or not
     *sets the default_string if the area type charts are selected
     *@param ValueChangeEvent event
     */
    public void chartChanged(ValueChangeEvent event) {
        
        chartChangedFlag = true;
        wasChanged = (String)event.getNewValue();
        
        if (event.getNewValue().equals("area") ||
                event.getNewValue().equals("areastacked")) {
            setClickedValue(
                    "A client side image map is not supported for Area charts (clicking on the chart will not display any values)");
        } else {
            setClickedValue(DEFAULT_STRING);
        }
    }
    
   
    
    /*
     *returnsd the clickedValue
     *@return String clickedValue
     */
    public String getClickedValue() {
       
        return clickedValue;
    }
    
    
    /*
     *returns the text effect
     *@return Effect EffectOutputText
     */
    public Effect getEffectOutputText() {
        return effectOutputText;
    }
    
    /*
     *sets the output text effect
     *@param Effect effectOutputText
     */
    public void setEffectOutputText(Effect effectOutputText) {
        this.effectOutputText = (Highlight) effectOutputText;
    }
    
    /*
     *sets the clicked value
     *@param String clickedValue
     */
    public void setClickedValue(String clickedValue) {
       
        this.clickedValue = clickedValue;
    }
    
    /*
     *when the image map has been clicked this method returns the value
     *@param ActionEvent event
     */
    public void imageClicked(ActionEvent event) {
        
        if (event.getSource() instanceof OutputChart) {
            OutputChart charte = (OutputChart) event.getSource();
            if (charte.getClickedImageMapArea().getXAxisLabel() != null) {
                
                setClickedValue(DEFAULT_STRING +
                        charte.getClickedImageMapArea().getXAxisLabel() +
                        "  :  " +
                        charte.getClickedImageMapArea().getValue());
                
                effectOutputText = new Highlight("#ffff99");
            }
            else{System.out.println("in imageClicked - mapArea.getXAxisLabel=null");}
        }
        else{System.out.println("get source !instance of outputchart");}
    }
    
}
