package com.example.project_gift.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.project_gift.R;
import com.example.project_gift.adapter.common.DisciplinaAdapter;
import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Disciplina;
import com.example.project_gift.ui.bottomsheet.DisciplinaSelecionarFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ProfessorAddActivity extends AppCompatActivity {

    private final String USER_PROFILE = "USER_PROFILE";
    private final String DISCIPLINA = "DISCIPLINA";

    private TextInputLayout nomeTextInput;
    private FloatingActionButton buttonAdd;
    private FloatingActionButton buttonNext;

    private MutableLiveData<String> mNome = new MutableLiveData<>();

    private FirebaseAuth firebaseAuth;
    private CollectionReference teacherRef;

    private RecyclerView recyclerView;
    private DisciplinaAdapter disciplinaAdapter;

    private MutableLiveData<List<Disciplina>> mDisciplinas = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_professor_add);

        // setUp firebase objects
        firebaseAuth = FirebaseAuth.getInstance();
        teacherRef = Database.getTeacherRef();

        // init views
        nomeTextInput = findViewById(R.id.nome);
        buttonNext = findViewById(R.id.buttonNext);
        buttonAdd = findViewById(R.id.buttonAdd);

        mDisciplinas.setValue(new ArrayList<>());

        // set events
        buttonNext.setOnClickListener(v -> save());
        buttonAdd.setOnClickListener(v -> {
            DisciplinaSelecionarFragment disciplinaSelecionarFragment = new DisciplinaSelecionarFragment();
            disciplinaSelecionarFragment.show(getSupportFragmentManager(), "");
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

        // configure recyclerview
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        // set adapter
        mDisciplinas.observe(this, disciplinas -> {
            disciplinaAdapter = new DisciplinaAdapter();
            disciplinaAdapter.setDisciplinas(disciplinas);
            recyclerView.setAdapter(disciplinaAdapter);
        });

        nomeTextInput.getEditText().addTextChangedListener(textWatcher);
    }

    private void save() {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(nomeTextInput.getEditText().getText().toString())
                .build();

        Intent returnIntent = new Intent();
        returnIntent.putExtra(USER_PROFILE, profileUpdates);
        returnIntent.putExtra(DISCIPLINA, (Serializable) mDisciplinas.getValue());
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    public void selectDisciplinas(Disciplina disciplina) {
        disciplina.setUserId(LoggedUser.getLoggedUser().getUid());
        List<Disciplina> disciplinas = mDisciplinas.getValue();
        disciplinas.add(disciplina);

        mDisciplinas.setValue(disciplinas);
    }
}