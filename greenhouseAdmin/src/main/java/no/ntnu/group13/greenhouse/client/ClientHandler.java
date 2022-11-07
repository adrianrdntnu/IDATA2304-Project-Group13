package no.ntnu.group13.greenhouse.client;

public class ClientHandler {
  private String recievedData;

  public ClientHandler(String recievedData) {
    this.recievedData = recievedData;
  }

  /**
   * Gets the received data from the client.
   *
   * @return The received data from the client
   */
  public String getRecievedData() {
    return recievedData;
  }
}
