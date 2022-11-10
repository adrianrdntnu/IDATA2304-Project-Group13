package no.ntnu.group13.greenhouse;

import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.sensors.Sensor;
import no.ntnu.group13.greenhouse.sensors.TemperatureSensor;
import no.ntnu.group13.greenhouse.server.PublishData;
import no.ntnu.group13.greenhouse.server.SendData;

import java.util.List;

/**
 * Starts a connection between a Sensor and a Client.
 */
public class App {

  public static void main(String[] args) {
    App app = new App();
    app.start();
  }

  public void start() {
    try {
      PublishData receiveData = new PublishData(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.CLIENT_ID, LOGIC.QOS);
      SendData sendData = new SendData(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.SENSOR_ID, LOGIC.QOS);

      receiveData.run();
      sendData.start();

      // Generate values
      Sensor temperatureSensor = new TemperatureSensor();
      List<Double> temperatures = temperatureSensor.generateValuesAlternateTemps(5, 5);

      System.out.println(temperatures);

      // sends a value each second
      for (Double t : temperatures) {
        sendData.sendMessage(t.toString());
        Thread.sleep(500);
      }

      sendData.stop();

      // Sleeps so client has time to receive all data before it disconnects.
      System.out.println("Received messages: " + receiveData.getData());
      System.out.println("Disconnecting client: " + receiveData.getClientId());
      receiveData.disconnectClient();

    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
