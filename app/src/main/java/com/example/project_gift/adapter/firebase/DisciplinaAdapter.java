package com.example.project_gift.adapter.firebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_gift.R;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Disciplina;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class DisciplinaAdapter extends FirestoreRecyclerAdapter<Disciplina, DisciplinaAdapter.DisciplinaHolder> {
    private OnItemClickListener listener;
    private CollectionReference cursoRef = Database.getCursoRef();

    public DisciplinaAdapter(@NonNull FirestoreRecyclerOptions<Disciplina> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DisciplinaHolder holder, int position, @NonNull Disciplina model) {
        holder.disciplinaText.setText(model.getNome());
        if(model.getCursoId() == null) {
            return;
        }
        cursoRef.document(model.getCursoId())
                .addSnapshotListener((documentSnapshot, e) -> {
                    Curso curso = documentSnapshot.toObject(Curso.class);
                    holder.cursoText.setText(curso.getName());
                });
    }

    @NonNull
    @Override
    public DisciplinaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.disciplina_item, parent, false);
        return new DisciplinaHolder(v);
    }

    class DisciplinaHolder extends RecyclerView.ViewHolder {

        private TextView disciplinaText;
        private TextView cursoText;

        public DisciplinaHolder(@NonNull View itemView) {
            super(itemView);

            disciplinaText = itemView.findViewById(R.id.textViewDisciplina);
            cursoText = itemView.findViewById(R.id.textCurso);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
