package com.example.project_gift.adapter.firebase;

import android.media.Image;
import android.text.format.DateFormat;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_gift.R;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Aula;
import com.example.project_gift.model.AulaStudent;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Disciplina;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;

import java.util.Calendar;

public class AulaAdapter extends FirestoreRecyclerAdapter<Aula, AulaAdapter.AulaHolder> {
    private OnItemClickListener listener;
    private FirebaseUser user = null;

    private CollectionReference disciplinaRef = Database.getDisciplinaRef();
    private CollectionReference cursoRef = Database.getCursoRef();
    private CollectionReference aulaStudentRef = Database.getAulaStudentRef();

    public AulaAdapter(@NonNull FirestoreRecyclerOptions<Aula> options) {
        super(options);
    }

    public AulaAdapter(@NonNull FirestoreRecyclerOptions<Aula> options, FirebaseUser user) {
        super(options);
        this.user = user;
    }

    @Override
    protected void onBindViewHolder(@NonNull AulaHolder holder, int position, @NonNull Aula model) {
        holder.textStartTime.setText(setAulaTime(model));
        holder.textEndTime.setText(addEndTime(model));

        disciplinaRef.document(model.getDisciplinaId())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Disciplina disciplina = task.getResult().toObject(Disciplina.class);
                holder.textDisciplina.setText(disciplina.getNome());
            }
        });

        cursoRef.document(model.getCursoId()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Curso curso = task.getResult().toObject(Curso.class);
                        holder.textCurso.setText(curso.getName());
                    }
                });

        if (user != null) {
            model.setAulaId(getSnapshots().getSnapshot(position).getId());
            showAlunoAulaStatus(holder, model);
        } else {
            holder.textViewStatus.setVisibility(View.GONE);
        }
    }

    private void showAlunoAulaStatus(AulaHolder holder, Aula aula) {
        holder.textViewStatus.setText(getStatusString(null, aula));
        holder.textViewStatus.setTextColor(holder.itemView.getResources().getColor(getStatusColor(null, aula)));
        holder.imageView.setImageResource(getStatusIcon(null, aula));

        aulaStudentRef.whereEqualTo("userId", user.getUid())
                .whereEqualTo("aulaId", aula.getAulaId())
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null || queryDocumentSnapshots.getDocuments().size() == 0) {
                        return;
                    }
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    AulaStudent aulaStudent = documentSnapshot.toObject(AulaStudent.class);

                    holder.textViewStatus.setText(getStatusString(aulaStudent, aula));
                    holder.textViewStatus.setTextColor(holder.itemView.getResources().getColor(getStatusColor(aulaStudent, aula)));
                    holder.imageView.setImageResource(getStatusIcon(aulaStudent, aula));
                });
    }

    private String getStatusString(AulaStudent aulaStudent, Aula aula) {
        if(aulaStudent == null && aula.getEndDate().after(Calendar.getInstance().getTime()))
            return "Pendente check-in";
        if(aulaStudent == null)
            return "Ausente";

        if (aulaStudent.isCheckIn() && aulaStudent.isCheckOut())
            return "Presente";
        else if (aulaStudent.isCheckIn() && !aulaStudent.isCheckOut())
            return "Pendente check-out";
        else if(aula.getEndDate().after(Calendar.getInstance().getTime()))
            return "Pendente check-in";
        else
            return "Ausente";
    }

    @ColorRes
    private int getStatusColor(AulaStudent aulaStudent, Aula aula) {
        if(aulaStudent == null && aula.getEndDate().after(Calendar.getInstance().getTime()))
            return R.color.energy_yellow;

        if(aulaStudent == null)
            return R.color.flame_red;

        if (aulaStudent.isCheckIn() && aulaStudent.isCheckOut())
            return R.color.sea_green;
        else if (aulaStudent.isCheckIn() && !aulaStudent.isCheckOut())
            return R.color.energy_yellow;
        else if(aula.getEndDate().after(Calendar.getInstance().getTime()))
            return R.color.energy_yellow;
        else
            return R.color.flame_red;
    }

    @DrawableRes
    private int getStatusIcon(AulaStudent aulaStudent, Aula aula) {
        if(aulaStudent == null && aula.getEndDate().after(Calendar.getInstance().getTime()))
            return R.drawable.ic_wait_yellow_24x;

        if(aulaStudent == null)
            return R.drawable.ic_close_red_24x;

        if (aulaStudent.isCheckIn() && aulaStudent.isCheckOut())
            return R.drawable.ic_check_green24x;
        else if (aulaStudent.isCheckIn() && !aulaStudent.isCheckOut())
            return R.drawable.ic_wait_yellow_24x;
        else if(aula.getEndDate().after(Calendar.getInstance().getTime()))
            return R.drawable.ic_wait_yellow_24x;
        else
            return R.drawable.ic_close_red_24x;
    }

    private String setAulaTime(Aula aula) {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(aula.getStartDate());

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "HH:mm";
        final String dateTimeFormatString = "EEEE, dd MMMM, HH:mm";

        if (now.get(Calendar.DATE) == startTime.get(Calendar.DATE)) {
            return "Hoje " + DateFormat.format(timeFormatString, startTime);
        } else if (now.get(Calendar.DATE) - startTime.get(Calendar.DATE) == 1) {
            return  "Amanhã " + DateFormat.format(timeFormatString, startTime);
        } else if (now.get(Calendar.YEAR) == startTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, startTime).toString();
        } else {
            return DateFormat.format("dd MMMM yyyy, HH:mm", startTime).toString();
        }
    }

    private String addEndTime(Aula aula) {
        Calendar endTime = Calendar.getInstance();
        endTime.setTime(aula.getEndDate());

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "HH:mm";
        final String dateTimeFormatString = "EEEE, dd MMMM, HH:mm";
        if (now.get(Calendar.DATE) == endTime.get(Calendar.DATE)) {
            return "Hoje " + DateFormat.format(timeFormatString, endTime);
        } else if (now.get(Calendar.DATE) - endTime.get(Calendar.DATE) == 1) {
            return "Amanhã " + DateFormat.format(timeFormatString, endTime);
        } else if (now.get(Calendar.YEAR) == endTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, endTime).toString();
        } else {
            return DateFormat.format("dd MMMM yyyy, HH:mm", endTime).toString();
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
        private TextView textViewStatus;
        private ImageView imageView;

        public AulaHolder(@NonNull View itemView) {
            super(itemView);

            textDisciplina = itemView.findViewById(R.id.textDisciplina);
            textCurso = itemView.findViewById(R.id.textCurso);
            textStartTime = itemView.findViewById(R.id.textStartTime);
            textEndTime = itemView.findViewById(R.id.textEndTime);
            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            imageView = itemView.findViewById(R.id.imageView);

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
