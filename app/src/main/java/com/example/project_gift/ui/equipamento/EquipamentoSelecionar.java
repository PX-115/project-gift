package com.example.project_gift.ui.equipamento;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.project_gift.R;
import com.example.project_gift.adapter.firebase.EquipamentoAdapter;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Equipamento;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class EquipamentoSelecionar extends AppCompatActivity {


    private static final String EQUIPAMENTO = "EQUIPAMENTO";
    private static final int EQUIPAMENTO_ADD_REQUEST = 1;
    private static final int EQUIPAMENTO_EDIT_REQUEST = 2;

    private RecyclerView recyclerView;
    private FloatingActionButton buttonAdd;

    private CollectionReference equipamentoRef;
    private EquipamentoAdapter equipamentoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipamento_selecionar);
        getSupportActionBar().setTitle(getString(R.string.equipamento_title));

        // initViews
        recyclerView = findViewById(R.id.recyclerView);
        buttonAdd = findViewById(R.id.buttonAdd);

        // init firebase objects
        equipamentoRef = Database.getEquipamentoRef();

        FirestoreRecyclerOptions<Equipamento> options = getOptions();
        equipamentoAdapter = new EquipamentoAdapter(options);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(equipamentoAdapter);

        equipamentoAdapter.setOnItemClickListener((documentSnapshot, position) -> {
            edit(documentSnapshot);
        });
        equipamentoAdapter.startListening();

        buttonAdd.setOnClickListener(v -> novo());
    }

    private FirestoreRecyclerOptions<Equipamento> getOptions() {
        Query query = equipamentoRef.orderBy("displayName");

        FirestoreRecyclerOptions<Equipamento> options = new FirestoreRecyclerOptions.Builder<Equipamento>()
                .setQuery(query, Equipamento.class)
                .build();

        return options;
    }

    @Override
    protected void onResume() {
        super.onResume();
        equipamentoAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        equipamentoAdapter.stopListening();
    }

    private void edit(DocumentSnapshot documentSnapshot) {
        Equipamento equipamento = documentSnapshot.toObject(Equipamento.class);
        equipamento.setEquipamentoId(documentSnapshot.getId());

        Intent intent = new Intent(this, EquipamentoCadastrarActivity.class);
        intent.putExtra(EQUIPAMENTO, equipamento);
        startActivityForResult(intent, EQUIPAMENTO_EDIT_REQUEST);
    }

    private void novo() {
        Intent intent = new Intent(this, EquipamentoCadastrarActivity.class);
        startActivityForResult(intent, EQUIPAMENTO_ADD_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) return;
            Equipamento equipamento = (Equipamento) data.getSerializableExtra(EQUIPAMENTO);
            if (requestCode == EQUIPAMENTO_ADD_REQUEST) {
                addOnFirebase(equipamento);
            } else if (requestCode == EQUIPAMENTO_EDIT_REQUEST) {
                editOnFirebase(equipamento);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addOnFirebase(Equipamento equipamento) {
        equipamentoRef.add(equipamento)
                .addOnSuccessListener(documentReference -> showSnackBar("Salvo com sucesso"))
                .addOnFailureListener(e -> showSnackBar(e.getMessage()));
    }

    private void editOnFirebase(Equipamento equipamento) {
        equipamentoRef.document(equipamento.getEquipamentoId()).set(equipamento)
                .addOnSuccessListener(aVoid -> showSnackBar("Salvo com sucesso"))
                .addOnFailureListener(e -> showSnackBar(e.getMessage()));

        showSnackBar("Salvo com sucesso");
    }

    private void showSnackBar(String message) {
        Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_SHORT)
                .show();
    }
}