package com.example.osm.Teacher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osm.R;
import com.example.osm.Uploads.UploadNotes;
import com.example.osm.UserCurrent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class TeacherUploadNotes extends AppCompatActivity {
    RelativeLayout layout1, layout2, layout3;
    SearchableSpinner spinner1, spinnerFileType, spinner2;
    DatabaseReference reference;
    StorageReference storageReference;
    String mobile, uploadStatus = null, nameOfNotes, fileType, mainFolder, subFolder = "";
    Button btnUploadNotes;
    EditText mNameofNotes;
    LoadingDialog loadingDialog;
    private static final int STORAGE_PERMISSION_CODE = 101;
    Uri dataImage;

    ArrayAdapter<String> adapterFileType, adapterMainFolder, adapterSubFolder;
    ArrayList<String> spinnerDataListFileType, spinnerDataListMainFolder, spinnerDataListSubFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_upload_notes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Upload Notes");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadingDialog = new LoadingDialog(TeacherUploadNotes.this);
        mobile = new UserCurrent(TeacherUploadNotes.this).getUsername();
        reference = getInstance().getReference("credentials").child("teacher").child(mobile).child("subjects");
        storageReference = FirebaseStorage.getInstance().getReference();

        layout1 = findViewById(R.id.uploadLayout1);
        layout2 = findViewById(R.id.uploadLayout2);
        layout3 = findViewById(R.id.uploadLayout3);
        spinner1 = findViewById(R.id.spinner);
        spinnerFileType = findViewById(R.id.spinner2);
        spinner2 = findViewById(R.id.spinner3);
        btnUploadNotes = findViewById(R.id.btnAddNotes);
        mNameofNotes = findViewById(R.id.nameofnotes);

        checkPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                STORAGE_PERMISSION_CODE);
