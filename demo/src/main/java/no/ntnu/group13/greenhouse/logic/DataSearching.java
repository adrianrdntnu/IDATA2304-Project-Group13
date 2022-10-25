package no.ntnu.group13.greenhouse.logic;

import java.util.List;

/**
 * Class that can perform multiple operations on a list
 */
public class DataSearching {

  /**
   * Returns the highest value in the list
   * @param list the list to search through
   * @return The highest value
   */
  public double getHighestValue(List<Double> list) {
    //TODO check if the list contains at least 1 element
    double currentHighest = list.get(0);
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i) > currentHighest) {
        currentHighest = list.get(i);
      }
    }

    return currentHighest;
  }

  /**
   * Returns the lowest value in the list
   * @param list the list to search through
   * @return The lowest value
   */
  public double getLowestValue(List<Double> list) {
    //TODO check if the list contains at least 1 element
    double currentLowest = list.get(0);
    for (int i = 1; i < list.size(); i++) {
      if (list.get(i) < currentLowest) {
        currentLowest = list.get(i);
      }
    }

    return currentLowest;
  }

  /**
   * Returns the average value of the list
   * @param list list to search through
   * @return The average value
   */
  public double getAverageValue(List<Double> list) {
    //TODO check if the list contains at least 1 element
    double sum = 0;
    for (int i = 0; i < list.size(); i++) {
      sum += list.get(i);
    }
    double average = sum/list.size();
    return average;
  }
}
