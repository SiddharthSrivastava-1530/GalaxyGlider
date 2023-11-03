package com.example.newapp.DataModel;

public class Customer {

    private String name;
    private String number;
    private String email;
    private String profilePic;
    private String loginMode;

    public Customer(String name, String number, String email, String profilePic, String loginMode) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.profilePic = profilePic;
        this.loginMode = loginMode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(String loginMode) {
        this.loginMode = loginMode;
    }
}

