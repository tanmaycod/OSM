package com.example.osm.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class TeacherViewNotes1 extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<AddSubjectNew> arrayListHistory;
    FirebaseRecyclerOptions<AddSubjectNew> options;
    FirebaseRecyclerAdapter<AddSubjectNew, FireViewHoldSubjects> adapterHistory;
    DatabaseReference reference;
    String mobile;
    EditText mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view_notes1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Subject");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mobile = new UserCurrent(TeacherViewNotes1.this).getUsername();
        reference = getInstance().getReference("credentials").child("teacher").child(mobile).child("subjects");
        mSearchView = findViewById(R.id.searchView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(TeacherViewNotes1.this));
        recyclerView.setHasFixedSize(true);

        arrayListHistory = new ArrayList<AddSubjectNew>();

        LoadData("");
        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() != null) {
                    LoadData(s.toString());
                } else {
                    LoadData("");
                }
            }
        });
    }

    private void LoadData(String data) {
        Query query1 = reference.orderByChild("name").startAt(data).endAt(data + "\uf8ff");
        options = new FirebaseRecyclerOptions.Builder<AddSubjectNew>().setQuery(query1, AddSubjectNew.class).build();
        adapterHistory = new FirebaseRecyclerAdapter<AddSubjectNew, FireViewHoldSubjects>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FireViewHoldSubjects holder, int position, @NonNull final AddSubjectNew model) {
                String folderType = model.getFolderType();
                if (folderType.equals("main")) {
                    holder.subjectName.setText(model.getName());

                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            try {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(TeacherViewNotes1.this);
                                builder.setMessage("Are you sure want to delete this folder?");
                                builder.setCancelable(false);
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        reference.child(model.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    long count = dataSnapshot.getChildrenCount();
                                                    String cnt = String.valueOf(count);
                                                    if (cnt.equals("2")) {
                                                        reference.child(model.getName()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                dialog.dismiss();
                                                                Toast.makeText(TeacherViewNotes1.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                dialog.dismiss();
                                                                Toast.makeText(TeacherViewNotes1.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    } else {
                                                        Toast.makeText(TeacherViewNotes1.this, "This folder contains data.. remove that first..", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(TeacherViewNotes1.this, "data not exist..", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Toast.makeText(TeacherViewNotes1.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                            } catch (Exception e) {
                                Toast.makeText(TeacherViewNotes1.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reference.child(model.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        long count = dataSnapshot.getChildrenCount();
                                        String cnt = String.valueOf(count);
                                        if (cnt.equals("2")) {
                                            Toast.makeText(TeacherViewNotes1.this, "Not uploaded any notes yet to this folder.", Toast.LENGTH_SHORT).show();
                                        } else {

                                            Intent intent = new Intent(TeacherViewNotes1.this, TeacherViewNotes2.class);
                                            intent.putExtra("subjectName", model.getName());
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(TeacherViewNotes1.this, "data not exist..", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(TeacherViewNotes1.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            }

            @NonNull
            @Override
            public FireViewHoldSubjects onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                return new FireViewHoldSubjects(LayoutInflater.from(TeacherViewNotes1.this).inflate(R.layout.row_subjects, viewGroup, false));
            }
        };
        adapterHistory.startListening();
        recyclerView.setAdapter(adapterHistory);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TeacherViewNotes1.this, TeacherMainActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}