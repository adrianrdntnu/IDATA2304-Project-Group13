package no.ntnu.group13.greenhouse.client;

import no.ntnu.group13.greenhouse.logic.MQTT;
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
			receiveData = new ReceiveData(
					MQTT.TEMPERATURE_TOPIC, MQTT.BROKER, MQTT.CLIENT_ID, MQTT.QOS
			);
			receiveData.run();
		} catch (Exception e) {
			System.err.println(e);
		}
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
