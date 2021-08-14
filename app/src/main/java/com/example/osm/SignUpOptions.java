package com.example.osm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SignUpOptions extends AppCompatActivity {
    Button mBtnTeacher, mBtnStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_options);

        mBtnStudent = findViewById(R.id.btn_student);
        mBtnTeacher = findViewById(R.id.btn_teacher);

        mBtnStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpOptions.this, SignUpStudentOtp.class));
                finish();
            }
        });

        mBtnTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpOptions.this, SignUpTeacherOtp.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpOptions.this, LoginActivity.class));
        finish();
    }
}
