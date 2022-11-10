package no.ntnu.group13.greenhouse.server;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Sends a collection of numbers to an MQTT broker.
 */
public class MqttPublisher {

  private String topic;
  private final String broker;
  private final String sensorID;
  private final int qos;

  private MqttClient client;

  /**
   * Creates a client that sends data to an MQTT broker.
   *
   * @param topic    The topic to upload to.
   * @param broker   The broker to connect to.
   * @param sensorID The client id.
   * @param qos      The "Quality of Service"
   */
  public MqttPublisher(String topic, String broker, String sensorID, int qos) {
    this.topic = topic;
    this.broker = broker;
    this.sensorID = sensorID;
    this.qos = qos;
  }

  /**
   * Starts the connection between the client and the server,
   * and sends data to the MQTT broker.
   * TODO: Send custom data.
   */
  public void start() {
    try {
      this.client = new MqttClient(broker, sensorID, new MemoryPersistence());

      // connect options
      MqttConnectOptions options = new MqttConnectOptions();
      options.setConnectionTimeout(60);
      options.setKeepAliveInterval(60);

      // connect
      client.connect(options);
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sends an individual message to the MQTT broker.
   *
   * @param message message to
   */
  public void sendMessage(String message) {
    try {
      // create message and setup QoS
      MqttMessage m = new MqttMessage(message.getBytes());
      m.setQos(this.qos);

      // publish message
      client.publish(topic, m);
      System.out.println("Message sent to topic: " + topic);
      System.out.println("Message content: " + new String(m.getPayload()));
      System.out.println("----------------");
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Terminates connection with the MQTT broker.
   */
  public void stop() {
    try {
      // disconnect
      client.disconnect();

      // close client
      client.close();
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }
  }
}
