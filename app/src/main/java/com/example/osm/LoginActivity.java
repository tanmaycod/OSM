package com.example.osm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osm.Student.StudentMainActivity;
import com.example.osm.Teacher.TeacherMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class LoginActivity extends AppCompatActivity {
    Button mBtnLogIn;
    TextView mBtnSignUp;
    EditText mUsername, mPassword;
    String username, password;
    String passwordDB, loginIDDB, collegeDB;
    DatabaseReference referenceData;
    LoadingDialog loadingDialog;
    private static final int CALL_PERMISSION_CODE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mBtnSignUp = findViewById(R.id.btnSignUp);
        mBtnLogIn = findViewById(R.id.btnLogin);
        mUsername = findViewById(R.id.username);
        mPassword = findViewById(R.id.password);

        loadingDialog = new LoadingDialog(LoginActivity.this);
        referenceData = getInstance().getReference("credentials");

        checkSession();

        checkPermission(
                Manifest.permission.CALL_PHONE,
                CALL_PERMISSION_CODE);

        mBtnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                loadingDialog.setText("Logging In..");
                username = mUsername.getText().toString().trim();
                password = mPassword.getText().toString().trim();

                if (username.equals("") || password.equals("")) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(LoginActivity.this, "fill the login.", Toast.LENGTH_SHORT).show();
                } else {
                    referenceData.child("teacher").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                passwordDB = dataSnapshot.child("password").getValue().toString();
                                loginIDDB = dataSnapshot.child("loginId").getValue().toString();
                                collegeDB = dataSnapshot.child("college").getValue().toString();
                                if (password.equals(passwordDB)) {
                                    new UserCurrent(LoginActivity.this).setUsername(username);
                                    new UserCurrent(LoginActivity.this).setPass(passwordDB);
                                    new UserCurrent(LoginActivity.this).setCollegeNameDefault(collegeDB);
                                    loginByDesignation(loginIDDB);
                                } else {
                                    loadingDialog.dismissDialog();
                                    mPassword.setError("Password is incorrect.");
                                }
                            } else {
                                referenceData.child("student").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            passwordDB = dataSnapshot.child("password").getValue().toString();
                                            loginIDDB = dataSnapshot.child("loginId").getValue().toString();
                                            collegeDB = dataSnapshot.child("college").getValue().toString();
                                            if (password.equals(passwordDB)) {
                                                new UserCurrent(LoginActivity.this).setUsername(username);
                                                new UserCurrent(LoginActivity.this).setPass(passwordDB);
                                                new UserCurrent(LoginActivity.this).setCollegeNameDefault(collegeDB);
                                                loginByDesignation(loginIDDB);
                                            } else {
                                                loadingDialog.dismissDialog();
                                                mPassword.setError("Password is incorrect.");
                                            }
                                        } else {
                                            loadingDialog.dismissDialog();
                                            Toast.makeText(LoginActivity.this, "account not present"
                                                    , Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(LoginActivity.this, "Error: " + databaseError.getMessage()
                                                , Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(LoginActivity.this, "Error: " + databaseError.getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpOptions.class));
                finish();
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    private void checkSession() {
        loadingDialog.startLoadingDialog();
        if (new UserCurrent(LoginActivity.this).getPass() != "") {
            String p = new UserCurrent(LoginActivity.this).getLoginid();
            loginByDesignation(p);
        } else {
            loadingDialog.dismissDialog();
        }
    }

    private void loginByDesignation(String id) {
        Intent intent = null;
        if (id.equals("1")) {
            new UserCurrent(LoginActivity.this).setLoginid(id);
            loadingDialog.dismissDialog();
            intent = new Intent(LoginActivity.this, TeacherMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (id.equals("2")) {
            new UserCurrent(LoginActivity.this).setLoginid(id);
            loadingDialog.dismissDialog();
            intent = new Intent(LoginActivity.this, StudentMainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            loadingDialog.dismissDialog();
            new UserCurrent(LoginActivity.this).removeUser();
            Toast.makeText(this, "wrong", Toast.LENGTH_SHORT).show();
            intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        startActivity(intent);
        finish();
    }

    //here exit app start..........................................
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Are you sure want to exit from app?");
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
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        //here exit app alert close............................................
    }

    public void onForgotPassword(View view) {
        Intent intent = new Intent(LoginActivity.this, ForgotPassword1.class);
        startActivity(intent);
        finish();
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(LoginActivity.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{permission},
                    requestCode);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == CALL_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LoginActivity.this,
                        "Call Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(LoginActivity.this,
                        "Call Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
