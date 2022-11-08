package no.ntnu.group13.greenhouse.javafx.controllers;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class MainWindowController {

  @FXML
  private LineChart<?, ?> lineChart;
  @FXML
  private Text mainHeader;
  private List<Double> values = new ArrayList<>();;
  private int counter = 0;

  private ExecutorService executor;
  private ConcurrentLinkedQueue<Number> dataQ1 = new ConcurrentLinkedQueue<>();
  private ConcurrentLinkedQueue<Number> dataQ2 = new ConcurrentLinkedQueue<>();
  private ConcurrentLinkedQueue<Number> dataQ3 = new ConcurrentLinkedQueue<>();
  private XYChart.Series series1 = new XYChart.Series();
  private XYChart.Series series2 = new XYChart.Series();
  private XYChart.Series series3 = new XYChart.Series();
  private static final int MAX_DATA_POINTS = 50;
  private int xSeriesData = 0;
  @FXML
  private NumberAxis xAxis;

  public void initialize() {
    mainHeader.setText("Flowchart");


    // series.getData().add(new XYChart.Data("1",2));
    // lineChart.getData().addAll(series);

    xAxis = new NumberAxis(0, MAX_DATA_POINTS, MAX_DATA_POINTS / 10);
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
    series1.setName("Series 1");
    series2.setName("Series 2");
    series3.setName("Series 3");

    // Add Chart Series
    lineChart.getData().addAll(series1, series2, series3);
  }

  @FXML
  public void startRecordButton(ActionEvent actionEvent) {
    // startSendingData();
    update();
  }
  public void startSendingData() {
    try {
      SendData sendData = new SendData(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.SENSOR_ID, LOGIC.QOS);
      ReceiveData receiveData = new ReceiveData(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.CLIENT_ID, LOGIC.QOS);
      receiveData.setMainWindowController(this);

      receiveData.run();

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
        counter++;
      }

      // Sleeps so client has time to receive all data before it disconnects.
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

    AddToQueue addToQueue = new AddToQueue();
    executor.execute(addToQueue);
    //-- Prepare Timeline
    prepareTimeline();
  }

  private class AddToQueue implements Runnable {
    public void run() {
      try {
        // add a item of random data to queue
        dataQ1.add(Math.random());
        dataQ2.add(Math.random());
        dataQ3.add(Math.random());

        Thread.sleep(500);
        executor.execute(this);
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
  }

  //-- Timeline gets called in the JavaFX Main thread
  private void prepareTimeline() {
    // Every frame to take any data from queue and add to chart
    new AnimationTimer() {
      @Override
      public void handle(long now) {
        addDataToSeries();
      }
    }.start();
  }

  private void addDataToSeries() {
    for (int i = 0; i < 20; i++) { //-- add 20 numbers to the plot+
      if (dataQ1.isEmpty()) break;
      series1.getData().add(new XYChart.Data<>(""+ xSeriesData++, dataQ1.remove()));
      series2.getData().add(new XYChart.Data<>(""+ xSeriesData++, dataQ2.remove()));
      series3.getData().add(new XYChart.Data<>(""+ xSeriesData++, dataQ3.remove()));
    }
    // remove points to keep us at no more than MAX_DATA_POINTS
    if (series1.getData().size() > MAX_DATA_POINTS) {
      series1.getData().remove(0, series1.getData().size() - MAX_DATA_POINTS);
    }
    if (series2.getData().size() > MAX_DATA_POINTS) {
      series2.getData().remove(0, series2.getData().size() - MAX_DATA_POINTS);
    }
    if (series3.getData().size() > MAX_DATA_POINTS) {
      series3.getData().remove(0, series3.getData().size() - MAX_DATA_POINTS);
    }
    // update
    xAxis.setLowerBound(xSeriesData - MAX_DATA_POINTS);
    xAxis.setUpperBound(xSeriesData - 1);
  }

  public void sendData(Double d) {
    this.values.add(d);
  }
}
