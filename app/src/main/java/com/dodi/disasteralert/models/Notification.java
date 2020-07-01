package com.dodi.disasteralert.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Notification {
    private String object;
    private String image;
    private String score;
    private double timestamp;
    private double reversetime;

    public Notification() {

    }

    public Notification(String object, String image, String score, double timestamp, double reverse) {
        this.object = object;
        this.image = image;
        this.score = score;
        this.timestamp = timestamp;
        this.reversetime = reverse;
    }

    public String getObject() {
        return object;
    }

    public String getImage() {
        return image;
    }

    public String getScore() {
        return score;
    }

    public double getTimestamp() {
        return timestamp;
    }
}
