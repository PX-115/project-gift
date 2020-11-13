package com.example.project_gift.adapter;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_gift.R;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Aula;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Disciplina;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class AulaAdapter extends FirestoreRecyclerAdapter<Aula, AulaAdapter.AulaHolder> {
    private OnItemClickListener listener;

    private CollectionReference disciplinaRef = Database.getDisciplinaRef();
    private CollectionReference cursoRef = Database.getCursoRef();

    public AulaAdapter(@NonNull FirestoreRecyclerOptions<Aula> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AulaHolder holder, int position, @NonNull Aula model) {
        holder.textStartTime.setText(setAulaTime(model));
        holder.textEndTime.setText(addEndTime(model));

        disciplinaRef.document(model.getDisciplinaId())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Disciplina disciplina = task.getResult().toObject(Disciplina.class);
                holder.textDisciplina.setText(disciplina.getName());
            }
        });

        cursoRef.document(model.getCursoId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Curso curso = task.getResult().toObject(Curso.class);
                        holder.textCurso.setText(curso.getName());
                    }
                });
    }

    private String setAulaTime(Aula aula) {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(aula.getStartDate());

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "HH:mm";
        final String dateTimeFormatString = "EEEE, dd MMMM, HH:mm";

        String retorno = "";
        if (startTime.before(now)) {
            retorno = "Hoje às " + DateFormat.format(timeFormatString, startTime);
        } else {
            if (now.get(Calendar.DATE) == startTime.get(Calendar.DATE)) {
                retorno = "Hoje " + DateFormat.format(timeFormatString, startTime);
            } else if (now.get(Calendar.DATE) - startTime.get(Calendar.DATE) == 1) {
                retorno = "Amanhã " + DateFormat.format(timeFormatString, startTime);
            } else if (now.get(Calendar.YEAR) == startTime.get(Calendar.YEAR)) {
                retorno = DateFormat.format(dateTimeFormatString, startTime).toString();
            } else {
                retorno = DateFormat.format("dd MMMM yyyy, HH:mm", startTime).toString();
            }
        }
        retorno = retorno + "\n" + addEndTime(aula);
        return retorno;
    }

    private String addEndTime(Aula aula) {
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(aula.getEndDate());

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "HH:mm";
        final String dateTimeFormatString = "EEEE, dd MMMM, HH:mm";
        if (now.get(Calendar.DATE) == endTime.get(Calendar.DATE)) {
            return "Término: Hoje " + DateFormat.format(timeFormatString, endTime);
        } else if (now.get(Calendar.DATE) - endTime.get(Calendar.DATE) == 1) {
            return "Término: Amanhã " + DateFormat.format(timeFormatString, endTime);
        } else if (now.get(Calendar.YEAR) == endTime.get(Calendar.YEAR)) {
            return "Término: " + DateFormat.format(dateTimeFormatString, endTime).toString();
        } else {
            return "Término: " + DateFormat.format("dd MMMM yyyy, HH:mm", endTime).toString();
        }
    }

    @NonNull
    @Override
    public AulaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.aula_item, parent, false);
        return new AulaHolder(v);
    }

    class AulaHolder extends RecyclerView.ViewHolder {

        private TextView textDisciplina;
        private TextView textCurso;
        private TextView textStartTime;
        private TextView textEndTime;

        public AulaHolder(@NonNull View itemView) {
            super(itemView);

            textDisciplina = itemView.findViewById(R.id.textDisciplina);
            textCurso = itemView.findViewById(R.id.textCurso);
            textStartTime = itemView.findViewById(R.id.textStartTime);
            textEndTime = itemView.findViewById(R.id.textEndTime);

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
