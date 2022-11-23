package no.ntnu.group13.greenhouse.javafx.controllers;

import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class TemperatureWindowController extends WindowController {

  @FXML
  private Text textTempCurrent;
  @FXML
  private Text textTempHigh;
  @FXML
  private Text textTempLow;
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

    tempLineChart.getXAxis().setLabel("Time (seconds)");
    tempLineChart.getYAxis().setLabel("Value");
    tempLineChart.setAnimated(false);
    tempLineChart.setTitle("Animated Line Chart");
    tempLineChart.setHorizontalGridLinesVisible(true);

    // Set Name for Series
    tempSeries.setName("Temperature");

    // Add Chart Series
    // tempLineChart.getData().addAll(tempSeries);

    // this.tempClientHandler = new ClientHandler(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.TEMP_CLIENT, LOGIC.QOS);
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
  public void updateDetailedValues() {
    textTempCurrent.setText(currentTemp + "°C");

    if (currentTemp > highTemp) {
      highTemp = currentTemp;
      textTempHigh.setText(currentTemp + "°C");
    }
    if (currentTemp < lowTemp || lowTemp == 0.0) {
      lowTemp = currentTemp;
      textTempLow.setText(currentTemp + "°C");
    }
  }

  public LineChart<?, ?> getTempLineChart() {
    return tempLineChart;
  }
}
