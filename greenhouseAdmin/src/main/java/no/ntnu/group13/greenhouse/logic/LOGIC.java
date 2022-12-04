package no.ntnu.group13.greenhouse.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Class containing reused strings and methods.
 */
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

  /**
   * Rounds a double to the given decimal.
   *
   * @param value  the number to round
   * @param places amount of decimals to round to
   * @return a rounded double value
   */
  public static double round(double value, int places) {
    // Code adapted from: https://stackoverflow.com/a/2808648
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
}
