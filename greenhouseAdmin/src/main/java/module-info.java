module volleyBallAdmin {
  requires javafx.graphics;
  requires javafx.controls;
  requires javafx.fxml;
  requires org.eclipse.paho.client.mqttv3;

  opens no.ntnu.group13.greenhouse to java.base, javafx.fxml;
  opens no.ntnu.group13.greenhouse.server to java.base;
  opens no.ntnu.group13.greenhouse.sensors to java.base, javafx.fxml;
  opens no.ntnu.group13.greenhouse.logic to java.base;
  opens no.ntnu.group13.greenhouse.javafx to javafx.graphics, javafx.fxml;
  opens no.ntnu.group13.greenhouse.javafx.controllers to javafx.graphics, javafx.fxml;

  exports no.ntnu.group13.greenhouse.server to java.base;
  exports no.ntnu.group13.greenhouse.sensors to java.base, javafx.fxml;
  exports no.ntnu.group13.greenhouse.logic to java.base;
  exports no.ntnu.group13.greenhouse.javafx to javafx.fxml;
  exports no.ntnu.group13.greenhouse.javafx.controllers to javafx.fxml;
  exports no.ntnu.group13.greenhouse to javafx.fxml;
}
