package no.ntnu.group13.greenhouse.javafx.controllers;

import java.util.Queue;
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
import javax.crypto.spec.IvParameterSpec;
import no.ntnu.group13.greenhouse.client.ClientHandler;
import no.ntnu.group13.greenhouse.logic.BinarySearchTree;
import no.ntnu.group13.greenhouse.logic.EncryptAndDecryptMessage;
import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.sensors.Co2Sensor;
import no.ntnu.group13.greenhouse.sensors.HumiditySensor;
import no.ntnu.group13.greenhouse.sensors.TemperatureSensor;
import org.eclipse.paho.client.mqttv3.MqttException;

public class OverviewWindowController extends WindowController {

  private int updatecounter = 0;

  @FXML
  private Button stopButton;
  @FXML
  private Button startButton;
  @FXML
  private Text textTempCurrentOverview;
  @FXML
  private Text textTempHighOverview;
  @FXML
  private Text textTempLowOverview;
  @FXML
  private Text textHumidCurrentOverview;
  @FXML
  private Text textHumidHighOverview;
  @FXML
  private Text textHumidLowOverview;
  @FXML
  private Text textCo2CurrentOverview;
  @FXML
  private Text textCo2HighOverview;
  @FXML
  private Text textCo2LowOverview;

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
    overviewTempSeries.setName("Temperature");
    overviewHumidSeries.setName("Humidity");
    overviewCo2Series.setName("Co2");

    // Generates initialization vector for encryption and decryption
    this.ivParameterSpec = EncryptAndDecryptMessage.generateIv();

    // Clients
    this.tempClientHandler = new ClientHandler(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER,
        LOGIC.TEMP_CLIENT, LOGIC.QOS, ivParameterSpec);
    this.humidClientHandler = new ClientHandler(LOGIC.HUMIDITY_TOPIC, LOGIC.BROKER,
        LOGIC.HUMID_CLIENT, LOGIC.QOS, ivParameterSpec);
    this.co2ClientHandler = new ClientHandler(LOGIC.CO2_TOPIC, LOGIC.BROKER, LOGIC.CO2_CLIENT,
        LOGIC.QOS, ivParameterSpec);

    // Add Chart Series
    dashboardLineChart.getData().addAll(overviewTempSeries, overviewHumidSeries, overviewCo2Series);
  }

  @FXML
  public void startRecordButton(ActionEvent actionEvent) {
    stopButton.setDisable(false);
    startButton.setDisable(true);

    initializeLineCharts();
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
    // Adds value twice because the value is removed after it's applied to a LineChart.
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
            updateDetailedValues(currentTemp, highTemp, lowTemp, textTempCurrentOverview,
                textTempHighOverview, textTempLowOverview, TEMP_SYMBOL);
            updateDetailedValues(currentTemp, highTemp, lowTemp, textTempCurrent, textTempHigh,
                textTempLow, TEMP_SYMBOL);

            updateDetailedValues(currentHumid, highHumid, lowHumid, textHumidCurrentOverview,
                textHumidHighOverview, textHumidLowOverview, HUMID_SYMBOL);
            updateDetailedValues(currentHumid, highHumid, lowHumid, textHumidCurrent, textHumidHigh,
                textHumidLow, HUMID_SYMBOL);

            updateDetailedValues(currentCo2, highCo2, lowCo2, textCo2CurrentOverview,
                textCo2HighOverview, textCo2LowOverview, CO2_SYMBOL);
            updateDetailedValues(currentCo2, highCo2, lowCo2, textCo2Current, textCo2High,
                textCo2Low, CO2_SYMBOL);
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

  /**
   * Initializes the lineCharts on other panes.
   */
  public void initializeLineCharts() {
    tempSeries.setName("Temperature");
    humidSeries.setName("Humidity");
    co2Series.setName("Co2");

    tempLineChart.getData().addAll(tempSeries);
    humidityLineChart.getData().addAll(humidSeries);
    co2LineChart.getData().addAll(co2Series);
  }

  /**
   * Updates detailed values.
   * Only updates the stored highValue and lowValue once the method is run twice.
   * Because of this the order of which the method is called is important.
   */
  public void updateDetailedValues(Double currentValue, Double highValue, Double lowValue,
      Text currentText, Text highText, Text lowText, String symbol) {

    currentText.setText(currentValue + symbol);

    if (currentValue > highValue) {
      highText.setText(currentValue + symbol);
    }
    if (currentValue < lowValue || lowValue == 0.0) {
      lowText.setText(currentValue + symbol);
    }

    updatecounter++;

    if (updatecounter % 2 == 0) {
      switch (symbol) {
        case TEMP_SYMBOL:
          if (currentValue > highTemp) {
            highTemp = currentValue;
          }
          if (currentValue < lowTemp || lowTemp == 0.0) {
            lowTemp = currentValue;
          }
          break;
        case HUMID_SYMBOL:
          if (currentValue > highHumid) {
            highHumid = currentValue;
          }
          if (currentValue < lowHumid || lowHumid == 0.0) {
            lowHumid = currentValue;
          }
          break;
        case CO2_SYMBOL:
          if (currentValue > highCo2) {
            highCo2 = currentValue;
          }
          if (currentValue < lowCo2 || lowCo2 == 0.0) {
            lowCo2 = currentValue;
          }
          break;
      }
    }
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

  public void setTempTexts(Text textTempCurrent, Text textTempHigh, Text textTempLow) {
    this.textTempCurrent = textTempCurrent;
    this.textTempHigh = textTempHigh;
    this.textTempLow = textTempLow;
  }

  public void setHumidTexts(Text textHumidCurrent, Text textHumidHigh, Text textHumidLow) {
    this.textHumidCurrent = textHumidCurrent;
    this.textHumidHigh = textHumidHigh;
    this.textHumidLow = textHumidLow;
  }

  public void setCo2Texts(Text textCo2Current, Text textCo2High, Text textCo2Low) {
    this.textCo2Current = textCo2Current;
    this.textCo2High = textCo2High;
    this.textCo2Low = textCo2Low;
  }
}