package com.example.project_gift.ui.disciplina;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.project_gift.R;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Disciplina;
import com.example.project_gift.model.Equipamento;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;

public class DisciplinaCadastrarActivity extends AppCompatActivity {

    private static final String DISCIPLINA = "DISCIPLINA";

    private TextInputLayout textInputLayoutNome;
    private TextView textViewCurso;
    private TextView textViewEquipamento;

    private CollectionReference cursoRef;
    private CollectionReference equipamentoRef;

    private Disciplina disciplina = null;
    private Curso curso = null;
    private Equipamento equipamento = null;

    private MutableLiveData<String> mCurso = new MutableLiveData<>();
    private MutableLiveData<String> mEquipamento = new MutableLiveData<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disciplina_cadastrar);

        // initViews
        textInputLayoutNome = findViewById(R.id.textInputNome);
        textViewCurso = findViewById(R.id.textViewCurso);
        textViewEquipamento = findViewById(R.id.textViewEquipamento);

        // init firebase objects
        cursoRef = Database.getCursoRef();
        equipamentoRef = Database.getEquipamentoRef();

        // observers
        mCurso.observe(this, s -> textViewCurso.setText(s));
        mEquipamento.observe(this, s -> textViewEquipamento.setText(s));

        Intent intent = getIntent();
        Object data = intent.getSerializableExtra(DISCIPLINA);
        if(data != null) {
            disciplina = (Disciplina) data;
            assignValues();
        }
    }

    private void assignValues() {
        textInputLayoutNome.getEditText().setText(disciplina.getNome());
        getCurso(disciplina.getCursoId());
        getEquipamento("");
    }

    private void getCurso(String cursoId) {
        cursoRef.document(cursoId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    curso = documentSnapshot.toObject(Curso.class);
                    mCurso.setValue(curso.getName());
                });
    }

    private void getEquipamento(String equipamentoId) {
        equipamentoRef.document(equipamentoId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    equipamento = documentSnapshot.toObject(Equipamento.class);
                    mEquipamento.setValue(equipamento.getDisplayName());
                });
    }
}