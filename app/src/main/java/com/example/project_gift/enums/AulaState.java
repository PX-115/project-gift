package com.example.project_gift.enums;

import java.util.HashMap;
import java.util.Map;

public enum AulaState {
    NAO_INICIADA(1),
    EM_ANDAMENTO(2),
    ENCERRADA(3),
    NAO_ENCONTRADA(4);

    private int value;
    private static Map map = new HashMap<>();

    private AulaState(int value) {
        this.value = value;
    }

    static {
        for (AulaState aulaState : AulaState.values()) {
            map.put(aulaState.value, aulaState);
        }
    }

    public static AulaState valueOf(int aulaState){
        return (AulaState) map.get(aulaState);
    }

    public int getValue() {
        return value;
    }
}
