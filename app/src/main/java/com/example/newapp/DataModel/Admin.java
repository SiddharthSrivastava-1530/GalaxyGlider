package com.example.newapp.DataModel;

public class Admin {
    private String name;
    private String email;
    private String number;
    private String loginMode;

    public Admin(){

    }

    public Admin(String name, String email, String number, String loginMode) {
        this.name = name;
        this.email = email;
        this.number = number;
        this.loginMode = loginMode;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getLoginMode() {
        return loginMode;
    }

    public void setLoginMode(String loginMode) {
        this.loginMode = loginMode;
    }
}
