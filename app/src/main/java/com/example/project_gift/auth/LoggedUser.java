package com.example.project_gift.auth;

import com.example.project_gift.model.Student;
import com.example.project_gift.model.Teacher;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoggedUser {
    private static FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static FirebaseUser user = null;
    private static Student mStudent = null;
    private static Teacher mTeacher = null;

    private LoggedUser() {
    }

    public static FirebaseUser getLoggedUser() {
        if(user == null) {
            user = firebaseAuth.getCurrentUser();
        }
        return user;
    }

    public static void setStudent(Student student) {
        if(mTeacher != null) {
            throw new RuntimeException("User already setup");
        }
        mStudent = student;
    }

    public static void setTeacher(Teacher teacher) {
        if(mStudent != null) {
            throw new RuntimeException("User already setup");
        }
        mTeacher = teacher;
    }

    public static Object getType() {
        return mStudent != null ? mStudent : mTeacher;
    }

    public void signOut() {
        mTeacher = null;
        mStudent = null;
        firebaseAuth.signOut();
    }
}
