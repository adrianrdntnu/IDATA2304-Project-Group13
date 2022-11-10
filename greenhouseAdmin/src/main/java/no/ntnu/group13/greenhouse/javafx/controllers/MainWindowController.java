package no.ntnu.group13.greenhouse.javafx.controllers;

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
import no.ntnu.group13.greenhouse.server.ReceiveData;
import no.ntnu.group13.greenhouse.server.SendData;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MainWindowController {

  private List<Double> temperatures;
  private Sensor temperatureSensor;
  private SendData sendData;
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
    update();
  }

  @FXML
  public void stopRecordButton(ActionEvent actionEvent) {
    executor.shutdown();
    stopSensor();
  }

  public void startSendingData() {
    try {
      SendData sendData = new SendData(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.SENSOR_ID, LOGIC.QOS);
      ReceiveData receiveData = new ReceiveData(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.CLIENT_ID, LOGIC.QOS);
      receiveData.setMainWindowController(this);

      receiveData.run();
      sendData.start();

      // Generate values
      Sensor temperatureSensor = new TemperatureSensor();
      List<Double> temperatures = temperatureSensor.generateValuesAlternateTemps(5, 1);

      System.out.println(temperatures);

      // sends a value each second
      for (Double t : temperatures) {
        sendData.sendMessage(t.toString());
        Thread.sleep(1000);

        // series.getData().add(new XYChart.Data("" + counter, values.get(counter)));
        // lineChart.getData().addAll(series);
        // counter++;
      }

      // Sleeps so client has time to receive all receivedMessages before it disconnects.
      System.out.println("Received messages: " + receiveData.getData());
      System.out.println("Disconnecting client: " + receiveData.getClientId());
      receiveData.disconnectClient();

    } catch (Exception e) {
      System.err.println(e);
    }
  }

  public void update() {
    executor = Executors.newCachedThreadPool(new ThreadFactory() {
      @Override
      public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
      }
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
          temperatures.addAll(temperatureSensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT));
        }

        sendData.sendMessage("" + temperatures.get(xSeriesData));

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
      if (receivedMessages.isEmpty()) break;
      series.getData().add(new XYChart.Data<>(""+ xSeriesData++, receivedMessages.remove()));
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
      ReceiveData receiveData = new ReceiveData(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.CLIENT_ID, LOGIC.QOS);
      receiveData.setMainWindowController(mainWindowController);
      receiveData.run();
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  private void startSensor() {
    this.sendData = new SendData(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.SENSOR_ID, LOGIC.QOS);
    sendData.start();

    // Generate initial values at first start.
    if (temperatures == null) {
      this.temperatureSensor = new TemperatureSensor(27.5, 2);
      this.temperatures = this.temperatureSensor.generateValuesAlternateTemps(GENERATE_VALUES, VALUE_SPLIT);
    }
  }

  private void stopSensor() {
    sendData.stop();
  }

  public SendData getSensor() {
    return this.sendData;
  }
}
