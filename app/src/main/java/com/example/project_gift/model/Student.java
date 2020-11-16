package com.example.project_gift.model;

public class Student {

    private String displayName;
    private String userId;
    private String cursoId;

    public Student() {

    }

    public Student(String displayName, String userId, String cursoId) {
        this.displayName = displayName;
        this.userId = userId;
        this.cursoId = cursoId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getCursoId() {
        return cursoId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setCursoId(String cursoId) {
        this.cursoId = cursoId;
    }
}
