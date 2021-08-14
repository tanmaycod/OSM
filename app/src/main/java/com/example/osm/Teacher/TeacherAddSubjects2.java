package com.example.osm.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.osm.R;
import com.example.osm.Uploads.AddSubjectNew;
import com.example.osm.UserCurrent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class TeacherAddSubjects2 extends AppCompatActivity {
    String mobile, subjectName;
    DatabaseReference reference;
    EditText mSubjectName;
    Button mAddSubject;
    LoadingDialog loadingDialog2;
    String uploadStatus = null, name;
    AddSubjectNew addSubjectNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_add_subjects2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add subfolder");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadingDialog2 = new LoadingDialog(TeacherAddSubjects2.this);

        mobile = new UserCurrent(TeacherAddSubjects2.this).getUsername();
        reference = getInstance().getReference("credentials").child("teacher").child(mobile);

        name = getIntent().getStringExtra("name");

        mSubjectName = findViewById(R.id.enterSubjectName);
        mAddSubject = findViewById(R.id.btnAddSubject);

        mAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    subjectName = mSubjectName.getText().toString();

                    loadingDialog2.startLoadingDialog();
                    loadingDialog2.setText("Adding subject...");

                    if (subjectName.equals("")) {
                        loadingDialog2.dismissDialog();
                        mSubjectName.setError("Subject name is required..");
                    } else {
                        reference.child("subjects").orderByChild("name").equalTo(subjectName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    loadingDialog2.dismissDialog();
                                    Toast.makeText(TeacherAddSubjects2.this, "This name of folder is already present", Toast.LENGTH_SHORT).show();
                                } else {
                                    reference.child("subjects").child(name).orderByChild("name").equalTo(subjectName).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()){
                                                loadingDialog2.dismissDialog();
                                                Toast.makeText(TeacherAddSubjects2.this, "This name of folder is already present", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                addSubjectFirebase(new TeacherAddSubjects2.FirebaseCallback() {
                                                    @Override
                                                    public void onCallback(String value) {
                                                        if (value.equals("true")) {
                                                            loadingDialog2.dismissDialog();
                                                            Toast.makeText(TeacherAddSubjects2.this, "Created..", Toast.LENGTH_SHORT).show();
                                                            startActivity(new Intent(TeacherAddSubjects2.this, TeacherMainActivity.class));
                                                            finish();
                                                        } else {
                                                            loadingDialog2.dismissDialog();
                                                            Toast.makeText(TeacherAddSubjects2.this, "Error try again..", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                } catch (Exception e) {
                    loadingDialog2.dismissDialog();
                    Toast.makeText(TeacherAddSubjects2.this, "Error. don't add any kind of symbols", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    @Override
    public void onBackPressed() {
        startActivity(new Intent(TeacherAddSubjects2.this, TeacherAddSubjects.class));
        finish();
    }

    private void addSubjectFirebase(final TeacherAddSubjects2.FirebaseCallback firebaseCallback) {


        reference.child("subjects").child(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                addSubjectNew = new AddSubjectNew("sub",subjectName);
                reference.child("subjects").child(name).child(subjectName).setValue(addSubjectNew).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadStatus = "true";
                        firebaseCallback.onCallback(uploadStatus);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadStatus = "false";
                        firebaseCallback.onCallback(uploadStatus);
                    }
                });
                /*
                String key;
                final UploadSubjects uploadSubjects = new UploadSubjects(subjectName, key = reference.push().getKey());
                reference.child("subjects").child(key).setValue(uploadSubjects).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadStatus = "true";
                        firebaseCallback.onCallback(uploadStatus);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadStatus = "false";
                        firebaseCallback.onCallback(uploadStatus);
                    }
                });

                */

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingDialog2.dismissDialog();
                Toast.makeText(TeacherAddSubjects2.this, "Error: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    private interface FirebaseCallback {
        void onCallback(String value);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}