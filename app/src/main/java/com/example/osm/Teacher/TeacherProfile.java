package com.example.osm.Teacher;

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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osm.R;
import com.example.osm.Uploads.UserTheme;
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

public class TeacherProfile extends AppCompatActivity {
    TextView mBtnBackHome, mNameProfile, mMobileProfile, mEmailProfile, mCityProfile, mDobProfile,
            mCollegeHeader, mNameHeader, mEditDetails, mQualification;
    DatabaseReference reference;
    String name, college, mobile, email, city, dob, qualification, profileImage, emailVisible,phoneVisible;
    String firstName, lastName;
    StorageReference UserProfileImageReference;
    CircleImageView mProfilePicTeacher;
    private static final int GalaryPick = 1;
    LoadingDialog loadingDialog;
    private Switch mySwitchEmail,mySwitchPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_profile);

        loadingDialog = new LoadingDialog(TeacherProfile.this);
        mySwitchEmail = findViewById(R.id.myswitchemail);
        mySwitchPhone = findViewById(R.id.myswitchphone);

        mBtnBackHome = findViewById(R.id.btnBackToHome);
        mNameHeader = findViewById(R.id.header_name);
        mCollegeHeader = findViewById(R.id.header_college);
        mNameProfile = findViewById(R.id.nameProfile);
        mMobileProfile = findViewById(R.id.mobileProfile);
        mEmailProfile = findViewById(R.id.emailProfile);
        mCityProfile = findViewById(R.id.cityProfile);
        mDobProfile = findViewById(R.id.dobProfile);
        mEditDetails = findViewById(R.id.edit);
        mQualification = findViewById(R.id.qualification);
        mProfilePicTeacher = findViewById(R.id.teacher_profile_pic);

        SpannableString content = new SpannableString("Edit?");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mEditDetails.setText(content);

        mobile = new UserCurrent(TeacherProfile.this).getUsername();

        reference = getInstance().getReference("credentials").child("teacher");
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
                    qualification = dataSnapshot.child("qualification").getValue().toString();
                    profileImage = dataSnapshot.child("profileImg").getValue().toString();
                    emailVisible = dataSnapshot.child("emailVisible").getValue().toString();
                    phoneVisible = dataSnapshot.child("phoneVisible").getValue().toString();

                    mNameHeader.setText("Prof. " + name);
                    mCollegeHeader.setText(college);
                    mNameProfile.setText(name);
                    mMobileProfile.setText(mobile);
                    mEmailProfile.setText(email);
                    mCityProfile.setText(city);
                    mDobProfile.setText(dob);
                    mQualification.setText(qualification);
                    if(emailVisible.equals("true")){
                        mySwitchEmail.setChecked(true);
                    }
                    if(phoneVisible.equals("true")){
                        mySwitchPhone.setChecked(true);
                    }
                    if (!profileImage.equals("-")) {
                        Picasso.get().load(profileImage).into(mProfilePicTeacher);
                    }
                } else {
                    Toast.makeText(TeacherProfile.this, "Error.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeacherProfile.this, "Error: " + databaseError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        mySwitchEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reference.child(mobile).child("emailVisible").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                } else {
                    reference.child(mobile).child("emailVisible").setValue("false").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                }
            }
        });

        mySwitchPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    reference.child(mobile).child("phoneVisible").setValue("true").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                } else {
                    reference.child(mobile).child("phoneVisible").setValue("false").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    });
                }
            }
        });

        mBtnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(TeacherProfile.this, TeacherMainActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                finish();
            }
        });

        mEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherProfile.this, TeacherProfileEdit.class);
                intent.putExtra("firstName", firstName);
                intent.putExtra("mobileNo", mobile);
                intent.putExtra("lastName", lastName);
                intent.putExtra("email", email);
                intent.putExtra("city", city);
                intent.putExtra("dob", dob);
                intent.putExtra("qualification", qualification);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent2 = new Intent(TeacherProfile.this, TeacherMainActivity.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent2);
        finish();
    }

    public void onTeacherProfilePic(View view) {
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
                final File file = new File(SiliCompressor.with(TeacherProfile.this)
                        .compress(FileUtils.getPath(TeacherProfile.this, resultUri), new File(TeacherProfile.this.getCacheDir(), "temp")));
                Uri uri = Uri.fromFile(file);

                StorageReference filePath = UserProfileImageReference.child(mobile + ".jpg");
                filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        file.delete();
                        Toast.makeText(TeacherProfile.this, "Profile Image Uploaded successfully..", Toast.LENGTH_SHORT).show();
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uri.isComplete()) ;
                        Uri url1 = uri.getResult();
                        reference.child(mobile).child("profileImg")
                                .setValue(url1.toString())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(TeacherProfile.this, "image saved in database", Toast.LENGTH_SHORT).show();
                                            loadingDialog.dismissDialog();
                                        } else {
                                            String message = task.getException().toString();
                                            Toast.makeText(TeacherProfile.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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