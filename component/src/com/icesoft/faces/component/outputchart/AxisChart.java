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

package com.icesoft.faces.component.outputchart;

import org.krysalis.jcharts.chartData.AxisChartDataSet;
import org.krysalis.jcharts.chartData.DataSeries;
import org.krysalis.jcharts.properties.AreaChartProperties;
import org.krysalis.jcharts.properties.AxisProperties;
import org.krysalis.jcharts.properties.BarChartProperties;
import org.krysalis.jcharts.properties.ChartProperties;
import org.krysalis.jcharts.properties.ChartTypeProperties;
import org.krysalis.jcharts.properties.ClusteredBarChartProperties;
import org.krysalis.jcharts.properties.LegendProperties;
import org.krysalis.jcharts.properties.LineChartProperties;
import org.krysalis.jcharts.properties.PointChartProperties;
import org.krysalis.jcharts.properties.StackedAreaChartProperties;
import org.krysalis.jcharts.properties.StackedBarChartProperties;
import org.krysalis.jcharts.test.TestDataGenerator;
import org.krysalis.jcharts.types.ChartType;

import javax.faces.component.UIComponent;
import java.awt.*;

public class AxisChart extends AbstractChart {

    public AxisChart(UIComponent uiComponent) throws Throwable {
        super(uiComponent);
    }

    protected void buildChart() throws Throwable {
        if (data == null) {
            getData(outputChart.getData());
        }
        if (type.equalsIgnoreCase(OutputChart.AREA_CHART_TYPE)) {
            buildAreaChart();
        } else if (type.equalsIgnoreCase(OutputChart.AREA_STACKED_CHART_TYPE)) {
            buildAreaStackedChart();
        } else if (type.equalsIgnoreCase(OutputChart.BAR_CHART_TYPE)) {
            buildBarChart();
        } else if (type.equalsIgnoreCase(OutputChart.BAR_STACKED_CHART_TYPE)) {
            buildBarStackedChart();
        } else
        if (type.equalsIgnoreCase(OutputChart.BAR_CLUSTERED_CHART_TYPE)) {
            buildBarClusteredChart();
        } else if (type.equalsIgnoreCase(OutputChart.LINE_CHART_TYPE)) {
            buildLineChart();
        } else if (type.equalsIgnoreCase(OutputChart.POINT_CHART_TYPE)) {
            buildPointChart();
        }
    }

    private void buildAreaChart() throws Throwable {
        AreaChartProperties areaChartProperties = new AreaChartProperties();
        buildAxisChart(ChartType.AREA, areaChartProperties);
    }

    private void buildAreaStackedChart() throws Throwable {
        StackedAreaChartProperties areaChartProperties =
                new StackedAreaChartProperties();
        buildAxisChart(ChartType.AREA_STACKED, areaChartProperties);
    }

    private void buildBarChart() throws Throwable {
        BarChartProperties barChartProperties = new BarChartProperties();
        buildAxisChart(ChartType.BAR, barChartProperties);
    }

    private void buildBarStackedChart() throws Throwable {
        StackedBarChartProperties barChartProperties =
                new StackedBarChartProperties();
        buildAxisChart(ChartType.BAR_STACKED, barChartProperties);
    }

    private void buildBarClusteredChart() throws Throwable {
        ClusteredBarChartProperties barChartProperties =
                new ClusteredBarChartProperties();
        buildAxisChart(ChartType.BAR_CLUSTERED, barChartProperties);
    }

    private void buildLineChart() throws Throwable {
        Stroke[] strokes = new Stroke[data.length];
        for (int i = 0; i < data.length; i++) {
            strokes[i] = LineChartProperties.DEFAULT_LINE_STROKE;
        }
        LineChartProperties lineChartProperties = new LineChartProperties(
                strokes, getShapes(outputChart.getShapes()));
        buildAxisChart(ChartType.LINE, lineChartProperties);
    }

    private void buildPointChart() throws Throwable {
        Paint[] outlinePaints = TestDataGenerator.getRandomPaints(data.length);
        boolean[] fillPointFlags = new boolean[data.length];
        for (int i = 0; i < data.length; i++) {
            fillPointFlags[i] = true;
        }
        PointChartProperties pointChartProperties = new PointChartProperties(
                getShapes(outputChart.getShapes()), fillPointFlags,
                outlinePaints);
        buildAxisChart(ChartType.POINT, pointChartProperties);
    }

    void buildAxisChart(ChartType chartType,
                        ChartTypeProperties chartTypeProperties)
            throws Throwable {
        DataSeries dataSeries = new DataSeries(
                getAsXaxisLabelsArray(outputChart.getXaxisLabels()),
                outputChart.getXaxisTitle(),
                outputChart.getYaxisTitle(),
                outputChart.getChartTitle());

        AxisChartDataSet axisChartDataSet = new AxisChartDataSet(
                getAs2dDoubleArray(outputChart.getData()),
                getAsLabelsArray(outputChart.getLabels()),
                getPaints(outputChart.getColors()),
                chartType,
                chartTypeProperties);

        dataSeries.addIAxisPlotDataSet(axisChartDataSet);
        chart = new org.krysalis.jcharts.axisChart.AxisChart(dataSeries,
                                                             new ChartProperties(),
                                                             new AxisProperties(),
                                                             getLegendProperties(),
                                                             new Integer(
                                                                     outputChart.getWidth()).intValue(),
                                                             new Integer(
                                                                     outputChart.getHeight()).intValue());
    }

    private Shape[] shapes;

    private Shape[] getShapes(Object obj) {
        if (obj == null && shapes == null) {
            return shapes = getGeneratedShapes(data.length);
        } else if (obj == null && shapes != null) {
            return shapes;
        } else {
            return shapes = getAsShapeArray(obj);
        }
    }

    String[] xaxisLabels = null;

    public String[] getAsXaxisLabelsArray(Object obj) {
        if (obj == null && xaxisLabels == null) {
            return xaxisLabels = getGeneratedLabels("Xlabel", data[0].length);
        } else if (obj == null && xaxisLabels != null) {
            return xaxisLabels;
        } else {
            return getAsStringArray(obj);
        }
    }

    String[] labels = null;

    public String[] getAsLabelsArray(Object obj) {
        if (obj == null && labels == null) {
            return labels = getGeneratedLabels("Label", data.length);
        } else if (obj == null && labels != null) {
            return labels;
        } else {
            return getAsStringArray(obj);
        }
    }

    private double[][] data = null;

    public double[][] getData(Object obj) {
        if (obj instanceof String && data != null) {
            return data;
        } else {
            return data = getAs2dDoubleArray(obj);
        }
    }

    public Paint[] getPaints(Object obj) {
        return getPaints(obj, data.length);
    }
}
