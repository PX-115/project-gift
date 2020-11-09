package com.example.project_gift.enums;

import java.util.HashMap;
import java.util.Map;

public enum UserType {
    STUDENT(1),
    TEACHER(2);

    private int value;
    private static Map map = new HashMap<>();

    private UserType(int value) {
        this.value = value;
    }

    static {
        for (UserType pageType : UserType.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static UserType valueOf(int pageType){
        return (UserType)map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}
