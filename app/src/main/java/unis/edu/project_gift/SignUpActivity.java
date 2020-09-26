package unis.edu.project_gift;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;

import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout tbRA;
    TextInputLayout tbPass;
    TextInputLayout tbPassConfirm;

    Button btSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Save();
            }
        });
    }

    private void initViews() {
        tbRA = findViewById(R.id.textInputUser);
        tbPass = findViewById(R.id.textInputSenha);
        tbPassConfirm = findViewById(R.id.textIputConfirmarSenha);
        btSave = findViewById(R.id.btSave);
    }

    private Boolean Validate() {
        String user = tbRA.getEditText().getText().toString().trim();
        String password = tbPass.getEditText().getText().toString().trim();
        String passwordConfirm = tbPassConfirm.getEditText().getText().toString().trim();

        return (ValidateUser(user) &&
                ValidatePassword(password) &&
                validatePasswordConfirm(password, passwordConfirm));
    }

    private Boolean ValidateUser(String username) {
        if(username.isEmpty()) {
            tbRA.setError(getText(R.string.informe_usuario));
            return false;
        }
        tbRA.setError(null);
        return true;
    }

    private Boolean ValidatePassword(String password) {
        if(password.isEmpty()) {
            tbPass.setError(getText(R.string.senha_em_branco));
            return false;
        }
        tbPass.setError(null);
        return true;
    }

    private Boolean validatePasswordConfirm(String password, String passwordConfirm) {
        if(passwordConfirm.isEmpty()) {
            tbPassConfirm.setError(getText(R.string.confirmacao_senha_em_branco));
            return false;
        }
        else if (!validateEqualsPassword(password, passwordConfirm)) {
            return false;
        }
        tbPassConfirm.setError(null);
        return true;
    }

    private Boolean validateEqualsPassword(String password, String passwordConfirm) {
        if(!password.equals(passwordConfirm)) {
            tbPassConfirm.setError(getText(R.string.senhas_nao_coincidem));
            return false;
        }
        tbPassConfirm.setError(null);
        return true;
    }

    private void Save() {
        String user = tbRA.getEditText().getText().toString().trim();
        String password = tbPass.getEditText().getText().toString().trim();
        if(Validate()) {
            SaveUser(user, password);
        }

        SaveUser(user, password);
    }

    private void SaveUser(final String username, String password) {
        final ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);

        final ProgressDialog processDialog = new ProgressDialog(SignUpActivity.this);
        processDialog.setTitle(getString(R.string.title_aguarde));
        processDialog.setMessage(getString(R.string.message_save_user));
        processDialog.show();

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    processDialog.dismiss();
                    alertDisplayer(getString(R.string.sucesso_usuario_cadastro), getString(R.string.bem_vindo_usuario, username));
                } else {
                    processDialog.dismiss();
                    ParseUser.logOut();
                    Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}