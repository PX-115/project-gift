package com.example.project_gift.model;

public class Disciplina {
    private String name;
    private String cursoId;
    private String userId;

    public Disciplina() {
    }

    public Disciplina(String name, String cursoId, String userId) {
        this.name = name;
        this.cursoId = cursoId;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getCursoId() {
        return cursoId;
    }

    public String getUserId() {
        return userId;
    }
}
