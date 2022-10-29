package no.ntnu.group13.greenhouse;

import no.ntnu.group13.greenhouse.logic.LOGIC;
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
      ReceiveData receiveData = new ReceiveData(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.CLIENT_ID, LOGIC.QOS);
      // ClientRunner clientRunner = new ClientRunner();
      SendData sendData = new SendData(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER, LOGIC.SENSOR_ID, LOGIC.QOS);
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
