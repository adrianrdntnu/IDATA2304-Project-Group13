package no.ntnu.group13.greenhouse.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class LOGIC {

  // MQTT BROKER
  public static final String BROKER = "tcp://129.241.152.12:1883";
  public static final String CLIENT_ID = "group13_client";
  public static final String SENSOR_ID = "group13_sensor";
  public static final String TEMP_CLIENT = "group13_temp_client";
  public static final String TEMP_SENSOR = "group13_temp_sensor";
  public static final String HUMID_CLIENT = "group13_humid_client";
  public static final String HUMID_SENSOR = "group13_humid_sensor";
  public static final String CO2_CLIENT = "group13_co2_client";
  public static final String CO2_SENSOR = "group13_co2_sensor";
  public static final int QOS = 0;

  // MQTT Static topics
  public static final String TEMPERATURE_TOPIC = "group13/greenhouse/sensors/temperature";
  public static final String HUMIDITY_TOPIC = "group13/greenhouse/sensors/humidity";
  public static final String CO2_TOPIC = "group13/greenhouse/sensors/co2";
  public static final String SENSORS_TOPIC = "group13/greenhouse/sensors/#";
  public static final String ALL_TOPIC = "group13/#";


  // Timeout
  public static final int sleepTimeout = Integer.parseInt(System.getProperty("timeout", "10000"));

  // Round decimal
  public static double round(double value, int places) {
    // Code adapted from: https://stackoverflow.com/a/2808648
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  /**
   * Splits an integer into chunks of smaller integers Code adapted from:
   * <a
   * href="https://www.geeksforgeeks.org/split-the-number-into-n-parts-such-that-difference-between-the-smallest-and-the-largest-part-is-minimum/">
   * geeksforgeeks.org</a>
   *
   * @param number number to split into chunks
   * @param split  amount of chunks to split into
   * @return a list of the original number split into chunks.
   */
  public static List<Integer> splitNumber(int number, int split) {
    List<Integer> splitNumbers = new ArrayList<>();

    // Cannot split the number into enough parts
    if (number < split) {
      splitNumbers.add(number);
      return splitNumbers;
    } else if (number % split == 0) {
      // If x % n == 0 then the minimum
      // difference is 0 and all
      // numbers are x / n

      for (int i = 0; i < split; i++) {
        splitNumbers.add((number / split));
      }

      return splitNumbers;
    } else {
      // upto n-(x % n) the values
      // will be x / n
      // after that the values
      // will be x / n + 1
      int splitRest = split - (number % split);
      int chunk = number / split;
      for (int i = 0; i < split; i++) {
        if (i >= splitRest) {
          splitNumbers.add(chunk + 1);
        } else {
          splitNumbers.add(chunk);
        }
      }
      return splitNumbers;
    }
  }
}
