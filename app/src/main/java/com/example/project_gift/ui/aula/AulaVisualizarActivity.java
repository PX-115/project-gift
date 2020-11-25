package com.example.project_gift.ui.aula;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.project_gift.R;
import com.example.project_gift.adapter.firebase.AulaAdapter;
import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Aula;
import com.example.project_gift.model.Student;
import com.example.project_gift.model.Teacher;
import com.example.project_gift.ui.bottomsheet.AulaVisualizarFragment;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Calendar;
import java.util.Date;

public class AulaVisualizarActivity extends AppCompatActivity {

    private CollectionReference aulaRef;
    private AulaAdapter aulaAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aula_visualizar);
        getSupportActionBar().setTitle("Aulas anteriores");

        // init firebase objects
        aulaRef = Database.getAulaRef();

        // setup recyclerView
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        Object userType = LoggedUser.getType();
        FirestoreRecyclerOptions<Aula> options = userType instanceof Student ?
                getOptionsStudent((Student) userType) :
                getOptionsTeacher((Teacher) userType);

        aulaAdapter = new AulaAdapter(options, userType instanceof Student ? LoggedUser.getLoggedUser() : null);
        recyclerView.setAdapter(aulaAdapter);
        aulaAdapter.startListening();

        aulaAdapter.setOnItemClickListener((documentSnapshot, position) -> {
            Aula aula = documentSnapshot.toObject(Aula.class);
            aula.setAulaId(documentSnapshot.getId());

            AulaVisualizarFragment aulaVisualizarFragment = AulaVisualizarFragment.newInstance(aula, false);
            aulaVisualizarFragment.show(getSupportFragmentManager(), "aulas_anteriores");
        });
    }

    private FirestoreRecyclerOptions<Aula> getOptionsTeacher(Teacher teacher) {
        Date date = Calendar.getInstance().getTime();
        Query query = aulaRef.whereEqualTo("userId", teacher.getUserId())
                .whereLessThan("startDate", date)
                .orderBy("startDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Aula> options = new FirestoreRecyclerOptions.Builder<Aula>()
                .setQuery(query, Aula.class)
                .build();

        return options;
    }

    private FirestoreRecyclerOptions<Aula> getOptionsStudent(Student student) {
        Date date = Calendar.getInstance().getTime();
        Query query = aulaRef.whereEqualTo("cursoId", student.getCursoId())
                .whereLessThan("startDate", date)
                .orderBy("startDate", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Aula> options = new FirestoreRecyclerOptions.Builder<Aula>()
                .setQuery(query, Aula.class)
                .build();

        return options;
    }

    @Override
    protected void onResume() {
        super.onResume();
        aulaAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        aulaAdapter.stopListening();
    }
}