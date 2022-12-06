package no.ntnu.group13.greenhouse.javafx.controllers;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.text.Text;

/**
 * Responsible for controlling the co2-page in the JavaFX application.
 */
public class Co2WindowController extends WindowController {

  public void initialize() {
    xAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
    xAxis.setForceZeroInRange(false);
    xAxis.setAutoRanging(false);
    xAxis.setTickLabelsVisible(false);
    xAxis.setTickMarkVisible(false);
    xAxis.setMinorTickVisible(false);

    co2LineChart.getXAxis().setLabel("Time (seconds)");
    co2LineChart.getYAxis().setLabel("Value");
    co2LineChart.setAnimated(false);
    co2LineChart.setTitle("Co2 Sensor Values");
    co2LineChart.setHorizontalGridLinesVisible(true);

    // Set Name for Series
    // co2Series.setName("co2");

    // Add Chart Series
    // co2LineChart.getData().addAll(co2Series);
  }

  public LineChart<?, ?> getCo2LineChart() {
    return co2LineChart;
  }

  public Text getTextCo2Current() {
    return textCo2Current;
  }

  public Text getTextCo2High() {
    return textCo2High;
  }

  public Text getTextCo2Low() {
    return textCo2Low;
  }
}
