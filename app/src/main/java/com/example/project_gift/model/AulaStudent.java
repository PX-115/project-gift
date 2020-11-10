package com.example.project_gift.model;

import java.util.Date;

public class AulaStudent {
    private String aulaId;
    private String userId;
    private Date checkInTime;
    private Date checkOutTime;
    private boolean checkIn;
    private boolean checkOut;

    public AulaStudent() {
    }

    public AulaStudent(String aulaId, String userId, Date checkInTime, Date checkOutTime, boolean checkIn, boolean checkOut) {
        this.aulaId = aulaId;
        this.userId = userId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
    }

    public String getAulaId() {
        return aulaId;
    }

    public String getUserId() {
        return userId;
    }

    public Date getCheckInTime() {
        return checkInTime;
    }

    public Date getCheckOutTime() {
        return checkOutTime;
    }

    public boolean isCheckIn() {
        return checkIn;
    }

    public boolean isCheckOut() {
        return checkOut;
    }
}
