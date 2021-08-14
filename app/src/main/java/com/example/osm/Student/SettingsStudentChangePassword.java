package com.example.osm.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.osm.LoginActivity;
import com.example.osm.R;
import com.example.osm.UserCurrent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SettingsStudentChangePassword extends AppCompatActivity {
    EditText mPassword, mReEnterPassword, mCurrentPassword;
    Button mBtnChangePassword;
    String password, rePassword, phone, currentPassword, activePassword;
    DatabaseReference reference;
    com.example.osm.Student.LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_student_change_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Change Password");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadingDialog = new LoadingDialog(SettingsStudentChangePassword.this);
        mCurrentPassword = findViewById(R.id.et_current_password);
        mPassword = findViewById(R.id.et_create_password);
        mReEnterPassword = findViewById(R.id.et_re_enter_password);
        mBtnChangePassword = findViewById(R.id.btn_changePassword);

        phone = new UserCurrent(SettingsStudentChangePassword.this).getUsername();
        activePassword = new UserCurrent(SettingsStudentChangePassword.this).getPass();

        reference = FirebaseDatabase.getInstance().getReference("credentials");

        mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPassword = mCurrentPassword.getText().toString().trim();
                password = mPassword.getText().toString().trim();
                rePassword = mReEnterPassword.getText().toString().trim();

                if (currentPassword.equals("") || password.equals("") || rePassword.equals("") || password.length() < 6) {
                    if (currentPassword.equals("")) {
                        mCurrentPassword.setError("Enter Current Password");
                    } else if (password.equals("")) {
                        mPassword.setError("Enter New Password");
                    } else if (password.length() < 6) {
                        mPassword.setError("Password must be greater than 6");
                    } else if (rePassword.equals("")) {
                        mReEnterPassword.setError("Confirm Password");
                    }
                } else {
                    if (rePassword.equals(password)) {
                        loadingDialog.startLoadingDialog();
                        loadingDialog.setText("Changing...");


                        if (currentPassword.equals(activePassword)) {
                            String dbNewPass = mReEnterPassword.getText().toString().trim();
                            HashMap<String, Object> profileMap = new HashMap<>();
                            profileMap.put("password", dbNewPass);
                            reference.child("student").child(phone).updateChildren(profileMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(SettingsStudentChangePassword.this, "Password changed succesfully.. Re-login..", Toast.LENGTH_LONG).show();
                                    new UserCurrent(SettingsStudentChangePassword.this).removeUser();
                                    Intent intent = new Intent(SettingsStudentChangePassword.this, LoginActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                    loadingDialog.dismissDialog();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingDialog.dismissDialog();
                                    Toast.makeText(SettingsStudentChangePassword.this, "Error: try again..", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            loadingDialog.dismissDialog();
                            mCurrentPassword.setError("Current Password is wrong..");
                        }


                    } else {
                        mReEnterPassword.setError("Password doesn't match");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsStudentChangePassword.this, SettingsStudent.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}