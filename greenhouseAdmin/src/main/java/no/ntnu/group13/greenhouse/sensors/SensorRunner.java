package no.ntnu.group13.greenhouse.sensors;

import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.server.MqttPublisher;

public class SensorRunner {

  private MqttPublisher mqttPublisher;

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
      mqttPublisher.startConnection();
      mqttPublisher.publishMessageToBroker("1");
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
    mqttPublisher = new MqttPublisher(topic, LOGIC.BROKER, LOGIC.SENSOR_ID, LOGIC.QOS);
  }
}