////////////////////////////////////////////////////////////////////////////////////////////////////
        spinnerDataListMainFolder = new ArrayList<>();
        adapterMainFolder = new ArrayAdapter<String>(TeacherUploadNotes.this, android.R.layout.simple_spinner_dropdown_item,
                spinnerDataListMainFolder);

        spinnerDataListSubFolder = new ArrayList<>();
        adapterSubFolder = new ArrayAdapter<String>(TeacherUploadNotes.this, android.R.layout.simple_spinner_dropdown_item,
                spinnerDataListSubFolder);

        spinner1.setAdapter(adapterMainFolder);
        retriveSpinnerData();

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mainFolder = spinner1.getSelectedItem().toString();
                View v = spinner1.getSelectedView();
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {

                } else ((TextView) v).setTextColor(Color.WHITE);

                reference.child(mainFolder).orderByChild("folderType").equalTo("sub").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            layout2.setVisibility(View.VISIBLE);
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout3.getLayoutParams();
                            lp.addRule(RelativeLayout.BELOW, layout2.getId());
                            layout3.setLayoutParams(lp);

                            reference.child(mainFolder).orderByChild("folderType").equalTo("sub").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Set<String> set = new HashSet<String>();
                                    Iterator i = dataSnapshot.getChildren().iterator();
                                    while (i.hasNext()) {
                                        set.add(((DataSnapshot) i.next()).getKey());
                                    }
                                    adapterSubFolder.clear();
                                    spinnerDataListSubFolder.add(0, "Don't want to store in sub folder");
                                    adapterSubFolder.addAll(set);
                                    adapterSubFolder.notifyDataSetChanged();
                                    spinner2.setAdapter(adapterSubFolder);

                                    spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                        @Override
                                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                            subFolder = spinner2.getSelectedItem().toString();
                                            if(subFolder.equals("Don't want to store in sub folder")){
                                                subFolder = "";
                                            }
                                            View v = spinner2.getSelectedView();
                                            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {

                                            } else ((TextView) v).setTextColor(Color.WHITE);

                                        }

                                        @Override
                                        public void onNothingSelected(AdapterView<?> parent) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(TeacherUploadNotes.this, "Error", Toast.LENGTH_SHORT).show();
                                }
                            });


                        } else {
                            layout2.setVisibility(View.INVISIBLE);
                            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) layout3.getLayoutParams();
                            lp.addRule(RelativeLayout.BELOW, layout1.getId());
                            layout3.setLayoutParams(lp);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////
        spinnerDataListFileType = new ArrayList<>();
        spinnerDataListFileType.add(0, "Choose File Type");
        spinnerDataListFileType.add("Text");
        spinnerDataListFileType.add("Pdf");
        spinnerDataListFileType.add("Ppt");
        spinnerDataListFileType.add("Excel");
        adapterFileType = new ArrayAdapter<String>(TeacherUploadNotes.this, android.R.layout.simple_spinner_dropdown_item,
                spinnerDataListFileType);
        spinnerFileType.setAdapter(adapterFileType);

        spinnerFileType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fileType = spinnerFileType.getSelectedItem().toString();
                View v = spinnerFileType.getSelectedView();
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {

                } else ((TextView) v).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////

        btnUploadNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameOfNotes = mNameofNotes.getText().toString().trim();
                loadingDialog.startLoadingDialog();
                loadingDialog.setText("uploading..");
                if (nameOfNotes.equals("") || mainFolder.equals("") || fileType.equals("Choose File Type")) {
                    loadingDialog.dismissDialog();
                    if (nameOfNotes.equals("")) {
                        mNameofNotes.setError("Name of subject or notes required..");
                    } else if (mainFolder.equals("")) {
                        Toast.makeText(TeacherUploadNotes.this, "select the subject..", Toast.LENGTH_SHORT).show();
                    } else if (fileType.equals("Choose File Type")) {
                        Toast.makeText(TeacherUploadNotes.this, "select the file type..", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    reference.child(mainFolder).orderByChild("name").equalTo(nameOfNotes).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                loadingDialog.dismissDialog();
                                Toast.makeText(TeacherUploadNotes.this, "Can't upload same name of notes", Toast.LENGTH_SHORT).show();
                            } else {
                                if (fileType.equals("Pdf")) {
                                    selectPDFFile();
                                } else if (fileType.equals("Excel")) {
                                    selectExcelFile();
                                } else if (fileType.equals("Text")) {
                                    selectTextFile();
                                } else if (fileType.equals("Ppt")) {
                                    selectPPTFile();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            }
        });
    }


    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(TeacherUploadNotes.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(TeacherUploadNotes.this,
                    new String[]{permission},
                    requestCode);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(TeacherUploadNotes.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(TeacherUploadNotes.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void selectPDFFile() {
        try {
            Intent intent = new Intent();
            intent.setType("application/pdf");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select your pdf note.."), 1);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void selectExcelFile() {
        try {
            Intent intent = new Intent();
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select your excel note.."), 2);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void selectTextFile() {
        try {
            Intent intent = new Intent();
            intent.setType("text/plain");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select your excel note.."), 3);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPPTFile() {
        try {
            Intent intent = new Intent();
            intent.setType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select your excel note.."), 4);
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            dataImage = data.getData();
            uploadFile(new FirebaseCallback() {
                @Override
                public void onCallback(String value) {
                    if (value.equals("true")) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(TeacherUploadNotes.this, "Uploaded..", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TeacherUploadNotes.this, TeacherMainActivity.class));
                        finish();
                    } else {
                        loadingDialog.dismissDialog();
                        Toast.makeText(TeacherUploadNotes.this, "Error try again..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            dataImage = data.getData();
            uploadFileExcel(new FirebaseCallback() {
                @Override
                public void onCallback(String value) {
                    if (value.equals("true")) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(TeacherUploadNotes.this, "Uploaded..", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TeacherUploadNotes.this, TeacherMainActivity.class));
                        finish();
                    } else {
                        loadingDialog.dismissDialog();
                        Toast.makeText(TeacherUploadNotes.this, "Error try again..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (requestCode == 3 && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            dataImage = data.getData();
            uploadFileText(new FirebaseCallback() {
                @Override
                public void onCallback(String value) {
                    if (value.equals("true")) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(TeacherUploadNotes.this, "Uploaded..", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TeacherUploadNotes.this, TeacherMainActivity.class));
                        finish();
                    } else {
                        loadingDialog.dismissDialog();
                        Toast.makeText(TeacherUploadNotes.this, "Error try again..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (requestCode == 4 && resultCode == RESULT_OK && data != null
                && data.getData() != null) {
            dataImage = data.getData();
            uploadFilePPT(new FirebaseCallback() {
                @Override
                public void onCallback(String value) {
                    if (value.equals("true")) {
                        loadingDialog.dismissDialog();
                        Toast.makeText(TeacherUploadNotes.this, "Uploaded..", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TeacherUploadNotes.this, TeacherMainActivity.class));
                        finish();
                    } else {
                        loadingDialog.dismissDialog();
                        Toast.makeText(TeacherUploadNotes.this, "Error try again..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            loadingDialog.dismissDialog();
            Toast.makeText(this, "try again.", Toast.LENGTH_SHORT).show();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void retriveSpinnerData() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                while (i.hasNext()) {
                    set.add(((DataSnapshot) i.next()).getKey());
                }
                adapterMainFolder.clear();
                adapterMainFolder.addAll(set);
                adapterMainFolder.notifyDataSetChanged();
                spinner1.setAdapter(adapterMainFolder);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeacherUploadNotes.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TeacherUploadNotes.this, TeacherMainActivity.class));
        finish();
    }

    private void uploadFile(final FirebaseCallback firebaseCallback) {
        StorageReference storageReference1 = storageReference.child("teacherNotes").child(mobile).child(mainFolder + "/" + subFolder + "/" + nameOfNotes + ".pdf");
        storageReference1.putFile(dataImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                Uri url = uri.getResult();

                String key2;
                final UploadNotes uploadNotes = new UploadNotes("notes",fileType, nameOfNotes, url.toString(), key2 = reference.push().getKey());
                reference.child(mainFolder).child(subFolder).child(key2).setValue(uploadNotes).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadStatus = "true";
                        firebaseCallback.onCallback(uploadStatus);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadStatus = "false";
                        firebaseCallback.onCallback(uploadStatus);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                loadingDialog.setText("uploaded: " + (int) progress + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadStatus = "false";
                firebaseCallback.onCallback(uploadStatus);
            }
        });

    }

    private void uploadFileExcel(final FirebaseCallback firebaseCallback) {
        StorageReference storageReference1 = storageReference.child("teacherNotes").child(mobile).child(mainFolder + "/"+ subFolder + "/" + nameOfNotes + ".xlsx");
        storageReference1.putFile(dataImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                Uri url = uri.getResult();

                String key2;
                final UploadNotes uploadNotes = new UploadNotes("notes",fileType, nameOfNotes, url.toString(), key2 = reference.push().getKey());
                reference.child(mainFolder).child(subFolder).child(key2).setValue(uploadNotes).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadStatus = "true";
                        firebaseCallback.onCallback(uploadStatus);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadStatus = "false";
                        firebaseCallback.onCallback(uploadStatus);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                loadingDialog.setText("uploaded: " + (int) progress + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadStatus = "false";
                firebaseCallback.onCallback(uploadStatus);
            }
        });

    }

    private void uploadFileText(final FirebaseCallback firebaseCallback) {
        StorageReference storageReference1 = storageReference.child("teacherNotes").child(mobile).child(mainFolder + "/"+ subFolder + "/" + nameOfNotes + ".txt");
        storageReference1.putFile(dataImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                Uri url = uri.getResult();

                String key2;
                final UploadNotes uploadNotes = new UploadNotes("notes",fileType, nameOfNotes, url.toString(), key2 = reference.push().getKey());
                reference.child(mainFolder).child(subFolder).child(key2).setValue(uploadNotes).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadStatus = "true";
                        firebaseCallback.onCallback(uploadStatus);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadStatus = "false";
                        firebaseCallback.onCallback(uploadStatus);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                loadingDialog.setText("uploaded: " + (int) progress + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadStatus = "false";
                firebaseCallback.onCallback(uploadStatus);
            }
        });

    }

    private void uploadFilePPT(final FirebaseCallback firebaseCallback) {
        StorageReference storageReference1 = storageReference.child("teacherNotes").child(mobile).child(mainFolder + "/" + subFolder + "/"+ nameOfNotes + ".pptx");
        storageReference1.putFile(dataImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete()) ;
                Uri url = uri.getResult();

                String key2;
                final UploadNotes uploadNotes = new UploadNotes("notes",fileType, nameOfNotes, url.toString(), key2 = reference.push().getKey());
                reference.child(mainFolder).child(subFolder).child(key2).setValue(uploadNotes).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadStatus = "true";
                        firebaseCallback.onCallback(uploadStatus);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploadStatus = "false";
                        firebaseCallback.onCallback(uploadStatus);
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                loadingDialog.setText("uploaded: " + (int) progress + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                uploadStatus = "false";
                firebaseCallback.onCallback(uploadStatus);
            }
        });

    }

    private interface FirebaseCallback {
        void onCallback(String value);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}