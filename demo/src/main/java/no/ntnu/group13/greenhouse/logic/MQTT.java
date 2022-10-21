package no.ntnu.group13.greenhouse.logic;

public class MQTT {
  public static final String BROKER = "tcp://129.241.152.12:1883";
  public static final String CLIENT_ID = "group13_client";
  // probably need unique ids for each sensor.
  public static final String SENSOR_ID = "group13_sensor";
  public static final int QOS = 0;

  // Static topics
  public static final String TEMPERATURE_TOPIC = "group13/greenhouse/sensors/temperature";
  public static final String HUMIDITY_TOPIC = "group13/greenhouse/sensors/humidity";
  public static final String SENSORS_TOPIC = "group13/greenhouse/sensors/#";
  public static final String ALL_TOPIC = "group13/#";

  // Timeout
  public static final int sleepTimeout = Integer.parseInt(System.getProperty("timeout", "10000"));
}
