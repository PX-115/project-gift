package com.example.project_gift.ui.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_gift.R;
import com.example.project_gift.adapter.firebase.EquipamentoAdapter;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Equipamento;
import com.example.project_gift.ui.disciplina.DisciplinaCadastrarActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

public class EquipamentoSelecionarFragment extends BottomSheetDialogFragment {

    private CollectionReference equipamentoRef;
    private EquipamentoAdapter equipamentoAdapter;

    public static EquipamentoSelecionarFragment newInstance() {
        final EquipamentoSelecionarFragment fragment = new EquipamentoSelecionarFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equipamento_selecionar, container, false);
        equipamentoRef = Database.getEquipamentoRef();

        final MaterialButton button = view.findViewById(R.id.materialButton);
        button.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FirestoreRecyclerOptions<Equipamento> options = getOptions();
        equipamentoAdapter = new EquipamentoAdapter(options);

        RecyclerView recyclerView = getView().findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(equipamentoAdapter);

        equipamentoAdapter.setOnItemClickListener((documentSnapshot, position) -> {
            Equipamento equipamento = documentSnapshot.toObject(Equipamento.class);
            equipamento.setEquipamentoId(documentSnapshot.getId());
            select(equipamento);
            dismiss();
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

    private void select(Equipamento equipamento) {
        ((DisciplinaCadastrarActivity)getActivity()).selectEquipamento(equipamento);
    }
}