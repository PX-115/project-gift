package com.example.project_gift.ui.horario;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_gift.R;
import com.example.project_gift.adapter.firebase.AulaAdapter;
import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Aula;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Student;
import com.example.project_gift.model.Teacher;
import com.example.project_gift.ui.aula.AulaVisualizarActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

public class HorarioFragment extends Fragment {

    private CollectionReference aulaRef;
    private AulaAdapter aulaAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_horario, container, false);

        // initviews
        ExtendedFloatingActionButton buttonUltimasAulas = root.findViewById(R.id.buttonUltimasAulas);
        buttonUltimasAulas.setOnClickListener(v -> {
            Intent intent  = new Intent(root.getContext(), AulaVisualizarActivity.class);
            startActivity(intent);
        });

        // init firebase objects
        aulaRef = Database.getAulaRef();

        // setup recyclerView
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setHasFixedSize(true);

        Object userType = LoggedUser.getType();
        FirestoreRecyclerOptions<Aula> options = userType instanceof Student ?
                getOptionsStudent((Student) userType) :
                getOptionsTeacher((Teacher) userType);

        aulaAdapter = new AulaAdapter(options);
        recyclerView.setAdapter(aulaAdapter);
        aulaAdapter.startListening();

        return root;
    }

    private FirestoreRecyclerOptions<Aula> getOptionsTeacher(Teacher teacher) {
        Query query = aulaRef.whereEqualTo("userId", teacher.getUserId())
                .whereGreaterThan("startDate", Calendar.getInstance().getTime())
                .orderBy("startDate");

        FirestoreRecyclerOptions<Aula> options = new FirestoreRecyclerOptions.Builder<Aula>()
                .setQuery(query, Aula.class)
                .build();

        return options;
    }

    private FirestoreRecyclerOptions<Aula> getOptionsStudent(Student student) {
        Query query = aulaRef.whereEqualTo("cursoId", student.getCursoId())
                .whereGreaterThan("startDate", Calendar.getInstance().getTime())
                .orderBy("startDate");

        FirestoreRecyclerOptions<Aula> options = new FirestoreRecyclerOptions.Builder<Aula>()
                .setQuery(query, Aula.class)
                .build();

        return options;
    }

    @Override
    public void onResume() {
        super.onResume();
        aulaAdapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        aulaAdapter.stopListening();
    }
}