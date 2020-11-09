package com.example.project_gift.ui.home;

import com.example.project_gift.ui.login.LoginFormState;

import javax.annotation.Nullable;

class StudentHomeFormState {

    private int aulaState;

    public StudentHomeFormState(int aulaState) {
        this.aulaState = aulaState;
    }

    public int getAulaState() {
        return aulaState;
    }
}
