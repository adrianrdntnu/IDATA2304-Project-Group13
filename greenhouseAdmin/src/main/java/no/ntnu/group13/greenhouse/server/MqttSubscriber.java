package no.ntnu.group13.greenhouse.server;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import no.ntnu.group13.greenhouse.logic.EncryptAndDecryptMessage;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Establishes a connection & subscribes to a topic from an MQTT broker. Code adapted from: <a
 * href="https://www.emqx.com/en/blog/how-to-use-mqtt-in-java">emx.com</a>
 */
public class MqttSubscriber implements MqttCallback {

  // Topic to receive data from
  private final String topic;
  private final List<Double> data;
  private final String broker;
  private final String clientId;
  private final int qos;
  private MqttClient client;

  // For decrypting
  protected IvParameterSpec ivParameterSpec;

  /**
   * Creates a client that receives data from an MQTT broker.
   *
   * @param topic    The topic to subscribe from.
   * @param broker   The broker to receive data from.
   * @param clientId The id of the client
   * @param qos      The "Quality of Service"
   */
  public MqttSubscriber(String topic, String broker, String clientId, int qos) {
    this.broker = broker;
    this.clientId = clientId;
    this.qos = qos;
    this.topic = topic;
    this.data = new ArrayList<>();
  }

  /**
   * Creates a client that receives data from an MQTT broker with an IvParameterSpec for
   * decryption.
   *
   * @param topic           The topic to subscribe from.
   * @param broker          The broker to receive data from.
   * @param clientId        The id of the client
   * @param qos             The "Quality of Service"
   * @param ivParameterSpec Initialization vector of the subscriber
   */
  public MqttSubscriber(String topic, String broker, String clientId, int qos,
      IvParameterSpec ivParameterSpec) {
    this.broker = broker;
    this.clientId = clientId;
    this.qos = qos;
    this.topic = topic;
    this.data = new ArrayList<>();
    this.ivParameterSpec = ivParameterSpec;
  }

  /**
   * Starts the client and receives data from the MQTT broker.
   */
  public void startClient() {
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
    } catch (MqttException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void connectionLost(Throwable throwable) {
    System.out.println("Connection lost. " + throwable);
  }

  @Override
  public void messageArrived(String topic, MqttMessage mqttMessage)
      throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException,
      NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
    String message = new String(mqttMessage.getPayload());

    SecretKey key = EncryptAndDecryptMessage.getKeyFromPassword("group13", "12345678");
    String algorithm = EncryptAndDecryptMessage.algorithm;

    String plainText = EncryptAndDecryptMessage.decrypt(algorithm, message, key,
        ivParameterSpec);

//    System.out.println("Received from topic: " + topic);
//    System.out.println("Message: " + message);
//    System.out.println("Message decrypted: " + plainText);
//    System.out.println("----------------");

    // **Do something with the message**
    this.data.add(Double.parseDouble(plainText));
  }

  @Override
  public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    System.out.println("deliveryComplete: " + iMqttDeliveryToken);
  }

  /**
   * Disconnects the client from the MQTT broker.
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

