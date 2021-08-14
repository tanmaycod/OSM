package com.example.osm.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osm.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class TeacherProfileEdit extends AppCompatActivity {
    EditText mFirstName, mLastName, mEmail, mCity, mQualification;
    TextView mDob;
    String firstName, lastName, email, city, dob, mobileNo, qualification;
    Button mBtnSaveEdit;
    DatabaseReference reference;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile_edit);
        loadingDialog = new LoadingDialog(TeacherProfileEdit.this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Teacher Profile Edit");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mobileNo = getIntent().getStringExtra("mobileNo");
        mBtnSaveEdit = findViewById(R.id.saveEdit);
        firstName = getIntent().getStringExtra("firstName");
        lastName = getIntent().getStringExtra("lastName");
        email = getIntent().getStringExtra("email");
        city = getIntent().getStringExtra("city");
        dob = getIntent().getStringExtra("dob");
        qualification = getIntent().getStringExtra("qualification");

        mFirstName = findViewById(R.id.first_name);
        mLastName = findViewById(R.id.last_name);
        mEmail = findViewById(R.id.email);
        mCity = findViewById(R.id.city);
        mDob = findViewById(R.id.dob);
        mQualification = findViewById(R.id.qualification);

        mFirstName.setText(firstName);
        mLastName.setText(lastName);
        mEmail.setText(email);
        mCity.setText(city);
        mDob.setText(dob);
        mQualification.setText(qualification);

        reference = getInstance().getReference("credentials").child("teacher");

        final DialogFragment dialogFragment = new TeacherProfileEdit.DatePickerDialogTheme4();
        mDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFragment.show(getSupportFragmentManager(), "theme 4");
            }
        });

        mBtnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                loadingDialog.setText("saving..");
                firstName = mFirstName.getText().toString().trim();
                lastName = mLastName.getText().toString().trim();
                email = mEmail.getText().toString().trim();
                city = mCity.getText().toString().trim();
                dob = mDob.getText().toString().trim();
                qualification = mQualification.getText().toString().trim();

                if (firstName.equals("") || lastName.equals("") || city.equals("") || qualification.equals("")) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(TeacherProfileEdit.this, "Please don't save blank details..", Toast.LENGTH_SHORT).show();
                } else {

                    HashMap<String, Object> profileMap = new HashMap<>();
                    profileMap.put("firstName", firstName);
                    profileMap.put("lastName", lastName);
                    profileMap.put("email", email);
                    profileMap.put("city", city);
                    profileMap.put("dob", dob);
                    profileMap.put("qualification", qualification);

                    String Expn =
                            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

                    if (email.equals("-")) {
                        reference.child(mobileNo).updateChildren(profileMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                loadingDialog.dismissDialog();
                                Intent intent2 = new Intent(TeacherProfileEdit.this, TeacherProfile.class);
                                startActivity(intent2);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingDialog.dismissDialog();
                                Toast.makeText(TeacherProfileEdit.this, "Error: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if (email.matches(Expn) && email.length() > 0) {
                            reference.child(mobileNo).updateChildren(profileMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    loadingDialog.dismissDialog();
                                    Intent intent2 = new Intent(TeacherProfileEdit.this, TeacherProfile.class);
                                    startActivity(intent2);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loadingDialog.dismissDialog();
                                    Toast.makeText(TeacherProfileEdit.this, "Error: " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            loadingDialog.dismissDialog();
                            mEmail.setError("invalid email");
                        }


                    }
                }
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    public static class DatePickerDialogTheme4 extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        String date;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
            //these three lines are used to for cancle set future dates
            calendar.add(Calendar.DATE, 0);
            Date newDate = calendar.getTime();
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 504910816000L);
            //here it ends
            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            int month2 = month + 1;
            String formattedMonth = "" + month2;
            String formattedDayOfMonth = "" + day;

            if (month2 < 10) {

                formattedMonth = "0" + month2;
            }
            if (day < 10) {

                formattedDayOfMonth = "0" + day;
            }
            TextView textView = getActivity().findViewById(R.id.dob);
            textView.setText(formattedDayOfMonth + "-" + formattedMonth + "-" + year);
            date = textView.getText().toString().trim();
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(TeacherProfileEdit.this, TeacherProfile.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent2);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
