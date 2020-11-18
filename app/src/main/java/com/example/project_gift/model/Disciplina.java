package com.example.project_gift.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Disciplina implements Serializable {
    @Exclude
    private String disciplinaId;
    private String nome;
    private String cursoId;
    private String userId;
    private Curso curso;

    public Disciplina() {
    }

    public Disciplina(String nome, String cursoId, String userId, Curso curso) {
        this.nome = nome;
        this.cursoId = cursoId;
        this.userId = userId;
        this.curso = curso;
    }

    public String getDisciplinaId() {
        return disciplinaId;
    }

    public void setDisciplinaId(String disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public String getNome() {
        return nome;
    }

    public String getCursoId() {
        return cursoId;
    }

    public String getUserId() {
        return userId;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
