package no.ntnu.group13.greenhouse.javafx.controllers;

import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class Co2WindowController extends WindowController {

  @FXML
  private Button stopButton;
  @FXML
  private Button startButton;
  @FXML
  private Text textCo2Current;
  @FXML
  private Text textCo2High;
  @FXML
  private Text textCo2Low;
  @FXML
  private LineChart<?, ?> co2LineChart;

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
    co2LineChart.setTitle("Animated Line Chart");
    co2LineChart.setHorizontalGridLinesVisible(true);

    // Set Name for Series
    co2Series.setName("co2");

    // Add Chart Series
    co2LineChart.getData().addAll(co2Series);
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
  public void updateCo2DetailedValues() {
    textCo2Current.setText(currentCo2 + "ppm");

    if (currentCo2 > highCo2) {
      highCo2 = currentCo2;
      textCo2High.setText(currentCo2 + "ppm");
    }
    if (currentCo2 < lowCo2 || lowCo2 == 0.0) {
      lowCo2 = currentCo2;
      textCo2Low.setText(currentCo2 + "ppm");
    }
  }
}
