package com.dodi.disasteralert.models;

import java.io.Serializable;

public class VisualRecognitionModel implements Serializable {

    private String className, score, typeHierarchy;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTypeHierarchy() {
        return typeHierarchy;
    }

    public void setTypeHierarchy(String typeHierarchy) {
        this.typeHierarchy = typeHierarchy;
    }
}
