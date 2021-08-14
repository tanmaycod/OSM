package com.example.osm.Teacher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.osm.LoginActivity;
import com.example.osm.R;
import com.example.osm.UserCurrent;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SettingsTeacherDeleteAccount extends AppCompatActivity {

    String contact, pass, confirmPass, activePassword;
    DatabaseReference referenceData, reference;
    EditText password, confirm;
    Button mBtnDeleteAccount;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_teacher_delete_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Delete Account");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadingDialog = new LoadingDialog(SettingsTeacherDeleteAccount.this);

        password = findViewById(R.id.et_password1);
        confirm = findViewById(R.id.et_password1Confirm);
        mBtnDeleteAccount = findViewById(R.id.btn_deleteMyAccount);

        contact = new UserCurrent(SettingsTeacherDeleteAccount.this).getUsername();
        activePassword = new UserCurrent(SettingsTeacherDeleteAccount.this).getPass();

        reference = FirebaseDatabase.getInstance().getReference("credentials").child("teacher").child(contact);
        referenceData = FirebaseDatabase.getInstance().getReference("data").child(contact);

        mBtnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass = password.getText().toString().trim();
                confirmPass = confirm.getText().toString().trim();

                loadingDialog.startLoadingDialog();
                loadingDialog.setText("Checking");

                if (pass.equals("") || confirmPass.equals("")) {
                    loadingDialog.dismissDialog();
                    if (pass.equals("")) {
                        password.setError("Please Enter Your Password.");
                    }
                    if (confirmPass.equals("")) {
                        confirm.setError("Please Confirm Your Password.");
                    }
                } else {
                    if (confirmPass.equals(pass)) {

                        if (confirmPass.equals(activePassword)) {
                            loadingDialog.dismissDialog();
                            final AlertDialog.Builder builder = new AlertDialog.Builder(SettingsTeacherDeleteAccount.this);
                            builder.setMessage("Are you sure want to delete your account?");
                            builder.setCancelable(false);
                            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    loadingDialog.startLoadingDialog();
                                    loadingDialog.setText("Deleting account..");

                                    reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            referenceData.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(SettingsTeacherDeleteAccount.this, "Account deleted succesfully..", Toast.LENGTH_LONG).show();
                                                    new UserCurrent(SettingsTeacherDeleteAccount.this).removeUser();
                                                    Intent intent = new Intent(SettingsTeacherDeleteAccount.this, LoginActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                    startActivity(intent);
                                                    finish();
                                                    loadingDialog.dismissDialog();
                                                }
                                            });
                                        }
                                    });


                                }
                            });
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        } else {
                            loadingDialog.dismissDialog();
                            password.setError("Wrong Password.");
                        }

                    } else {
                        loadingDialog.dismissDialog();
                        confirm.setError("Password does not match..");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsTeacherDeleteAccount.this, SettingsTeacher.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
