package com.example.project_gift.ui.aula;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.MutableLiveData;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.project_gift.R;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Aula;
import com.example.project_gift.model.Disciplina;
import com.example.project_gift.ui.bottomsheet.DisciplinaSelecionarFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.TimeZone;

public class AulaCadastrarActivity extends AppCompatActivity {

    private CollectionReference aulaRef;

    private TextView textViewDataInicio;
    private TextView textViewHoraInicio;
    private TextView textViewDataFinal;
    private TextView textViewHoraFinal;
    private TextView textViewDisciplina;
    private FloatingActionButton buttonSave;

    private MutableLiveData<String> mDataInicio = new MutableLiveData<>();
    private MutableLiveData<String> mHoraInicio = new MutableLiveData<>();
    private MutableLiveData<String> mDataFinal = new MutableLiveData<>();
    private MutableLiveData<String> mHoraFinal = new MutableLiveData<>();
    private MutableLiveData<String> mDisciplina = new MutableLiveData<>();

    private Disciplina disciplina = null;

    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aula_cadastrar);
        getSupportActionBar().setTitle("Nova aula");

        // initViews
        textViewDataInicio = findViewById(R.id.textViewDataInicio);
        textViewDataFinal = findViewById(R.id.textViewDataFinal);
        textViewHoraInicio = findViewById(R.id.textViewHoraInicio);
        textViewHoraFinal = findViewById(R.id.textViewHoraFinal);
        textViewDisciplina = findViewById(R.id.textViewDisciplina);
        buttonSave = findViewById(R.id.buttonSave);

        // init firebase objects
        aulaRef = Database.getAulaRef();

        // observers
        mDataInicio.observe(this, s -> textViewDataInicio.setText(s));
        mHoraInicio.observe(this, s -> textViewHoraInicio.setText(s));
        mDataFinal.observe(this, s -> textViewDataFinal.setText(s));
        mHoraFinal.observe(this, s -> textViewHoraFinal.setText(s));
        mDisciplina.observe(this, s -> textViewDisciplina.setText(s));

        // setInitial values
        updateDate(true, startDate);
        updateDate(false, endDate);
        onTimeSet(true, startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE));
        onTimeSet(false, endTime.get(Calendar.HOUR_OF_DAY), endTime.get(Calendar.MINUTE));

        textViewDataInicio.setOnClickListener(v -> showDatePicker(true));
        textViewDataFinal.setOnClickListener(v -> showDatePicker(false));
        textViewHoraInicio.setOnClickListener(v -> showTimePicker(true));
        textViewHoraFinal.setOnClickListener(v -> showTimePicker(false));
        textViewDisciplina.setOnClickListener(v -> {
            DisciplinaSelecionarFragment disciplinaSelecionarFragment = new DisciplinaSelecionarFragment();
            disciplinaSelecionarFragment.show(getSupportFragmentManager(), "AULA_CADASTRAR");
        });

        buttonSave.setOnClickListener(v -> save());
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePicker(Boolean isStartDate) {
        String title = isStartDate ? "Selecionar data de inicio" : "Selecionar data final";
        Calendar calendar = getCalendar(isStartDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        LocalDateTime local = LocalDateTime.of(year, month, day, 0, 0);
        Long dateInMillis = local.atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC)).toInstant().toEpochMilli();

        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setSelection(dateInMillis);
        builder.setTitleText(title);

        final MaterialDatePicker picker = builder.build();

        picker.show(getSupportFragmentManager(), "DATE_PICKER");

        picker.addOnPositiveButtonClickListener(selection -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.setTimeInMillis((long) selection);
            selectedDate.add(Calendar.DAY_OF_MONTH, 1);

            updateDate(isStartDate, selectedDate);
        });
    }

    private void updateDate(boolean isStartDate, Calendar calendar) {
        String format = "dd/MM/yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        if (isStartDate) {
            mDataInicio.setValue(simpleDateFormat.format(calendar.getTime()));
            startDate = calendar;
        } else {
            mDataFinal.setValue(simpleDateFormat.format(calendar.getTime()));
            endDate = calendar;
        }
    }

    private Calendar getCalendar(boolean isStartDate) {
        return isStartDate ? startDate : endDate;
    }

    private void showTimePicker(Boolean isStartHour) {
        Calendar calendar = isStartHour ? startTime : endTime;

        MaterialTimePicker materialTimePicker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                .setMinute(calendar.get(Calendar.MINUTE))
                .build();

        materialTimePicker.show(getSupportFragmentManager(), "fragment_tag");
        materialTimePicker.addOnPositiveButtonClickListener(dialog -> {
            int newHour = materialTimePicker.getHour();
            int newMinute = materialTimePicker.getMinute();
            onTimeSet(isStartHour, newHour, newMinute);
        });
    }

    private void onTimeSet(boolean isStartHour, int newHour, int newMinute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, newHour);
        calendar.set(Calendar.MINUTE, newMinute);

        final String timeFormatString = "HH:mm";
        String format = DateFormat.format(timeFormatString, calendar).toString();
        if (isStartHour) {
            mHoraInicio.setValue(format);
            startTime = calendar;
        } else {
            mHoraFinal.setValue(format);
            endTime = calendar;
        }
    }

    private boolean validate() {
        return validateEndDate() && validateDisciplina();
    }

    private boolean validateEndDate() {
        if (endDate.before(startDate)) {
            showSnack("Data inicio deve ser menor ou igual a final");
            return false;
        }
        return true;
    }

    private boolean validateDisciplina() {
        if( disciplina == null){
            showSnack("Selecione uma disciplina");
            return false;
        }
        return true;
    }

    private void showSnack(String msg) {
        Snackbar.make(findViewById(R.id.container), msg, Snackbar.LENGTH_SHORT)
                .show();
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
        mDisciplina.setValue(disciplina.getNome());
    }

    private Aula createAula() {
        startDate.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
        startDate.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
        endDate.set(Calendar.HOUR_OF_DAY, endTime.get(Calendar.HOUR_OF_DAY));
        endDate.set(Calendar.MINUTE, endTime.get(Calendar.MINUTE));

        return new Aula(startDate.getTime(), endDate.getTime(), disciplina.getCursoId(), disciplina.getDisciplinaId(), disciplina.getUserId());
    }

    private void save() {
        if (!validate()) {
            return;
        }

        Aula aula = createAula();
        aulaRef.add(aula)
                .addOnSuccessListener(documentReference -> {
                    showSnack("Aula criada com sucesso");
                    finish();
                })
                .addOnFailureListener(e -> showSnack(e.getMessage()));
    }
}