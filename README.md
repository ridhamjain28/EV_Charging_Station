# EV Charging System

A Java-based Electric Vehicle Charging System developed as a college project.

The system allows customers to register their EV, add money to a wallet, view compatible charging stations, book chargers, manage reservations, and cancel bookings.

## Features

- Customer registration
- 2-Wheeler and 4-Wheeler support
- AC and DC Fast Chargers
- Vehicle-charger compatibility checking
- Wallet system
- Charger booking
- Charging duration and price calculation
- Reservation history
- Reservation cancellation
- 80% refund on cancellation
- Station revenue tracking
- Charger availability tracking
- Local web-based interface

## Technologies Used

- Java
- Object-Oriented Programming
- Java Collections
- Java HTTP Server
- HTML
- CSS
- JavaScript
- Git & GitHub


## How to Run

### 1. Clone the repository


git clone https://github.com/ridhamjain28/EV_Charging_Station/

cd EVChargingSystem


### 2. Compile the Java project


javac -d out src\User.java src\Customer.java src\Admin.java src\Charger.java src\ChargingStation.java src\Reservation.java src\EVChargingSystem.java server\WebServer.java


### 3. Start the web server


**java -cp out WebServer**


### 4. Open the application

Open:


http://localhost:8080


## OOP Concepts Used

- Classes and Objects
- Inheritance
- Encapsulation
- Method Overriding
- Constructors
- Arrays
- ArrayList
- Object Relationships


## Project Status

College project – Version 1

The current version focuses on implementing the core EV charging system using Java and a simple local web interface.


