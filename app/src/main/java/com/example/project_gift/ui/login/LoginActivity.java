package com.example.project_gift.ui.login;

import android.app.Activity;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.project_gift.MainActivity;
import com.example.project_gift.R;
import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.enums.UserType;
import com.example.project_gift.model.Student;
import com.example.project_gift.model.Teacher;
import com.example.project_gift.ui.user.UserTypeActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

public class LoginActivity extends AppCompatActivity {

    private static final int USER_TYPE_REQUEST = 1;
    private static final String USER_TYPE = "user_type";

    private LoginViewModel loginViewModel;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private CollectionReference studentRef;
    private CollectionReference teacherRef;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressBar loadingProgressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // SetUp viewModel
        loginViewModel = new ViewModelProvider(this)
                .get(LoginViewModel.class);

        // SetUp firebase
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        studentRef = db.collection(getString(R.string.student_collection));
        teacherRef = db.collection(getString(R.string.teacher_collection));

        // SetUp views
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        loginButton.setOnClickListener(v -> login(usernameEditText.getText().toString(),
                passwordEditText.getText().toString()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == USER_TYPE_REQUEST) {
                int option = data.getIntExtra(USER_TYPE, UserType.STUDENT.getValue());
                if (option == UserType.STUDENT.getValue()) {
                    Student student = new Student(LoggedUser.getLoggedUser().getUid(), "");
                    saveStudent(student);
                } else {
                    Teacher teacher = new Teacher(LoggedUser.getLoggedUser().getUid());
                    saveTeacher(teacher);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveStudent(Student student) {
        studentRef.document(student.getUserId()).set(student).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                LoggedUser.setStudent(student);
                openMainActivity();
            } else {
                showSnack(task.getException().getMessage());
            }
        });
    }

    private void saveTeacher(Teacher teacher) {
        teacherRef.document(teacher.getUserId()).set(teacher).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                LoggedUser.setTeacher(teacher);
                openMainActivity();
            } else {
                showSnack(task.getException().getMessage());
            }
        });
    }

    private void getStudentOrTeacher(FirebaseUser user) {
        showProgressBar();
        studentRef.document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Student student = task.getResult().toObject(Student.class);
                if (student != null) {
                    LoggedUser.setStudent(student);
                    openMainActivity();
                    finish();
                    return;
                }
            }
            getTeacher(user);
            showProgressBar();
        });
    }

    private void getTeacher(FirebaseUser user) {
        showProgressBar();
        teacherRef.document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Teacher teacher = task.getResult().toObject(Teacher.class);
                if (teacher != null) {
                    LoggedUser.setTeacher(teacher);
                    openMainActivity();
                    finish();
                    return;
                }
            }
            openUserTypeActivity();
            showProgressBar();
        });
    }

    private void login(String username, String password) {
        showProgressBar();
        firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        openUserTypeActivity();
                    } else {
                        loginExists(username, password);
                    }
                    showProgressBar();
                });
    }

    private void loginExists(String username, String password) {
        showProgressBar();
        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        getStudentOrTeacher(task.getResult().getUser());
                    } else {
                        showSnack(getString(R.string.login_failed));
                    }
                    showProgressBar();
                });
    }

    private void showSnack(String message) {
        Snackbar.make(findViewById(R.id.container), message, Snackbar.LENGTH_SHORT)
                .show();
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openUserTypeActivity() {
        Intent intent = new Intent(this, UserTypeActivity.class);
        startActivityForResult(intent, USER_TYPE_REQUEST);
    }

    private void showProgressBar() {
        if (loadingProgressBar.getVisibility() == View.VISIBLE) {
            loadingProgressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
        } else {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
        }
    }
}