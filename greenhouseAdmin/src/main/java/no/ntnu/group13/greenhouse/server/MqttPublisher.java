package no.ntnu.group13.greenhouse.server;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import no.ntnu.group13.greenhouse.logic.EncryptAndDecryptMessage;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Establishes a connection & publishes data to an MQTT broker. Code adapted from: <a
 * href="https://www.emqx.com/en/blog/how-to-use-mqtt-in-java">emx.com</a>
 */
public class MqttPublisher {

  private final String topic;
  private final String broker;
  private final String sensorID;
  private final int qos;
  private MqttClient client;

  // For encrypting
  protected IvParameterSpec ivParameterSpec;

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
   * Creates a client that sends data to an MQTT broker with an IvParameterSpec for encryption.
   *
   * @param topic           The topic to upload to.
   * @param broker          The broker to connect to.
   * @param sensorID        The client id.
   * @param qos             The "Quality of Service"
   * @param ivParameterSpec Initialization vector of the publisher
   */
  public MqttPublisher(String topic, String broker, String sensorID, int qos,
      IvParameterSpec ivParameterSpec) {
    this.topic = topic;
    this.broker = broker;
    this.sensorID = sensorID;
    this.qos = qos;
    this.ivParameterSpec = ivParameterSpec;
  }

  /**
   * Starts the connection between the client and the server, and sends data to the MQTT broker.
   */
  public void startConnection() {
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

  public void setIvParameterSpec(IvParameterSpec ivParameterSpec) {
    this.ivParameterSpec = ivParameterSpec;
  }

  /**
   * Sends a message to the MQTT broker.
   *
   * @param message message to
   */
  public void publishMessageToBroker(String message) {
    try {
      // Encrypt message
      SecretKey key = EncryptAndDecryptMessage.getKeyFromPassword("group13", "12345678");
      String algorithm = EncryptAndDecryptMessage.algorithm;

      String cipherText = EncryptAndDecryptMessage.encrypt(algorithm, message, key,
          ivParameterSpec);

      // create message and setup QoS
      MqttMessage m = new MqttMessage(cipherText.getBytes());
      m.setQos(this.qos);

      // publish message
      client.publish(topic, m);
//      System.out.println("Message sent to topic: " + topic);
//      System.out.println("Message content: " + new String(m.getPayload()));
//      System.out.println("Original message: " + message);
//      System.out.println("----------------");
    } catch (MqttException e) {
      throw new RuntimeException(e);
    } catch (InvalidAlgorithmParameterException e) {
      throw new RuntimeException(e);
    } catch (NoSuchPaddingException e) {
      throw new RuntimeException(e);
    } catch (IllegalBlockSizeException e) {
      throw new RuntimeException(e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (BadPaddingException e) {
      throw new RuntimeException(e);
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    } catch (InvalidKeySpecException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Terminates connection with the MQTT broker.
   */
  public void terminateConnection() {
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
