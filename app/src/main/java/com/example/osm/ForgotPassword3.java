package com.example.osm;

import androidx.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ForgotPassword3 extends AppCompatActivity {

    EditText mPassword, mReEnterPassword;
    Button mBtnChangePassword;
    String password, rePassword, phone;
    DatabaseReference reference;
    FirebaseAuth fAuth;
    LoadingDialog loadingDialog;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password3);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Forgot Password");
        setSupportActionBar(toolbar);

        loadingDialog = new LoadingDialog(ForgotPassword3.this);

        mPassword = findViewById(R.id.et_create_password);
        mReEnterPassword = findViewById(R.id.et_re_enter_password);
        mBtnChangePassword = findViewById(R.id.btn_changePassword);

        fAuth = FirebaseAuth.getInstance();
        phone = getIntent().getStringExtra("phone");

        reference = FirebaseDatabase.getInstance().getReference("credentials");

        mBtnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = mPassword.getText().toString().trim();
                rePassword = mReEnterPassword.getText().toString().trim();

                if (password.equals("") || rePassword.equals("") || password.length() < 6) {
                    if (password.equals("")) {
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

                        reference.child("student").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    String dbNewPass = mReEnterPassword.getText().toString().trim();
//                                    HashMap<String, Object> profileMap = new HashMap<>();
//                                    profileMap.put("password", dbNewPass);
                                    reference.child("student").child(phone).child("password").setValue(dbNewPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            loadingDialog.dismissDialog();
                                            Toast.makeText(ForgotPassword3.this, "Password changed succesfully.", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ForgotPassword3.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ForgotPassword3.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                } else {
                                    reference.child("teacher").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                String dbNewPass = mReEnterPassword.getText().toString().trim();
//                                                HashMap<String, Object> profileMap = new HashMap<>();
//                                                profileMap.put("password", dbNewPass);
                                                reference.child("teacher").child(phone).child("password").setValue(dbNewPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(ForgotPassword3.this, "Password changed successfully.", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(ForgotPassword3.this, LoginActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                        loadingDialog.dismissDialog();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(ForgotPassword3.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                            } else {
                                                Toast.makeText(ForgotPassword3.this, "contact us for query..", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(ForgotPassword3.this, "Error: " +
                                                    databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismissDialog();
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(ForgotPassword3.this, "Error: " +
                                        databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                loadingDialog.dismissDialog();
                            }
                        });


                    } else {
                        mReEnterPassword.setError("Password doesn't match");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPassword3.this);
        builder.setMessage("Are you sure want to go on login?");
        builder.setCancelable(true);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(ForgotPassword3.this, LoginActivity.class));
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}