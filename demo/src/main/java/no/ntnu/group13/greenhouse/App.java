package no.ntnu.group13.greenhouse;

import no.ntnu.group13.greenhouse.server.ReceiveData;
import no.ntnu.group13.greenhouse.server.SendData;

/**
 * App
 */
public class App {
	private String topic = "group13/greenhouse/sensors/temperature";

	public static void main(String[] args) {
		App app = new App();
		app.start();
		System.out.println("yyo!");
	}

	public void start() {
		ReceiveData recieveData = new ReceiveData(topic);
		SendData sendData = new SendData("test", topic);

		recieveData.run();
		sendData.run();
	}
}
