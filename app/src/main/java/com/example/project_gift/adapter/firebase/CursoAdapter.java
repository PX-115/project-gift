package com.example.project_gift.adapter.firebase;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_gift.R;
import com.example.project_gift.model.Curso;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class CursoAdapter extends FirestoreRecyclerAdapter<Curso, CursoAdapter.CursoHolder> {
    private OnItemClickListener listener;

    public CursoAdapter(@NonNull FirestoreRecyclerOptions<Curso> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CursoHolder holder, int position, @NonNull Curso model) {
        holder.textCurso.setText(model.getName());
    }

    @NonNull
    @Override
    public CursoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.curso_item, parent, false);
        return new CursoHolder(v);
    }

    class CursoHolder extends RecyclerView.ViewHolder {

        private TextView textCurso;

        public CursoHolder(@NonNull View itemView) {
            super(itemView);

            textCurso = itemView.findViewById(R.id.textCurso);

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
