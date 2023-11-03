package com.example.newapp.DataModel;

import java.util.ArrayList;

public class Company {

    private String name;
    private String email;
    private String description;
    private String imageUrl;
    private String companyId;
    private String loginMode;
    private Boolean isOperational;
    private ArrayList<SpaceShip> spaceShips;

    public Company(String name, String email, String description, String imageUrl, String companyId, String loginMode, Boolean isOperational, ArrayList<SpaceShip> spaceShips) {
        this.name = name;
        this.email = email;
        this.description = description;
        this.imageUrl = imageUrl;
        this.companyId = companyId;
        this.loginMode = loginMode;
        this.isOperational = isOperational;
        this.spaceShips = spaceShips;
    }

    public Company(){

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

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(String loginMode) {
        this.loginMode = loginMode;
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

}
