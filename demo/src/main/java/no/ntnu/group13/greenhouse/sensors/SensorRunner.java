package no.ntnu.group13.greenhouse.sensors;

import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.server.SendData;

public class SensorRunner {
  private SendData sendData;

  public static void main(String[] args) {
    SensorRunner sensorRunner = new SensorRunner();
    sensorRunner.start();
  }

  /**
   * Sends a message to the MQTT topic.
   */
  public void start() {
    try {
      sendToTopic(LOGIC.TEMPERATURE_TOPIC);
      sendData.run();
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  /**
   * Sends data to a topic.
   *
   * @param topic topic to send to
   */
  private void sendToTopic(String topic) {
    sendData = new SendData(topic, LOGIC.BROKER, LOGIC.SENSOR_ID, LOGIC.QOS);
  }
}
