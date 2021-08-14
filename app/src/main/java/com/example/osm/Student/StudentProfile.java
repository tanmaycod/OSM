package com.example.osm.Student;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osm.R;
import com.example.osm.UserCurrent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class StudentProfile extends AppCompatActivity {
    TextView mBtnBackHome, mNameProfile, mMobileProfile, mEmailProfile, mCityProfile, mDobProfile,
            mCollegeHeader, mNameHeader, mEditDetails;
    DatabaseReference reference;
    StorageReference UserProfileImageReference;
    String name, college, mobile, email, city, dob, profileImage;
    String firstName, lastName;
    CircleImageView mProfilePicStudent;
    private static final int GalaryPick = 1;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        loadingDialog = new LoadingDialog(StudentProfile.this);

        mBtnBackHome = findViewById(R.id.btnBackToHome);
        mNameHeader = findViewById(R.id.header_name);
        mCollegeHeader = findViewById(R.id.header_college);
        mNameProfile = findViewById(R.id.nameProfile);
        mMobileProfile = findViewById(R.id.mobileProfile);
        mEmailProfile = findViewById(R.id.emailProfile);
        mCityProfile = findViewById(R.id.cityProfile);
        mDobProfile = findViewById(R.id.dobProfile);
        mEditDetails = findViewById(R.id.edit);
        mProfilePicStudent = findViewById(R.id.student_profile_pic);

        SpannableString content = new SpannableString("Edit?");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mEditDetails.setText(content);

        mobile = new UserCurrent(StudentProfile.this).getUsername();

        reference = getInstance().getReference("credentials").child("student");
        UserProfileImageReference = FirebaseStorage.getInstance().getReference().child("Profile Image");

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
                    dob = dataSnapshot.child("dob").getValue().toString();
                    profileImage = dataSnapshot.child("profileImg").getValue().toString();

                    mNameHeader.setText(name);
                    mCollegeHeader.setText(college);
                    mNameProfile.setText(name);
                    mMobileProfile.setText(mobile);
                    mEmailProfile.setText(email);
                    mCityProfile.setText(city);
                    mDobProfile.setText(dob);
                    if (!profileImage.equals("-")) {
                        Picasso.get().load(profileImage).into(mProfilePicStudent);
                    }
                } else {
                    Toast.makeText(StudentProfile.this, "Error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(StudentProfile.this, "Error: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        mBtnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(StudentProfile.this, StudentMainActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                finish();
            }
        });

        mEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentProfile.this, StudentProfileEdit.class);
                intent.putExtra("firstName", firstName);
                intent.putExtra("mobileNo", mobile);
                intent.putExtra("lastName", lastName);
                intent.putExtra("email", email);
                intent.putExtra("city", city);
                intent.putExtra("dob", dob);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(StudentProfile.this, StudentMainActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent2);
        finish();
    }

    public void onStudentProfilePic(View view) {
        Intent galaryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galaryIntent, GalaryPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalaryPick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();

            CropImage.activity(ImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingDialog.startLoadingDialog();
                loadingDialog.setText("Please wait profile image is updating..");

                Uri resultUri = result.getUri();
                final File file = new File(SiliCompressor.with(StudentProfile.this)
                        .compress(FileUtils.getPath(StudentProfile.this, resultUri), new File(StudentProfile.this.getCacheDir(), "temp")));
                Uri uri = Uri.fromFile(file);

                StorageReference filePath = UserProfileImageReference.child(mobile + ".jpg");
                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        file.delete();
                        Toast.makeText(StudentProfile.this, "Profile Image Uploaded successfully..", Toast.LENGTH_SHORT).show();

                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete()) ;
                        Uri url1 = uri.getResult();

                        reference.child(mobile).child("profileImg")
                                .setValue(url1.toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            Toast.makeText(StudentProfile.this, "image saved in database", Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismissDialog();
                                        } else {
                                            String message = task.getException().toString();
                                            Toast.makeText(StudentProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismissDialog();
                                        }
                                    }
                                });

                    }
                });
            }


        }
    }
}
