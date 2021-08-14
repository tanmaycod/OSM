package com.example.osm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osm.Student.SignUpStudent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class ForgotPassword2 extends AppCompatActivity {
    EditText otp;
    Button submit;
    TextView resend;
    String id, phone;
    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password2);

        resend = findViewById(R.id.tv_resend);
        otp = findViewById(R.id.et_otp);
        submit = findViewById(R.id.btn_submit);

        mAuth = FirebaseAuth.getInstance();
        phone = getIntent().getStringExtra("phone");

        sendVerificationCode();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(otp.getText().toString())) {
                    otp.setError("Enter OTP");
                } else {
                    try {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(id, otp.getText().toString());
                        signInWithPhoneAuthCredential(credential);
                    } catch (Exception e) {
                        Toast.makeText(ForgotPassword2.this, "wrong OTP", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificationCode();
            }
        });

    }

    private void sendVerificationCode() {
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                resend.setText("" + l / 1000);
                resend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                resend.setText("Resend");
                resend.setEnabled(true);
            }
        }.start();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + phone,
                60,
                TimeUnit.SECONDS,
                ForgotPassword2.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        ForgotPassword2.this.id = id;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(ForgotPassword2.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            firebaseUser = mAuth.getCurrentUser();
                            firebaseUser.delete().addOnCompleteListener(
                                    new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent intent = new Intent(ForgotPassword2.this, ForgotPassword3.class);
                                                intent.putExtra("phone", phone);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else {
                                                Toast.makeText(ForgotPassword2.this, "error:"+task.getException(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                            );

                        } else {
                            mAuth.signOut();
                            Toast.makeText(ForgotPassword2.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
