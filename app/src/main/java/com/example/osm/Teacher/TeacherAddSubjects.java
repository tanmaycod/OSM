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
import android.widget.TextView;
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

public class TeacherAddSubjects extends AppCompatActivity {
    TextView textView;
    String mobile, subjectName;
    DatabaseReference reference;
    EditText mSubjectName;
    Button mAddSubject;
    LoadingDialog loadingDialog2;
    String uploadStatus = null;
    RecyclerView recyclerView;
    AddSubjectNew addSubjectNew;
    ArrayList<AddSubjectNew> arrayListHistory;
    FirebaseRecyclerOptions<AddSubjectNew> options;
    FirebaseRecyclerAdapter<AddSubjectNew, FireViewHoldSubjects> adapterHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_add_subjects);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Subject Folder");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadingDialog2 = new LoadingDialog(TeacherAddSubjects.this);

        mobile = new UserCurrent(TeacherAddSubjects.this).getUsername();
        reference = getInstance().getReference("credentials").child("teacher").child(mobile);

        textView = findViewById(R.id.tv1);
        mSubjectName = findViewById(R.id.enterSubjectName);
        mAddSubject = findViewById(R.id.btnAddSubject);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(TeacherAddSubjects.this));
        recyclerView.setHasFixedSize(true);
        arrayListHistory = new ArrayList<AddSubjectNew>();

        LoadData("");

        reference.child("subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    textView.setVisibility(View.VISIBLE);
                }
                else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
                                    Toast.makeText(TeacherAddSubjects.this, "This name of folder is already present", Toast.LENGTH_SHORT).show();
                                } else {

                                    addSubjectFirebase(new FirebaseCallback() {
                                        @Override
                                        public void onCallback(String value) {
                                            if (value.equals("true")) {
                                                loadingDialog2.dismissDialog();
                                                Toast.makeText(TeacherAddSubjects.this, "Created..", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(TeacherAddSubjects.this, TeacherMainActivity.class));
                                                finish();
                                            } else {
                                                loadingDialog2.dismissDialog();
                                                Toast.makeText(TeacherAddSubjects.this, "Error try again..", Toast.LENGTH_SHORT).show();
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

                } catch (Exception e) {
                    loadingDialog2.dismissDialog();
                    Toast.makeText(TeacherAddSubjects.this, "Error. don't add any kind of symbols", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void LoadData(final String data) {
        try {
            Query query1 = reference.child("subjects");
            options = new FirebaseRecyclerOptions.Builder<AddSubjectNew>().setQuery(query1, AddSubjectNew.class).build();
            adapterHistory = new FirebaseRecyclerAdapter<AddSubjectNew, FireViewHoldSubjects>(options) {
                @Override
                protected void onBindViewHolder(@NonNull FireViewHoldSubjects holder, int position, @NonNull final AddSubjectNew model) {
                    holder.subjectName.setText(model.getName());

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Toast.makeText(AdminCheckUserVendor.this, ""+model.getSerialNo(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TeacherAddSubjects.this, TeacherAddSubjects2.class);
                            intent.putExtra("name", model.getName());
                            startActivity(intent);
                            finish();
                        }
                    });
                }

                @NonNull
                @Override
                public FireViewHoldSubjects onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                    return new FireViewHoldSubjects(LayoutInflater.from(TeacherAddSubjects.this).inflate(R.layout.row_subjects, viewGroup, false));
                }
            };
            adapterHistory.startListening();
            recyclerView.setAdapter(adapterHistory);

        } catch (Exception e) {
        }

    }


    @Override
    public void onBackPressed() {
        startActivity(new Intent(TeacherAddSubjects.this, TeacherMainActivity.class));
        finish();
    }

    private void addSubjectFirebase(final FirebaseCallback firebaseCallback) {


        reference.child("subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                addSubjectNew = new AddSubjectNew("main",subjectName);
                reference.child("subjects").child(subjectName).setValue(addSubjectNew).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                Toast.makeText(TeacherAddSubjects.this, "Error: " + databaseError.getMessage(),
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