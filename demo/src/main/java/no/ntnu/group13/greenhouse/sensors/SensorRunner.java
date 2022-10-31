package no.ntnu.group13.greenhouse.sensors;

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
      // SendData sendData = new SendData(MQTT.TEMPERATURE_TOPIC, MQTT.BROKER, MQTT.SENSOR_ID, MQTT.QOS);
      // sendData.run();

      Sensor temperatureSensor = new TemperatureSensor(27, 0.1);

      System.out.println(temperatureSensor.generateValuesAlternateTemps(25, 5));

    } catch (Exception e) {
      System.err.println(e);
    }
  }
}
