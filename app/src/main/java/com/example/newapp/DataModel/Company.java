package com.example.newapp.DataModel;

import java.io.Serializable;
import java.security.SecureRandom;
import java.util.ArrayList;

public class Company implements Serializable {

    private String name;
    private String email;
    private String loginMode;
    private String companyId;
    private String licenseUrl;
    private String description;
    private String imageUrl;
    private Boolean isOperational;
    private ArrayList<SpaceShip> spaceShips;
    private boolean isCurrentSlotUpdated;


    public Company() {

    }

    public Company(String name, String email, String loginMode, String companyId, String licenseUrl, String description, String imageUrl, Boolean isOperational, ArrayList<SpaceShip> spaceShips, boolean isCurrentSlotUpdated) {
        this.name = name;
        this.email = email;
        this.loginMode = loginMode;
        this.companyId = companyId;
        this.licenseUrl = licenseUrl;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isOperational = isOperational;
        this.spaceShips = spaceShips;
        this.isCurrentSlotUpdated = isCurrentSlotUpdated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(String loginMode) {
        this.loginMode = loginMode;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getOperational() {
        return isOperational;
    }

    public void setOperational(Boolean operational) {
        isOperational = operational;
    }

    public ArrayList<SpaceShip> getSpaceShips() {
        return spaceShips;
    }

    public void setSpaceShips(ArrayList<SpaceShip> spaceShips) {
        this.spaceShips = spaceShips;
    }

    public boolean isCurrentSlotUpdated() {
        return isCurrentSlotUpdated;
    }

    public void setCurrentSlotUpdated(boolean currentSlotUpdated) {
        isCurrentSlotUpdated = currentSlotUpdated;
    }

}
