package com.example.osm.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.osm.LoginActivity;
import com.example.osm.R;
import com.example.osm.UserCurrent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class TeacherMainActivity extends AppCompatActivity {
    String TAG = "myLog";
    DatabaseReference reference;
    String mobile, profileImage;
    String subjectCreated = "asd";
    CircleImageView mProfilePicTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        mProfilePicTeacher = findViewById(R.id.image_view_toolbar);
        mobile = new UserCurrent(TeacherMainActivity.this).getUsername();
        reference = getInstance().getReference("credentials").child("teacher").child(mobile);

        readProfile(new FirebaseCallback() {
            @Override
            public void onCallback(String value) {
                if (!value.equals("-")) {
                    Picasso.get().load(value).into(mProfilePicTeacher);
                }
                readSubject(new FirebaseCallback() {
                    @Override
                    public void onCallback(String value) {
                    }
                });
            }
        });

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(TeacherMainActivity.this);
        builder.setMessage("Are you sure want to exit from app?");
        builder.setCancelable(false);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //here exit app alert close............................................
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                Intent intent2 = new Intent(TeacherMainActivity.this, TeacherProfile.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                finish();
                return true;
            case R.id.settings:
                Intent intent3 = new Intent(TeacherMainActivity.this, SettingsTeacher.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent3);
                finish();
                return true;
            case R.id.logout:
                new UserCurrent(TeacherMainActivity.this).removeUser();
                Intent intent = new Intent(TeacherMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onAddSubject(View view) {
        startActivity(new Intent(TeacherMainActivity.this, TeacherAddSubjects.class));
        finish();
    }

    public void onUploadNotes(View view) {
        if (subjectCreated.equals("true")) {
            startActivity(new Intent(TeacherMainActivity.this, TeacherUploadNotes.class));
            finish();
        } else if (subjectCreated.equals("false")) {
            Toast.makeText(this, "Add Your Subject Before Uploading Notes..", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Wait data is loading..", Toast.LENGTH_SHORT).show();
        }
    }

    public void onViewNotes(View view) {
        if (subjectCreated.equals("true")) {
            startActivity(new Intent(TeacherMainActivity.this, TeacherViewNotes1.class));
            finish();
        } else if (subjectCreated.equals("false")) {
            Toast.makeText(this, "Add Your Subject and Notes Before Viewing or Deleting Notes", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Wait data is loading..", Toast.LENGTH_SHORT).show();
        }

    }

    private void readProfile(final FirebaseCallback firebaseCallback) {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileImage = snapshot.child("profileImg").getValue().toString();
                    firebaseCallback.onCallback(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TeacherMainActivity.this, "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readSubject(final FirebaseCallback firebaseCallback) {
        reference.child("subjects").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    subjectCreated = "true";
                    firebaseCallback.onCallback(subjectCreated);
                } else {
                    subjectCreated = "false";
                    firebaseCallback.onCallback(subjectCreated);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private interface FirebaseCallback {
        void onCallback(String value);
    }
}
