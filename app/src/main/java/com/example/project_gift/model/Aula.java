package com.example.project_gift.model;

import java.util.Date;

public class Aula {
    private Date startDate;
    private Date endDate;
    private String cursoId;
    private String disciplinaId;
    private String userId;

    public Aula(){
    }

    public Aula(Date startHour, Date endDate, String cursoId, String disciplinaId, String userId) {
        this.startDate = startHour;
        this.endDate = endDate;
        this.cursoId = cursoId;
        this.disciplinaId = disciplinaId;
        this.userId = userId;
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
}
