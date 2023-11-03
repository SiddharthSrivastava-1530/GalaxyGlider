package com.example.newapp.DataModel;

public class SpaceShip {
    private String spaceShipName;
    private String spaceShipId;
    private String ratings;
    private String feedback;
    private String seatAvailability;
    private boolean haveRideSharing;
    private long busyTime;
    private float price;

    public SpaceShip(String spaceShipName, String spaceShipId, String ratings, String feedback, String seatAvailability, boolean haveRideSharing, long busyTime, float price) {
        this.spaceShipName = spaceShipName;
        this.spaceShipId = spaceShipId;
        this.ratings = ratings;
        this.feedback = feedback;
        this.seatAvailability = seatAvailability;
        this.haveRideSharing = haveRideSharing;
        this.busyTime = busyTime;
        this.price = price;
    }

    public String getSpaceShipName() {
        return spaceShipName;
    }

    public void setSpaceShipName(String spaceShipName) {
        this.spaceShipName = spaceShipName;
    }

    public String getSpaceShipId() {
        return spaceShipId;
    }

    public void setSpaceShipId(String spaceShipId) {
        this.spaceShipId = spaceShipId;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
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
}
