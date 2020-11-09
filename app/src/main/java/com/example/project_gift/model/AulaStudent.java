package com.example.project_gift.model;

public class AulaStudent {
    private String aulaId;
    private String userId;
    private boolean presence;

    public AulaStudent(String aulaId, String userId, boolean presence) {
        this.aulaId = aulaId;
        this.userId = userId;
        this.presence = presence;
    }

    public String getAulaId() {
        return aulaId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isPresence() {
        return presence;
    }
}
