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
import android.widget.ProgressBar;

import com.example.project_gift.MainActivity;
import com.example.project_gift.R;
import com.example.project_gift.auth.LoggedUser;
import com.example.project_gift.database.Database;
import com.example.project_gift.enums.UserType;
import com.example.project_gift.model.Disciplina;
import com.example.project_gift.model.Student;
import com.example.project_gift.model.Teacher;
import com.example.project_gift.ui.user.AlunoAddActivity;
import com.example.project_gift.ui.user.ProfessorAddActivity;
import com.example.project_gift.ui.user.UserTypeActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int USER_TYPE_REQUEST = 1;
    private static final int STUDENT_PROFILE_REQUEST = 2;
    private static final int TEACHER_PROFILE_REQUEST = 3;

    private static final String USER_TYPE = "user_type";
    private final String USER_PROFILE = "USER_PROFILE";
    private final String DISCIPLINA = "DISCIPLINA";
    private final String CURSO = "CURSO";

    private LoginViewModel loginViewModel;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private CollectionReference studentRef;
    private CollectionReference teacherRef;
    private CollectionReference disciplinaRef;

    private TextInputLayout usernameTextInput;
    private TextInputLayout passwordTextInput;
    private Button loginButton;
    private ProgressBar loadingProgressBar;

    private Student student = null;
    private Teacher teacher = null;

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
        disciplinaRef = Database.getDisciplinaRef();

        // SetUp views
        usernameTextInput = findViewById(R.id.username);
        passwordTextInput = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getUsernameError() != null) {
                usernameTextInput.setError(getString(loginFormState.getUsernameError()));
            } else {
                usernameTextInput.setError(null);
            }
            if (loginFormState.getPasswordError() != null) {
                passwordTextInput.setError(getString(loginFormState.getPasswordError()));
            } else {
                passwordTextInput.setError(null);
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
                loginViewModel.loginDataChanged(usernameTextInput.getEditText().getText().toString(),
                        passwordTextInput.getEditText().getText().toString());
            }
        };
        usernameTextInput.getEditText().addTextChangedListener(afterTextChangedListener);
        passwordTextInput.getEditText().addTextChangedListener(afterTextChangedListener);

        loginButton.setOnClickListener(v -> login(usernameTextInput.getEditText().getText().toString(),
                passwordTextInput.getEditText().getText().toString()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == USER_TYPE_REQUEST) {
                int option = data.getIntExtra(USER_TYPE, UserType.STUDENT.getValue());
                if (option == UserType.STUDENT.getValue()) {
                    student = new Student("", LoggedUser.getLoggedUser().getUid(), "");
                    LoggedUser.setStudent(student);
                    openStudentProfileUpdate();
                } else {
                    teacher = new Teacher("", LoggedUser.getLoggedUser().getUid());
                    LoggedUser.setTeacher(teacher);
                    openTeacherProfileUpdate();
                }
            } else if (requestCode == STUDENT_PROFILE_REQUEST) {
                assert data != null;
                UserProfileChangeRequest profileUpdates = data.getParcelableExtra(USER_PROFILE);
                String cursoId = data.getStringExtra(CURSO);
                student.setCursoId(cursoId);
                student.setDisplayName(profileUpdates.getDisplayName());

                updateUserProfile(profileUpdates, null);
            } else if (requestCode == TEACHER_PROFILE_REQUEST) {
                assert data != null;
                UserProfileChangeRequest profileUpdates = data.getParcelableExtra(USER_PROFILE);
                List<Disciplina> disciplinas = (ArrayList<Disciplina>) data.getSerializableExtra(DISCIPLINA);
                teacher.setDisplayName(profileUpdates.getDisplayName());

                updateUserProfile(profileUpdates, disciplinas);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openStudentProfileUpdate() {
        Intent intent = new Intent(this, AlunoAddActivity.class);
        startActivityForResult(intent, STUDENT_PROFILE_REQUEST);
    }

    private void openTeacherProfileUpdate() {
        Intent intent = new Intent(this, ProfessorAddActivity.class);
        startActivityForResult(intent, TEACHER_PROFILE_REQUEST);
    }

    private void updateUserProfile(UserProfileChangeRequest profileUpdates, List<Disciplina> disciplinas) {
        LoggedUser.getLoggedUser().updateProfile(profileUpdates)
                .addOnSuccessListener(aVoid -> {
                    if (LoggedUser.getType() instanceof Student)
                        saveStudent(student);
                    else {
                        saveTeacher(teacher, disciplinas);
                    }
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(findViewById(R.id.container), e.getMessage(), Snackbar.LENGTH_SHORT)
                            .show();
                });
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

    private void saveTeacher(Teacher teacher, List<Disciplina> disciplinas) {
        teacherRef.document(teacher.getUserId()).set(teacher).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                LoggedUser.setTeacher(teacher);
                udpateDisciplinas(disciplinas);
            } else {
                showSnack(task.getException().getMessage());
            }
        });
    }

    private void udpateDisciplinas(List<Disciplina> disciplinas) {
        for(Disciplina disciplina : disciplinas) {
            disciplinaRef.document(disciplina.getDisciplinaId()).update("userId", disciplina.getUserId());
        }
        openMainActivity();
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