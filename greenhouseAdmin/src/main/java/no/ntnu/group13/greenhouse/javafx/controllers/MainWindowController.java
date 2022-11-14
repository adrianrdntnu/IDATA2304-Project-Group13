package no.ntnu.group13.greenhouse.javafx.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import no.ntnu.group13.greenhouse.client.ClientHandler;
import no.ntnu.group13.greenhouse.logic.BinarySearchTree;
import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.sensors.Co2Sensor;
import no.ntnu.group13.greenhouse.sensors.HumiditySensor;
import no.ntnu.group13.greenhouse.sensors.Sensor;
import no.ntnu.group13.greenhouse.sensors.TemperatureSensor;
import org.eclipse.paho.client.mqttv3.MqttException;

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
  private static final int GENERATE_VALUES = 20;
  private static final int VALUE_SPLIT = 2;
  private boolean clientOnlineStatus = false;
  private boolean sensorOnlineStatus = false;

  // For Linechart
  private int xSeriesData = 0;
  private static final int MAX_DATA_POINTS = 100;
  private static final int LINECHART_UPDATE_INTERVAL = 1000;
  private final XYChart.Series tempSeries = new XYChart.Series<>();
  private final XYChart.Series humidSeries = new XYChart.Series<>();
  private final XYChart.Series co2Series = new XYChart.Series<>();
  private ExecutorService executor;
  private final ConcurrentLinkedQueue<Number> receivedTempMessages = new ConcurrentLinkedQueue<>();
  private final ConcurrentLinkedQueue<Number> receivedHumidMessages = new ConcurrentLinkedQueue<>();
  private final ConcurrentLinkedQueue<Number> receivedCo2Messages = new ConcurrentLinkedQueue<>();

  // Value trackers
  private Double lowTemp = 0.0;
  private Double highTemp = 0.0;
  private Double lowHumid = 0.0;
  private Double highHumid = 0.0;
  private Double lowCo2 = 0.0;
  private Double highCo2 = 0.0;
  private Double currentTemp;
  private Double currentHumid;
  private Double currentCo2;

  @FXML
  private LineChart<?, ?> dashboardLineChart;
  @FXML
  private NumberAxis xAxis;
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
    this.clientOnlineStatus = true;
  }

  /**
   * Starts connection between the sensor and MQTT broker.
   */
  private void startSensors() {
    this.temperatureSensor = new TemperatureSensor(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER,
        LOGIC.TEMP_SENSOR, LOGIC.QOS, 27.5, 1);
    this.humiditySensor = new HumiditySensor(LOGIC.HUMIDITY_TOPIC, LOGIC.BROKER, LOGIC.HUMID_SENSOR,
        LOGIC.QOS, 50, 3);
    this.co2Sensor = new Co2Sensor(LOGIC.CO2_TOPIC, LOGIC.BROKER, LOGIC.CO2_SENSOR, LOGIC.QOS, 100,
        3);

    this.temperatureSensor.startConnection();
    this.humiditySensor.startConnection();
    this.co2Sensor.startConnection();
    this.sensorOnlineStatus = true;
  }

  /**
   * Terminates connection from the sensor to the MQTT broker.
   */
  public void stopSensors() {
    this.temperatureSensor.terminateConnection();
    this.humiditySensor.terminateConnection();
    this.co2Sensor.terminateConnection();
    this.sensorOnlineStatus = false;
  }

  /**
   * Disconnects clients from the MQTT broker.
   */
  public void disconnectClients() {
    try {
      this.tempClientHandler.disconnectClient();
      this.humidClientHandler.disconnectClient();
      this.co2ClientHandler.disconnectClient();
      this.clientOnlineStatus = false;
    } catch (MqttException e) {
      System.out.println("Disconnect failed: " + e);
    }
  }

  /**
   * Receives and stores message from sensor.
   */
  public void receiveMessageFromSensor(BinarySearchTree tree, Queue<Number> queue,
      ClientHandler client) {
    Double d = client.getMostRecentMessage();
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
        // Generates new values to send to the client, created dynamically to prevent necessary
        // overloading at program startup.
        if ((xSeriesData % GENERATE_VALUES) == 0) {
//          temperatureValues.addAll(
//              temperatureSensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT));
//          humidityValues.addAll(
//              humiditySensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT));
//          co2Values.addAll(co2Sensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT));
          temperatureValues.addAll(temperatureSensor.generateValuesToNewMean(GENERATE_VALUES, 30));
          humidityValues.addAll(humiditySensor.generateValuesToNewMean(GENERATE_VALUES, 60));
          co2Values.addAll(co2Sensor.generateValuesToNewMean(GENERATE_VALUES, 75));
        }

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
            updateDetailedValues();
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
    for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
      if (receivedTempMessages.isEmpty()) {
        break;
      }
      tempSeries.getData().add(
          new XYChart.Data<>("" + xSeriesData * (LINECHART_UPDATE_INTERVAL / 1000),
              receivedTempMessages.remove()));
      humidSeries.getData().add(
          new XYChart.Data<>("" + xSeriesData * (LINECHART_UPDATE_INTERVAL / 1000),
              receivedHumidMessages.remove()));
      co2Series.getData().add(
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

  /**
   * Updates detailed values.
   */
  private void updateDetailedValues() {
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

  public boolean getSensorOnlineStatus() {
    return sensorOnlineStatus;
  }

  public boolean getClientOnlineStatus() {
    return clientOnlineStatus;
  }
}