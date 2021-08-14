package com.example.osm.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osm.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class InfoTeacher5 extends AppCompatActivity {
    TextView mNameProfile, mCityProfile, mQualification, mCollegeProfile;
    String name, college, mobile, city, qualification, firstName, lastName, profileImage;
    DatabaseReference reference;
    CircleImageView mProfilePicTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_teacher5);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("About Teacher");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mobile = getIntent().getStringExtra("teacherMobile");

        mNameProfile = findViewById(R.id.nameProfile);
        mCityProfile = findViewById(R.id.cityProfile);
        mQualification = findViewById(R.id.qualification);
        mCollegeProfile = findViewById(R.id.collegeProfile);
        mProfilePicTeacher = findViewById(R.id.teacher_profile_pic);

        reference = getInstance().getReference("credentials").child("teacher");
        reference.child(mobile).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    firstName = dataSnapshot.child("firstName").getValue().toString();
                    lastName = dataSnapshot.child("lastName").getValue().toString();
                    name = firstName + " " + lastName;
                    college = dataSnapshot.child("college").getValue().toString();
                    city = dataSnapshot.child("city").getValue().toString();
                    qualification = dataSnapshot.child("qualification").getValue().toString();
                    profileImage = dataSnapshot.child("profileImg").getValue().toString();

                    mNameProfile.setText(name);
                    mCityProfile.setText(city);
                    mCollegeProfile.setText(college);
                    mQualification.setText(qualification);
                    if (!profileImage.equals("-")) {
                        Picasso.get().load(profileImage).into(mProfilePicTeacher);
                    }
                } else {
                    Toast.makeText(InfoTeacher5.this, "Error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InfoTeacher5.this, "Error: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(InfoTeacher5.this, InfoTeacher.class);
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