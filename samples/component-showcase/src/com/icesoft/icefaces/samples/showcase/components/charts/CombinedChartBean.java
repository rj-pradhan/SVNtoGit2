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
//Original copyright
/***********************************************************************************************
 * Copyright 2002 (C) Nathaniel G. Auvil. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation ("Software"), with or
 * without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and notices.
 * 	Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of
 * 	conditions and the following disclaimer in the documentation and/or other materials
 * 	provided with the distribution.
 *
 * 3. The name "jCharts" or "Nathaniel G. Auvil" must not be used to endorse or promote
 * 	products derived from this Software without prior written permission of Nathaniel G.
 * 	Auvil.  For written permission, please contact nathaniel_auvil@users.sourceforge.net
 *
 * 4. Products derived from this Software may not be called "jCharts" nor may "jCharts" appear
 * 	in their names without prior written permission of Nathaniel G. Auvil. jCharts is a
 * 	registered trademark of Nathaniel G. Auvil.
 *
 * 5. Due credit should be given to the jCharts Project (http://jcharts.sourceforge.net/).
 *
 * THIS SOFTWARE IS PROVIDED BY Nathaniel G. Auvil AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * jCharts OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN
 * IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
 ************************************************************************************************/
package com.icesoft.icefaces.samples.showcase.components.charts;

import com.icesoft.faces.component.outputchart.OutputChart;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Pulsate;
import org.krysalis.jcharts.axisChart.AxisChart;
import org.krysalis.jcharts.chartData.AxisChartDataSet;
import org.krysalis.jcharts.chartData.DataSeries;
import org.krysalis.jcharts.properties.AxisProperties;
import org.krysalis.jcharts.properties.BarChartProperties;
import org.krysalis.jcharts.properties.ChartProperties;
import org.krysalis.jcharts.properties.LegendProperties;
import org.krysalis.jcharts.properties.LineChartProperties;
import org.krysalis.jcharts.properties.PointChartProperties;
import org.krysalis.jcharts.test.TestDataGenerator;
import org.krysalis.jcharts.types.ChartType;

import javax.faces.event.ActionEvent;
import java.awt.*;

/**
 * CombinedChartBean class. this class holds the backing information for the
 * combined chart component of the showcase.
 */
public class CombinedChartBean {

    //flag to determine if initialized
    private boolean initialzed = false;

    //the text value returned after clicking on the chart
    private String clickedValue;

    //highlight effect when text is changed
    private Effect effectOutputText;

    //local variable for the axis chart component of the combined chart
    private static AxisChart axisChart;

    public CombinedChartBean() {
        try {
            String[] xAxisLabels =
                    {"1998", "1999", "2000", "2001", "2002", "2003", "2004"};
            String xAxisTitle = "Years";
            String yAxisTitle = "Problems";
            String title = "Company Software";
            DataSeries dataSeries =
                    new DataSeries(xAxisLabels, xAxisTitle, yAxisTitle, title);


            double[][] data = TestDataGenerator.getRandomNumbers(3, 7, 0, 5000);
            String[] legendLabels = {"Bugs", "Security Holes", "Backdoors"};
            Paint[] paints = TestDataGenerator.getRandomPaints(3);

            BarChartProperties barChartProperties = new BarChartProperties();
            AxisChartDataSet axisChartDataSet = new AxisChartDataSet(data,
                                                                     legendLabels,
                                                                     paints,
                                                                     ChartType.BAR,
                                                                     barChartProperties);
            dataSeries.addIAxisPlotDataSet(axisChartDataSet);


            data = TestDataGenerator.getRandomNumbers(2, 7, 1000, 5000);
            legendLabels = new String[]{"Patches", "New Patch Bugs"};
            paints = new Paint[]{Color.black, Color.red};

            Stroke[] strokes = {LineChartProperties.DEFAULT_LINE_STROKE,
                                LineChartProperties.DEFAULT_LINE_STROKE};
            Shape[] shapes = {PointChartProperties.SHAPE_CIRCLE,
                              PointChartProperties.SHAPE_TRIANGLE};
            LineChartProperties lineChartProperties =
                    new LineChartProperties(strokes, shapes);
            axisChartDataSet = new AxisChartDataSet(data, legendLabels, paints,
                                                    ChartType.LINE,
                                                    lineChartProperties);
            dataSeries.addIAxisPlotDataSet(axisChartDataSet);


            ChartProperties chartProperties = new ChartProperties();
            AxisProperties axisProperties = new AxisProperties();
            LegendProperties legendProperties = new LegendProperties();

            axisChart = new AxisChart(dataSeries, chartProperties,
                                      axisProperties,
                                      legendProperties, 500, 500);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Method to tell the page to render or not based on the initialized flag
     *
     * @param component chart component which will be rendered.
     *
     * @return boolean true if OutputChart should be re-rendered; otherwise, false.
     */
    public boolean renderOnSubmit(OutputChart component) {


        component.setChart(axisChart);

        return !initialzed && (initialzed = true);

    }


    /**
     * Method to change the output text to the valuse selected by the user when
     * they click on the chart
     *
     * @param event JSF action event
     */
    public void action(ActionEvent event) {
        if (event.getSource() instanceof OutputChart) {
            OutputChart chart = (OutputChart) event.getSource();
            clickedValue = "";
            if (chart.getClickedImageMapArea().getXAxisLabel() != null) {
                setClickedValue(chart.getClickedImageMapArea().getXAxisLabel() +
                                "  :  " +
                                chart.getClickedImageMapArea().getValue());
                effectOutputText = new Pulsate(2.0f);
            }
        }
    }


    public String getClickedValue() {
        return clickedValue;
    }

    public void setClickedValue(String clickedValue) {
        this.clickedValue = clickedValue;
    }


    public Effect getEffectOutputText() {
        return effectOutputText;
    }

    public void setEffectOutputText(Effect effectOutputText) {
        this.effectOutputText = effectOutputText;
    }
}
