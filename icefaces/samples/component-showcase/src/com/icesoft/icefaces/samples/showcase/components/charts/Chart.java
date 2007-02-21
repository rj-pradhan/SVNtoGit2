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

/*
 * Chart.java
 *
 * Created on January 30, 2007, 1:16 PM
 */

package com.icesoft.icefaces.samples.showcase.components.charts;

import com.icesoft.faces.component.outputchart.AxisChart;
import com.icesoft.faces.component.outputchart.OutputChart;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Chart {
    
    //The type of chart ie. pie2D, axis, or pie3D
    public static String type;
    
    //The data used by the chart
    public static List data;
    
    //The labels used by the chart
    public static List labels;
    
    //Variable for axis chart specific data
    public static final List AXIS_DATA  = AxisChartBean.areaData;
    
    //Variable for axis chart specific labels
    public static final List AXIS_LABELS = AxisChartBean.areaXaxisLabels;
    
    
    public Chart(String type) {
        
        this.type = type;
        
        //sets the data for the chart depending on the type of type
        if(type.equals(OutputChart.PIE2D_CHART_TYPE)||type.equals(OutputChart.PIE3D_CHART_TYPE)) {
            //PieChartBean.buildSales();
            data = PieChartBean.data;
            labels = PieChartBean.labels;
            
        } else {
            data = AXIS_DATA;
            labels = AXIS_LABELS;
        }
        
    }
    public Chart(){
    }
    
    public String getType() {
        return type;
    }
    
    public List getData() {
        return data;
    }
    
    public List getLabels() {
        return labels;
    }
    
    public void setType(String type) {
        this.type = type;
        
        if(type.equals(OutputChart.PIE2D_CHART_TYPE)||type.equals(OutputChart.PIE3D_CHART_TYPE)) {
            
            PieChartBean.buildSales();
            
            data = PieChartBean.data;
            labels = PieChartBean.labels;
            
        } else {
            data = AXIS_DATA;
            labels = AXIS_LABELS;
            
        }
    }
    
}
