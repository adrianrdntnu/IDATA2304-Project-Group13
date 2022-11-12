package no.ntnu.group13.greenhouse.sensors;

import java.util.ArrayList;
import java.util.List;
import no.ntnu.group13.greenhouse.logic.BinarySearchTree;
import no.ntnu.group13.greenhouse.logic.DataSearching;
import no.ntnu.group13.greenhouse.logic.LOGIC;
import no.ntnu.group13.greenhouse.logic.RandomNormalDistributionData;
import no.ntnu.group13.greenhouse.server.MqttPublisher;

/**
 * Creates a sensor that records fake data.
 * TODO: Generate temperatures based on time of day
 */
public abstract class Sensor extends MqttPublisher {

  protected double mean;
  protected double standardDeviation;
  protected RandomNormalDistributionData rndd = new RandomNormalDistributionData();
  protected DataSearching dataSearching = new DataSearching();
  protected BinarySearchTree finalTree = new BinarySearchTree();

  /**
   * Creates a sensor with a mean value and standard deviation
   *
   * @param topic             mqtt topic
   * @param broker            mqtt broker
   * @param sensorId          the sensor id
   * @param qos               qos value
   * @param mean              mean value of the sensor
   * @param standardDeviation standard deviation of the sensor
   */
  protected Sensor(String topic, String broker, String sensorId, int qos, double mean,
      double standardDeviation) {
    super(topic, broker, sensorId, qos);
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
   * Returns a list of (fake) recordings from the sensor. The mean value becomes the average of all
   * generated values to prevent .
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
        recordings.add(realisticNextNumber(recordings.get(i - 1)));
      }
    }

    this.mean = dataSearching.getAverageValue(recordings);
    return recordings;
  }

  /**
   * Returns a list of fake values. The values repeat a low -> high -> low pattern
   *
   * @param amount amount of fake values to generate
   * @param split  amount of values until it switches from increasing to decreasing and vice versa
   * @return a List of fake values
   */
  public List<Double> generateValuesAlternateTemps(int amount, int split) {
    List<Double> currentValues;
    List<Integer> splitAmount;

    // To keep whole collection of values as a list
    List<Double> values = new ArrayList<>();

    splitAmount = LOGIC.splitNumber(amount, split);

    for (int i = 0; i < splitAmount.size(); i++) {
      BinarySearchTree tempTree = new BinarySearchTree();

      currentValues = rndd.generateRandomValues(mean, standardDeviation, splitAmount.get(i));
      for (Double d : currentValues) {
        tempTree.insert(d);
      }

      // Varies between increasing and decreasing values
      if ((i % 2) == 0) {
        // Add to complete tree
        for (Double d : tempTree.getListSmallToBig()) {
          this.finalTree.insert(d);
        }

        values.addAll(tempTree.getListSmallToBig());
      } else {
        // Add to complete tree
        for (Double d : tempTree.getListBigToSmall()) {
          this.finalTree.insert(d);
        }

        values.addAll(tempTree.getListBigToSmall());
      }
    }

//    printTestTimers();

    return values;
  }

  /**
   * Returns the BinarySearchTree of the sensor containing observed values.
   *
   * @return The BinarySearchTree of the sensor
   */
  public BinarySearchTree getTree() {
    return this.finalTree;
  }

  /**
   * Inserts a list of values to the sensor.
   *
   * @param values list of values to insert
   */
  public void insertValues(List<Double> values) {
    for (Double value : values) {
      finalTree.insert(value);
    }
  }

  /**
   * For testing, to visualize the time it takes to execute selected functions.
   */
  private void printTestTimers() {
    long startTime = System.nanoTime();
    System.out.println("Average value: " + this.finalTree.getAverageValue());
    long endTime = System.nanoTime();
    long duration = (endTime - startTime) / 1000;
    System.out.println("Duration: " + duration + " micro-seconds\n");

    startTime = System.nanoTime();
    System.out.println("Min value: " + this.finalTree.getMinValue());
    endTime = System.nanoTime();
    duration = (endTime - startTime) / 1000;
    System.out.println("Duration: " + duration + " micro-seconds\n");

    startTime = System.nanoTime();
    System.out.println("Max value: " + this.finalTree.getMaxValue());
    endTime = System.nanoTime();
    duration = (endTime - startTime) / 1000;
    System.out.println("Duration: " + duration + " micro-seconds\n");

    startTime = System.nanoTime();
    System.out.println("Tree in pre-order traversal: " + this.finalTree.getTreeAsList());
    endTime = System.nanoTime();
    duration = (endTime - startTime) / 1000;
    System.out.println("Duration: " + duration + " micro-seconds\n");

    startTime = System.nanoTime();
    System.out.println("Sorted small to big: " + this.finalTree.getListSmallToBig());
    endTime = System.nanoTime();
    duration = (endTime - startTime) / 1000;
    System.out.println("Duration: " + duration + " micro-seconds\n");

    startTime = System.nanoTime();
    System.out.println("Sorted big to small: " + this.finalTree.getListBigToSmall());
    endTime = System.nanoTime();
    duration = (endTime - startTime) / 1000;
    System.out.println("Duration: " + duration + " micro-seconds\n");

//    startTime = System.nanoTime();
//    System.out.println("Printing tree: ");
//    printBST();
//    endTime = System.nanoTime();
//    duration = (endTime - startTime) / 1000;
//    System.out.println("Duration: " + duration + " micro-seconds\n");
  }

  /**
   * Prints the BST tree, useless for the application, used just for fun.
   * TODO: Remove
   */
  public void printBST() {
    finalTree.printTree(finalTree.getRootNode());
  }
}
