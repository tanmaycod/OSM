package com.example.osm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpStudentOtp extends AppCompatActivity {
    EditText number;
    Button next;
    String phone;
    DatabaseReference referenceMain;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_student_otp);

        loadingDialog = new LoadingDialog(SignUpStudentOtp.this);

        number = findViewById(R.id.et_phone_number);
        next = findViewById(R.id.btn_next);
        referenceMain = FirebaseDatabase.getInstance().getReference("data");

        //  countryCodePicker.registerCarrierNumberEditText(number);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();
                if (TextUtils.isEmpty(number.getText().toString())) {
                    loadingDialog.dismissDialog();
                    number.setError("Enter the phone number");
                } else if (number.getText().toString().replace(" ", "").length() != 10) {
                    loadingDialog.dismissDialog();
                    Toast.makeText(SignUpStudentOtp.this, "Enter correct no..", Toast.LENGTH_SHORT).show();
                } else {
                    phone = number.getText().toString().replace(" ", "");

                    referenceMain.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                number.setError("You have already registered this number");
                                loadingDialog.dismissDialog();
                            } else {
                                Intent intent = new Intent(SignUpStudentOtp.this, SignUpStudentOtp2.class);
                                intent.putExtra("phone", phone);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                                loadingDialog.dismissDialog();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(SignUpStudentOtp.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SignUpStudentOtp.this, SignUpOptions.class));
        finish();
    }
}
