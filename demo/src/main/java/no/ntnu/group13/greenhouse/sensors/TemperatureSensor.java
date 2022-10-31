package no.ntnu.group13.greenhouse.sensors;

/**
 * Represents a temperature sensor that records temperature each interval.
 */
public class TemperatureSensor extends Sensor {
  /**
   * Creates a temperature sensor with the
   * mean value of 38 degrees and
   * standard deviation of 0.1 degrees
   */
  public TemperatureSensor() {
    super(27, 0.1);
  }

  /**
   * Creates a temperature sensor with a custom standard deviation.
   *
   * @param mean mean value of the sensor
   * @param standardDeviation standard deviation of the sensor
   */
  public TemperatureSensor(double mean, double standardDeviation) {
    super(mean, standardDeviation);
  }

  @Override
  public double realisticNextNumber(double lastValue) {
    return rndd.getRandomGaussian(lastValue, 0.1);
  }
}
