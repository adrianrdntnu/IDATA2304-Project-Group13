package no.ntnu.group13.greenhouse.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Keeps a list of random temperatures and humidity values
 */
public class RandomNormalDistributionData {
  private final Random rng = new Random();

  private List<Double> temperatures;
  private List<Double> humidityValues;

  public RandomNormalDistributionData() {
    this.temperatures = new ArrayList<>();
    this.humidityValues = new ArrayList<>();
  }

  /**
   * Returns a random value from a normal distribution with mean, and standard deviation values
   *
   * @param mean              The mean of the normal distribution
   * @param standardDeviation The standard deviation of the normal distribution
   * @return The random value
   */
  public double getRandomGaussian(double mean, double standardDeviation) {
    return rng.nextGaussian() * standardDeviation + mean;
  }

  /**
   * Adds n number of random temperatures from a normal distribution with mean, and
   * standard deviation values to the list of temperatures.
   *
   * @param mean              The mean of the normal distribution
   * @param standardDeviation The standard deviation of the normal distribution
   * @param n                 Number of temperatures to add
   */
  public void generateRandomTemperatures(double mean, double standardDeviation, int n) {
    for (int i = 0; i < n; i++) {
      this.temperatures.add(getRandomGaussian(mean, standardDeviation));
    }
  }

  /**
   * Adds n number of random humidity values from a normal distribution with mean, and
   * standard deviation values to the list of humidity values.
   *
   * @param mean              The mean of the normal distribution
   * @param standardDeviation The standard deviation of the normal distribution
   * @param n                 Number of humidity values to add
   */
  public void generateRandomHumidityValues(double mean, double standardDeviation, int n) {
    for (int i = 0; i < n; i++) {
      this.humidityValues.add(getRandomGaussian(mean, standardDeviation));
    }
  }

  /**
   * Returns the list of temperatures stored in the class
   *
   * @return The list of temperatures
   */
  public List<Double> getTemperatures() {
    return temperatures;
  }

  /**
   * Returns the list of humidity values stored in the class
   *
   * @return The list of humidity values
   */
  public List<Double> getHumidityValues() {
    return humidityValues;
  }

}
