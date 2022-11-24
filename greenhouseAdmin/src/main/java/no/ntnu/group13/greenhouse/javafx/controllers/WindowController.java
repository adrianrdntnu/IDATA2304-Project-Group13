package no.ntnu.group13.greenhouse.javafx.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javax.crypto.spec.IvParameterSpec;
import no.ntnu.group13.greenhouse.client.ClientHandler;
import no.ntnu.group13.greenhouse.logic.BinarySearchTree;
import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.sensors.Co2Sensor;
import no.ntnu.group13.greenhouse.sensors.HumiditySensor;
import no.ntnu.group13.greenhouse.sensors.Sensor;
import no.ntnu.group13.greenhouse.sensors.TemperatureSensor;
import org.eclipse.paho.client.mqttv3.MqttException;

public class WindowController {

  protected ExecutorService executor;

  // For Linechart
  protected int xSeriesData = 0;
  protected static final int MAX_DATA_POINTS = 100;
  protected static final int LINECHART_UPDATE_INTERVAL = 3000;

  // Sensor-client communication
  protected final List<Double> temperatureValues = new ArrayList<>();
  protected final List<Double> humidityValues = new ArrayList<>();
  protected final List<Double> co2Values = new ArrayList<>();
  protected final BinarySearchTree bstTemperatureTree = new BinarySearchTree();
  protected final BinarySearchTree bstHumidityTree = new BinarySearchTree();
  protected final BinarySearchTree bstCo2Tree = new BinarySearchTree();

  // Value trackers
  protected Double lowTemp = 0.0;
  protected Double highTemp = 0.0;
  protected Double lowHumid = 0.0;
  protected Double highHumid = 0.0;
  protected Double lowCo2 = 0.0;
  protected Double highCo2 = 0.0;
  protected Double currentTemp;
  protected Double currentHumid;
  protected Double currentCo2;
  protected static final String TEMP_SYMBOL = "°C";
  protected static final String HUMID_SYMBOL = "%";
  protected static final String CO2_SYMBOL = "ppm";

  protected boolean clientOnlineStatus = false;
  protected boolean sensorOnlineStatus = false;

  protected Sensor temperatureSensor;
  protected Sensor humiditySensor;
  protected Sensor co2Sensor;
  protected ClientHandler tempClientHandler;
  protected ClientHandler humidClientHandler;
  protected ClientHandler co2ClientHandler;

  // For decrypting and encrypting
  protected IvParameterSpec ivParameterSpec;

  protected XYChart.Series tempSeries = new XYChart.Series<>();
  protected XYChart.Series humidSeries = new XYChart.Series<>();
  protected XYChart.Series co2Series = new XYChart.Series<>();
  protected XYChart.Series overviewTempSeries = new XYChart.Series<>();
  protected XYChart.Series overviewHumidSeries = new XYChart.Series<>();
  protected XYChart.Series overviewCo2Series = new XYChart.Series<>();
  protected final ConcurrentLinkedQueue<Number> receivedTempMessages = new ConcurrentLinkedQueue<>();
  protected final ConcurrentLinkedQueue<Number> receivedHumidMessages = new ConcurrentLinkedQueue<>();
  protected final ConcurrentLinkedQueue<Number> receivedCo2Messages = new ConcurrentLinkedQueue<>();

  protected Parent overviewPage;
  protected Parent tempPane;
  protected Parent humidityPane;
  protected Parent co2Pane;
  @FXML
  private BorderPane centerBorderPane;
  protected NumberAxis xAxis;
  @FXML
  protected LineChart<?, ?> dashboardLineChart;
  @FXML
  protected LineChart<?, ?> tempLineChart;
  @FXML
  protected LineChart<?, ?> humidityLineChart;
  @FXML
  protected LineChart<?, ?> co2LineChart;
  @FXML
  protected Text textCo2Current;
  @FXML
  protected Text textCo2High;
  @FXML
  protected Text textCo2Low;
  @FXML
  protected Text textHumidCurrent;
  @FXML
  protected Text textHumidHigh;
  @FXML
  protected Text textHumidLow;
  @FXML
  protected Text textTempCurrent;
  @FXML
  protected Text textTempHigh;
  @FXML
  protected Text textTempLow;

  // Menu buttons
  @FXML
  public void dashboardMenuButton(ActionEvent actionEvent) {
    this.centerBorderPane.setCenter(overviewPage);
  }

  @FXML
  public void tempMenuButton(ActionEvent actionEvent) {
    this.centerBorderPane.setCenter(tempPane);
  }

  @FXML
  public void humidityMenuButton(ActionEvent actionEvent) {
    this.centerBorderPane.setCenter(humidityPane);
  }

  @FXML
  public void co2MenuButton(ActionEvent actionEvent) {
    this.centerBorderPane.setCenter(co2Pane);
  }

  @FXML
  public void startProgram(ActionEvent actionEvent) {
    this.centerBorderPane.setCenter(overviewPage);
  }

  public void setOverviewPage(Parent overviewPage) {
    this.overviewPage = overviewPage;
  }

  public void setTemperaturePage(Parent tempPane) {
    this.tempPane = tempPane;
  }

  public void setHumidityPage(Parent humidityPane) {
    this.humidityPane = humidityPane;
  }

  public void setCo2Page(Parent co2Pane) {
    this.co2Pane = co2Pane;
  }
}
