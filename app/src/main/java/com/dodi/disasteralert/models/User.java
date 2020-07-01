package com.dodi.disasteralert.models;

public class User {
    private String email;
    private String name;
    private String tokenNotification;

    public User(String email, String name, String token) {
        this.email = email;
        this.name = name;
        this.tokenNotification = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTokenNotification() {
        return tokenNotification;
    }

    public void setTokenNotification(String tokenNotification) {
        this.tokenNotification = tokenNotification;
    }
}
