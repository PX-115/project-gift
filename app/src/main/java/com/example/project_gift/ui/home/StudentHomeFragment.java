    package com.example.project_gift.ui.home;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.project_gift.R;
import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Aula;
import com.example.project_gift.model.AulaStudent;
import com.example.project_gift.model.Curso;
import com.example.project_gift.model.Disciplina;
import com.example.project_gift.model.Student;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.List;

public class StudentHomeFragment extends Fragment {

    private final int REQUEST_CHANGE_WIFI_STATE_PERMISSION = 1;

    private TextView textView;
    private TextView textCurso;
    private TextView textStatus;
    private TextView textHora;
    private TextView textDisciplina;
    private ImageView imageView;
    private MaterialButton buttonSave;

    private CollectionReference cursoRef;
    private CollectionReference aulaRef;
    private CollectionReference aulaStudentsRef;
    private CollectionReference disciplinaRef;

    private MutableLiveData<String> mAluno = new MutableLiveData<>();
    private MutableLiveData<String> mCurso = new MutableLiveData<>();
    private MutableLiveData<String> mDate = new MutableLiveData<>();
    private MutableLiveData<String> mDisciplina = new MutableLiveData<>();
    private MutableLiveData<String> mStatus = new MutableLiveData<>();
    private MutableLiveData<String> mButtonText = new MutableLiveData<>();

    private MutableLiveData<Integer> mStatusTextColor = new MutableLiveData<>();

    private Student aluno;
    private DocumentSnapshot aula = null;
    private DocumentSnapshot aulaStudent = null;

