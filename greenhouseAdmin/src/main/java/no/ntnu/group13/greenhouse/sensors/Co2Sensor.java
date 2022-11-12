package no.ntnu.group13.greenhouse.sensors;

public class Co2Sensor extends Sensor {

  /**
   * Creates a humidity sensor with the mean value of 100 and standard deviation of 1.
   *
   * @param topic    mqtt topic
   * @param broker   mqtt broker
   * @param sensorId the sensor id
   * @param qos      qos value
   */
  public Co2Sensor(String topic, String broker, String sensorId, int qos) {
    super(topic, broker, sensorId, qos, 300, 5);
  }

  /**
   * Creates a humidity sensor with custom mean value and standard deviation.
   *
   * @param topic             mqtt topic
   * @param broker            mqtt broker
   * @param sensorId          the sensor id
   * @param qos               qos value
   * @param mean              mean value of the co2-value
   * @param standardDeviation standard deviation of the co2-value
   */
  public Co2Sensor(String topic, String broker, String sensorId, int qos, double mean,
      double standardDeviation) {
    super(topic, broker, sensorId, qos, mean, standardDeviation);
  }

  @Override
  public double realisticNextNumber(double lastValue) {
    return rndd.getRandomGaussian(lastValue, 1);
  }

}
