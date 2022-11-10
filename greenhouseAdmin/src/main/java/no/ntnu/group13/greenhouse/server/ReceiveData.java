package no.ntnu.group13.greenhouse.server;

import java.util.ArrayList;
import java.util.List;

import no.ntnu.group13.greenhouse.javafx.controllers.MainWindowController;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Responsible for receiving data from the MQTT broker.
 */
public class ReceiveData implements MqttCallback {
  // Topic to receive data from
  private String topic;
  // Message received.
  private List<Double> data;
  private String broker;
  private String clientId;
  private int qos;
  private MqttClient client;
  private MainWindowController mainWindowController;

  /**
   * Creates a client that receives data from an MQTT broker.
   *
   * @param topic    The topic to subscribe from.
   * @param broker   The broker to receive data from.
   * @param clientId The id of the client
   * @param qos      The "Quality of Service"
   */
  public ReceiveData(String topic, String broker, String clientId, int qos) {
    this.broker = broker;
    this.clientId = clientId;
    this.qos = qos;
    this.topic = topic;
    this.data = new ArrayList<>();
  }

  /**
   * Starts the client and receives data from the MQTT broker.
   */
  public void run() {
    try {
      client = new MqttClient(broker, clientId, new MemoryPersistence());

      // connect options
      MqttConnectOptions options = new MqttConnectOptions();
      options.setConnectionTimeout(60);
      options.setKeepAliveInterval(60);

      // setup callback using MqttCallback
      client.setCallback(this);

      client.connect(options);
      client.subscribe(this.topic, this.qos);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void setMainWindowController(MainWindowController controller) {
    this.mainWindowController = controller;
  }

  @Override
  public void connectionLost(Throwable throwable) {
    System.out.println("Connection lost. " + throwable);
  }

  @Override
  public void messageArrived(String topic, MqttMessage mqttMessage) {
    String message = new String(mqttMessage.getPayload());

    System.out.println("Received from topic: " + topic);
    System.out.println("Message: " + message);
    System.out.println("----------------");

    // **Do something with the message**
    this.data.add(Double.parseDouble(message));

    this.mainWindowController.receiveMessageFromSensor(Double.parseDouble(message));
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    System.out.println("deliveryComplete: " + iMqttDeliveryToken);
  }

  /**
   * Disconnects the client from the MQTT broker.
   *
   * @throws MqttException
   */
  public void disconnectClient() throws MqttException {
    this.client.disconnect();
  }

  /**
   * Returns the clientID.
   *
   * @return The clientID
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Returns a list of data received.
   *
   * @return The list of data received
   */
  public List<Double> getData() {
    return this.data;
  }

  /**
   * Returns the topic data was received from.
   *
   * @return The topic data was received from
   */
  public String getTopic() {
    return topic;
  }
}

