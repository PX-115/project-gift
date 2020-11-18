package com.example.project_gift.ui.disciplina;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDiskIOException;
import android.os.Bundle;

import com.example.project_gift.R;
import com.example.project_gift.adapter.firebase.DisciplinaAdapter;
import com.example.project_gift.adapter.firebase.EquipamentoAdapter;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Disciplina;
import com.example.project_gift.model.Equipamento;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

public class DisciplinaSelecionarActivity extends AppCompatActivity {

    private static final String DISCIPLINA = "DISCIPLINA";
    private static final int DISCIPLINA_ADD_REQUEST = 1;
    private static final int DISCIPLINA_EDIT_REQUEST = 2;

    private FloatingActionButton buttonAdd;

    private CollectionReference disciplinaRef;
    private DisciplinaAdapter disciplinaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disciplina_selecionar);

        // initViews
        buttonAdd = findViewById(R.id.buttonAdd);

        // init firebase objects
        disciplinaRef = Database.getDisciplinaRef();

        FirestoreRecyclerOptions<Disciplina> options = getOptions();
        disciplinaAdapter = new DisciplinaAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(disciplinaAdapter);

        disciplinaAdapter.setOnItemClickListener((documentSnapshot, position) -> {
            Disciplina disciplina = documentSnapshot.toObject(Disciplina.class);
            disciplina.setDisciplinaId(documentSnapshot.getId());
            openForEdit(disciplina);
        });
        disciplinaAdapter.startListening();

        buttonAdd.setOnClickListener(v -> openForAdd());
    }

    private FirestoreRecyclerOptions<Disciplina> getOptions() {
        Query query = disciplinaRef.orderBy("nome");

        FirestoreRecyclerOptions<Disciplina> options = new FirestoreRecyclerOptions.Builder<Disciplina>()
                .setQuery(query, Disciplina.class)
                .build();

        return options;
    }

    @Override
    protected void onResume() {
        super.onResume();
        disciplinaAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        disciplinaAdapter.stopListening();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK) {
            if(data == null) return;
            Disciplina disciplina = (Disciplina) data.getSerializableExtra(DISCIPLINA);
            if(requestCode == DISCIPLINA_ADD_REQUEST) {
                addDisciplina(disciplina);
            } else if(requestCode == DISCIPLINA_EDIT_REQUEST) {
                editDisciplina(disciplina);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addDisciplina(Disciplina disciplina) {
        disciplinaRef.add(disciplina)
                .addOnSuccessListener(documentReference -> showSnackBar("Salvo com sucesso"))
                .addOnFailureListener(e -> showSnackBar(e.getMessage()));
    }

    private void editDisciplina(Disciplina disciplina) {
        disciplinaRef.document(disciplina.getDisciplinaId()).set(disciplina)
                .addOnSuccessListener(aVoid -> showSnackBar("Salvo com sucesso"))
                .addOnFailureListener(e -> showSnackBar(e.getMessage()));
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    private void openForAdd() {
        Intent intent = new Intent(this, DisciplinaCadastrarActivity.class);
        startActivityForResult(intent, DISCIPLINA_ADD_REQUEST);
    }

    private void openForEdit(Disciplina disciplina) {
        Intent intent = new Intent(this, DisciplinaCadastrarActivity.class);
        intent.putExtra(DISCIPLINA, disciplina);
        startActivityForResult(intent, DISCIPLINA_EDIT_REQUEST);
    }
}