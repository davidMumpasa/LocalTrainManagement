package com.example.myapplication.Model;

import java.util.Timer;

public class Train {
    private int TrainId;
    private String trainName;
    private String originStation;
    private String destinationStation;
    private String departureTime;
    private String ArrivalTime;
    private int capacity;
    private String price;

    public Train() {
    }

    public Train(int trainId, String trainName, String originStation, String destinationStation, String departureTime, String arrivalTime, int capacity, String price) {
        TrainId = trainId;
        this.trainName = trainName;
        this.originStation = originStation;
        this.destinationStation = destinationStation;
        this.departureTime = departureTime;
        ArrivalTime = arrivalTime;
        this.capacity = capacity;
        this.price = price;
    }

    public int getTrainId() {
        return TrainId;
    }

    public String getOriginStation() {
        return originStation;
    }

    public void setOriginStation(String originStation) {
        this.originStation = originStation;
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(String destinationStation) {
        this.destinationStation = destinationStation;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return ArrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        ArrivalTime = arrivalTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setTrainId(int trainId) {
        TrainId = trainId;
    }




    public void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTrainName() {
        return trainName;
    }

    // Add getter and setter methods for other fields
}
