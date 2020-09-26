package unis.edu.project_gift;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout tbUser;
    private TextInputLayout tbPassword;

    private Button btLogin;
    private Button btSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ParseObject myFirstClass = new ParseObject("MyFirstClass");
        myFirstClass.put("name", "I'm able to save objects!");
        myFirstClass.saveInBackground();

        initViews();

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp();
            }
        });
    }

    private void initViews() {
        tbUser = findViewById(R.id.textInputUser);
        tbPassword = findViewById(R.id.textInputPassword);
        btLogin = findViewById(R.id.btLogin);
        btSignUp = findViewById(R.id.btCadastrar);
    }

    private Boolean Validate(String user, String password) {
        return (ValidateUser(user) &&
                ValidatePassword(password));
    }

    private Boolean ValidateUser(String username) {
        if(username.isEmpty()) {
            tbUser.setError(getText(R.string.informe_usuario));
            return false;
        }
        tbUser.setError(null);
        return true;
    }

    private Boolean ValidatePassword(String password) {
        if(password.isEmpty()) {
            tbPassword.setError(getText(R.string.senha_em_branco));
            return false;
        }
        tbPassword.setError(null);
        return true;
    }

    private void Login() {
        final String username = tbUser.getEditText().getText().toString().trim();
        String password = tbPassword.getEditText().getText().toString().trim();

        if(!Validate(username, password)){
            return;
        }

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    alertDisplayer(getString(R.string.sucesso_login),getString(R.string.bem_vindo_de_volta, username));
                } else {
                    ParseUser.logOut();
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void SignUp() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }
}