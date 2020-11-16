package com.example.project_gift.ui.bottomsheet;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.example.project_gift.adapter.firebase.CursoAdapter;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Curso;
import com.example.project_gift.ui.user.AlunoAddActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.project_gift.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class CursoSelecionarFragment extends BottomSheetDialogFragment {

    private CollectionReference cursoRef;
    private CursoAdapter cursoAdapter;

    public static CursoSelecionarFragment newInstance() {
        final CursoSelecionarFragment fragment = new CursoSelecionarFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_curso_selecionar, container, false);
        cursoRef = Database.getCursoRef();

        final MaterialButton button = view.findViewById(R.id.materialButton);
        button.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        FirestoreRecyclerOptions<Curso> options = getOptions();
        cursoAdapter = new CursoAdapter(options);

        RecyclerView recyclerView = getView().findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cursoAdapter);

        cursoAdapter.setOnItemClickListener((documentSnapshot, position) -> {
            select(documentSnapshot);
        });
        cursoAdapter.startListening();
    }

    private FirestoreRecyclerOptions<Curso> getOptions() {
        Query query = cursoRef.orderBy("name");

        FirestoreRecyclerOptions<Curso> options = new FirestoreRecyclerOptions.Builder<Curso>()
                .setQuery(query, Curso.class)
                .build();

        return options;
    }

    private void select(DocumentSnapshot curso) {
        ((AlunoAddActivity)getActivity()).selectCurso(curso);
        dismiss();
    }
}