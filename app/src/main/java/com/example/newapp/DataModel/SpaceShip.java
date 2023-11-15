package com.example.newapp.DataModel;

import java.io.Serializable;
import java.util.ArrayList;

public class SpaceShip implements Serializable {
    private String spaceShipName;
    private String description;
    private String spaceShipId;
    private String spaceShipRating;
    private String slot1;
    private String slot2;
    private String slot3;
    private String slot4;
    private String slot5;
    private String slot6;
    private String slot7;
    private String slot8;
    private String servicesAvailable;
    private boolean haveRideSharing;
    private long busyTime;
    private float price;
    private String nextSlotConfig;
    private String seatConfiguration;
    private ArrayList<String> transactionIds;

    public SpaceShip(){

    }

    public SpaceShip(String spaceShipName, String description, String spaceShipId, String spaceShipRating, String slot1, String slot2, String slot3, String slot4, String slot5, String slot6, String slot7, String slot8, String servicesAvailable, boolean haveRideSharing, long busyTime, float price, String nextSlotConfig, String seatConfiguration, ArrayList<String> transactionIds) {
        this.spaceShipName = spaceShipName;
        this.description = description;
        this.spaceShipId = spaceShipId;
        this.spaceShipRating = spaceShipRating;
        this.slot1 = slot1;
        this.slot2 = slot2;
        this.slot3 = slot3;
        this.slot4 = slot4;
        this.slot5 = slot5;
        this.slot6 = slot6;
        this.slot7 = slot7;
        this.slot8 = slot8;
        this.servicesAvailable = servicesAvailable;
        this.haveRideSharing = haveRideSharing;
        this.busyTime = busyTime;
        this.price = price;
        this.nextSlotConfig = nextSlotConfig;
        this.seatConfiguration = seatConfiguration;
        this.transactionIds = transactionIds;
    }


    public String getSpaceShipName() {
        return spaceShipName;
    }

    public void setSpaceShipName(String spaceShipName) {
        this.spaceShipName = spaceShipName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpaceShipId() {
        return spaceShipId;
    }

    public void setSpaceShipId(String spaceShipId) {
        this.spaceShipId = spaceShipId;
    }

    public String getSpaceShipRating() {
        return spaceShipRating;
    }

    public void setSpaceShipRating(String spaceShipRating) {
        this.spaceShipRating = spaceShipRating;
    }

    public String getSlot1() {
        return slot1;
    }

    public void setSlot1(String slot1) {
        this.slot1 = slot1;
    }

    public String getSlot2() {
        return slot2;
    }

    public void setSlot2(String slot2) {
        this.slot2 = slot2;
    }

    public String getSlot3() {
        return slot3;
    }

    public void setSlot3(String slot3) {
        this.slot3 = slot3;
    }

    public String getSlot4() {
        return slot4;
    }

    public void setSlot4(String slot4) {
        this.slot4 = slot4;
    }

    public String getSlot5() {
        return slot5;
    }

    public void setSlot5(String slot5) {
        this.slot5 = slot5;
    }

    public String getSlot6() {
        return slot6;
    }

    public void setSlot6(String slot6) {
        this.slot6 = slot6;
    }

    public String getSlot7() {
        return slot7;
    }

    public void setSlot7(String slot7) {
        this.slot7 = slot7;
    }

    public String getSlot8() {
        return slot8;
    }

    public void setSlot8(String slot8) {
        this.slot8 = slot8;
    }

    public String getServicesAvailable() {
        return servicesAvailable;
    }

    public void setServicesAvailable(String servicesAvailable) {
        this.servicesAvailable = servicesAvailable;
    }

    public boolean isHaveRideSharing() {
        return haveRideSharing;
    }

    public void setHaveRideSharing(boolean haveRideSharing) {
        this.haveRideSharing = haveRideSharing;
    }

    public long getBusyTime() {
        return busyTime;
    }

    public void setBusyTime(long busyTime) {
        this.busyTime = busyTime;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getNextSlotConfig() {
        return nextSlotConfig;
    }

    public void setNextSlotConfig(String nextSlotConfig) {
        this.nextSlotConfig = nextSlotConfig;
    }

    public String getSeatConfiguration() {
        return seatConfiguration;
    }

    public void setSeatConfiguration(String seatConfiguration) {
        this.seatConfiguration = seatConfiguration;
    }

    public ArrayList<String> getTransactionIds() {
        return transactionIds;
    }

    public void setTransactionIds(ArrayList<String> transactionIds) {
        this.transactionIds = transactionIds;
    }
}
