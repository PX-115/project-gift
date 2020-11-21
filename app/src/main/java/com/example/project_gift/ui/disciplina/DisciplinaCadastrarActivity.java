package com.example.project_gift.ui.disciplina;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.project_gift.R;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Disciplina;
import com.example.project_gift.model.Equipamento;
import com.example.project_gift.ui.bottomsheet.CursoSelecionarFragment;
import com.example.project_gift.ui.bottomsheet.EquipamentoSelecionarFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class DisciplinaCadastrarActivity extends AppCompatActivity {

    private static final String DISCIPLINA = "DISCIPLINA";

    private TextInputLayout textInputLayoutNome;
    private TextView textViewCurso;
    private TextView textViewEquipamento;
    private FloatingActionButton buttonSave;

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
        getSupportActionBar().setTitle(getString(R.string.disciplina_title));

        // initViews
        textInputLayoutNome = findViewById(R.id.textInputNome);
        textViewCurso = findViewById(R.id.textViewCurso);
        textViewEquipamento = findViewById(R.id.textViewEquipamento);
        buttonSave = findViewById(R.id.buttonSave);

        // init firebase objects
        cursoRef = Database.getCursoRef();
        equipamentoRef = Database.getEquipamentoRef();

        // observers
        mCurso.observe(this, s -> textViewCurso.setText(s));
        mEquipamento.observe(this, s -> textViewEquipamento.setText(s));

        Intent intent = getIntent();
        Object data = intent.getSerializableExtra(DISCIPLINA);
        if (data != null) {
            disciplina = (Disciplina) data;
            assignValues();
        }
        buttonSave.setOnClickListener(v -> save());
        textViewCurso.setOnClickListener(v -> {
            CursoSelecionarFragment cursoSelecionarFragment = new CursoSelecionarFragment();
            cursoSelecionarFragment.show(getSupportFragmentManager(), "");
        });
        textViewEquipamento.setOnClickListener(v -> {
            EquipamentoSelecionarFragment equipamentoSelecionarFragment = new EquipamentoSelecionarFragment();
            equipamentoSelecionarFragment.show(getSupportFragmentManager(), "");
        });
    }

    private void assignValues() {
        textInputLayoutNome.getEditText().setText(disciplina.getNome());
        getCurso(disciplina.getCursoId());
        getEquipamento(disciplina.getEquipamentoId());
    }

    private void getCurso(String cursoId) {
        if (cursoId == null) return;
        cursoRef.document(cursoId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    curso = documentSnapshot.toObject(Curso.class);
                    curso.setCursoId(documentSnapshot.getId());
                    mCurso.setValue(curso.getName());
                });
    }

    private void getEquipamento(String equipamentoId) {
        if (equipamentoId == null) return;
        equipamentoRef.document(equipamentoId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    equipamento = documentSnapshot.toObject(Equipamento.class);
                    equipamento.setEquipamentoId(documentSnapshot.getId());
                    mEquipamento.setValue(equipamento.getDisplayName());
                });
    }

    public void selectCurso(DocumentSnapshot documentSnapshot) {
        curso = documentSnapshot.toObject(Curso.class);
        curso.setCursoId(documentSnapshot.getId());
        mCurso.setValue(curso.getName());
    }

    public void selectEquipamento(Equipamento equipamento) {
        this.equipamento = equipamento;
        mEquipamento.setValue(equipamento.getDisplayName());
    }

    private boolean validate() {
        String nome = textInputLayoutNome.getEditText().getText().toString();
        return validateNome(nome) && curso != null && equipamento != null;
    }

    private boolean validateNome(String nome) {
        if (nome.isEmpty()) {
            textInputLayoutNome.setError("Nome é obrigatório");
            return false;
        } else {
            textInputLayoutNome.setError(null);
            return true;
        }
    }

    private void setValues() {
        if (disciplina == null) {
            disciplina = new Disciplina(textInputLayoutNome.getEditText().getText().toString(),
                    curso.getCursoId(),
                    "",
                    curso);
            disciplina.setEquipamentoId(equipamento.getEquipamentoId());
            return;
        }
        disciplina.setNome(textInputLayoutNome.getEditText().getText().toString());
        disciplina.setCursoId(curso.getCursoId());
        disciplina.setEquipamentoId(equipamento.getEquipamentoId());
        disciplina.setCurso(curso);
    }

    private void save() {
        if (!validate()) {
            return;
        }
        setValues();

        Intent returnIntent = new Intent();
        returnIntent.putExtra(DISCIPLINA, disciplina);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}