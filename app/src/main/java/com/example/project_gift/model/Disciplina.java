package com.example.project_gift.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Disciplina implements Serializable {
    @Exclude
    private String disciplinaId;
    private String nome;
    private String cursoId;
    private String userId;
    private String equipamentoId;
    @Exclude
    private Curso curso;

    public Disciplina() {
    }

    public Disciplina(String nome, String cursoId, String userId, Curso curso) {
        this.nome = nome;
        this.cursoId = cursoId;
        this.userId = userId;
        this.curso = curso;
    }

    @Exclude
    public String getDisciplinaId() {
        return disciplinaId;
    }

    @Exclude
    public void setDisciplinaId(String disciplinaId) {
        this.disciplinaId = disciplinaId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCursoId() {
        return cursoId;
    }

    public void setCursoId(String cursoId) {
        this.cursoId = cursoId;
    }

    public String getUserId() {
        return userId;
    }

    @Exclude
    public Curso getCurso() {
        return curso;
    }

    @Exclude
    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEquipamentoId() {
        return equipamentoId;
    }

    public void setEquipamentoId(String equipamentoId) {
        this.equipamentoId = equipamentoId;
    }
}
