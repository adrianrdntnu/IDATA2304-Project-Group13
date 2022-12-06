package no.ntnu.group13.greenhouse.server;

import static org.junit.Assert.assertEquals;

import javax.crypto.spec.IvParameterSpec;
import no.ntnu.group13.greenhouse.logic.EncryptAndDecryptMessage;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.Test;

/**
 * Tests the classes responsible for communicating with the MQTT broker.
 */
public class MqttTest {

  public final String broker = "tcp://129.241.152.12:1883";
  public final String testTopic = "group13/greenhouse/sensors/test";
  public final String testPublisherId = "group13_testPublisher";
  public final String testSubscriberId = "group13_testSubscriber";
  public final int qos = 0;
  private final IvParameterSpec ivParameterSpec = EncryptAndDecryptMessage.generateIv();

  @Test
  public void testSuccessfulPublisherConnection() {
    MqttPublisher mqttPublisher = new MqttPublisher(testTopic, broker, testPublisherId, qos,
        ivParameterSpec);
    mqttPublisher.startConnection();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailedPublisherConnection() {
    MqttPublisher mqttPublisher = new MqttPublisher("", "", "", qos, ivParameterSpec);
    mqttPublisher.startConnection();
  }

  @Test
  public void testSuccessfulSubscriberConnection() {
    MqttSubscriber mqttSubscriber = new MqttSubscriber(testTopic, broker, testSubscriberId, qos,
        ivParameterSpec);
    mqttSubscriber.startClient();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFailedSubscriberConnection() {
    MqttSubscriber mqttSubscriber = new MqttSubscriber("", "", "", qos, ivParameterSpec);
    mqttSubscriber.startClient();
  }

  @Test
  public void testSubscriberReceivedMessageFromPublisher()
      throws InterruptedException, MqttException {
    MqttPublisher mqttPublisher = new MqttPublisher(testTopic, broker, testPublisherId, qos,
        ivParameterSpec);
    MqttSubscriber mqttSubscriber = new MqttSubscriber(testTopic, broker, testSubscriberId, qos,
        ivParameterSpec);

    mqttPublisher.startConnection();
    mqttSubscriber.startClient();

    Double sentMessage = 1.00;

    mqttPublisher.publishMessageToBroker("" + sentMessage);

    // Waits to make sure message has arrived
    Thread.sleep(200);

    mqttPublisher.terminateConnection();
    mqttSubscriber.disconnectClient();

    Double receivedMessage = mqttSubscriber.getData().get(0);

    assertEquals(receivedMessage, sentMessage);
  }
}
