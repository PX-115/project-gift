package com.example.project_gift.model;

import java.io.Serializable;

public class Equipamento implements Serializable {
    private String displayName;
    private String macAdress;

    public Equipamento() {
    }

    public Equipamento(String displayName, String macAdress) {
        this.displayName = displayName;
        this.macAdress = macAdress;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMacAdress() {
        return macAdress;
    }
}
