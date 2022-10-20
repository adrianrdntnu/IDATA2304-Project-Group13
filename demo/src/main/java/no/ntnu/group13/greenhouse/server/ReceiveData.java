package no.ntnu.group13.greenhouse.server;

import no.ntnu.group13.greenhouse.logic.MQTT;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

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
			MqttClient client = new MqttClient(broker, clientId, new MemoryPersistence());

			final List<Double> data;

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

	@Override
	public void connectionLost(Throwable cause) {
		System.out.println("connectionLost: " + cause.getMessage());
	}

	@Override
	public void messageArrived(String topic, MqttMessage m) {
		String message = new String(m.getPayload());
		addToList(Double.parseDouble(message));

		System.out.println(this.data);

		// System.out.println("topic: " + topic);
		// System.out.println("message content: " + message);
		// System.out.println("----------------");
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("deliveryComplete---------" + token.isComplete());
	}

	/**
	 * Returns a list of data received.
	 *
	 * @return The list of data received
	 */
	public List<Double> getData() {
		return this.data;
	}

	public void addToList(Double d) {
		this.data.add(d);
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
