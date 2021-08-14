package com.example.osm.Student;

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

public class StudentMainActivity extends AppCompatActivity {
    DatabaseReference reference;
    String mobile, profileImage;
    CircleImageView mProfilePicStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        mProfilePicStudent = findViewById(R.id.image_view_toolbar);
        mobile = new UserCurrent(StudentMainActivity.this).getUsername();
        reference = getInstance().getReference("credentials").child("student").child(mobile);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileImage = snapshot.child("profileImg").getValue().toString();
                    if (!profileImage.equals("-")) {
                        Picasso.get().load(profileImage).into(mProfilePicStudent);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StudentMainActivity.this, "Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(StudentMainActivity.this);
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
                Intent intent2 = new Intent(StudentMainActivity.this, StudentProfile.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                finish();
                return true;
            case R.id.settings:
                Intent intent3 = new Intent(StudentMainActivity.this, SettingsStudent.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent3);
                finish();
                return true;
            case R.id.logout:
                new UserCurrent(StudentMainActivity.this).removeUser();
                Intent intent = new Intent(StudentMainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onFindTeacher(View view) {
        Intent intent = new Intent(StudentMainActivity.this, FindTeacher1.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onAboutTeacher(View view) {
        Intent intent = new Intent(StudentMainActivity.this, InfoTeacher.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //do nothing
    }
}
