package com.example.project_gift.model;

import com.example.project_gift.functions.DistanceCalculate;
import com.google.firebase.firestore.Exclude;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class Aula implements Serializable {
    private String aulaId;
    private Date startDate;
    private Date endDate;
    private String cursoId;
    private String disciplinaId;
    private String userId;

    @Exclude
    private Disciplina disciplina;

    public Aula() {
    }

    public Aula(Date startDate, Date endDate, String cursoId, String disciplinaId, String userId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.cursoId = cursoId;
        this.disciplinaId = disciplinaId;
        this.userId = userId;
    }

    public String getAulaId() {
        return aulaId;
    }

    public void setAulaId(String aulaId) {
        this.aulaId = aulaId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getCursoId() {
        return cursoId;
    }

    public String getDisciplinaId() {
        return disciplinaId;
    }

    public String getUserId() {
        return userId;
    }

    @Exclude
    public Disciplina getDisciplina() {
        return disciplina;
    }

    @Exclude
    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }
}
