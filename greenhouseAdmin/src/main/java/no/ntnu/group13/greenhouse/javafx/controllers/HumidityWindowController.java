package no.ntnu.group13.greenhouse.javafx.controllers;

import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class HumidityWindowController extends WindowController {

  @FXML
  private Text textHumidCurrent;
  @FXML
  private Text textHumidHigh;
  @FXML
  private Text textHumidLow;
  @FXML
  private Button startButton;
  @FXML
  private Button stopButton;

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
    humidSeries.setName("Humidity");

    // Add Chart Series
    // humidityLineChart.getData().addAll(humidSeries);
  }

  @FXML
  public void startRecordButton(ActionEvent actionEvent) {
    stopButton.setDisable(false);
    startButton.setDisable(true);

    startRecording();
  }

  @FXML
  public void stopRecordButton(ActionEvent actionEvent) throws InterruptedException {
    executor.awaitTermination(LINECHART_UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
    executor.shutdown();
    stopSensors();

    stopButton.setDisable(true);
    startButton.setDisable(false);
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
}
