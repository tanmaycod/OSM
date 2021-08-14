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

import com.example.osm.R;
import com.example.osm.Uploads.UploadRegTeacher;
import com.example.osm.UserCurrent;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class InfoTeacher extends AppCompatActivity {
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
        setContentView(R.layout.activity_info_teacher);
        tvCurrentClg = findViewById(R.id.currentCollegeTv);

        mobile = new UserCurrent(InfoTeacher.this).getUsername();
        college = new UserCurrent(InfoTeacher.this).getCollegeNameDefault();
        loadingDialog = new LoadingDialog(InfoTeacher.this);
        reference = FirebaseDatabase.getInstance().getReference("credentials").child("teacher");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Select Teacher");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvCurrentClg.setText(college);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(InfoTeacher.this));
        recyclerView.setHasFixedSize(true);

        arrayListHistory = new ArrayList<UploadRegTeacher>();

        LoadData(college);

    }

    private void LoadData(final String data) {
        Query query1 = reference.orderByChild("college").equalTo(data);
        options = new FirebaseRecyclerOptions.Builder<UploadRegTeacher>().setQuery(query1, UploadRegTeacher.class).build();
        adapterHistory = new FirebaseRecyclerAdapter<UploadRegTeacher, FireViewHoldTeachersList>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FireViewHoldTeachersList holder, int position, @NonNull final UploadRegTeacher model) {
                holder.teacherName.setText("Prof. " + model.getFirstName() + " " + model.getLastName());
                if (!model.getProfileImg().equals("-")) {
                    Picasso.get().load(model.getProfileImg()).into(holder.teacherProfile);
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String emailVisible = model.getEmailVisible();
                        String phoneVisible = model.getPhoneVisible();
                        if (emailVisible.equals("true") && phoneVisible.equals("true")) {
                            Intent intent = new Intent(InfoTeacher.this, InfoTeacher2.class);
                            intent.putExtra("teacherMobile", model.getMobileNo());
                            startActivity(intent);
                            finish();
                        } else if (emailVisible.equals("true") && phoneVisible.equals("false")){
                            Intent intent = new Intent(InfoTeacher.this, InfoTeacher3.class);
                            intent.putExtra("teacherMobile", model.getMobileNo());
                            startActivity(intent);
                            finish();
                        }else if (emailVisible.equals("false") && phoneVisible.equals("true")){
                            Intent intent = new Intent(InfoTeacher.this, InfoTeacher4.class);
                            intent.putExtra("teacherMobile", model.getMobileNo());
                            startActivity(intent);
                            finish();
                        }
                        else if (emailVisible.equals("false") && phoneVisible.equals("false")){
                            Intent intent = new Intent(InfoTeacher.this, InfoTeacher5.class);
                            intent.putExtra("teacherMobile", model.getMobileNo());
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public FireViewHoldTeachersList onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                return new FireViewHoldTeachersList(LayoutInflater.from(InfoTeacher.this).inflate(R.layout.row_list_teachers, viewGroup, false));
            }
        };
        adapterHistory.startListening();
        recyclerView.setAdapter(adapterHistory);

    }

    public void onChangeCollegeFind1(View view) {
        startActivity(new Intent(InfoTeacher.this, ChangeCollege2.class));
        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}