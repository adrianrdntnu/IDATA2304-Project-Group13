package no.ntnu.group13.greenhouse.logic;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * Class that can perform multiple operations on a list
 * @deprecated This class has been superseded by the BinarySearchTree class
 */
@Deprecated
public class DataSearching {

  /**
   * Returns the highest value in the list
   * @param list the list to search through
   * @return The highest value
   */
  public double getHighestValue(List<Double> list) {
    if (!list.isEmpty()) {
      double currentHighest = list.get(0);
      for (int i = 1; i < list.size(); i++) {
        if (list.get(i) > currentHighest) {
          currentHighest = list.get(i);
        }
      }

      return currentHighest;
    } else {
      throw new InvalidParameterException("List doesn't contain any elements");
    }
  }

  /**
   * Returns the lowest value in the list
   * @param list the list to search through
   * @return The lowest value
   */
  public double getLowestValue(List<Double> list) {
    if (!list.isEmpty()) {
      double currentLowest = list.get(0);
      for (int i = 1; i < list.size(); i++) {
        if (list.get(i) < currentLowest) {
          currentLowest = list.get(i);
        }
      }

      return currentLowest;
    } else {
      throw new InvalidParameterException("List doesn't contain any elements");
    }

  }

  /**
   * Returns the average value of the list
   * @param list list to search through
   * @return The average value
   */
  public double getAverageValue(List<Double> list) {
    if (!list.isEmpty()) {
      double sum = 0;
      for (int i = 0; i < list.size(); i++) {
        sum += list.get(i);
      }
      double average = sum/list.size();
      return average;
    } else {
      throw new InvalidParameterException("List doesn't contain any elements");
    }
  }
}
