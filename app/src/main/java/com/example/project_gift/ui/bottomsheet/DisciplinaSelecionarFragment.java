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
import com.example.project_gift.adapter.firebase.DisciplinaAdapter;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Disciplina;
import com.example.project_gift.ui.user.ProfessorAddActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

public class DisciplinaSelecionarFragment extends BottomSheetDialogFragment {

    private CollectionReference disciplinaRef;
    private DisciplinaAdapter disciplinaAdapter;

    public static DisciplinaSelecionarFragment newInstance() {
        final DisciplinaSelecionarFragment fragment = new DisciplinaSelecionarFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_disciplina_selecionar, container, false);
        disciplinaRef = Database.getDisciplinaRef();

        final MaterialButton button = view.findViewById(R.id.materialButton);
        button.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FirestoreRecyclerOptions<Disciplina> options = getOptions();
        disciplinaAdapter = new DisciplinaAdapter(options);

        RecyclerView recyclerView = getView().findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(disciplinaAdapter);

        disciplinaAdapter.setOnItemClickListener((documentSnapshot, position) -> {
            Disciplina disciplina = documentSnapshot.toObject(Disciplina.class);
            disciplina.setDisciplinaId(documentSnapshot.getId());
            select(disciplina);
        });
        disciplinaAdapter.startListening();
    }

    private FirestoreRecyclerOptions<Disciplina> getOptions() {
        Query query = disciplinaRef.orderBy("nome");

        FirestoreRecyclerOptions<Disciplina> options = new FirestoreRecyclerOptions.Builder<Disciplina>()
                .setQuery(query, Disciplina.class)
                .build();

        return options;
    }

    private void select(Disciplina disciplina) {
        ((ProfessorAddActivity)getActivity()).selectDisciplinas(disciplina);
        dismiss();
    }
}