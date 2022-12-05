# IDATA2304-Project-Group13
This is a school group project for the course IDATA2304 Computer networks.
 
## Abstract
In a greenhouse it is important that the temperature, humidity and co2 levels constantly stay the same correct value. Monitoring these values is therefore very important, so that we can adjust the values if they are wrong. Therefore we made a program that keeps a live feed of the temperatures, so that you can keep track of these values, and notice any irregularities. We constantly evaluated our solution ourselves and came up with ideas of improvement that we deemed necessary. 

Creating an application that gets temperature inputs from a greenhouse, visualizes the data on a graph/timeline, and gives a response in case the temperature deviates to a harmful value for the plants in the greenhouse. 

The application is intended to be used by an administrator over the greenhouse and monitor its values without having to be present at the greenhouse.

## Introduction
This solution can be used by both professional greenhouse farmers, and private hobbyists. Constantly keeping track of the temperature, humidity and co2 levels can prove to be tiresome work. With this solution you can keep track of the temperature remotely and could keep track of several different greenhouses.

In [Theory and technology](https://github.com/adrianrdntnu/IDATA2304-Project-Group13/blob/main/README.md#theory-and-technology) we go through the theory and technologies we used to implement this solution. We then describe our work process in [Methodology](https://github.com/adrianrdntnu/IDATA2304-Project-Group13/blob/main/README.md#methodology), and our result in [Results](https://github.com/adrianrdntnu/IDATA2304-Project-Group13/blob/main/README.md#results). In the end we discuss our result in [Discussion](https://github.com/adrianrdntnu/IDATA2304-Project-Group13/blob/main/README.md#discussion) and reflect over possible improvements and future work, in [Conclusion and future work](https://github.com/adrianrdntnu/IDATA2304-Project-Group13/blob/main/README.md#conclusion-and-future-work).

## Theory and technology

### Data simulation, processing, and visualization
* For data simulation we have used the java class Random. Here we use the method nextGaussian, to get a random normal distributed number. This way we can create some randomness/deviation to the "sensor reading" without changing the mean (actual temp/humidity/co2 value). To process the data, we chose to store the data in a BST, which we added methods for extracting highest and lowest values. We have used JavaFX for our application and Line Chart for visualization.

### Connections to other subjects
* Storing values in a BST is something we learned in IDATA2302 Algorithms and datastructures. This tree structure makes the runtime of finding the lowest and highest value shorter. Using nextGaussian has a slight connection to what we learn in ISTA1003 Statistics, as we learn to work with normal distributed data.

### MQTT broker
* An MQTT broker is a simple server that retrieves and sends data trough publishing and subscribing to different topics. In our case the sensor node publishes to the topic "group13/greenhouse/sensors/#", where the # could be "temperature", "humidity" or "co2" depending on the sensor, and the visualization node subscribes to the same topic to receive data. The MQTT broker uses a TCP connection to create a connection between the sensor-nodes and the MQTT broker and visualization-nodes and the MQTT broker.

// source: https://www.emqx.com/en/blog/how-to-use-mqtt-in-java 

// source 2: https://mqtt.org/

### TCP/IP
* A TCP (Transmission Control Protocol) and IP (Internet Protocol) are two protocols that is used to establish a connection between servers. TCP is a connection-oriented protocol which means it makes sure to establish a connection before the data starts transmitting. The IP is used as an identifier to know where to send the data to and where it came from. The most important feature of the TCP/IP protocols is that it ensures reliable transfer of data.

// source: https://snl.no/TCP/IP

### JavaFX
* To visualize the data for the user we chose to create a JavaFX application that will function as the user interface between the user and the data received from the different sensors. To design the application Scene Builder was used to create FXML files which could be implemented to the application.

### AES Symmetric key cryptography
* Symmetric key cryptography is when the same key is used for both encryption and decryption. The way the data is encrypted is with the Advanced Encryption Standard (AES) algorithm which transforms the data to 128-bit block ciphers that can only be encrypted and decrypted with the same key.

// Source: https://www.baeldung.com/java-aes-encryption-decryption

// https://docs.oracle.com/javase/8/javafx/get-started-tutorial/jfx-overview.htm#JFXST784

### Domain knowledge

#### Temperature
* Optimal temperature for a greenhouse is around 18-24 degrees Celsius
* Easiest ways to regulate temperature for a greenhouse is using fans, heaters, shading and natural ventilation.

#### Humidity
* Humidity should be around 80%
* If the humidity is too high bacterial and fungal infections can harm the plants. 
* If it is too low, the plants will evaporate their water and be unable to grow and perform photosynthesis.
* One could mist the plants by using a spray bottle or use an air humidifier to increase the humidity. Planting plants closer together also help the plants trap the water between them.
* To decrease the humidity, one could use a fan or simply open windows/doors to ventilate it.

#### Co2
* Natural concentration of co2 in the air is around 340 ppm, but most crops grow better with a concentration around 1000 ppm
* Supplementation of co2 is not necessary at night, since photosynthesis normally only occurs when daylight is present.
* Too low of a concentration reduces the rate of photosynthesis drastically, while having a co2 level too high not only is a waste of money, it can also damage the plants.

// Sources: ...

## Approach
### GitHub
* For collaboration and to get an overview over the project, GitHub has been used to plan sprints, issues and store commits.

### Maven
* Maven is used as a project management tool and to easily download dependencies for the project.

### Google checkstyle
* For code consistency the Google Checkstyle to structure code. The Google checkstyle uses the Google coding conventions

### Storing as a BST
* The original idea was to store all the data received from the sensors in a BST to quickly retrieve the smallest and highest values in case we wanted a better summary of the data. The reason we wanted to get the lowest and highest values was in case we wanted to get an overview over the values of different days and present a high-low difference between days. As the project went on we realised a major flaw with the BST is that it doesn't store input order, so if we wanted to present the data as a linear graph, the better approach was to just store the data as a List and update and store the high and low values as they are retrieved from the sensors. Because of this the BST was deprecated.

## Additional Features
### JavaFx Interface
* Provides the user with an interface resembling an administrative application. Within the JavaFX application a start and stop button controlled the when to start retrieving data from the sensors, which are then displayed on a line chart as a linear timeline. In addition to the start and stop buttons, extra buttons to were implemented to mimic tools that could affect the conditions in a greenhouse, like the function of turning on and off a heater that increases the temperature. Because of how different the values retrieved from the different sensors can be, each sensor got their own dedicated page within the application where only values from a single type of sensor is displayed to create a more detailed overview where the changes in values are more visible.

### Fake Value Generator
* The values received from the sensors are generated by a fake value generator providing close to realistic values imitating the given sensor. The reason we did this was for simplicity, because the main feature of the application is to retrieve and visualize values from a server and not to create an actual greenhouse.

### Encrypted data
* The data is encrypted and decrypted using a symmetric key, this means that both the sensor and client uses the same key to decrypt and encrypt data. The data is sent to the MQTT broker as unreadable encrypted ciphertext, and then decrypted when it arrives at the client so it can be used by the application. This is to ensure the data is secure from unauthorized users since anyone connected to the MQTT broker can read the data sent to it.

## Methodology
* Diagram?
* 

## Results
The final product is a JavaFX application mimicking an administrative program used to administrate a Greenhouse and its sensors.

Because the program uses fake sensors, it both sends to and retrieves data from an MQTT broker.

## Discussion
#### What works well:
* It's clear what is going on. When you press buttons or change tabs for example.
* Hard for the user to "misuse" the program (For example inputting data that is not valid).
* Different chart pages allow for better visibility and understanding of the graph data.

#### What doesn't work as well:
* Program lags a bit when you start and stop the sensor. 
* Using JavaFX might not be very beneficial in the long run.

## Conclusion and future work
* Storing and loading data
* Individually start and stop sensors
* Login page with username and password
* Modify fake value generator to include time of day as a variable
* Add mechanism to detect if any values are out of the ordinary
* Use real-life sensors stationed in a real-life greenhouse instead of fake numbers.
* A better method to decrypt the data would be to implement public-key cryptography instead of symmetric key, where the sensors encrypt the data using the greenhouse-applications public key, and is decrypted using the greenhouse-applications private key.

## References
