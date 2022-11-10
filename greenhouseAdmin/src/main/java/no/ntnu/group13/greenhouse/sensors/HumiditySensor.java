package no.ntnu.group13.greenhouse.sensors;

public class HumiditySensor extends Sensor {

  /**
   * Creates a humidity sensor with the mean value of 100 and standard deviation of 1.
   */
  public HumiditySensor() {
    super(100, 1);
  }

  /**
   * Creates a humidity sensor with a custom standard deviation.
   *
   * @param mean              mean value of the sensor
   * @param standardDeviation standard deviation of the sensor
   */
  public HumiditySensor(double mean, double standardDeviation) {
    super(mean, standardDeviation);
  }

  @Override
  public double realisticNextNumber(double lastValue) {
    return rndd.getRandomGaussian(lastValue, 1);
  }
}
