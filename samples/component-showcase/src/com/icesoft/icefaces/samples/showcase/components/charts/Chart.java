/*
 * Chart.java
 *
 * Created on January 30, 2007, 1:16 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.icesoft.icefaces.samples.showcase.components.charts;

import com.icesoft.faces.component.outputchart.AxisChart;
import com.icesoft.faces.component.outputchart.OutputChart;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author dnorthcott
 */
public class Chart {
    
    
    
    
    public static String type;
    
    public static List data;
    
    public static List labels;
    
    public static final List AXIS_DATA  = AxisChartBean.areaData;
    
    public static final List AXIS_LABELS = AxisChartBean.areaXaxisLabels; 
    
    /** Creates a new instance of Chart */
    public Chart(String type) {
        
        this.type = type;
        
        
        if(type.equals(OutputChart.PIE2D_CHART_TYPE)||type.equals(OutputChart.PIE3D_CHART_TYPE))
        {
            //PieChartBean.buildSales();
            data = PieChartBean.data;
            labels = PieChartBean.labels;
            
            
        }
        else
        {
            data = AXIS_DATA;
            labels = AXIS_LABELS;
        }
        
    }
    public Chart(){}
    
    public String getType()
    {
        return type;
    }
    
    public List getData()
    {
        return data;
    }
    
    public List getLabels()
    {
        return labels;
    }
    
    public void setType(String type)
    {
        this.type = type;
     
        if(type.equals(OutputChart.PIE2D_CHART_TYPE)||type.equals(OutputChart.PIE3D_CHART_TYPE))
        {
            
            
            PieChartBean.buildSales();
            
           
            
            data = PieChartBean.data;
            labels = PieChartBean.labels;
          
            
            
        }
        else
        {
            data = AXIS_DATA;
            labels = AXIS_LABELS;
            
        }
    }
    
}
