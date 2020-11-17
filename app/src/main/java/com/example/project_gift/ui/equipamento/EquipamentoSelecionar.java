package com.example.project_gift.ui.equipamento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.project_gift.R;
import com.example.project_gift.adapter.firebase.CursoAdapter;
import com.example.project_gift.adapter.firebase.EquipamentoAdapter;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Equipamento;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;

public class EquipamentoSelecionar extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FloatingActionButton buttonAdd;

    private CollectionReference equipamentoRef;
    private EquipamentoAdapter equipamentoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipamento_selecionar);

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
            select(documentSnapshot);
        });
        equipamentoAdapter.startListening();
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

    private void select(DocumentSnapshot documentSnapshot){
        Equipamento equipamento = documentSnapshot.toObject(Equipamento.class);
        equipamento.setEquipamentoId(documentSnapshot.getId());
    }
}