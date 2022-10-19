package no.ntnu.group13.greenhouse.server;

import javax.swing.text.AbstractDocument.BranchElement;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SendData {

    private String data;
    private String topic;

    public SendData(String data, String topic) {
        this.data = data;
        this.topic = topic;
    }

    public void run() {
        try {
            MqttClient client = new MqttClient(mqtt.BROKER, mqtt.CLIENT_ID, new MemoryPersistence());

            // connect options
            MqttConnectOptions options = new MqttConnectOptions();
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);

            // connect
            client.connect(options);

            // create message and setup QoS
            MqttMessage message = new MqttMessage(data.getBytes());
            message.setQos(mqtt.QOS);

            // publish message
            client.publish(topic, message);
            System.out.println("Message published");
            System.out.println("Topic: " + topic);
            System.out.println("Message content: " + data);

            // disconnect
            client.disconnect();

            // close client
            client.close();
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }
}
