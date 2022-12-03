# IDATA2304-Project-Group13
This is a school group project for the course IDATA2304 Computer networks.
 
## Abstract
In a greenhouse it is important that the temperature, humidity and co2 levels constantly stay the same correct value. Monitoring these values is therefore very important, so that we can adjust the values if they are wrong. This is why we made a program that keeps a live feed of the temperatures, so that you can keep track of these values, and notice any irregularities. We constantly evaluated our solution ourselves, and came up with ideas of improvement that we deemed necessary. 


Creating an application that gets temperature inputs from a greenhouse, visualizes the data on a graph/timeline, and gives a response in case the temperature deviates to a harmful value for the plants in the greenhouse. 

The application is intened to be used by an administrator over the greenhouse and monitor its values without having to be present at the greenhouse.

## Introduction
This solution can be used by both professional greenhouse farmers, and private hobbyists. Constantly keeping track of the temperature, humidity and co2 levels can prove to be tiresome work. With this solution you can keep track of the temperature remotely, and could keep track of several different greenhouses.

In [Theory and technology](https://github.com/adrianrdntnu/IDATA2304-Project-Group13/blob/main/README.md#theory-and-technology) we go through the theory and technologies we used to implement this solution. We then describe our work proccess in [Methodology](https://github.com/adrianrdntnu/IDATA2304-Project-Group13/blob/main/README.md#methodology), and our result in [Results](https://github.com/adrianrdntnu/IDATA2304-Project-Group13/blob/main/README.md#results). In the end we discuss our result in [Discussion](https://github.com/adrianrdntnu/IDATA2304-Project-Group13/blob/main/README.md#discussion) and reflect over possible improvements and future work, in [Conclusion and future work](https://github.com/adrianrdntnu/IDATA2304-Project-Group13/blob/main/README.md#conclusion-and-future-work).

## Theory and technology

* For data simulation we have used the java class Random. Here we use the method nextGaussian, to get a random normal distributed number. This way we can create some randomness/deviation to the "sensor reading" without changing the mean (actual temp/humidity/co2 value). To process the data we chose to store the data in a BST, which we added methods for extracting highest and lowest values. We have used JavaFX for our application and LineChart for visualization.
* Storing values in a BST is something we learned in IDATA2302 Algorithms and datastructures. This tree structure makes the runtime of finding the lowest and highest value shorter. Using nextGaussian has a slight connection to what we learn in ISTA1003 Statistics, as we learn to work with normal distributed data.

### 1. MQTT broker
- Stuff about MQTT broker

### 2. JavaFX
- Stuff about JavaFX features used

### 3. TCP? idk if we use this

## Approach
### 1. GitHub
- (this)

### 2. Maven
- Stuff about maven / file structure

### 3. Google checkstyle
- Why google checkstyle was chosen

### 3. Storing as a BST
- Stuff about BST, realising it probably wasn't necessary

## Additional Features
### 1. JavaFx Interface
Provides the user with an interface resembling an administrative application. The user can start and stop a temperature sensor, while getting *live** values vizualised on a linechart.

*The live values are generated by a fake value generator providing "realistic" values immitating the given sensor.

### 2. Fake Value Generator
- Stuff about how the generator works

## Methodology
* Diagram?

## Results
The final product is a JavaFX application mimicing an administrative program used to administrate a Greenhouse and its sensors.

Because the program uses fake sensors, it both sends to and retreives data from an MQTT broker.

## Discussion

## Conclusion and future work
* Storing and loading data.
* Individually start and stop sensors
* Login page with username and password
* Modify fake value generator to include time of day as a variable.

## References
