package no.ntnu.group13.greenhouse.javafx.controllers;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import no.ntnu.group13.greenhouse.client.ClientHandler;
import no.ntnu.group13.greenhouse.logic.LOGIC;

public class OverviewWindowController extends WindowController {

  // For fake value generation
  private static final int GENERATE_VALUES = 20;
  private static final int VALUE_SPLIT = 2;
  private static final int TEMP_UPPER_MEAN = 28;
  private static final int TEMP_LOWER_MEAN = 24;
  private static final int HUMID_UPPER_MEAN = 65;
  private static final int HUMID_LOWER_MEAN = 55;
  private static final int CO2_UPPER_MEAN = 105;
  private static final int CO2_LOWER_MEAN = 95;
  private final Random random = new Random();

  @FXML
  private LineChart<?, ?> dashboardLineChart;
  @FXML
  private Button stopButton;
  @FXML
  private Button startButton;
  @FXML
  private Text textTempCurrent;
  @FXML
  private Text textTempHigh;
  @FXML
  private Text textTempLow;
  @FXML
  private Text textHumidCurrent;
  @FXML
  private Text textHumidHigh;
  @FXML
  private Text textHumidLow;
  @FXML
  private Text textCo2Current;
  @FXML
  private Text textCo2High;
  @FXML
  private Text textCo2Low;

  /**
   * Initialize Controller.
   */
  public void initialize() {
    xAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
    xAxis.setForceZeroInRange(false);
    xAxis.setAutoRanging(false);
    xAxis.setTickLabelsVisible(false);
    xAxis.setTickMarkVisible(false);
    xAxis.setMinorTickVisible(false);

    dashboardLineChart.getXAxis().setLabel("Time (seconds)");
    dashboardLineChart.getYAxis().setLabel("Value");
    dashboardLineChart.setAnimated(false);
    dashboardLineChart.setTitle("Animated Line Chart");
    dashboardLineChart.setHorizontalGridLinesVisible(true);

    // Set Name for Series
    tempSeries.setName("Temperature");
    humidSeries.setName("Humidity");
    co2Series.setName("Co2");

    // Add Chart Series
    dashboardLineChart.getData().addAll(tempSeries, humidSeries, co2Series);

    this.tempClientHandler = new ClientHandler(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER,
        LOGIC.TEMP_CLIENT, LOGIC.QOS);
    this.humidClientHandler = new ClientHandler(LOGIC.HUMIDITY_TOPIC, LOGIC.BROKER,
        LOGIC.HUMID_CLIENT, LOGIC.QOS);
    this.co2ClientHandler = new ClientHandler(LOGIC.CO2_TOPIC, LOGIC.BROKER, LOGIC.CO2_CLIENT,
        LOGIC.QOS);
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
  public void updateOverviewDetailedValues() {
    textTempCurrent.setText(currentTemp + "°C");
    textHumidCurrent.setText(currentHumid + "%");
    textCo2Current.setText(currentCo2 + "ppm");

    if (currentTemp > highTemp) {
      highTemp = currentTemp;
      textTempHigh.setText(currentTemp + "°C");
    }
    if (currentTemp < lowTemp || lowTemp == 0.0) {
      lowTemp = currentTemp;
      textTempLow.setText(currentTemp + "°C");
    }
    if (currentHumid > highHumid) {
      highHumid = currentHumid;
      textHumidHigh.setText(currentHumid + "%");
    }
    if (currentHumid < lowHumid || lowHumid == 0.0) {
      lowHumid = currentHumid;
      textHumidLow.setText(currentHumid + "%");
    }
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