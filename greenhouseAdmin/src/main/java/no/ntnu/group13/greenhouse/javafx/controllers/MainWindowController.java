package no.ntnu.group13.greenhouse.javafx.controllers;

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
import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.sensors.Sensor;
import no.ntnu.group13.greenhouse.sensors.TemperatureSensor;
import no.ntnu.group13.greenhouse.server.MqttPublisher;
import no.ntnu.group13.greenhouse.server.MqttSubscriber;

public class MainWindowController {

  private List<Double> temperatures;
  private Sensor temperatureSensor;
  private MqttPublisher mqttPublisher;
  private ExecutorService executor;
  private ConcurrentLinkedQueue<Number> receivedMessages = new ConcurrentLinkedQueue<>();
  private XYChart.Series series = new XYChart.Series();
  private static final int MAX_DATA_POINTS = 50;
  private static final int GENERATE_VALUES = 10;
  private static final int VALUE_SPLIT = 2;
  private int xSeriesData = 0;
  private double lowValue = 0;
  private double highValue = 0;

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
    startRecording();
  }

  @FXML
  public void stopRecordButton(ActionEvent actionEvent) {
    executor.shutdown();
    stopSensor();
  }

  public void startRecording() {
    executor = Executors.newCachedThreadPool(r -> {
      Thread thread = new Thread(r);
      thread.setDaemon(true);
      return thread;
    });

    startClient(this);
    startSensor();

    AddToQueue addToQueue = new AddToQueue();
    executor.execute(addToQueue);
    //-- Prepare Timeline
    prepareTimeline();
  }

  private class AddToQueue implements Runnable {

    public void run() {
      try {
        // Generates new values to send to the client, created dynamically to prevent overloading at start.
        if ((xSeriesData % GENERATE_VALUES) == 0 && xSeriesData >= 10) {
          temperatures.addAll(
              temperatureSensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT));
        }

        mqttPublisher.publishMessageToBroker("" + temperatures.get(xSeriesData));

        //textHighValue.setText("" + temperatureSensor.getTree().getMaxValue());
        textHighValue.setText("" + highValue);
        //textLowValue.setText("" + temperatureSensor.getTree().getMinValue());
        textLowValue.setText("" + lowValue);

        Thread.sleep(1000);
        executor.execute(this);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
  }

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

  public void receiveMessageFromSensor(Double d) {
    this.receivedMessages.add(d);
    if (d > highValue) {
      this.highValue = d;
    } else if (d < lowValue) {
      lowValue = d;
    }

    if (d != 0 && lowValue == 0) {
      lowValue = d;
    }
  }

  private void startClient(MainWindowController mainWindowController) {
    try {
      MqttSubscriber receiveData = new MqttSubscriber(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER,
          LOGIC.CLIENT_ID, LOGIC.QOS);
      receiveData.setMainWindowController(mainWindowController);
      receiveData.startClient();
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  private void startSensor() {
    this.mqttPublisher = new MqttPublisher(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.SENSOR_ID,
        LOGIC.QOS);
    mqttPublisher.startConnection();

    // Generate initial values at first start.
    if (temperatures == null) {
      this.temperatureSensor = new TemperatureSensor(27.5, 2);
      this.temperatures = this.temperatureSensor.generateValuesAlternateTemps(GENERATE_VALUES,
          VALUE_SPLIT);
    }
  }

  private void stopSensor() {
    mqttPublisher.terminateConnection();
  }

  public MqttPublisher getSensor() {
    return this.mqttPublisher;
  }
}