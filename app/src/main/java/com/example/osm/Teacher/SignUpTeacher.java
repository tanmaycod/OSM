package com.example.osm.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osm.LoginActivity;
import com.example.osm.R;
import com.example.osm.Uploads.UploadRegTeacher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;

public class SignUpTeacher extends AppCompatActivity {
    SearchableSpinner spinner;
    DatabaseReference reference, referenceTeacher;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;
    EditText mFirstName, mLastName, mQualification, mPassword, mConfirmPassword;
    String firstName, lastName, mobileNo, qualification, college = "", password, confirmPassword,
            email = "-", loginId = "1", emailVisible = "false", phoneVisible = "false";
    TextView mMobileNo;
    Button mBtnSignUp;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_teacher);
        loadingDialog = new LoadingDialog(SignUpTeacher.this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sign Up Teacher");
        setSupportActionBar(toolbar);
        reference = FirebaseDatabase.getInstance().getReference("credentials").child("teacher");
        referenceTeacher = FirebaseDatabase.getInstance().getReference("data");

        mobileNo = getIntent().getStringExtra("phone");

        mFirstName = findViewById(R.id.first_name);
        mLastName = findViewById(R.id.last_name);
        mQualification = findViewById(R.id.qualification);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirmpassword);
        spinner = findViewById(R.id.spinner_college_name);
        mBtnSignUp = findViewById(R.id.btnSignUp);
        mMobileNo = findViewById(R.id.mobile_no);

        mMobileNo.setText(mobileNo);
        ////////////////////////////////////////////////////////////////////////////////////////////
        spinnerDataList = new ArrayList<>();
        spinnerDataList.add(0, "Choose Your College");
        spinnerDataList.add("SVERI's College of Engineering(poly.) Pandharpur");
        spinnerDataList.add("SVERI's College of Engineering Pandharpur");
        adapter = new ArrayAdapter<String>(SignUpTeacher.this, android.R.layout.simple_spinner_dropdown_item,
                spinnerDataList);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                college = spinner.getSelectedItem().toString();
                View v = spinner.getSelectedView();
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {

                } else ((TextView) v).setTextColor(Color.WHITE);
                Toast.makeText(SignUpTeacher.this, "" + college, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////
        mBtnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                loadingDialog.setText("Creating Account..");
                firstName = mFirstName.getText().toString();
                lastName = mLastName.getText().toString();
                mobileNo = mMobileNo.getText().toString();
                qualification = mQualification.getText().toString();
                password = mPassword.getText().toString();
                confirmPassword = mConfirmPassword.getText().toString();

                if (firstName.equals("") || lastName.equals("") || mobileNo.equals("") || qualification.equals("")
                        || password.equals("") || confirmPassword.equals("") || college.equals("Choose Your College") || password.length() < 6) {
                    loadingDialog.dismissDialog();
                    if (firstName.equals("")) {
                        mFirstName.setError("First name is required.");
                    } else if (lastName.equals("")) {
                        mLastName.setError("Last name is required.");
                    } else if (qualification.equals("")) {
                        mQualification.setError("Qualification is required.");
                    } else if (college.equals("Choose Your College")) {
                        Toast.makeText(SignUpTeacher.this, "Please select the college..", Toast.LENGTH_SHORT).show();
                    } else if (password.equals("")) {
                        mPassword.setError("Password is required.");
                    } else if (confirmPassword.equals("")) {
                        mConfirmPassword.setError("Confirmation of password is required.");
                    } else if (password.length() < 6) {
                        mPassword.setError("Password minimum length should be 6.");
                    }
                } else {
                    if (confirmPassword.equals(password)) {
                        final UploadRegTeacher u = new UploadRegTeacher(firstName, lastName, email, college, qualification,
                                confirmPassword, mobileNo, loginId, "-", "-", "-", emailVisible, phoneVisible);

                        reference.child(mobileNo).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                referenceTeacher.child(mobileNo).child("mobileNo").setValue(mobileNo).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(SignUpTeacher.this, LoginActivity.class));
                                        finish();
                                        loadingDialog.dismissDialog();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        loadingDialog.dismissDialog();
                                        reference.child(mobileNo).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignUpTeacher.this, "Error try again.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismissDialog();
                                Toast.makeText(SignUpTeacher.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else {
                        loadingDialog.dismissDialog();
                        mConfirmPassword.setError("Password doesn't match.");
                    }
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SignUpTeacher.this);
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
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
}
