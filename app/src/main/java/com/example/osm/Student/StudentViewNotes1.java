package com.example.osm.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.osm.R;

import com.example.osm.Uploads.AddSubjectNew;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class StudentViewNotes1 extends AppCompatActivity {
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
        setContentView(R.layout.activity_student_view_notes1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Subject");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mobile = getIntent().getStringExtra("teacherMobile");
        reference = getInstance().getReference("credentials").child("teacher").child(mobile).child("subjects");
        mSearchView = findViewById(R.id.searchView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(StudentViewNotes1.this));
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

                holder.subjectName.setText(model.getName());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(StudentViewNotes1.this, StudentViewNotes2.class);
                        intent.putExtra("teacherMobile", mobile);
                        intent.putExtra("subjectName", model.getName());
                        startActivity(intent);
                        finish();
                    }
                });

            }

            @NonNull
            @Override
            public FireViewHoldSubjects onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                return new FireViewHoldSubjects(LayoutInflater.from(StudentViewNotes1.this).inflate(R.layout.row_subjects, viewGroup, false));
            }
        };
        adapterHistory.startListening();
        recyclerView.setAdapter(adapterHistory);

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(StudentViewNotes1.this, FindTeacher1.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}