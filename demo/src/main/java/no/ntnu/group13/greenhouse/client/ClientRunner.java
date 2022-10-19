package no.ntnu.group13.greenhouse.client;

import no.ntnu.group13.greenhouse.server.ReceiveData;

public class ClientRunner {
	private String topic = "group13/greenhouse/sensors/temperature";

	public static void main(String[] args) {
		ClientRunner clientRunner = new ClientRunner();
		clientRunner.start();
	}

	/**
	 *
	 */
	public void start() {
		ReceiveData recieveData = new ReceiveData(topic);

		recieveData.run();
	}
}