    private WifiManager wifiManager;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_student_home, container, false);

        // Init firebase objects
        cursoRef = Database.getCursoRef();
        aulaRef = Database.getAulaRef();
        aulaStudentsRef = Database.getAulaStudentRef();
        disciplinaRef = Database.getDisciplinaRef();

        // Init Views
        textView = root.findViewById(R.id.text_user);
        textCurso = root.findViewById(R.id.text_curso);
        textDisciplina = root.findViewById(R.id.text_disciplina);
        textHora = root.findViewById(R.id.text_hora);

        imageView = root.findViewById(R.id.imageView);
        textStatus = root.findViewById(R.id.text_status);

        buttonSave = root.findViewById(R.id.buttonSave);

        // initial values
        aluno = (Student) LoggedUser.getType();
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getActivity().getApplicationContext(), "Turning WiFi ON...", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        mStatus.setValue("Nenhum horário localizado");
        buttonSave.setText("CHECK-IN");
        buttonSave.setVisibility(View.VISIBLE);
        buttonSave.setEnabled(false);

        // observers
        mAluno.observe(getViewLifecycleOwner(), s -> textView.setText(s));
        mCurso.observe(getViewLifecycleOwner(), s -> textCurso.setText(s));
        mDisciplina.observe(getViewLifecycleOwner(), s -> textDisciplina.setText(s));
        mDate.observe(getViewLifecycleOwner(), s -> textHora.setText(s));
        mStatus.observe(getViewLifecycleOwner(), s -> textStatus.setText(s));
        mButtonText.observe(getViewLifecycleOwner(), s -> buttonSave.setText(s));
        mStatusTextColor.observe(getViewLifecycleOwner(), color -> textStatus.setTextColor(getResources().getColor(color)));

        // events
        buttonSave.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
                getActivity().registerReceiver(receiverWifi, intentFilter);
                getWifi();
            }
        });

        // set values
        setAlunoText();
        getCurso();

        return root;
    }

    private void setAlunoText() {
        mAluno.setValue("Bem-vindo, " + LoggedUser.getLoggedUser().getDisplayName());
    }

    private void getCurso() {
        cursoRef.document(aluno.getCursoId())
                .addSnapshotListener((documentSnapshot, e) -> {
                    Curso curso = documentSnapshot.toObject(Curso.class);
                    mCurso.setValue(curso.getName());

                    getAula(documentSnapshot.getId());
                });
    }

    private void getAula(String idCurso) {
        aulaRef.whereEqualTo("cursoId", idCurso)
                .whereGreaterThan("endDate", Calendar.getInstance().getTime())
                .orderBy("endDate")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null || queryDocumentSnapshots.getDocuments().size() == 0) {
                        resetViews();
                        return;
                    }
                    textHora.setVisibility(View.VISIBLE);

                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    this.aula = documentSnapshot;

                    Aula aula = documentSnapshot.toObject(Aula.class);

                    mDate.setValue(setAulaTime(aula));

                    getDisciplina(aula.getDisciplinaId());
                    getAulaStudents(aula, documentSnapshot.getId());
                });
    }

    private void resetViews() {
        textHora.setVisibility(View.GONE);
        textDisciplina.setVisibility(View.GONE);
        mStatus.setValue("Nenhum horário localizado");
        textStatus.setTextColor(getResources().getColor(R.color.flame_red));
        imageView.setImageResource(R.drawable.ic_close_red);
        buttonSave.setEnabled(false);
        aula = null;
        aulaStudent = null;
    }

    private String setAulaTime(Aula aula) {
        Calendar startTime = Calendar.getInstance();
        startTime.setTime(aula.getStartDate());

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "HH:mm";
        final String dateTimeFormatString = "EEEE, dd MMMM, HH:mm";

        String retorno = "";
        if (startTime.before(now)) {
            retorno = "Inicio: Hoje às " + DateFormat.format(timeFormatString, startTime);
        } else {
            if (now.get(Calendar.DATE) == startTime.get(Calendar.DATE)) {
                retorno = "Inicio: Hoje " + DateFormat.format(timeFormatString, startTime);
            } else if (now.get(Calendar.DATE) - startTime.get(Calendar.DATE) == 1) {
                retorno = "Inicio: Amanhã " + DateFormat.format(timeFormatString, startTime);
            } else if (now.get(Calendar.YEAR) == startTime.get(Calendar.YEAR)) {
                retorno = "Inicio: " + DateFormat.format(dateTimeFormatString, startTime).toString();
            } else {
                retorno = "Inicio: " + DateFormat.format("dd MMMM yyyy, HH:mm", startTime).toString();
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

    private void getDisciplina(String document) {
        disciplinaRef.document(document).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Disciplina disciplina = documentSnapshot.toObject(Disciplina.class);
                        mDisciplina.setValue(disciplina.getNome());
                        textDisciplina.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void getAulaStudents(Aula aula, String aulaId) {
        mButtonText.setValue("CHECK-IN");
        buttonSave.setVisibility(View.VISIBLE);
        buttonSave.setEnabled(false);

        validateAula(aula);

        aulaStudentsRef.whereEqualTo("aulaId", aulaId)
                .whereEqualTo("userId", LoggedUser.getLoggedUser().getUid())
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null || queryDocumentSnapshots.getDocuments().size() == 0) {
                        return;
                    }

                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    AulaStudent aulaStudent = documentSnapshot.toObject(AulaStudent.class);
                    this.aulaStudent = documentSnapshot;

                    validateStudent(aulaStudent);
                    setButtonStatus(aula, aulaStudent);
                });
    }

    private void validateAula(Aula aula) {
        if (aula.getStartDate().after(Calendar.getInstance().getTime())) {
            mStatus.setValue("Aguarde inicio da aula");
            mStatusTextColor.setValue(R.color.energy_yellow);
            imageView.setImageResource(R.drawable.ic_wait_time);

            buttonSave.setEnabled(true);
        } else if (aula.getStartDate().before(Calendar.getInstance().getTime())) {
            mStatus.setValue("Aguardando confirmação de entrada");
            mStatusTextColor.setValue(R.color.energy_yellow);
            imageView.setImageResource(R.drawable.ic_wait_time);

            buttonSave.setEnabled(true);
        }
    }

    private void validateStudent(AulaStudent aulaStudent) {
        if (!aulaStudent.isCheckIn() && !aulaStudent.isCheckOut()) {
            mStatus.setValue("Aguardando confirmação de entrada");
            mStatusTextColor.setValue(R.color.energy_yellow);
            imageView.setImageResource(R.drawable.ic_wait_time);
        } else if (aulaStudent.isCheckIn() && !aulaStudent.isCheckOut()) {
            mStatus.setValue("Aguardando confirmação de saida");
            mStatusTextColor.setValue(R.color.energy_yellow);
            imageView.setImageResource(R.drawable.ic_exit);

            mButtonText.setValue("CHECK-OUT");
            buttonSave.setEnabled(true);
        } else {
            mStatus.setValue("Presença confirmada");
            mStatusTextColor.setValue(R.color.sea_green);
            imageView.setImageResource(R.drawable.ic_check_green);
        }
    }

    private void setButtonStatus(Aula aula, AulaStudent aulaStudent) {
        buttonSave.setVisibility(View.VISIBLE);

        if (!aulaStudent.isCheckIn() && !aulaStudent.isCheckOut()) {
            mButtonText.setValue("CHECK-IN");
            buttonSave.setEnabled(!aula.getStartDate().after(Calendar.getInstance().getTime()));
        } else if (aulaStudent.isCheckIn() && !aulaStudent.isCheckOut()) {
            mButtonText.setValue("CHECK-OUT");
            buttonSave.setEnabled(true);
        } else {
            buttonSave.setVisibility(View.GONE);
        }
    }

    private void getWifi() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Toast.makeText(getContext(), "version> = marshmallow", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "location turned off", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
            } else {
                Toast.makeText(getContext(), "location turned on", Toast.LENGTH_SHORT).show();
                wifiManager.startScan();
            }
        } else {
            Toast.makeText(getContext(), "scanning", Toast.LENGTH_SHORT).show();
            wifiManager.startScan();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    //Broadcast receiver
    StringBuilder sb;
    private final BroadcastReceiver receiverWifi = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
                sb = new StringBuilder();
                List<ScanResult> wifiList = wifiManager.getScanResults();
                for (ScanResult scanResult : wifiList) {
                    sb.append("\n").append(scanResult.SSID).append(" - ").append(scanResult.capabilities);
                }

                setStatusPresenca();

                getActivity().unregisterReceiver(receiverWifi);
                Toast.makeText(context, sb, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private AulaStudent newAulaStudent() {
        return new AulaStudent(
                aula.getId(),
                LoggedUser.getLoggedUser().getUid(),
                null,
                null,
                false,
                false
        );
    }


    private void setStatusPresenca() {
        if (this.aulaStudent == null) {
            AulaStudent aulaStudent = newAulaStudent();
            aulaStudentsRef.add(aulaStudent)
                    .addOnSuccessListener(documentReference -> documentReference.get()
                            .addOnSuccessListener(documentSnapshot -> {
                                this.aulaStudent = documentSnapshot;
                                saveAulaStudent();
                            }));
        } else {
            saveAulaStudent();
        }
    }

    private void saveAulaStudent() {
        AulaStudent aulaStudent = this.aulaStudent.toObject(AulaStudent.class);

        if (!aulaStudent.isCheckIn() && !aulaStudent.isCheckOut()) {
            aulaStudentsRef.document(this.aulaStudent.getId()).update("checkIn", true);
            aulaStudentsRef.document(this.aulaStudent.getId()).update("checkInTime", Calendar.getInstance().getTime());
        } else if (aulaStudent.isCheckIn() && !aulaStudent.isCheckOut()) {
            aulaStudentsRef.document(this.aulaStudent.getId()).update("checkOut", true);
            aulaStudentsRef.document(this.aulaStudent.getId()).update("checkOutTime", Calendar.getInstance().getTime());
        }
    }
}