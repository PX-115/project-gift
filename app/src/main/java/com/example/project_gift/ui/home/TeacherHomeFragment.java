package com.example.project_gift.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.project_gift.R;
import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Aula;
import com.example.project_gift.model.Disciplina;
import com.example.project_gift.model.Teacher;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;

public class TeacherHomeFragment extends Fragment {

    private TextView textView;
    private TextView textCurso;
    private TextView textStatus;
    private TextView textHora;
    private TextView textDisciplina;
    private ImageView imageView;
    private MaterialButton buttonSave;

    private CollectionReference aulaRef;
    private CollectionReference disciplinaRef;

    private MutableLiveData<String> mProfessor = new MutableLiveData<>();
    private MutableLiveData<String> mCurso = new MutableLiveData<>();
    private MutableLiveData<String> mDate = new MutableLiveData<>();
    private MutableLiveData<String> mDisciplina = new MutableLiveData<>();
    private MutableLiveData<String> mStatus = new MutableLiveData<>();
    private MutableLiveData<String> mButtonText = new MutableLiveData<>();

    private MutableLiveData<Integer> mStatusTextColor = new MutableLiveData<>();

    private Teacher teacher = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        teacher = (Teacher) LoggedUser.getType();

        // Init firebase objects
        aulaRef = Database.getAulaRef();
        disciplinaRef = Database.getDisciplinaRef();

        // Init Views
        textView = root.findViewById(R.id.text_user);
        textCurso = root.findViewById(R.id.text_curso);
        textDisciplina = root.findViewById(R.id.text_disciplina);
        textHora = root.findViewById(R.id.text_hora);

        imageView = root.findViewById(R.id.imageView);
        textStatus = root.findViewById(R.id.text_status);

        buttonSave = root.findViewById(R.id.buttonSave);

        // initial values
        mStatus.setValue("Nenhum horário localizado");
        buttonSave.setText("CHECK-IN");
        buttonSave.setVisibility(View.VISIBLE);
        buttonSave.setEnabled(false);

        // observers
        mProfessor.observe(getViewLifecycleOwner(), s -> textView.setText(s));
        mCurso.observe(getViewLifecycleOwner(), s -> textCurso.setText(s));
        mDisciplina.observe(getViewLifecycleOwner(), s -> textDisciplina.setText(s));
        mDate.observe(getViewLifecycleOwner(), s -> textHora.setText(s));
        mStatus.observe(getViewLifecycleOwner(), s -> textStatus.setText(s));
        mButtonText.observe(getViewLifecycleOwner(), s -> buttonSave.setText(s));
        mStatusTextColor.observe(getViewLifecycleOwner(), color -> textStatus.setTextColor(getResources().getColor(color)));

        // events
        buttonSave.setOnClickListener(v -> showActivityAlunos());

        // set values
        setAlunoText();

        getAula(LoggedUser.getLoggedUser().getUid());


        return root;
    }

    private void setAlunoText() {
        mProfessor.setValue("Bem-vindo, \n" + LoggedUser.getLoggedUser().getEmail());
    }

    private void getAula(String userId) {
        aulaRef.whereEqualTo("userId", userId)
                .whereGreaterThan("endDate", Calendar.getInstance().getTime())
                .orderBy("endDate")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null || queryDocumentSnapshots.getDocuments().size() == 0) {
                        resetViews("Nenhum horário encontrado");
                        return;
                    }
                    textHora.setVisibility(View.VISIBLE);

                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    Aula aula = documentSnapshot.toObject(Aula.class);

                    validateAula(aula);
                    mDate.setValue(setAulaTime(aula));

                    getDisciplina(aula.getDisciplinaId());
                });
    }

    private void getDisciplina(String disciplinaId) {
        disciplinaRef.document(disciplinaId).get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        Disciplina disciplina = task.getResult().toObject(Disciplina.class);
                        mDisciplina.setValue(disciplina.getName());
                        textDisciplina.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void resetViews(String mensagem) {
        textHora.setVisibility(View.GONE);
        textDisciplina.setVisibility(View.GONE);
        mStatus.setValue(mensagem);
        textStatus.setTextColor(getResources().getColor(R.color.flame_red));
        imageView.setImageResource(R.drawable.ic_close_red);
        buttonSave.setEnabled(false);
    }

    private String setAulaTime(Aula aula) {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(aula.getStartDate());

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "HH:mm";
        final String dateTimeFormatString = "EEEE, dd MMMM, HH:mm";

        String retorno = "";
        if (startTime.before(now)) {
            retorno = "Hoje às " + DateFormat.format(timeFormatString, startTime);
        } else {
            if (now.get(Calendar.DATE) == startTime.get(Calendar.DATE)) {
                retorno = "Hoje " + DateFormat.format(timeFormatString, startTime);
            } else if (now.get(Calendar.DATE) - startTime.get(Calendar.DATE) == 1) {
                retorno = "Amanhã " + DateFormat.format(timeFormatString, startTime);
            } else if (now.get(Calendar.YEAR) == startTime.get(Calendar.YEAR)) {
                retorno = DateFormat.format(dateTimeFormatString, startTime).toString();
            } else {
                retorno = DateFormat.format("dd MMMM yyyy, HH:mm", startTime).toString();
            }
        }
        retorno = retorno + "\n" + addEndTime(aula);
        return retorno;
    }

    private String addEndTime(Aula aula) {
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(aula.getEndDate());

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "HH:mm";
        final String dateTimeFormatString = "EEEE, dd MMMM, HH:mm";
        if (now.get(Calendar.DATE) == endTime.get(Calendar.DATE)) {
            return "Término: Hoje " + DateFormat.format(timeFormatString, endTime);
        } else if (now.get(Calendar.DATE) - endTime.get(Calendar.DATE) == 1) {
            return "Término: Amanhã " + DateFormat.format(timeFormatString, endTime);
        } else if (now.get(Calendar.YEAR) == endTime.get(Calendar.YEAR)) {
            return "Término: " + DateFormat.format(dateTimeFormatString, endTime).toString();
        } else {
            return "Término: " + DateFormat.format("dd MMMM yyyy, HH:mm", endTime).toString();
        }
    }

    private void validateAula(Aula aula) {
        if (aula.getStartDate().after(Calendar.getInstance().getTime())) {
            mStatus.setValue("Aguarde inicio da aula");
            mStatusTextColor.setValue(R.color.energy_yellow);
            imageView.setImageResource(R.drawable.ic_wait_time);

            buttonSave.setEnabled(true);
        } else if (aula.getStartDate().before(Calendar.getInstance().getTime())) {
            mStatus.setValue("Aula em andamento");
            mStatusTextColor.setValue(R.color.energy_yellow);
            imageView.setImageResource(R.drawable.ic_wait_time);

            buttonSave.setEnabled(true);
        }
    }

    private void showActivityAlunos() {
        Intent intent = new Intent();
        startActivity(intent);
    }
}