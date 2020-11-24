package com.example.project_gift.database;

import com.example.project_gift.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Database {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static CollectionReference studentRef = db.collection("student");
    private static CollectionReference teacherRef = db.collection("teacher");
    private static CollectionReference cursoRef = db.collection("curso");
    private static CollectionReference disciplinaRef = db.collection("disciplina");
    private static CollectionReference aulaRef = db.collection("aula");
    private static CollectionReference aulaStudentRef = db.collection("aula_student");
    private static CollectionReference equipamentoRef = db.collection("equipamento");

    private Database(){}

    public static CollectionReference getStudentRef() {
        return studentRef;
    }

    public static CollectionReference getTeacherRef() {
        return teacherRef;
    }

    public static CollectionReference getCursoRef() {
        return cursoRef;
    }

    public static CollectionReference getDisciplinaRef() {
        return disciplinaRef;
    }

    public static CollectionReference getAulaRef() {
        return aulaRef;
    }

    public static CollectionReference getAulaStudentRef() {
        return aulaStudentRef;
    }

    public static CollectionReference getEquipamentoRef() {
        return equipamentoRef;
    }
}
