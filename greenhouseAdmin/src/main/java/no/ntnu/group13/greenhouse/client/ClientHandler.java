package no.ntnu.group13.greenhouse.client;

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
import no.ntnu.group13.greenhouse.server.MqttSubscriber;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Responsible for establishing a connection to the MQTT broker.
 */
public class ClientHandler extends MqttSubscriber {

  Double mostRecentMessage;

  /**
   * Creates a client with a connection to an MQTT broker.
   *
   * @param topic    mqtt topic
   * @param broker   mqtt broker
   * @param clientId the client id
   * @param qos      qos value
   */
  public ClientHandler(String topic, String broker, String clientId, int qos,
      IvParameterSpec ivParameterSpec) {
    super(topic, broker, clientId, qos, ivParameterSpec);
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

    System.out.println("Received from topic: " + topic);
    System.out.println("Message: " + message);
    System.out.println("Decrypted message: " + plainText);
    System.out.println("----------------");

    // **Do something with the message**
    this.mostRecentMessage = Double.parseDouble(plainText);
  }

  public Double getMostRecentMessage() {
    return mostRecentMessage;
  }
}
