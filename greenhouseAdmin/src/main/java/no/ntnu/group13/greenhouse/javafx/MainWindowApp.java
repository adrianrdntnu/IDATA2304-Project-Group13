package no.ntnu.group13.greenhouse.javafx;

import java.util.Optional;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.crypto.spec.IvParameterSpec;
import no.ntnu.group13.greenhouse.client.ClientHandler;
import no.ntnu.group13.greenhouse.javafx.controllers.Co2WindowController;
import no.ntnu.group13.greenhouse.javafx.controllers.HumidityWindowController;
import no.ntnu.group13.greenhouse.javafx.controllers.OverviewWindowController;
import no.ntnu.group13.greenhouse.javafx.controllers.TemperatureWindowController;
import no.ntnu.group13.greenhouse.javafx.controllers.WindowController;
import no.ntnu.group13.greenhouse.logic.EncryptAndDecryptMessage;
import no.ntnu.group13.greenhouse.logic.LOGIC;

public class MainWindowApp extends Application {

  private Stage primaryStage;
  private BorderPane mainBorderPane;
  private WindowController mainWindowController;
  private OverviewWindowController overviewWindowController;

  // Different pages
  private Scene mainScene;
  private Parent overviewPane;
  private Parent temperaturePane;
  private Parent humidityPane;
  private Parent co2Pane;

  private ClientHandler tempClientHandler;
  private ClientHandler humidClientHandler;
  private ClientHandler co2ClientHandler;
  private IvParameterSpec ivParameterSpec;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;
    primaryStage.setOnCloseRequest(confirmCloseEventHandler);

    // Getting loader and pane for the main window.
    FXMLLoader mainPaneLoader = new FXMLLoader(
        getClass().getResource("gui/mainApplicationWindow.fxml"));
    Parent mainPane = mainPaneLoader.load();
    this.mainScene = new Scene(mainPane, 1000, 730);

    // Getting loader and pane for the main window.
    FXMLLoader overviewPageLoader = new FXMLLoader(
        getClass().getResource("gui/overviewWindow.fxml"));
    this.overviewPane = overviewPageLoader.load();
    this.overviewWindowController = overviewPageLoader.getController();

    // Temperature page
    FXMLLoader temperaturePageLoader = new FXMLLoader(
        getClass().getResource("gui/tempWindow.fxml"));
    this.temperaturePane = temperaturePageLoader.load();
    TemperatureWindowController tempWindowController = temperaturePageLoader.getController();

    // Humidity page
    FXMLLoader humidityPageLoader = new FXMLLoader(
        getClass().getResource("gui/humidityWindow.fxml"));
    this.humidityPane = humidityPageLoader.load();
    HumidityWindowController humidWindowController = humidityPageLoader.getController();

    // Co2 page
    FXMLLoader co2PageLoader = new FXMLLoader(getClass().getResource("gui/co2Window.fxml"));
    this.co2Pane = co2PageLoader.load();
    Co2WindowController co2WindowController = co2PageLoader.getController();

    // Sets panes to mainController
    this.mainWindowController = mainPaneLoader.getController();
    this.mainWindowController.setOverviewPage(this.overviewPane);
    this.mainWindowController.setTemperaturePage(this.temperaturePane);
    this.mainWindowController.setHumidityPage(this.humidityPane);
    this.mainWindowController.setCo2Page(this.co2Pane);

    // Connects the linecharts to overViewWindowController
    this.mainWindowController.setCo2LineChart(co2WindowController.getCo2LineChart());
    this.mainWindowController.setTempLineChart(tempWindowController.getTempLineChart());
    this.mainWindowController.setHumidityLineChart(humidWindowController.getHumidityLineChart());
    this.mainWindowController.setdDashboardLineChart(overviewWindowController.getDashboardLineChart());

    // Generates initialization vector
    this.ivParameterSpec = EncryptAndDecryptMessage.generateIv();
    this.mainWindowController.setIvParameterSpec(ivParameterSpec);

    // Clients
    this.tempClientHandler = new ClientHandler(LOGIC.TEMPERATURE_TOPIC, LOGIC.BROKER,
        LOGIC.TEMP_CLIENT, LOGIC.QOS, ivParameterSpec);
    this.humidClientHandler = new ClientHandler(LOGIC.HUMIDITY_TOPIC, LOGIC.BROKER,
        LOGIC.HUMID_CLIENT, LOGIC.QOS, ivParameterSpec);
    this.co2ClientHandler = new ClientHandler(LOGIC.CO2_TOPIC, LOGIC.BROKER, LOGIC.CO2_CLIENT,
        LOGIC.QOS, ivParameterSpec);

    this.overviewWindowController.setTempClientHandler(tempClientHandler);
    this.overviewWindowController.setHumidClientHandler(humidClientHandler);
    this.overviewWindowController.setCo2ClientHandler(co2ClientHandler);
    this.overviewWindowController.setIvParameterSpec(ivParameterSpec);

    // To modify other linecharts
    this.overviewWindowController.setCo2LineChart(co2WindowController.getCo2LineChart());
    this.overviewWindowController.setTempLineChart(tempWindowController.getTempLineChart());
    this.overviewWindowController.setHumidityLineChart(humidWindowController.getHumidityLineChart());

    // tempWindowController.setTempSeries(overviewWindowController.getTempSeries());
    // humidWindowController.setHumidSeries(overviewWindowController.getHumidSeries());
    // co2WindowController.setCo2Series(overviewWindowController.getCo2Series());

    // tempWindowController.setxAxis(overviewWindowController.getxAxis());
    // humidWindowController.setxAxis(overviewWindowController.getxAxis());
    // co2WindowController.setxAxis(overviewWindowController.getxAxis());

    // Applies initialization vector

    primaryStage.setTitle("Greenhouse Administrator");
    primaryStage.setScene(this.mainScene);
    primaryStage.show();
  }

  /**
   * Presents user with an alert box when closing the program. Saves all tournaments when program is
   * exited.
   */
  private final EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
    Alert closeWindowAlert = new Alert(
        Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?"
    );
    closeWindowAlert.setHeaderText("Confirm exit");

    Button exitButton = (Button) closeWindowAlert.getDialogPane().lookupButton(ButtonType.OK);
    exitButton.setText("Exit");

    closeWindowAlert.initModality(Modality.APPLICATION_MODAL);
    closeWindowAlert.initOwner(primaryStage);

    // Stops clients & sensors
    if (overviewWindowController.getClientOnlineStatus()) {
      this.overviewWindowController.disconnectClients();
    }
    if (overviewWindowController.getSensorOnlineStatus()) {
      this.overviewWindowController.stopSensors();
    }

    Optional<ButtonType> closeResponse = closeWindowAlert.showAndWait();
    if (!ButtonType.OK.equals(closeResponse.get())) {
      event.consume();
    }
  };
}
