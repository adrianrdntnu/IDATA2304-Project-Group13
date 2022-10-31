package no.ntnu.group13.greenhouse.sensors;

import no.ntnu.group13.greenhouse.logic.BinarySearchTree;
import no.ntnu.group13.greenhouse.logic.DataSearching;
import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.logic.RandomNormalDistributionData;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates a sensor that records fake data.
 * TODO: Generate temperatures based on time of day
 */
public abstract class Sensor {
  protected double mean;
  protected double standardDeviation;
  protected RandomNormalDistributionData rndd = new RandomNormalDistributionData();
  protected DataSearching dataSearching = new DataSearching();

  /**
   * Creates a sensor with a mean value and standard deviation
   *
   * @param mean mean value of the sensor
   * @param standardDeviation standard deviation of the sensor
   */
  public Sensor(double mean, double standardDeviation) {
    this.mean = mean;
    this.standardDeviation = standardDeviation;
  }

  /**
   * Generates a value that is close to the previous value.
   *
   * @param lastValue last value
   * @return next number the sensor records
   */
  public abstract double realisticNextNumber(double lastValue);

  /**
   * Returns a list of (fake) recordings from the sensor.
   * The mean value becomes the average of all generated values to prevent .
   *
   * @param amount amount to record
   * @return a list of recorded values by the sensor
   */
  public List<Double> generateValuesAlternativeOne(int amount) {

    List<Double> recordings = new ArrayList<>();
    for (int i = 0; i < amount; i++) {
      if (recordings.isEmpty()) {
        recordings.add(realisticNextNumber(this.mean));
      } else {
        recordings.add(realisticNextNumber(recordings.get(i-1)));
      }
    }

    this.mean = dataSearching.getAverageValue(recordings);
    return recordings;
  }

  /**
   * Returns a list of fake values.
   * The amount values goes from low -> high and
   *
   * @param amount amount of fake values to generate
   * @param split amount of values until it switches from increasing to decreasing and vice versa
   * @return a List of fake values
   */
  public List<Double> generateValuesAlternateTemps(int amount, int split) {
    List<Double> currentValues;
    List<Integer> splitAmount;

    // To keep whole collection of values as a list
    List<Double> values = new ArrayList<>();

    // Keep whole collection of values as a tree
    // To get smallest & largest values easier.
    BinarySearchTree finalTree = new BinarySearchTree();

    splitAmount = LOGIC.splitNumber(amount, split);

    for (int i = 0; i < (splitAmount.size()-1); i++) {
      BinarySearchTree tempTree = new BinarySearchTree();

      currentValues = rndd.generateRandomValues(mean, standardDeviation, splitAmount.get(i));
      for (Double d: currentValues) {
        tempTree.insert(d);
      }

      // Varies between increasing and decreasing values
      if ((i%2) == 0) {
        // Add to complete tree
        for (Double d: tempTree.getListSmallToBig()) {
          finalTree.insert(d);
        }

        values.addAll(tempTree.getListSmallToBig());
      } else {
        // Add to complete tree
        for (Double d: tempTree.getListBigToSmall()) {
          finalTree.insert(d);
        }

        values.addAll(tempTree.getListBigToSmall());
      }
    }

    // For testing
    System.out.println("Split : " + splitAmount + "\n");
    printTimers(finalTree);

    return values;
  }

  /**
   * For testing, to visualize the time it takes for certain functions.
   * @param tree
   */
  private void printTimers(BinarySearchTree tree) {
    long startTime = System.nanoTime();
    System.out.println("Average value: " + tree.getAverageValue());
    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1000;
    System.out.println("Duration: " + duration + " micro-seconds\n");

    startTime = System.nanoTime();
    System.out.println("Min value: " + tree.getMinValue());
    endTime = System.nanoTime();
    duration = (endTime - startTime) / 1000;
    System.out.println("Duration: " + duration + " micro-seconds\n");

    startTime = System.nanoTime();
    System.out.println("Max value: " + tree.getMaxValue());
    endTime = System.nanoTime();
    duration = (endTime - startTime) / 1000;
    System.out.println("Duration: " + duration + " micro-seconds\n");

    startTime = System.nanoTime();
    System.out.println("Tree in pre-order traversal: " + tree.getTreeAsList());
    endTime = System.nanoTime();
    duration = (endTime - startTime) / 1000;
    System.out.println("Duration: " + duration + " micro-seconds\n");

    startTime = System.nanoTime();
    System.out.println("Sorted small to big: " + tree.getListSmallToBig());
    endTime = System.nanoTime();
    duration = (endTime - startTime) / 1000;
    System.out.println("Duration: " + duration + " micro-seconds\n");

    startTime = System.nanoTime();
    System.out.println("Sorted big to small: " + tree.getListBigToSmall());
    endTime = System.nanoTime();
    duration = (endTime - startTime) / 1000;
    System.out.println("Duration: " + duration + " micro-seconds\n");
  }
}
