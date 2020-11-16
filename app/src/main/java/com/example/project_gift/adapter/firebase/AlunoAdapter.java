package com.example.project_gift.adapter.firebase;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_gift.R;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Aula;
import com.example.project_gift.model.AulaStudent;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Student;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;

public class AlunoAdapter extends FirestoreRecyclerAdapter<Student, AlunoAdapter.AlunoHolder> {
    private OnItemClickListener listener;
    private Aula aula;

    private CollectionReference aulaStudentRef = Database.getAulaStudentRef();

    public AlunoAdapter(@NonNull FirestoreRecyclerOptions<Student> options, Aula aula) {
        super(options);
        this.aula = aula;
    }

    @Override
    protected void onBindViewHolder(@NonNull AlunoHolder holder, int position, @NonNull Student model) {
        holder.textViewAluno.setText(model.getDisplayName());
        aulaStudentRef.whereEqualTo("aulaId", aula.getAulaId())
                .whereEqualTo("userId", model.getUserId())
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null || queryDocumentSnapshots.getDocuments().size() == 0) {
                        holder.checkBoxEntrada.setChecked(false);
                        holder.checkBoxSaida.setChecked(false);
                        holder.textViewStartTime.setText("");
                        holder.textViewEndTime.setText("");
                        return;
                    }

                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    AulaStudent aulaStudent = documentSnapshot.toObject(AulaStudent.class);
                    holder.checkBoxEntrada.setChecked(aulaStudent.isCheckIn());
                    holder.checkBoxSaida.setChecked(aulaStudent.isCheckOut());

                    if (aulaStudent.getCheckInTime() != null) {
                        Calendar startTime = Calendar.getInstance();
                        startTime.setTime(aulaStudent.getCheckInTime());
                        String startTimeStr = DateFormat.format("dd/MM/yyyy HH:mm", startTime).toString();
                        holder.textViewStartTime.setText(startTimeStr);
                    } else holder.textViewStartTime.setText("");

                    if (aulaStudent.getCheckOutTime() != null) {
                        Calendar endTime = Calendar.getInstance();
                        endTime.setTime(aulaStudent.getCheckOutTime());
                        String endTimeStr = DateFormat.format("dd/MM/yyyy HH:mm", endTime).toString();
                        holder.textViewEndTime.setText(endTimeStr);
                    } else holder.textViewEndTime.setText("");
                });
    }

    @NonNull
    @Override
    public AlunoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.aluno_item, parent, false);
        return new AlunoHolder(v);
    }

    class AlunoHolder extends RecyclerView.ViewHolder {

        private TextView textViewAluno;
        private CheckBox checkBoxEntrada;
        private CheckBox checkBoxSaida;
        private TextView textViewStartTime;
        private TextView textViewEndTime;

        public AlunoHolder(@NonNull View itemView) {
            super(itemView);

            textViewAluno = itemView.findViewById(R.id.textViewAluno);
            checkBoxEntrada = itemView.findViewById(R.id.checkboxEntrada);
            checkBoxSaida = itemView.findViewById(R.id.checkboxSaida);
            textViewStartTime = itemView.findViewById(R.id.textViewStartTime);
            textViewEndTime = itemView.findViewById(R.id.textViewEndTime);

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
