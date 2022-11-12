package no.ntnu.group13.greenhouse.sensors;

/**
 * Represents a temperature sensor that records temperature each interval.
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
  public TemperatureSensor(String topic, String broker, String sensorId, int qos) {
    super(topic, broker, sensorId, qos, 38, 0.1);
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
      double standardDeviation) {
    super(topic, broker, sensorId, qos, mean, standardDeviation);
  }

  @Override
  public double realisticNextNumber(double lastValue) {
    return rndd.getRandomGaussian(lastValue, 0.1);
  }
}
