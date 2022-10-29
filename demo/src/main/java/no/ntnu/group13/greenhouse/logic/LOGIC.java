package no.ntnu.group13.greenhouse.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LOGIC {

  // MQTT BROKER
  public static final String BROKER = "tcp://129.241.152.12:1883";
  public static final String CLIENT_ID = "group13_client";
  public static final String SENSOR_ID = "group13_sensor";
  public static final int QOS = 0;

  // MQTT Static topics
  public static final String TEMPERATURE_TOPIC = "group13/greenhouse/sensors/temperature";
  public static final String HUMIDITY_TOPIC = "group13/greenhouse/sensors/humidity";
  public static final String SENSORS_TOPIC = "group13/greenhouse/sensors/#";
  public static final String ALL_TOPIC = "group13/#";

  // Timeout
  public static final int sleepTimeout = Integer.parseInt(System.getProperty("timeout", "10000"));

  // Round decimal
  public static double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }
}
