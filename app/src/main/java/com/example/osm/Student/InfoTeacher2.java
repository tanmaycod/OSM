package com.example.osm.Student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.ActivityNotFoundException;
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

public class InfoTeacher2 extends AppCompatActivity {
    TextView mNameProfile, mMobileProfile, mEmailProfile, mCityProfile, mQualification, mCollegeProfile;
    String name, college, mobile, email, city, qualification, firstName, lastName, profileImage;
    DatabaseReference reference;
    CircleImageView mProfilePicTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_teacher2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("About Teacher");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mobile = getIntent().getStringExtra("teacherMobile");

        mNameProfile = findViewById(R.id.nameProfile);
        mMobileProfile = findViewById(R.id.mobileProfile);
        mEmailProfile = findViewById(R.id.emailProfile);
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
                    email = dataSnapshot.child("email").getValue().toString();
                    city = dataSnapshot.child("city").getValue().toString();
                    qualification = dataSnapshot.child("qualification").getValue().toString();
                    profileImage = dataSnapshot.child("profileImg").getValue().toString();

                    mNameProfile.setText(name);
                    SpannableString content = new SpannableString(mobile);
                    content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                    mMobileProfile.setText(content);
                    SpannableString content2 = new SpannableString(email);
                    content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
                    mEmailProfile.setText(content2);
                    mCityProfile.setText(city);
                    mCollegeProfile.setText(college);
                    mQualification.setText(qualification);
                    if (!profileImage.equals("-")) {
                        Picasso.get().load(profileImage).into(mProfilePicTeacher);
                    }
                } else {
                    Toast.makeText(InfoTeacher2.this, "Error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(InfoTeacher2.this, "Error: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(InfoTeacher2.this, InfoTeacher.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent2);
        finish();
    }

    public void onCallTeacher(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mobile));
        startActivity(intent);
    }

    public void onMailTeacher(View view) {
        if (email.equals("-")) {
            Toast.makeText(this, "Prof." + lastName + " not added his / her email..", Toast.LENGTH_SHORT).show();
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + email));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                //TODO smth
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}