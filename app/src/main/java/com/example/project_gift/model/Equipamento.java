package com.example.project_gift.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

public class Equipamento implements Serializable {
    @Exclude
    private String equipamentoId;
    private String displayName;
    private String macAdress;
    private int maxDistance;

    public Equipamento() {
    }

    public Equipamento(String displayName, String macAdress) {
        this.displayName = displayName;
        this.macAdress = macAdress;
    }

    public String getEquipamentoId() {
        return equipamentoId;
    }

    public void setEquipamentoId(String equipamentoId) {
        this.equipamentoId = equipamentoId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMacAdress() {
        return macAdress;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setMacAdress(String macAdress) {
        this.macAdress = macAdress;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }
}
