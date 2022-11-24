package no.ntnu.group13.greenhouse.javafx.controllers;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.text.Text;

public class HumidityWindowController extends WindowController {

  public void initialize() {
    xAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
    xAxis.setForceZeroInRange(false);
    xAxis.setAutoRanging(false);
    xAxis.setTickLabelsVisible(false);
    xAxis.setTickMarkVisible(false);
    xAxis.setMinorTickVisible(false);

    humidityLineChart.getXAxis().setLabel("Time (seconds)");
    humidityLineChart.getYAxis().setLabel("Value");
    humidityLineChart.setAnimated(false);
    humidityLineChart.setTitle("Animated Line Chart");
    humidityLineChart.setHorizontalGridLinesVisible(true);

    // Set Name for Series
    // humidSeries.setName("Humidity");

    // Add Chart Series
    // humidityLineChart.getData().addAll(humidSeries);
  }

  /**
   * Updates detailed values.
   */
  public void updateHumidityDetailedValues() {
    textHumidCurrent.setText(currentHumid + "%");

    if (currentHumid > highHumid) {
      highHumid = currentHumid;
      textHumidHigh.setText(currentHumid + "%");
    }
    if (currentHumid < lowHumid || lowHumid == 0.0) {
      lowHumid = currentHumid;
      textHumidLow.setText(currentHumid + "%");
    }
  }

  public LineChart<?, ?> getHumidityLineChart() {
    return humidityLineChart;
  }

  public Text getTextHumidCurrent() {
    return textHumidCurrent;
  }

  public Text getTextHumidHigh() {
    return textHumidHigh;
  }

  public Text getTextHumidLow() {
    return textHumidLow;
  }
}
