package no.ntnu.group13.greenhouse.javafx.controllers;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.text.Text;

public class TemperatureWindowController extends WindowController {

  public void initialize() {
    xAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
    xAxis.setForceZeroInRange(false);
    xAxis.setAutoRanging(false);
    xAxis.setTickLabelsVisible(false);
    xAxis.setTickMarkVisible(false);
    xAxis.setMinorTickVisible(false);

    tempLineChart.getXAxis().setLabel("Time (seconds)");
    tempLineChart.getYAxis().setLabel("Value");
    tempLineChart.setAnimated(false);
    tempLineChart.setTitle("Animated Line Chart");
    tempLineChart.setHorizontalGridLinesVisible(true);

    // Set Name for Series
    // tempSeries.setName("Temperature");

    // Add Chart Series
    // tempLineChart.getData().addAll(tempSeries);

    // this.tempClientHandler = new ClientHandler(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.TEMP_CLIENT, LOGIC.QOS);
  }

  public LineChart<?, ?> getTempLineChart() {
    return tempLineChart;
  }

  public Text getTextTempCurrent() {
    return textTempCurrent;
  }

  public Text getTextTempHigh() {
    return textTempHigh;
  }

  public Text getTextTempLow() {
    return textTempLow;
  }
}
