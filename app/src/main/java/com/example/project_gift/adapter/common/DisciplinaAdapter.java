package com.example.project_gift.adapter.common;

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
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;

public class DisciplinaAdapter extends RecyclerView.Adapter<DisciplinaAdapter.DisciplinaHolder> {
    private List<Disciplina> disciplinas = new ArrayList<>();
    private OnItemClickListener listener;

    private CollectionReference cursoRef = Database.getCursoRef();

    @NonNull
    @Override
    public DisciplinaAdapter.DisciplinaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.disciplina_item, parent, false);
        return new DisciplinaHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DisciplinaAdapter.DisciplinaHolder holder, int position) {
        Disciplina disciplina = disciplinas.get(position);

        holder.textDisciplina.setText(disciplina.getNome());
        cursoRef.document(disciplina.getCursoId())
                .addSnapshotListener((documentSnapshot, e) -> {
                    Curso curso = documentSnapshot.toObject(Curso.class);
                    holder.textCurso.setText(curso.getName());
                });
    }

    @Override
    public int getItemCount() {
        return disciplinas.size();
    }

    public void setDisciplinas(List<Disciplina> disciplinas) {
        this.disciplinas = disciplinas;
        notifyDataSetChanged();
    }

    public Disciplina getDisciplinaAt(int position) {
        return disciplinas.get(position);
    }

    class DisciplinaHolder extends RecyclerView.ViewHolder {
        private TextView textDisciplina;
        private TextView textCurso;

        public DisciplinaHolder(@NonNull View itemView) {
            super(itemView);

            textDisciplina = itemView.findViewById(R.id.textViewDisciplina);
            textCurso = itemView.findViewById(R.id.textCurso);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(disciplinas.get(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Disciplina disciplina);
    }

    public void setOnClickListener(DisciplinaAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }
}
