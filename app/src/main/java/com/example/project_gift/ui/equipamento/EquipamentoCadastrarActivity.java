package com.example.project_gift.ui.equipamento;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.project_gift.R;
import com.example.project_gift.database.Database;
import com.example.project_gift.model.Equipamento;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;

public class EquipamentoCadastrarActivity extends AppCompatActivity {

    private static final String EQUIPAMENTO = "EQUIPAMENTO";

    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutMac;
    private FloatingActionButton buttonSave;

    private Equipamento equipamento = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipamento_cadastrar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Nova sala de aula");

        // initViews
        textInputLayoutName = findViewById(R.id.textInputDisplayName);
        textInputLayoutMac = findViewById(R.id.textInputMacAdress);
        buttonSave = findViewById(R.id.buttonSave);

        Intent intent = getIntent();
        Object data = intent.getSerializableExtra(EQUIPAMENTO);
        if (data != null) {
            getSupportActionBar().setTitle("Editar sala de aula");
            equipamento = (Equipamento) data;
            assignValues();
        }

        buttonSave.setOnClickListener(v -> save());
    }

    private void assignValues() {
        textInputLayoutName.getEditText().setText(equipamento.getDisplayName());
        textInputLayoutMac.getEditText().setText(equipamento.getMacAdress());
    }

    private boolean validate() {
        String displayName = textInputLayoutName.getEditText().getText().toString();
        String macAddress = textInputLayoutMac.getEditText().getText().toString();

        return validateDisplayName(displayName) && validateMac(macAddress);
    }

    private boolean validateDisplayName(String displayName) {
        if (displayName.isEmpty()) {
            textInputLayoutName.setError("Digite um nome");
            return false;
        } else {
            textInputLayoutName.setError(null);
            return true;
        }
    }

    private boolean validateMac(String mac) {
        if (mac.isEmpty()) {
            textInputLayoutMac.setError("Digite o endereço mac do dispositivo");
            return false;
        } else {
            textInputLayoutMac.setError(null);
            return true;
        }
    }

    private void setValues() {
        if (equipamento == null) {
            equipamento = new Equipamento(textInputLayoutName.getEditText().getText().toString(),
                    textInputLayoutMac.getEditText().getText().toString());
            return;
        }
        equipamento.setDisplayName(textInputLayoutName.getEditText().getText().toString());
        equipamento.setMacAdress(textInputLayoutMac.getEditText().getText().toString());
    }

    private void save() {
        if (!validate()) {
            return;
        }

        setValues();

        Intent returnIntent = new Intent();
        returnIntent.putExtra(EQUIPAMENTO, equipamento);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}