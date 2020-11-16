package com.example.project_gift.model;

public class Teacher {

    private String displayName;
    private String userId;

    public Teacher() {

    }

    public Teacher(String displayName, String userId) {
        this.displayName = displayName;
        this.userId = userId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    public String getDisplayName() {
        return displayName;
    }

    public String getUserId() {
        return userId;
    }
}
