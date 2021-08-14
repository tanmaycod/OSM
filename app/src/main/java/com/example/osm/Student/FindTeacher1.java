package com.example.osm.Student;

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

import android.widget.TextView;
import android.widget.Toast;

import com.example.osm.R;
import com.example.osm.Uploads.UploadRegTeacher;
import com.example.osm.UserCurrent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class FindTeacher1 extends AppCompatActivity {
    TextView tvCurrentClg;

    DatabaseReference reference, reference2;
    RecyclerView recyclerView;
    ArrayList<UploadRegTeacher> arrayListHistory;
    FirebaseRecyclerOptions<UploadRegTeacher> options;
    FirebaseRecyclerAdapter<UploadRegTeacher, FireViewHoldTeachersList> adapterHistory;
    String college, mobile;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_teacher1);
        tvCurrentClg = findViewById(R.id.currentCollegeTv);

        mobile = new UserCurrent(FindTeacher1.this).getUsername();
        college = new UserCurrent(FindTeacher1.this).getCollegeNameDefault();
        loadingDialog = new LoadingDialog(FindTeacher1.this);
        reference = FirebaseDatabase.getInstance().getReference("credentials").child("teacher");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Teacher");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvCurrentClg.setText(college);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(FindTeacher1.this));
        recyclerView.setHasFixedSize(true);

        arrayListHistory = new ArrayList<UploadRegTeacher>();

        LoadData(college);

    }

    private void LoadData(final String data) {
        Query query1 = reference.orderByChild("college").equalTo(data);
        options = new FirebaseRecyclerOptions.Builder<UploadRegTeacher>().setQuery(query1, UploadRegTeacher.class).build();
        adapterHistory = new FirebaseRecyclerAdapter<UploadRegTeacher, FireViewHoldTeachersList>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FireViewHoldTeachersList holder, int position, @NonNull final UploadRegTeacher model) {
                holder.teacherName.setText("Prof. " + model.getFirstName() + " " + model.getLastName());
                if (!model.getProfileImg().equals("-")) {
                    Picasso.get().load(model.getProfileImg()).into(holder.teacherProfile);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadingDialog.startLoadingDialog();
                        loadingDialog.setText("loading..");
                        reference2 = getInstance().getReference("credentials").child("teacher").child(model.getMobileNo())
                                .child("subjects");
                        reference2.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    Intent intent = new Intent(FindTeacher1.this, StudentViewNotes1.class);
                                    intent.putExtra("teacherMobile", model.getMobileNo());
                                    startActivity(intent);
                                    finish();
                                } else {
                                    loadingDialog.dismissDialog();
                                    Toast.makeText(FindTeacher1.this, "This teacher haven't uploaded any notes yet..", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }

            @NonNull
            @Override
            public FireViewHoldTeachersList onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                return new FireViewHoldTeachersList(LayoutInflater.from(FindTeacher1.this).inflate(R.layout.row_list_teachers, viewGroup, false));
            }
        };
        adapterHistory.startListening();
        recyclerView.setAdapter(adapterHistory);

    }

    public void onChangeCollegeFind1(View view) {
        startActivity(new Intent(FindTeacher1.this, ChangeCollege.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}