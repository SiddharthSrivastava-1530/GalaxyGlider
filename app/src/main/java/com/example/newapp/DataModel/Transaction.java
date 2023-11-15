package com.example.newapp.DataModel;

import java.io.Serializable;

public class Transaction implements Serializable {

    private String userUID;
    private String userName;
    private String userEmail;
    private String transactionId;
    private String companyId;
    private String companyName;
    private String spaceShipId;
    private String spaceShipName;
    private String chosenSeatConfiguration;
    private String departure;
    private String destination;
    private String distance;
    private long transactionTime;
    private float totalFare;
    private boolean isTransactionComplete;

    public Transaction(){

    }

    public Transaction(String userUID, String userName, String userEmail, String transactionId, String companyId, String companyName, String spaceShipId, String spaceShipName, String chosenSeatConfiguration, String departure, String destination, String distance, long transactionTime, float totalFare, boolean isTransactionComplete) {
        this.userUID = userUID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.transactionId = transactionId;
        this.companyId = companyId;
        this.companyName = companyName;
        this.spaceShipId = spaceShipId;
        this.spaceShipName = spaceShipName;
        this.chosenSeatConfiguration = chosenSeatConfiguration;
        this.departure = departure;
        this.destination = destination;
        this.distance = distance;
        this.transactionTime = transactionTime;
        this.totalFare = totalFare;
        this.isTransactionComplete = isTransactionComplete;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSpaceShipId() {
        return spaceShipId;
    }

    public void setSpaceShipId(String spaceShipId) {
        this.spaceShipId = spaceShipId;
    }

    public String getSpaceShipName() {
        return spaceShipName;
    }

    public void setSpaceShipName(String spaceShipName) {
        this.spaceShipName = spaceShipName;
    }

    public String getChosenSeatConfiguration() {
        return chosenSeatConfiguration;
    }

    public void setChosenSeatConfiguration(String chosenSeatConfiguration) {
        this.chosenSeatConfiguration = chosenSeatConfiguration;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public long getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(long transactionTime) {
        this.transactionTime = transactionTime;
    }

    public float getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(float totalFare) {
        this.totalFare = totalFare;
    }

    public boolean isTransactionComplete() {
        return isTransactionComplete;
    }

    public void setTransactionComplete(boolean transactionComplete) {
        isTransactionComplete = transactionComplete;
    }

}
