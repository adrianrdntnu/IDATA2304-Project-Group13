package no.ntnu.group13.greenhouse.sensors;

import no.ntnu.group13.greenhouse.logic.MQTT;
import no.ntnu.group13.greenhouse.server.SendData;

public class SensorRunner {

	public static void main(String[] args) {
		SensorRunner sensorRunner = new SensorRunner();
		sensorRunner.start();
	}

	/**
	 * Sends a message to the MQTT topic.
	 */
	public void start() {
		try {
			SendData sendData = new SendData(MQTT.TEMPERATURE_TOPIC, MQTT.BROKER, MQTT.SENSOR_ID, MQTT.QOS);
			sendData.run();
		} catch (Exception e) {
			System.err.println(e);
		}
	}
}
