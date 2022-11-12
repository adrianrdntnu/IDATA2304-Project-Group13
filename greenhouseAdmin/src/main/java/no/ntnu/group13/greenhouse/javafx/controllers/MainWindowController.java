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
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import no.ntnu.group13.greenhouse.client.ClientHandler;
import no.ntnu.group13.greenhouse.logic.BinarySearchTree;
import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.sensors.Co2Sensor;
import no.ntnu.group13.greenhouse.sensors.HumiditySensor;
import no.ntnu.group13.greenhouse.sensors.Sensor;
import no.ntnu.group13.greenhouse.sensors.TemperatureSensor;

public class MainWindowController {

  // Sensor-client communication
  private final List<Double> temperatureValues = new ArrayList<>();
  private final List<Double> humidityValues = new ArrayList<>();
  private final List<Double> co2Values = new ArrayList<>();
  private final BinarySearchTree bstTemperatureTree = new BinarySearchTree();
  private final BinarySearchTree bstHumidityTree = new BinarySearchTree();
  private final BinarySearchTree bstCo2Tree = new BinarySearchTree();
  private Sensor temperatureSensor;
  private Sensor humiditySensor;
  private Sensor co2Sensor;
  private ClientHandler tempClientHandler;
  private ClientHandler humidClientHandler;
  private ClientHandler co2ClientHandler;
  private static final int GENERATE_VALUES = 10;
  private static final int VALUE_SPLIT = 2;

  // For Linechart
  private int xSeriesData = 0;
  private static final int MAX_DATA_POINTS = 50;
  private final XYChart.Series tempSeries = new XYChart.Series<>();
  private final XYChart.Series humidSeries = new XYChart.Series<>();
  private final XYChart.Series co2Series = new XYChart.Series<>();
  private ExecutorService executor;
  private final ConcurrentLinkedQueue<Number> receivedTempMessages = new ConcurrentLinkedQueue<>();
  private final ConcurrentLinkedQueue<Number> receivedHumidMessages = new ConcurrentLinkedQueue<>();
  private final ConcurrentLinkedQueue<Number> receivedCo2Messages = new ConcurrentLinkedQueue<>();
  private int tempValueCounter = 0;
  private int humidValueCounter = 0;
  private int co2ValueCounter = 0;

  @FXML
  private LineChart<?, ?> dashboardLineChart;
  @FXML
  private NumberAxis xAxis;
  @FXML
  private Button stopButton;
  @FXML
  private Button startButton;

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
    dashboardLineChart.getYAxis().setLabel("Temperature");
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
  public void stopRecordButton(ActionEvent actionEvent) {
    stopButton.setDisable(true);
    startButton.setDisable(false);

    executor.shutdown();
    stopSensor();
  }

  // Menu buttons
  @FXML
  public void dashboardMenuButton(ActionEvent actionEvent) {
    System.out.println("Dashboard");
  }

  @FXML
  public void tempMenuButton(ActionEvent actionEvent) {
    System.out.println("Temperature");
  }

  @FXML
  public void humidityMenuButton(ActionEvent actionEvent) {
    System.out.println("Humidity");
  }

  @FXML
  public void co2MenuButton(ActionEvent actionEvent) {
    System.out.println("Co2");
  }

  /**
   * Starts connection between clients and MQTT broker
   */
  private void startClients() {
    this.tempClientHandler.startClient();
    this.humidClientHandler.startClient();
    this.co2ClientHandler.startClient();
  }

  /**
   * Starts connection between the sensor and MQTT broker.
   */
  private void startSensors() {
    this.temperatureSensor = new TemperatureSensor(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER,
        LOGIC.TEMP_SENSOR, LOGIC.QOS, 10, 3);
    this.humiditySensor = new HumiditySensor(LOGIC.HUMIDITY_TOPIC, LOGIC.BROKER, LOGIC.HUMID_SENSOR,
        LOGIC.QOS, 10, 3);
    this.co2Sensor = new Co2Sensor(LOGIC.CO2_TOPIC, LOGIC.BROKER, LOGIC.CO2_SENSOR, LOGIC.QOS, 10,
        3);

    this.temperatureSensor.startConnection();
    this.humiditySensor.startConnection();
    this.co2Sensor.startConnection();
  }

  /**
   * Terminates connection to the sensor.
   */
  private void stopSensor() {
    this.temperatureSensor.terminateConnection();
  }

  /**
   * Receives and stores message from sensor.
   */
  public void receiveMessageFromSensor(BinarySearchTree tree, Queue<Number> queue,
      ClientHandler client) {
    Double d = client.getLastValue();
    tree.insert(d);
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

    startClients();
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
        // Generates new values to send to the client, created dynamically to prevent necessary
        // overloading at program startup.
        if ((xSeriesData % GENERATE_VALUES) == 0) {
          temperatureValues.addAll(
              temperatureSensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT));
          humidityValues.addAll(
              humiditySensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT));
          co2Values.addAll(
              co2Sensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT));
        }

        temperatureSensor.publishMessageToBroker("" + temperatureValues.get(xSeriesData));
        humiditySensor.publishMessageToBroker("" + humidityValues.get(xSeriesData));
        co2Sensor.publishMessageToBroker("" + co2Values.get(xSeriesData));

        // Waits for 1 second and HOPEFULLY the message has arrived by then.
        // TODO: continue directly after message is received.
        Thread.sleep(1000);

        receiveMessageFromSensor(bstTemperatureTree, receivedTempMessages, tempClientHandler);
        receiveMessageFromSensor(bstHumidityTree, receivedHumidMessages, humidClientHandler);
        receiveMessageFromSensor(bstCo2Tree, receivedCo2Messages, co2ClientHandler);

        // update
        xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
        xAxis.setUpperBound(xSeriesData - 1);

        if (temperatureSensor.getOnlineStatus()) {
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
        addDataToSeries();
      }
    }.start();
  }

  /**
   * Adds Part of code adapted from: <a
   * href="https://stackoverflow.com/a/22093579">stackoverflow</a>
   */
  private void addDataToSeries() {
    for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
      if (receivedTempMessages.isEmpty()) {
        break;
      }
      tempSeries.getData().add(new XYChart.Data<>("" + xSeriesData, receivedTempMessages.remove()));
      humidSeries.getData()
          .add(new XYChart.Data<>("" + xSeriesData, receivedHumidMessages.remove()));
      co2Series.getData().add(new XYChart.Data<>("" + xSeriesData, receivedCo2Messages.remove()));
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
}