package no.ntnu.group13.greenhouse.client;

import no.ntnu.group13.greenhouse.server.RecieveData;
import no.ntnu.group13.greenhouse.server.SendData;

public class ClientRunner {
    private String topic = "group13/greenhouse/sensors/temperature";

    public static void main(String[] args) {
        ClientRunner clientRunner = new ClientRunner();
        clientRunner.start();
    }

    public void start() {
        RecieveData recieveData = new RecieveData(topic);
        SendData sendData = new SendData("test", topic);
        
        recieveData.run();
        sendData.run();
    }
}
