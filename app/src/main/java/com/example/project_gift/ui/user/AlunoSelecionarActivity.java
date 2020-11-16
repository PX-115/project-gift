package com.example.project_gift.ui.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.project_gift.R;
import com.example.project_gift.adapter.firebase.AlunoAdapter;
import com.example.project_gift.adapter.firebase.DisciplinaAdapter;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Aula;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Disciplina;
import com.example.project_gift.model.Student;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

public class AlunoSelecionarActivity extends AppCompatActivity {

    private final String DOC_AULA = "DOC_AULA";
    private final String CURSO_NAME = "CURSO_NAME";
    private AlunoAdapter alunoAdapter;
    private Aula aulaDoc;

    private TextView textViewCurso;
    private TextView textViewDisciplina;

    private CollectionReference studentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aluno_selecionar);

        getSupportActionBar().setTitle("Alunos da classe");

        // initViews
        textViewCurso = findViewById(R.id.textViewCurso);

        Intent intent = getIntent();
        Object data = intent.getSerializableExtra(DOC_AULA);
        if (data == null) {
            finish();
        }
        aulaDoc = (Aula) data;
        String cursoName = intent.getStringExtra(CURSO_NAME);
        textViewCurso.setText(cursoName);

        studentRef = Database.getStudentRef();

        FirestoreRecyclerOptions<Student> options = getOptions();
        alunoAdapter = new AlunoAdapter(options, aulaDoc);

        RecyclerView recyclerView = findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(alunoAdapter);

        alunoAdapter.setOnItemClickListener((documentSnapshot, position) -> {
            Disciplina disciplina = documentSnapshot.toObject(Disciplina.class);
            disciplina.setDisciplinaId(documentSnapshot.getId());
        });
        alunoAdapter.startListening();
    }

    private FirestoreRecyclerOptions<Student> getOptions() {
        Query query = studentRef.whereEqualTo("cursoId", aulaDoc.getCursoId()).orderBy("displayName");

        FirestoreRecyclerOptions<Student> options = new FirestoreRecyclerOptions.Builder<Student>()
                .setQuery(query, Student.class)
                .build();

        return options;
    }

    @Override
    protected void onResume() {
        super.onResume();

        alunoAdapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();

        alunoAdapter.stopListening();
    }
}