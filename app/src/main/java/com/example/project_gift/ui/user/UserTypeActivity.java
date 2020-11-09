package com.example.project_gift.ui.user;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.example.project_gift.R;
import com.example.project_gift.enums.UserType;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.auth.User;

public class UserTypeActivity extends AppCompatActivity {

    private static final String USER_TYPE = "user_type";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type);

        MaterialCardView cardStudent = findViewById(R.id.cardStudent);
        MaterialCardView cardTeacher = findViewById(R.id.cardTeacher);

        cardStudent.setOnClickListener(v -> select(UserType.STUDENT.getValue()));
        cardTeacher.setOnClickListener(v -> select(UserType.TEACHER.getValue()));
    }

    private void select(int option) {
        Intent intent = new Intent();
        intent.putExtra(USER_TYPE, option);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}