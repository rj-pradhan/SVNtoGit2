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

import com.icesoft.faces.component.outputchart.OutputChart;
import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AxisChartBean {

    private List areaXaxisLabels = new ArrayList();
    private List legendLabels = new ArrayList();
    private List areaData = new ArrayList();
    private List areaPaints = new ArrayList();
    private boolean axis = true;
    private boolean pie3D = false;
    private static final String DEFAULT_STRING =
            "Click on the image map below to display a chart value: ";

    public List getAreaXaxisLabels() {
        areaXaxisLabels.clear();
        areaXaxisLabels.add("2000");
        areaXaxisLabels.add("2001");
        areaXaxisLabels.add("2002");
        areaXaxisLabels.add("2003");
        areaXaxisLabels.add("2004");
        areaXaxisLabels.add("2005");
        areaXaxisLabels.add("2006");
        return areaXaxisLabels;
    }

    public void setAreaXaxisLabels(List areaXaxisLabels) {
        this.areaXaxisLabels = areaXaxisLabels;
    }

    public List getLegendLabels() {
        legendLabels.clear();
        legendLabels.add("Bugs");
        legendLabels.add("Enhancements");
        legendLabels.add("Fixed");
        return legendLabels;
    }

    public void setLegendLabels(List legendLabels) {
        this.legendLabels = legendLabels;
    }

    public List getAreaData() {
        areaData.clear();
        areaData.add(new double[]{350, 50, 400});
        areaData.add(new double[]{45, 145, 50});
        areaData.add(new double[]{-36, 6, 98});
        areaData.add(new double[]{66, 166, 74});
        areaData.add(new double[]{145, 105, 55});
        areaData.add(new double[]{80, 110, 4});
        areaData.add(new double[]{10, 90, 70});
        return areaData;
    }


    public void setAreaData(List areaData) {
        this.areaData = areaData;
    }

    public List getAreaPaints() {
        areaPaints.clear();
        areaPaints.add(new Color(153, 0, 255, 100));
        areaPaints.add(new Color(204, 0, 255, 150));
        areaPaints.add(new Color(204, 0, 1, 150));
        return areaPaints;
    }


    public void setAreaPaints(List areaPaints) {
        this.areaPaints = areaPaints;
    }

    String clickedValue = DEFAULT_STRING;

    public void imageClicked(ActionEvent event) {
        if (event.getSource() instanceof OutputChart) {
            OutputChart chart = (OutputChart) event.getSource();
            if (chart.getClickedImageMapArea().getXAxisLabel() != null) {
                setClickedValue(DEFAULT_STRING +
                                chart.getClickedImageMapArea().getXAxisLabel() +
                                "  :  " +
                                chart.getClickedImageMapArea().getValue());
                effectOutputText = new Highlight("#ffff99");
            }
        }
    }

    public String getClickedValue() {
        return clickedValue;
    }

    public void setClickedValue(String clickedValue) {
        this.clickedValue = clickedValue;
    }

    private Effect effectOutputText;

    public Effect getEffectOutputText() {
        return effectOutputText;
    }

    public void setEffectOutputText(Effect effectOutputText) {
        this.effectOutputText = effectOutputText;
    }


    private SelectItem[] chartList = null;

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

    public void setChartList(SelectItem[] chartList) {
        this.chartList = chartList;
    }

    private String chart = OutputChart.BAR_CHART_TYPE;

    public String getChart() {
        return chart;
    }

    public void setChart(String chart) {
        this.chart = chart;
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

    public boolean allCharts(OutputChart component) {
        if (chartChangedFlag) {
            chartChangedFlag = false;
            return true;
        } else {
            return chartChangedFlag;
        }
    }

    private boolean chartChangedFlag = true;

    public void chartChanged(ValueChangeEvent event) {
        chartChangedFlag = true;
        if (event.getNewValue().equals("area") ||
            event.getNewValue().equals("areastacked")) {
            setClickedValue(
                    "A client side image map is not supported for Area charts (clicking on the chart will not display any values)");
        } else {
            setClickedValue(DEFAULT_STRING);
        }
    }

    public boolean isAxis() {
        return axis;
    }

    public void setAxis(boolean axis) {
        this.axis = axis;
    }

    public boolean isPie3D() {
        return pie3D;
    }

    public void setPie3D(boolean pie3D) {
        this.pie3D = pie3D;
    }
}
