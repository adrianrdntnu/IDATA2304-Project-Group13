package no.ntnu.group13.greenhouse;

import java.util.List;
import javax.crypto.spec.IvParameterSpec;
import no.ntnu.group13.greenhouse.logic.EncryptAndDecryptMessage;
import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.sensors.Sensor;
import no.ntnu.group13.greenhouse.sensors.TemperatureSensor;
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
//      ClientHandler clientHandler = new ClientHandler(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.CLIENT_ID, LOGIC.QOS);
      Sensor temperatureSensor = new TemperatureSensor(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER,
          LOGIC.SENSOR_ID, LOGIC.QOS);

      MqttSubscriber mqttSubscriber = new MqttSubscriber(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER,
          LOGIC.CLIENT_ID, LOGIC.QOS);

//      clientHandler.startClient();
      mqttSubscriber.startClient();
      temperatureSensor.startConnection();

      // Generates initialization vector
      IvParameterSpec ivParameterSpec = EncryptAndDecryptMessage.generateIv();

      // Applies initialization vector
      mqttSubscriber.setIvParameterSpec(ivParameterSpec);
      temperatureSensor.setIvParameterSpec(ivParameterSpec);

      // Generate values
      List<Double> temperatures = temperatureSensor.generateValuesAlternateTemps(10, 5);

      System.out.println(temperatures);

      // sends a value each second
      for (Double t : temperatures) {
        temperatureSensor.publishMessageToBroker(t.toString());
        Thread.sleep(500);
      }

      temperatureSensor.terminateConnection();

      // Sleeps so client has time to receive all data before it disconnects.
      System.out.println("Received messages: " + mqttSubscriber.getData());
      System.out.println("Disconnecting client: " + mqttSubscriber.getClientId());
      mqttSubscriber.disconnectClient(); // <-- Not necessary?

    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
