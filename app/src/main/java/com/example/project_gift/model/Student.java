package com.example.project_gift.model;

public class Student {

    private String userId;
    private String cursoId;

    public Student() {

    }

    public Student(String userId, String cursoId) {
        this.userId = userId;
        this.cursoId = cursoId;
    }

    public String getUserId() {
        return userId;
    }

    public String getCursoId() {
        return cursoId;
    }
}
