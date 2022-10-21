package no.ntnu.group13.greenhouse;

import no.ntnu.group13.greenhouse.logic.MQTT;
import no.ntnu.group13.greenhouse.server.ReceiveData;
import no.ntnu.group13.greenhouse.server.SendData;

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
      ReceiveData receiveData = new ReceiveData(MQTT.TEMPERATURE_TOPIC, MQTT.BROKER, MQTT.CLIENT_ID, MQTT.QOS);
      // ClientRunner clientRunner = new ClientRunner();
      SendData sendData = new SendData(MQTT.TEMPERATURE_TOPIC, MQTT.BROKER, MQTT.SENSOR_ID, MQTT.QOS);
      // SensorRunner sensorRunner = new SensorRunner();

      receiveData.run();
      // clientRunner.start();
      sendData.run();
      // sensorRunner.start();

      System.out.println("Received temperatures: " + receiveData.getData());

      System.out.println("Disconnecting client: " + receiveData.getClientId());
      receiveData.disconnectClient();
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
