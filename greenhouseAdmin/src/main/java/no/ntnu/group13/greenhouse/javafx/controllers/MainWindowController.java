package no.ntnu.group13.greenhouse.javafx.controllers;

import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.text.Text;
import no.ntnu.group13.greenhouse.client.ClientHandler;
import no.ntnu.group13.greenhouse.logic.BinarySearchTree;
import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.sensors.Sensor;
import no.ntnu.group13.greenhouse.sensors.TemperatureSensor;

public class MainWindowController {

  private final List<Double> temperatures = new ArrayList<>();
  private final BinarySearchTree bstTemperatureTree = new BinarySearchTree();
  private Sensor temperatureSensor;
  private Sensor humiditySensor;
  private ClientHandler clientHandler;
  private ExecutorService executor;
  private ConcurrentLinkedQueue<Number> receivedMessages = new ConcurrentLinkedQueue<>();
  private XYChart.Series series = new XYChart.Series();
  private static final int MAX_DATA_POINTS = 50;
  private static final int GENERATE_VALUES = 10;
  private static final int VALUE_SPLIT = 2;
  private int xSeriesData = 0;

  @FXML
  private LineChart<?, ?> lineChart;
  @FXML
  private Text mainHeader;
  @FXML
  private NumberAxis xAxis;
  @FXML
  private Text textHighValue;
  @FXML
  private Text textLowValue;
  @FXML
  private Button stopButton;

  /**
   * Initialize Controller.
   */
  public void initialize() {
    mainHeader.setText("Flowchart");

    xAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS);
    xAxis.setForceZeroInRange(false);
    xAxis.setAutoRanging(false);
    xAxis.setTickLabelsVisible(false);
    xAxis.setTickMarkVisible(false);
    xAxis.setMinorTickVisible(false);

    lineChart.getXAxis().setLabel("Time (seconds)");
    lineChart.getYAxis().setLabel("Temperature");
    lineChart.setAnimated(false);
    lineChart.setTitle("Animated Line Chart");
    lineChart.setHorizontalGridLinesVisible(true);

    // Set Name for Series
    series.setName("Temperature");

    // Add Chart Series
    lineChart.getData().addAll(series);
  }

  @FXML
  public void startRecordButton(ActionEvent actionEvent) {
    stopButton.setDisable(false);
    startRecording(LOGIC.TEMPERATURE_TOPIC);
  }

  @FXML
  public void stopRecordButton(ActionEvent actionEvent) {
    executor.shutdown();
    stopSensor();
  }

  /**
   * Starts connection between client and MQTT broker.
   *
   * @param topic topic to subscribe to
   */
  private void startClient(String topic) {
    this.clientHandler = new ClientHandler(topic, LOGIC.BROKER, LOGIC.CLIENT_ID,
        LOGIC.QOS);
    this.clientHandler.startClient();
  }

  /**
   * Starts connection between the sensor and MQTT broker.
   *
   * @param topic topic to publish data to
   */
  private void startSensor(String topic) {
    this.temperatureSensor = new TemperatureSensor(topic, LOGIC.BROKER, LOGIC.SENSOR_ID,
        LOGIC.QOS, 27.5, 2);
    this.temperatureSensor.startConnection();
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
  public void receiveMessageFromSensor() {
    Double d = this.clientHandler.getLastValue();
    this.bstTemperatureTree.insert(d);
    this.receivedMessages.add(d);
  }

  /**
   * Starts recording data received from the sensor. Code adapted from: <a
   * href="https://stackoverflow.com/a/22093579">stackoverflow</a>
   */
  public void startRecording(String topic) {
    executor = Executors.newCachedThreadPool(r -> {
      Thread thread = new Thread(r);
      thread.setDaemon(true);
      return thread;
    });

    startClient(topic);
    startSensor(topic);

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
          temperatures.addAll(
              temperatureSensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT));
        }

        temperatureSensor.publishMessageToBroker("" + temperatures.get(xSeriesData));
        // Waits for 1 second and HOPEFULLY the message has arrived by then.
        // TODO: continue directly after message is received.
        Thread.sleep(1000);
        receiveMessageFromSensor();

        textHighValue.setText("" + bstTemperatureTree.getMaxValue());
        textLowValue.setText("" + bstTemperatureTree.getMinValue());

        executor.execute(this);
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
    // Every frame to take any receivedMessages from queue and add to chart
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
      if (receivedMessages.isEmpty()) {
        break;
      }
      series.getData().add(new XYChart.Data<>("" + xSeriesData++, receivedMessages.remove()));
    }
    // remove points to keep us at no more than MAX_DATA_POINTS
    if (series.getData().size() > MAX_DATA_POINTS) {
      series.getData().remove(0, series.getData().size() - MAX_DATA_POINTS);
    }
    // update
    xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
    xAxis.setUpperBound(xSeriesData - 1);
  }
}