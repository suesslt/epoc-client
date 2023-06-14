package com.jore.epoc.views.company;

import com.storedobject.chart.Data;
import com.storedobject.chart.DataType;
import com.storedobject.chart.LineChart;
import com.storedobject.chart.RectangularCoordinate;
import com.storedobject.chart.SOChart;
import com.storedobject.chart.Title;
import com.storedobject.chart.XAxis;
import com.storedobject.chart.YAxis;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("chartview")
@AnonymousAllowed
@SuppressWarnings("serial")
public class ChartView extends HorizontalLayout {
    public ChartView() {
        // Creating a chart display area
        SOChart soChart = new SOChart();
        soChart.setSize("600px", "650px");
        // Generating 10 set of values for 10 LineCharts for the equation:
        // y = a + a * x / (a - 11) where a = 1 to 10, x and y are positive
        LineChart[] lineCharts = new LineChart[10];
        Data[] xValues = new Data[lineCharts.length];
        Data[] yValues = new Data[lineCharts.length];
        int i;
        for (i = 0; i < lineCharts.length; i++) {
            xValues[i] = new Data();
            xValues[i].setName("X (a = " + (i + 1) + ")");
            yValues[i] = new Data();
            yValues[i].setName("Y (a = " + (i + 1) + ")");
        }
        // For each line chart, we need only 2 end-points (because they are straight lines).
        int a;
        for (i = 0; i < lineCharts.length; i++) {
            a = i + 1;
            xValues[i].add(0);
            yValues[i].add(a);
            xValues[i].add(11 - a);
            yValues[i].add(0);
        }
        // Line charts are initialized here
        for (i = 0; i < lineCharts.length; i++) {
            lineCharts[i] = new LineChart(xValues[i], yValues[i]);
            lineCharts[i].setName("a = " + (i + 1));
        }
        // Line charts need a coordinate system to plot on
        // We need Number-type for both X and Y axes in this case
        XAxis xAxis = new XAxis(DataType.NUMBER);
        YAxis yAxis = new YAxis(DataType.NUMBER);
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        for (i = 0; i < lineCharts.length; i++) {
            lineCharts[i].plotOn(rc);
            soChart.add(lineCharts[i]); // Add the chart to the display area
        }
        // Add a simple title too
        soChart.add(new Title("Equation: y = a + a * x / (a - 11) where a = 1 to 10, x and y are positive"));
        // We don't want any legends
        soChart.disableDefaultLegend();
        // Add it to my layout
        add(soChart);
    }
}
