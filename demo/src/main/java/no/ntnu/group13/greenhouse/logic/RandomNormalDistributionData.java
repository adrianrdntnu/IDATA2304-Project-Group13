package no.ntnu.group13.greenhouse.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static no.ntnu.group13.greenhouse.logic.LOGIC.round;

/**
 * Keeps a list of random temperatures and humidity values
 */
public class RandomNormalDistributionData {
  private final Random rng = new Random();

  public RandomNormalDistributionData() {
    // this.values = new ArrayList<>();
  }

  /**
   * Returns a random value from a normal distribution with mean, and standard deviation values
   *
   * @param mean              The mean of the normal distribution
   * @param standardDeviation The standard deviation of the normal distribution
   * @return The random value
   */
  public double getRandomGaussian(double mean, double standardDeviation) {
    return round((rng.nextGaussian() * standardDeviation + mean), 3);
  }

  /**
   * Generates a list of random values using a normal distribution with mean,
   * and standard deviation values.
   *
   * @param mean              The mean of the normal distribution
   * @param standardDeviation The standard deviation of the normal distribution
   * @param n                 Number of values in the list
   * @return a list of random values from a normal distribution
   */
  public List<Double> generateRandomValues(double mean, double standardDeviation, int n) {
    List<Double> values = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      values.add(getRandomGaussian(mean, standardDeviation));
    }
    return values;
  }
}
