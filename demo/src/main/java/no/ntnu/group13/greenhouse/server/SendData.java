package no.ntnu.group13.greenhouse.server;

import no.ntnu.group13.greenhouse.logic.RandomNormalDistributionData;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Sends a collection of numbers to an MQTT broker.
 */
public class SendData {

  private String topic;
  private final String broker;
  private final String sensorID;
  private final int qos;

  private final RandomNormalDistributionData rndData = new RandomNormalDistributionData();

  /**
   * Creates a client that sends data to an MQTT broker.
   *
   * @param topic    The topic to upload to.
   * @param broker   The broker to connect to.
   * @param sensorID The client id.
   * @param qos      The "Quality of Service"
   */
  public SendData(String topic, String broker, String sensorID, int qos) {
    this.topic = topic;
    this.broker = broker;
    this.sensorID = sensorID;
    this.qos = qos;

    // generates fake temperature values.
    this.rndData.generateRandomTemperatures(30, 3, 5);
  }

  /**
   * Starts the connection between the client and the server,
   * and sends data to the MQTT broker.
   */
  public void run() {
    try {
      MqttClient client = new MqttClient(broker, sensorID, new MemoryPersistence());

      // connect options
      MqttConnectOptions options = new MqttConnectOptions();
      options.setConnectionTimeout(60);
      options.setKeepAliveInterval(60);

      // connect
      client.connect(options);

      // Sends one "fake" temperature reading every second.
      int counter = 0;
      while (counter < this.rndData.getTemperatures().size()) {
        // create message and setup QoS
        MqttMessage message = new MqttMessage(
            this.rndData.getTemperatures().get(counter).toString().getBytes()
        );
        message.setQos(this.qos);

        // publish message
        client.publish(topic, message);
        System.out.println("Message sent to topic: " + topic);
        System.out.println("Message content: " + new String(message.getPayload()));
        System.out.println("----------------");

        Thread.sleep(1000);
        counter++;
      }

      // disconnect
      client.disconnect();

      // close client
      client.close();
    } catch (MqttException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  // TODO
  public void sendMessage(String message) {
    throw new RuntimeException("Not yet implemented!");
  }
}
