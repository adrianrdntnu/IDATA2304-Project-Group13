package no.ntnu.group13.greenhouse.sensors;

import javax.crypto.spec.IvParameterSpec;

/**
 * Responsible for replicating a temperature sensor.
 */
public class TemperatureSensor extends Sensor {

  /**
   * Creates a temperature sensor with the mean value of 38 degrees and standard deviation of 0.1
   * degrees.
   *
   * @param topic    mqtt topic
   * @param broker   mqtt broker
   * @param sensorId the sensor id
   * @param qos      qos value
   */
  public TemperatureSensor(String topic, String broker, String sensorId, int qos,
      IvParameterSpec ivParameterSpec) {
    super(topic, broker, sensorId, qos, 38, 0.1, ivParameterSpec);
  }

  /**
   * Creates a temperature sensor with a custom mean value of and standard deviation.
   *
   * @param topic             mqtt topic
   * @param broker            mqtt broker
   * @param sensorId          the sensor id
   * @param qos               qos value
   * @param mean              mean value of the temperature
   * @param standardDeviation standard deviation of the temperature
   */
  public TemperatureSensor(String topic, String broker, String sensorId, int qos, double mean,
      double standardDeviation, IvParameterSpec ivParameterSpec) {
    super(topic, broker, sensorId, qos, mean, standardDeviation, ivParameterSpec);
  }
}
