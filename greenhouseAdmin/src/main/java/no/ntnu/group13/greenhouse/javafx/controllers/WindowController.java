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

  /**
   * Starts connection between clients and MQTT broker
   */
  protected void startClients() {
    tempClientHandler.startClient();
    humidClientHandler.startClient();
    co2ClientHandler.startClient();
    clientOnlineStatus = true;
  }

  /**
   * Starts connection between the sensor and MQTT broker.
   */
  protected void startSensors() {
    temperatureSensor = new TemperatureSensor(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER,
        LOGIC.TEMP_SENSOR, LOGIC.QOS, 27.5, 1, ivParameterSpec);
    humiditySensor = new HumiditySensor(LOGIC.HUMIDITY_TOPIC, LOGIC.BROKER, LOGIC.HUMID_SENSOR,
        LOGIC.QOS, 50, 3, ivParameterSpec);
    co2Sensor = new Co2Sensor(LOGIC.CO2_TOPIC, LOGIC.BROKER, LOGIC.CO2_SENSOR, LOGIC.QOS, 100,
        3, ivParameterSpec);

    temperatureSensor.startConnection();
    humiditySensor.startConnection();
    co2Sensor.startConnection();
    sensorOnlineStatus = true;
  }

  /**
   * Terminates connection from the sensor to the MQTT broker.
   */
  public void stopSensors() {
    temperatureSensor.terminateConnection();
    humiditySensor.terminateConnection();
    co2Sensor.terminateConnection();
    sensorOnlineStatus = false;
  }

  /**
   * Disconnects clients from the MQTT broker.
   */
  public void disconnectClients() {
    try {
      tempClientHandler.disconnectClient();
      humidClientHandler.disconnectClient();
      co2ClientHandler.disconnectClient();
      clientOnlineStatus = false;
    } catch (MqttException e) {
      System.out.println("Disconnect failed: " + e);
    }
  }

  public boolean getSensorOnlineStatus() {
    return sensorOnlineStatus;
  }

  public boolean getClientOnlineStatus() {
    return clientOnlineStatus;
  }

  // Updating linechart

  /**
   * Receives and stores message from sensor.
   */
  public void receiveMessageFromSensor(BinarySearchTree tree, Queue<Number> queue,
      ClientHandler client) {
    Double d = client.getMostRecentMessage();
    tree.insert(d);
    queue.add(d);
    queue.add(d);
  }

  /**
   * Starts recording data received from the sensor. Code adapted from: <a
   * href="https://stackoverflow.com/a/22093579">stackoverflow</a>
   */
  public void startRecording() {
    executor = Executors.newCachedThreadPool(r -> {
      Thread thread = new Thread(r);
      thread.setDaemon(true);
      return thread;
    });

    if (!clientOnlineStatus) {
      startClients();
    }
    startSensors();

    AddToQueue addToQueue = new AddToQueue();
    executor.execute(addToQueue);
    //-- Prepare Timeline
    prepareTimeline();
  }

  /**
   * Generates a queue of values to publish from the Sensor. Part of code adapted from: <a
   * href="https://stackoverflow.com/a/22093579">stackoverflow</a>
   */
  private class AddToQueue implements Runnable {

    public void run() {
      try {
        // Generates new values to send to the client, created dynamically to prevent unnecessary
        // overloading at program startup.
//        if ((xSeriesData % GENERATE_VALUES) == 0) {
////          temperatureValues.addAll(
////              temperatureSensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT));
////          humidityValues.addAll(
////              humiditySensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT));
////          co2Values.addAll(co2Sensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT));
//          temperatureValues.addAll(temperatureSensor.generateValuesToNewMean(GENERATE_VALUES, 30));
//          humidityValues.addAll(humiditySensor.generateValuesToNewMean(GENERATE_VALUES, 60));
//          co2Values.addAll(co2Sensor.generateValuesToNewMean(GENERATE_VALUES, 75));
//        }  TODO: Remove this

        temperatureValues.add(temperatureSensor.nextValue());
        humidityValues.add(humiditySensor.nextValue());
        co2Values.add(co2Sensor.nextValue());

        currentTemp = temperatureValues.get(xSeriesData);
        currentHumid = humidityValues.get(xSeriesData);
        currentCo2 = co2Values.get(xSeriesData);

        temperatureSensor.publishMessageToBroker("" + currentTemp);
        humiditySensor.publishMessageToBroker("" + currentHumid);
        co2Sensor.publishMessageToBroker("" + currentCo2);

        // Waits for and HOPEFULLY the message has arrived by then.
        Thread.sleep(LINECHART_UPDATE_INTERVAL);

        receiveMessageFromSensor(bstTemperatureTree, receivedTempMessages, tempClientHandler);
        receiveMessageFromSensor(bstHumidityTree, receivedHumidMessages, humidClientHandler);
        receiveMessageFromSensor(bstCo2Tree, receivedCo2Messages, co2ClientHandler);

        // update
        xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData - 1);

        if (!executor.isShutdown()) {
          executor.execute(this);
        }
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
  }

  /**
   * Starts recording data received from the sensor. Part of code adapted from: <a
   * href="https://stackoverflow.com/a/22093579">stackoverflow</a>
   */
  //-- Timeline gets called in the JavaFX Main thread
  private void prepareTimeline() {
    // Every frame to take any receivedTempMessages from queue and add to chart
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        if (!executor.isShutdown()) {
          addDataToSeries();
          if (xSeriesData >= 1) {
            // updateDetailedValues();
            // TODO: abstract?
          }
        }
      }
    }.start();
  }

  /**
   * Adds Part of code adapted from: <a
   * href="https://stackoverflow.com/a/22093579">stackoverflow</a>
   */
  private void addDataToSeries() {
    if (!receivedTempMessages.isEmpty()) {
      tempSeries.getData().add(
          new XYChart.Data<>("" + xSeriesData * (LINECHART_UPDATE_INTERVAL / 1000),
              receivedTempMessages.remove()));
      overviewTempSeries.getData().add(
          new XYChart.Data<>("" + xSeriesData * (LINECHART_UPDATE_INTERVAL / 1000),
              receivedTempMessages.remove()));
      humidSeries.getData().add(
          new XYChart.Data<>("" + xSeriesData * (LINECHART_UPDATE_INTERVAL / 1000),
              receivedHumidMessages.remove()));
      overviewHumidSeries.getData().add(
          new XYChart.Data<>("" + xSeriesData * (LINECHART_UPDATE_INTERVAL / 1000),
              receivedHumidMessages.remove()));
      co2Series.getData().add(
          new XYChart.Data<>("" + xSeriesData * (LINECHART_UPDATE_INTERVAL / 1000),
              receivedCo2Messages.remove()));
      overviewCo2Series.getData().add(
          new XYChart.Data<>("" + xSeriesData * (LINECHART_UPDATE_INTERVAL / 1000),
              receivedCo2Messages.remove()));
      xSeriesData++;
    }
    // remove points to keep us at no more than MAX_DATA_POINTS
    if (tempSeries.getData().size() > MAX_DATA_POINTS) {
      tempSeries.getData().remove(0, tempSeries.getData().size() - MAX_DATA_POINTS);
    }
    if (humidSeries.getData().size() > MAX_DATA_POINTS) {
      humidSeries.getData().remove(0, humidSeries.getData().size() - MAX_DATA_POINTS);
    }
    if (co2Series.getData().size() > MAX_DATA_POINTS) {
      co2Series.getData().remove(0, co2Series.getData().size() - MAX_DATA_POINTS);
    }
    // update
    xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
    xAxis.setUpperBound(xSeriesData - 1);
  }

  // For modifying other Linecharts
  public void setTempLineChart(LineChart tempLineChart) {
    this.tempLineChart = tempLineChart;
  }

  public void setHumidityLineChart(LineChart humidityLineChart) {
    this.humidityLineChart = humidityLineChart;
  }

  public void setCo2LineChart(LineChart co2LineChart) {
    this.co2LineChart = co2LineChart;
  }

  public void setdDashboardLineChart(LineChart dashboardLineChart) {
    this.dashboardLineChart = dashboardLineChart;
  }

  public void setIvParameterSpec(IvParameterSpec ivParameterSpec) {
    this.ivParameterSpec = ivParameterSpec;
  }

  public IvParameterSpec getIvParameterSpec() {
    return this.ivParameterSpec;
  }

  public void setTempClientHandler(ClientHandler tempClientHandler) {
    this.tempClientHandler = tempClientHandler;
  }

  public void setHumidClientHandler(ClientHandler humidClientHandler) {
    this.humidClientHandler = humidClientHandler;
  }

  public void setCo2ClientHandler(ClientHandler co2ClientHandler) {
    this.co2ClientHandler = co2ClientHandler;
  }

  public Series getTempSeries() {
    return tempSeries;
  }

  public Series getHumidSeries() {
    return humidSeries;
  }

  public Series getCo2Series() {
    return co2Series;
  }

  public void setTempSeries(Series tempSeries) {
    this.tempSeries = tempSeries;
  }

  public void setHumidSeries(Series humidSeries) {
    this.humidSeries = humidSeries;
  }

  public void setCo2Series(Series co2Series) {
    this.co2Series = co2Series;
  }

  public void setxAxis(NumberAxis xAxis) {
    this.xAxis = xAxis;
  }

  public NumberAxis getxAxis() {
    return xAxis;
  }
}
