package no.ntnu.group13.greenhouse.javafx.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;

/**
 * Responsible for controlling the start-page in the JavaFX application.
 */
public class WindowController {

  protected static final int MAX_DATA_POINTS = 100;

  protected Parent overviewPage;
  protected Parent tempPane;
  protected Parent humidityPane;
  protected Parent co2Pane;
  protected NumberAxis xAxis;
  @FXML
  protected LineChart<?, ?> dashboardLineChart;
  @FXML
  protected LineChart<?, ?> tempLineChart;
  @FXML
  protected LineChart<?, ?> humidityLineChart;
  @FXML
  protected LineChart<?, ?> co2LineChart;
  @FXML
  protected Text textCo2Current;
  @FXML
  protected Text textCo2High;
  @FXML
  protected Text textCo2Low;
  @FXML
  protected Text textHumidCurrent;
  @FXML
  protected Text textHumidHigh;
  @FXML
  protected Text textHumidLow;
  @FXML
  protected Text textTempCurrent;
  @FXML
  protected Text textTempHigh;
  @FXML
  protected Text textTempLow;
  @FXML
  private BorderPane centerBorderPane;
  @FXML
  protected BorderPane sideMenuBorderPane;

  // Menu buttons
  @FXML
  public void dashboardMenuButton(ActionEvent actionEvent) {
    this.centerBorderPane.setCenter(overviewPage);
  }

  @FXML
  public void tempMenuButton(ActionEvent actionEvent) {
    this.centerBorderPane.setCenter(tempPane);
  }

  @FXML
  public void humidityMenuButton(ActionEvent actionEvent) {
    this.centerBorderPane.setCenter(humidityPane);
  }

  @FXML
  public void co2MenuButton(ActionEvent actionEvent) {
    this.centerBorderPane.setCenter(co2Pane);
  }

  @FXML
  public void startProgram(ActionEvent actionEvent) {
    this.centerBorderPane.setCenter(overviewPage);
    this.sideMenuBorderPane.setVisible(true);
  }

  public void setOverviewPage(Parent overviewPage) {
    this.overviewPage = overviewPage;
  }

  public void setTemperaturePage(Parent tempPane) {
    this.tempPane = tempPane;
  }

  public void setHumidityPage(Parent humidityPane) {
    this.humidityPane = humidityPane;
  }

  public void setCo2Page(Parent co2Pane) {
    this.co2Pane = co2Pane;
  }
}
