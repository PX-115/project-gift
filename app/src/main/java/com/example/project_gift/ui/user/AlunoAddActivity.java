package com.example.project_gift.ui.user;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.TextView;

import com.example.project_gift.R;
import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Student;
import com.example.project_gift.ui.bottomsheet.CursoSelecionarFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class AlunoAddActivity extends AppCompatActivity {

    private final String USER_PROFILE = "USER_PROFILE";
    private final String CURSO = "CURSO";

    private TextInputLayout nomeTextInput;
    private TextView cursoText;
    private FloatingActionButton buttonNext;

    private MutableLiveData<String> mNome = new MutableLiveData<>();

    private FirebaseAuth firebaseAuth;
    private CollectionReference studentRef;

    private Curso curso = null;
    private Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aluno_add);

        student = (Student) LoggedUser.getType();

        // setUp firebase objects
        firebaseAuth = FirebaseAuth.getInstance();
        studentRef = Database.getStudentRef();

        // init views
        nomeTextInput = findViewById(R.id.nome);
        cursoText = findViewById(R.id.textViewCurso);
        buttonNext = findViewById(R.id.buttonNext);

        // set events
        buttonNext.setOnClickListener(v -> save());
        cursoText.setOnClickListener(v -> {
            CursoSelecionarFragment cursoSelecionarFragment = new CursoSelecionarFragment();
            cursoSelecionarFragment.show(getSupportFragmentManager(), "");
        });

        // observers
        mNome.observe(this, nome -> {
            if (nome.isEmpty()) {
                nomeTextInput.setError(getString(R.string.invalid_displayName));
            } else {
                nomeTextInput.setError(null);
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mNome.setValue(s.toString());
            }
        };

        nomeTextInput.getEditText().addTextChangedListener(textWatcher);
    }

    private boolean validateCurso() {
        if (curso == null) {
            Snackbar.make(findViewById(R.id.container), "Selecione um curso", Snackbar.LENGTH_SHORT)
                    .show();
            return false;
        }
        return true;
    }

    private void save() {
        if (!validateCurso()) {
            return;
        }

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nomeTextInput.getEditText().getText().toString())
                .build();

        Intent returnIntent = new Intent();
        returnIntent.putExtra(USER_PROFILE, profileUpdates);
        returnIntent.putExtra(CURSO, student.getCursoId());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void selectCurso(DocumentSnapshot curso) {
        this.curso = curso.toObject(Curso.class);
        student.setCursoId(curso.getId());

        cursoText.setText(this.curso.getName());
    }
}