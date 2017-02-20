package com.ardikars.ann;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class ChartController {

    @FXML LineChart<Double, Integer> LineCharID;
    XYChart.Series<Double, Integer> series = new XYChart.Series<Double, Integer>();
    
    
    
    public void add(double sse, int epoch) {
        series.getData().add(new XYChart.Data<Double, Integer>(sse, epoch));
        LineCharID.getData().add(series);
    }
    
}
