package no.ntnu.group13.greenhouse.sensors;

import static no.ntnu.group13.greenhouse.logic.LOGIC.round;

import javax.crypto.spec.IvParameterSpec;
import no.ntnu.group13.greenhouse.logic.RandomNormalDistributionData;
import no.ntnu.group13.greenhouse.server.MqttPublisher;

/**
 * Creates a sensor that records fake data.
 */
public abstract class Sensor extends MqttPublisher {

  protected double mean;
  protected double newMean;
  protected double increment;
  protected double standardDeviation;
  protected RandomNormalDistributionData rnd = new RandomNormalDistributionData();

  /**
   * Creates a sensor with a mean value and standard deviation.
   *
   * @param topic             mqtt topic
   * @param broker            mqtt broker
   * @param sensorId          the sensor id
   * @param qos               qos value
   * @param mean              mean value of the sensor
   * @param standardDeviation standard deviation of the sensor
   */
  protected Sensor(String topic, String broker, String sensorId, int qos, double mean,
      double standardDeviation, IvParameterSpec ivParameterSpec) {
    super(topic, broker, sensorId, qos, ivParameterSpec);
    this.mean = mean;
    this.newMean = this.mean;
    this.increment = 0;
    this.standardDeviation = standardDeviation;
  }

  /**
   * Returns the next value of this sensor.
   *
   * @return The next value of this sensor
   */
  public double nextValue() {
    if (this.mean != newMean) { // Check if we have not reached the new mean yet
      this.mean += increment;
    }
    return rnd.getRandomGaussian(this.mean, this.standardDeviation);
  }

  /**
   * Sets the new mean of this sensor, changes the increment so that it takes "amount" amount of
   * values to get to the new mean.
   *
   * @param newMean New mean of this sensor
   * @param amount  Amount of values before reaching the new mean
   */
  public void setNewMeanWithAmount(double newMean, int amount) {
    this.newMean = newMean;

    double difference = this.newMean - this.mean;

    if (difference == 0) {
      this.increment = 0;
    } else {
      this.increment = difference / amount;
    }
  }

  /**
   * Sets the new mean of this sensor, changes the increment so that it takes the same amount of
   * values to get to the new mean as difference in the new and old mean. (value lowers/ increases
   * by roughly 1 per value)
   *
   * @param newMean New mean of this sensor
   */
  public void setNewMean(double newMean) {
    this.newMean = newMean;

    double difference = this.newMean - this.mean;

    // Set amount to 1 if the difference is lower than 1 but higher than 0.
    // If the difference is 0 no increment.
    // If the difference is greater than 1 set amount to difference rounded to 0 decimals.
    int amount = 0;
    if ((difference > 0 && difference < 1) || (difference < 0 && difference > -1)) {
      amount = 1;
      this.increment = difference / amount;
    } else if (difference == 0) {
      this.increment = 0;
    } else {
      amount = (int) round(difference, 0);
      // If difference is less than zero set change amount to positive number.
      if (difference < 0) {
        amount *= -1;
      }
      this.increment = difference / amount;
    }
  }

  /**
   * Sets the new mean of this sensor, changes the increment so that it takes the same amount of
   * values to get to the new mean as difference in the new and old mean. (value lowers/ increases
   * by roughly roughIncrement per value)
   *
   * @param newMean        New mean of this sensor
   * @param roughIncrement Roughly how much the value should change every time it increments
   */
  public void setNewMean(double newMean, int roughIncrement) {
    this.newMean = newMean;

    double difference = this.newMean - this.mean;

    // Set amount to 1 if the difference is lower than 1 but higher than 0.
    // If the difference is 0 no increment.
    // If the difference is greater than 1 set amount to difference rounded to 0 decimals
    // Times the rough increment.
    int amount = 0;
    if ((difference > 0 && difference < roughIncrement)
        || (difference < 0 && difference > -roughIncrement)) {
      amount = 1;
      this.increment = difference / amount;
    } else if (difference == 0) {
      this.increment = 0;
    } else {
      amount = (int) round(difference, 0);
      // If difference is less than zero set change amount to positive number.
      if (difference < 0) {
        amount *= -1;
      }
      this.increment = difference / amount * roughIncrement;
    }
  }

  /**
   * Returns this sensor's mean.
   *
   * @return mean of this sensor
   */
  public double getMean() {
    return this.mean;
  }
}
