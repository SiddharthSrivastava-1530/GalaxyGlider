package com.example.newapp.DataModel;

import java.util.ArrayList;

public class SpaceShip {
    private String spaceShipName;
    private String description;
    private String spaceShipId;
    private String spaceShipRating;
    private String seatAvailability;
    private boolean haveRideSharing;
    private long busyTime;
    private float price;
    private float speed;
    private ArrayList<Review> reviews;

    public SpaceShip(){

    }

    public SpaceShip(String spaceShipName, String description, String spaceShipId, String spaceShipRating, String seatAvailability, boolean haveRideSharing, long busyTime, float price, float speed, ArrayList<Review> reviews) {
        this.spaceShipName = spaceShipName;
        this.description = description;
        this.spaceShipId = spaceShipId;
        this.spaceShipRating = spaceShipRating;
        this.seatAvailability = seatAvailability;
        this.haveRideSharing = haveRideSharing;
        this.busyTime = busyTime;
        this.price = price;
        this.speed = speed;
        this.reviews = reviews;
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

    public String getSeatAvailability() {
        return seatAvailability;
    }

    public void setSeatAvailability(String seatAvailability) {
        this.seatAvailability = seatAvailability;
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

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }
}
