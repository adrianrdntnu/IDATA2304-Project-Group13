package no.ntnu.group13.greenhouse;

import java.util.List;
import no.ntnu.group13.greenhouse.client.ClientHandler;
import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.sensors.Sensor;
import no.ntnu.group13.greenhouse.sensors.TemperatureSensor;
import no.ntnu.group13.greenhouse.server.MqttPublisher;
import no.ntnu.group13.greenhouse.server.MqttSubscriber;

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
      ClientHandler clientHandler = new ClientHandler(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER,
          LOGIC.CLIENT_ID, LOGIC.QOS);
      Sensor temperatureSensor = new TemperatureSensor(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER,
          LOGIC.SENSOR_ID, LOGIC.QOS);

      clientHandler.startClient();
      temperatureSensor.startConnection();

      // Generate values

      List<Double> temperatures = temperatureSensor.generateValuesAlternateTemps(5, 5);

      System.out.println(temperatures);

      // sends a value each second
      for (Double t : temperatures) {
        temperatureSensor.publishMessageToBroker(t.toString());
        Thread.sleep(500);
      }

      temperatureSensor.terminateConnection();

      // Sleeps so client has time to receive all data before it disconnects.
      System.out.println("Received messages: " + clientHandler.getData());
      System.out.println("Disconnecting client: " + clientHandler.getClientId());
      clientHandler.disconnectClient();

    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
