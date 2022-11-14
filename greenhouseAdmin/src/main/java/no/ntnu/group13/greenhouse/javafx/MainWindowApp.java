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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import no.ntnu.group13.greenhouse.javafx.controllers.MainWindowController;

public class MainWindowApp extends Application {

  private Stage primaryStage;
  private Scene mainScene;
  private MainWindowController mainWindowController;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.primaryStage = primaryStage;
    primaryStage.setOnCloseRequest(confirmCloseEventHandler);

    // Getting loader and pane for the main scene.
    FXMLLoader mainPaneLoader = new FXMLLoader(getClass().getResource("gui/mainWindow.fxml"));
    Parent mainPane = mainPaneLoader.load();
    this.mainWindowController = mainPaneLoader.getController();
    this.mainScene = new Scene(mainPane, 1000, 730);

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
    if (mainWindowController.getClientOnlineStatus()) {
      this.mainWindowController.disconnectClients();
    }
    if (mainWindowController.getSensorOnlineStatus()) {
      this.mainWindowController.stopSensors();
    }

    Optional<ButtonType> closeResponse = closeWindowAlert.showAndWait();
    if (!ButtonType.OK.equals(closeResponse.get())) {
      event.consume();
    }
  };
}
