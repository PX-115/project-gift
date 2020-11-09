package com.example.project_gift.model;

public class Curso {
    private String name;
    private String userId;

    public Curso() {
    }

    public Curso(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }
}
