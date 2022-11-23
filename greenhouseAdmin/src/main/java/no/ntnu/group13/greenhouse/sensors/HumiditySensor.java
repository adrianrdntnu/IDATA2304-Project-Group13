package no.ntnu.group13.greenhouse.sensors;

import javax.crypto.spec.IvParameterSpec;

public class HumiditySensor extends Sensor {

  /**
   * Creates a humidity sensor with the mean value of 100 and standard deviation of 1.
   *
   * @param topic    mqtt topic
   * @param broker   mqtt broker
   * @param sensorId the sensor id
   * @param qos      qos value
   */
  public HumiditySensor(String topic, String broker, String sensorId, int qos, IvParameterSpec ivParameterSpec) {
    super(topic, broker, sensorId, qos, 100, 1, ivParameterSpec);
  }

  /**
   * Creates a humidity sensor with custom mean value and standard deviation.
   *
   * @param topic             mqtt topic
   * @param broker            mqtt broker
   * @param sensorId          the sensor id
   * @param qos               qos value
   * @param mean              mean value of the humidity
   * @param standardDeviation standard deviation of the humidity
   */
  public HumiditySensor(String topic, String broker, String sensorId, int qos, double mean,
      double standardDeviation, IvParameterSpec ivParameterSpec) {
    super(topic, broker, sensorId, qos, mean, standardDeviation, ivParameterSpec);
  }

  @Override
  public double realisticNextNumber(double lastValue) {
    return rndd.getRandomGaussian(lastValue, 1);
  }
}
