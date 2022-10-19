package no.ntnu.group13.greenhouse.server;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Responsible for receiving data from the MQTT broker.
 */
public class ReceiveData {
	// Topic to receive data from
	private String topic;
	// Message received.
	private String message;

	private MqttClient client;

	/**
	 * Receives data from an MQTT topic.
	 *
	 * @param topic The topic to receive data from
	 */
	public ReceiveData(String topic) {
		this.topic = topic;
	}

	/**
	 *
	 */
	public void run() {
		try {
			client = new MqttClient(mqtt.BROKER, mqtt.CLIENT_ID, new MemoryPersistence());

			// connect options
			MqttConnectOptions options = new MqttConnectOptions();
			options.setConnectionTimeout(60);
			options.setKeepAliveInterval(60);

			// setup callback
			client.setCallback(new MqttCallback() {
				public void connectionLost(Throwable cause) {
					System.out.println("connectionLost: " + cause.getMessage());
				}

				public void messageArrived(String topic, MqttMessage message) {
					System.out.println("topic: " + topic);
					System.out.println("Qos: " + message.getQos());
					System.out.println("message content: " + new String(message.getPayload()));
				}

				public void deliveryComplete(IMqttDeliveryToken token) {
					System.out.println("deliveryComplete---------" + token.isComplete());
				}
			});

			client.connect();
			client.subscribe(topic, mqtt.QOS);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
