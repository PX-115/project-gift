package com.example.project_gift.ui.bottomsheet;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_gift.R;
import com.example.project_gift.adapter.firebase.AulaAdapter;
import com.example.project_gift.adapter.firebase.CursoAdapter;
import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Aula;
import com.example.project_gift.model.AulaStudent;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Disciplina;
import com.example.project_gift.model.Equipamento;
import com.example.project_gift.ui.disciplina.DisciplinaCadastrarActivity;
import com.example.project_gift.ui.user.AlunoAddActivity;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.Calendar;

public class AulaVisualizarFragment extends BottomSheetDialogFragment {

    private static final String ARG_AULA = "aula";
    private static final String ARG_PROXAULA = "prox_aula";

    private TextView textViewStatus;
    private TextView textViewDataInicio;
    private TextView textViewDataFinal;
    private TextView textViewDisciplinaSala;
    private TextView textViewCheckIn;
    private TextView textViewCheckOut;
    private TextView textViewMensagem;
    private ImageView imageView;

    private CollectionReference aulaStudentRef;
    private CollectionReference disciplinaRef;
    private CollectionReference equipamentoRef;

    private Aula mAula;
    private boolean mProximasAulas;

    public static AulaVisualizarFragment newInstance(Aula aula, boolean isProximaAula) {
        final AulaVisualizarFragment fragment = new AulaVisualizarFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_AULA, aula);
        args.putBoolean(ARG_PROXAULA, isProximaAula);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAula = (Aula) getArguments().getSerializable(ARG_AULA);
            mProximasAulas = getArguments().getBoolean(ARG_PROXAULA);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aula_visualizar, container, false);

        // initViews
        textViewStatus = view.findViewById(R.id.textViewStatus);
        textViewDataInicio = view.findViewById(R.id.textViewDataInicio);
        textViewDataFinal = view.findViewById(R.id.textViewDateFinal);
        textViewDisciplinaSala = view.findViewById(R.id.textViewDisciplinaSala);
        textViewCheckIn = view.findViewById(R.id.textViewCheckIn);
        textViewCheckOut = view.findViewById(R.id.textViewCheckOut);
        textViewMensagem = view.findViewById(R.id.textViewMensagem);
        imageView = view.findViewById(R.id.imageView);

        // init firebase objects
        aulaStudentRef = Database.getAulaStudentRef();
        disciplinaRef = Database.getDisciplinaRef();
        equipamentoRef = Database.getEquipamentoRef();

        final MaterialButton button = view.findViewById(R.id.materialButton);
        button.setOnClickListener(v -> dismiss());

        // setvalues
        textViewDataInicio.setText("Inicio: " + setAulaTime(mAula));
        textViewDataFinal.setText("Término: " + addEndTime(mAula));
        getDisciplina();

        if (!mProximasAulas) {
            textViewMensagem.setVisibility(View.GONE);
            textViewCheckIn.setVisibility(View.VISIBLE);
            textViewCheckOut.setVisibility(View.VISIBLE);
            textViewStatus.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.VISIBLE);

            showAlunoAulaStatus();
        } else {
            textViewMensagem.setVisibility(View.VISIBLE);
            textViewCheckIn.setVisibility(View.GONE);
            textViewCheckOut.setVisibility(View.GONE);

            textViewStatus.setText("Aula não iniciada");
            imageView.setImageResource(R.drawable.ic_wait_yellow_24x);
        }

        return view;
    }

    private void getDisciplina() {
        disciplinaRef.document(mAula.getDisciplinaId())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Disciplina disciplina = task.getResult().toObject(Disciplina.class);
                getEquipamento(disciplina);
            }
        });
    }

    private void getEquipamento(Disciplina disciplina) {
        equipamentoRef.document(disciplina.getEquipamentoId())
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        return;
                    }
                    Equipamento equipamento = documentSnapshot.toObject(Equipamento.class);
                    textViewDisciplinaSala.setText(disciplina.getNome() + " | " + equipamento.getDisplayName());
                });
    }

    private String setAulaTime(Aula aula) {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(aula.getStartDate());

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "HH:mm";
        final String dateTimeFormatString = "EEEE, dd MMMM, HH:mm";

        if (now.get(Calendar.DATE) == startTime.get(Calendar.DATE)) {
            return "Hoje " + DateFormat.format(timeFormatString, startTime);
        } else if (now.get(Calendar.DAY_OF_MONTH) - startTime.get(Calendar.DAY_OF_MONTH) == 1) {
            return "Ontem " + DateFormat.format(timeFormatString, startTime);
        } else if (now.get(Calendar.DATE) - startTime.get(Calendar.DATE) == 1) {
            return "Amanhã " + DateFormat.format(timeFormatString, startTime);
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
        } else if (now.get(Calendar.DAY_OF_MONTH) - endTime.get(Calendar.DAY_OF_MONTH) == 1) {
            return "Ontem " + DateFormat.format(timeFormatString, endTime);
        } else if (now.get(Calendar.DATE) - endTime.get(Calendar.DATE) == 1) {
            return "Amanhã " + DateFormat.format(timeFormatString, endTime);
        } else if (now.get(Calendar.YEAR) == endTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, endTime).toString();
        } else {
            return DateFormat.format("dd MMMM yyyy, HH:mm", endTime).toString();
        }
    }

    private void showAlunoAulaStatus() {
        textViewStatus.setText(getStatusString(null, mAula));
        textViewStatus.setTextColor(getResources().getColor(getStatusColor(null, mAula)));
        imageView.setImageResource(getStatusIcon(null, mAula));
        textViewCheckIn.setText("Entrada: Não encontrado");
        textViewCheckOut.setText("Saida: Não encontrado");

        aulaStudentRef.whereEqualTo("userId", LoggedUser.getLoggedUser().getUid())
                .whereEqualTo("aulaId", mAula.getAulaId())
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null || queryDocumentSnapshots.getDocuments().size() == 0) {
                        return;
                    }
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    AulaStudent aulaStudent = documentSnapshot.toObject(AulaStudent.class);

                    textViewStatus.setText(getStatusString(aulaStudent, mAula));
                    textViewStatus.setTextColor(getResources().getColor(getStatusColor(aulaStudent, mAula)));
                    imageView.setImageResource(getStatusIcon(aulaStudent, mAula));

                    Calendar startTime = Calendar.getInstance();
                    startTime.setTime(aulaStudent.getCheckInTime());
                    String startTimeStr = DateFormat.format("dd/MM/yyyy HH:mm", startTime).toString();

                    Calendar endTime = Calendar.getInstance();
                    endTime.setTime(aulaStudent.getCheckOutTime());
                    String endTimeStr = DateFormat.format("dd/MM/yyyy HH:mm", endTime).toString();

                    textViewCheckIn.setText("Entrada: " + startTimeStr);
                    textViewCheckOut.setText("Saida: " + endTimeStr);
                });
    }

    private String getStatusString(AulaStudent aulaStudent, Aula aula) {
        if (aulaStudent == null && aula.getEndDate().after(Calendar.getInstance().getTime()))
            return "Pendente check-in";
        if (aulaStudent == null)
            return "Ausente";

        if (aulaStudent.isCheckIn() && aulaStudent.isCheckOut())
            return "Presente";
        else if (aulaStudent.isCheckIn() && !aulaStudent.isCheckOut())
            return "Pendente check-out";
        else if (aula.getEndDate().after(Calendar.getInstance().getTime()))
            return "Pendente check-in";
        else
            return "Ausente";
    }

    @ColorRes
    private int getStatusColor(AulaStudent aulaStudent, Aula aula) {
        if (aulaStudent == null && aula.getEndDate().after(Calendar.getInstance().getTime()))
            return R.color.energy_yellow;

        if (aulaStudent == null)
            return R.color.flame_red;

        if (aulaStudent.isCheckIn() && aulaStudent.isCheckOut())
            return R.color.sea_green;
        else if (aulaStudent.isCheckIn() && !aulaStudent.isCheckOut())
            return R.color.energy_yellow;
        else if (aula.getEndDate().after(Calendar.getInstance().getTime()))
            return R.color.energy_yellow;
        else
            return R.color.flame_red;
    }

    @DrawableRes
    private int getStatusIcon(AulaStudent aulaStudent, Aula aula) {
        if (aulaStudent == null && aula.getEndDate().after(Calendar.getInstance().getTime()))
            return R.drawable.ic_wait_yellow_24x;

        if (aulaStudent == null)
            return R.drawable.ic_close_red_24x;

        if (aulaStudent.isCheckIn() && aulaStudent.isCheckOut())
            return R.drawable.ic_check_green24x;
        else if (aulaStudent.isCheckIn() && !aulaStudent.isCheckOut())
            return R.drawable.ic_wait_yellow_24x;
        else if (aula.getEndDate().after(Calendar.getInstance().getTime()))
            return R.drawable.ic_wait_yellow_24x;
        else
            return R.drawable.ic_close_red_24x;
    }
}