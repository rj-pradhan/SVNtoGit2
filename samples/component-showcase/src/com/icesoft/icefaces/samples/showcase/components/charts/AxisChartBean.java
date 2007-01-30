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


import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Axis Chart Bean
 * The backend bean that supplies all the data for the axis chart
 *
 */
public class AxisChartBean {

   //list of the Labels for the x axis of the chart
    private static final List areaXaxisLabels = new ArrayList(Arrays.asList(
            "2000", 
            "2001",
            "2002",
            "2003",
            "2004",
            "2005",
            "2006"));
    
    //The list of the legend label for the chart
    private static final List legendLabels = new ArrayList(Arrays.asList("Bugs","Enhancements","Fixed"));
   
    //The list of the data used by the chart
    private static final List areaData = new ArrayList(Arrays.asList(new double[]{350, 50, 400},
                                                                new double[]{45, 145, 50},
                                                                new double[]{-36, 6, 98},
                                                                new double[]{66, 166, 74},
                                                                new double[]{145, 105, 55},
                                                                new double[]{80, 110, 4},
                                                                new double[]{10, 90, 70}));
    
   //The list of the colors used by the chart
    private static final List areaPaints = new ArrayList(Arrays.asList(new Color(153, 0, 255, 100),
                                                                new Color(204, 0, 255, 150),
                                                                new Color(204, 0, 1, 150)));

    

    public List getAreaXaxisLabels() {
       
        return areaXaxisLabels;
    }

  

    public List getLegendLabels() {
       
        return legendLabels;
    }

   

    public List getAreaData() {
       
        return areaData;
    }


   

    public List getAreaPaints() {
       
        return areaPaints;
    }


   




}
