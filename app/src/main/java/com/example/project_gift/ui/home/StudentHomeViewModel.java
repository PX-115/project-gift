package com.example.project_gift.ui.home;

import android.text.format.DateFormat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.database.Database;
import com.example.project_gift.enums.AulaState;
import com.example.project_gift.model.Aula;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Disciplina;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;

public class StudentHomeViewModel extends ViewModel {

    private MutableLiveData<StudentHomeFormState> studentHomeFormState = new MutableLiveData<>();

    private MutableLiveData<String> mText;
    private MutableLiveData<String> mCurso;
    private MutableLiveData<String> mStatus;
    private MutableLiveData<String> mDisciplina;
    private MutableLiveData<String> mDate;

    private CollectionReference cursoRef;
    private CollectionReference aulaRef;
    private CollectionReference aulaAlunoRef;
    private CollectionReference disciplinaRef;
    DocumentSnapshot documentSnapshotCurso = null;


    LiveData<StudentHomeFormState> getLoginFormState() {
        return studentHomeFormState;
    }

    public StudentHomeViewModel() {
        // Init firebase collections
        cursoRef = Database.getCursoRef();
        aulaRef = Database.getAulaRef();
        aulaAlunoRef = Database.getAulaStudentRef();
        disciplinaRef = Database.getDisciplinaRef();

        // Instance mutableVariables
        mText = new MutableLiveData<>();
        mCurso = new MutableLiveData<>();
        mStatus = new MutableLiveData<>();
        mDisciplina = new MutableLiveData<>();
        mDate = new MutableLiveData<>();

        // SetUp initialValues
        mStatus.setValue("Nenhuma aula encontrada");
        mCurso.setValue("Nenhum curso selecionado");

        mText.setValue("Bem-vindo,\n" + LoggedUser.getLoggedUser().getEmail());

        cursoRef.whereEqualTo("userId", LoggedUser.getLoggedUser().getUid())
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        return;
                    }
                    if (queryDocumentSnapshots.getDocuments().size() == 0) {
                        mCurso.setValue("Nenhum curso selecionado");
                        return;
                    }
                    documentSnapshotCurso = queryDocumentSnapshots.getDocuments().get(0);
                    Curso curso = documentSnapshotCurso.toObject(Curso.class);
                    mCurso.setValue(curso.getName());

                    getAulas();
                });
    }

    private void getAulas() {
        aulaRef.whereEqualTo("cursoId", documentSnapshotCurso.getId())
                .whereGreaterThan("endDate", Calendar.getInstance().getTime())
                .orderBy("endDate")
                .orderBy("startDate")
                .addSnapshotListener(((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        studentHomeFormState.setValue(new StudentHomeFormState(AulaState.NAO_ENCONTRADA.getValue()));
                        return;
                    }
                    if (queryDocumentSnapshots.getDocuments().size() == 0) {
                        studentHomeFormState.setValue(new StudentHomeFormState(AulaState.NAO_ENCONTRADA.getValue()));
                        mStatus.setValue("Nenhuma aula encontrada");
                        return;
                    }

                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    Aula aula = documentSnapshot.toObject(Aula.class);
                    mStatus.setValue(setAulaTime(aula));
                    setDisciplina(aula);
                }));
    }

    private String setAulaTime(Aula aula) {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(aula.getStartDate());

        Calendar endTime = Calendar.getInstance();
        startTime.setTime(aula.getEndDate());

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE, MMMM d, h:mm aa";
        if(startTime.before(now)) {
            studentHomeFormState.setValue(new StudentHomeFormState(AulaState.EM_ANDAMENTO.getValue()));
            return "Aula em andamento";
        }
        else {
            studentHomeFormState.setValue(new StudentHomeFormState(AulaState.NAO_INICIADA.getValue()));
            if (now.get(Calendar.DATE) == startTime.get(Calendar.DATE)) {
                return "Hoje " + DateFormat.format(timeFormatString, startTime);
            } else if (now.get(Calendar.DATE) - startTime.get(Calendar.DATE) == 1) {
                return "Amanhã " + DateFormat.format(timeFormatString, startTime);
            } else if (now.get(Calendar.YEAR) == startTime.get(Calendar.YEAR)) {
                return DateFormat.format(dateTimeFormatString, startTime).toString();
            } else {
                return DateFormat.format("MMMM dd yyyy, h:mm aa", startTime).toString();
            }
        }
    }

    private void setDisciplina(Aula aula) {
        disciplinaRef.document(aula.getDisciplinaId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Disciplina disciplina = task.getResult().toObject(Disciplina.class);
                        mDisciplina.setValue(disciplina.getName());
                    } else {
                        mDisciplina.setValue("Nenhuma disciplina encontrada");
                    }
                });
    }

    private void setHorario(Aula aula) {

    }

    public LiveData<String> getStatus() {
        return mStatus;
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<String> getCurso() {
        return mCurso;
    }

    public LiveData<String> getDisciplina() {
        return mDisciplina;
    }

    public LiveData<String> getDate() {
        return mDate;
    }
}