package com.example.project_gift.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Curso implements Serializable {
    @Exclude
    private String cursoId;
    private String name;
    private String userId;

    public Curso() {
    }

    public Curso(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public String getCursoId() {
        return cursoId;
    }

    public void setCursoId(String cursoId) {
        this.cursoId = cursoId;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }
}
