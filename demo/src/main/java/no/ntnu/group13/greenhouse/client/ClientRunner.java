package no.ntnu.group13.greenhouse.client;

import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.server.ReceiveData;

public class ClientRunner {
  ReceiveData receiveData;

  public static void main(String[] args) {
    ClientRunner clientRunner = new ClientRunner();
    clientRunner.start();
  }

  /**
   * Receives a message from an MQTT topic.
   */
  public void start() {
    try {
      receiveFromTopic(LOGIC.SENSORS_TOPIC);
      receiveData.run();
    } catch (Exception e) {
      System.err.println(e);
    }
  }

  /**
   * Receives data from a specified topic.
   *
   * @param topic topic to subscribe to
   */
  private void receiveFromTopic(String topic) {
    receiveData = new ReceiveData(
        topic, LOGIC.BROKER, LOGIC.CLIENT_ID, LOGIC.QOS
    );
  }

  /**
   * Gets the topic the client subscribes to.
   *
   * @return The topic the client subscribes to.
   */
  public String getClientTopic() {
    return receiveData.getTopic();
  }
}
