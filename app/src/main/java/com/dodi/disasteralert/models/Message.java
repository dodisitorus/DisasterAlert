package com.dodi.disasteralert.models;

import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;

import java.io.Serializable;

public class Message implements Serializable {
    private String id, message, url, title, description, time;
    private Type type;

    public Message() {
        this.type = Type.TEXT;
    }

    public Message(RuntimeResponseGeneric r) {
        this.message = "";
        this.title = r.title();
        this.description = r.description();
        this.url = r.source();
        this.id = "2";
        this.type = Type.IMAGE;
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public enum Type {
        TEXT,
        IMAGE
    }
}

