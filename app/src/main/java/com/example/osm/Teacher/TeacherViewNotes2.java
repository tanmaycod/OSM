package com.example.osm.Teacher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.osm.PDFActivity;
import com.example.osm.R;
import com.example.osm.Student.StudentViewNotes3;
import com.example.osm.Uploads.UploadNotes;
import com.example.osm.UserCurrent;
import com.example.osm.WEBActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class TeacherViewNotes2 extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<UploadNotes> arrayListHistory;
    FirebaseRecyclerOptions<UploadNotes> options;
    FirebaseRecyclerAdapter<UploadNotes, FireViewHoldNotes> adapterHistory;
    DatabaseReference reference;
    FirebaseStorage mStorage;
    String mobile, subjectName, urlDownloadFile, nameDownloadFile;
    EditText mSearchView;
    TextView mText;
    int countNotes = 0;
    // private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view_notes2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("View Notes");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//
//        mInterstitialAd = new InterstitialAd(this);
        //  mInterstitialAd.setAdUnitId("ca-app-pub-9166191804845649/4894089753");
        // mInterstitialAd.loadAd(new AdRequest.Builder().build());

        subjectName = getIntent().getStringExtra("subjectName");
        mobile = new UserCurrent(TeacherViewNotes2.this).getUsername();
        reference = getInstance().getReference("credentials").child("teacher").child(mobile).child("subjects").child(subjectName);
        mStorage = FirebaseStorage.getInstance();

        mSearchView = findViewById(R.id.searchView);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(TeacherViewNotes2.this));
        recyclerView.setHasFixedSize(true);
        mText = findViewById(R.id.countNotes);
        arrayListHistory = new ArrayList<UploadNotes>();


        reference.orderByChild("folderType").equalTo("notes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    countNotes = (int) dataSnapshot.getChildrenCount();
                    if (countNotes == 0) {
                    } else {
                        mText.setText("Total count of available notes are " + countNotes);
                    }
                } else {
                    mText.setText("Not uploaded any notes yet.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        options = new FirebaseRecyclerOptions.Builder<UploadNotes>().setQuery(query1, UploadNotes.class).build();
        adapterHistory = new FirebaseRecyclerAdapter<UploadNotes, FireViewHoldNotes>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FireViewHoldNotes holder, int position, @NonNull final UploadNotes model) {
                if (model.getFolderType().equals("sub")) {
                    holder.imageView.setImageDrawable(ContextCompat.getDrawable(TeacherViewNotes2.this, R.drawable.folder));
                    holder.name.setText(model.getName());

                    holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            try {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(TeacherViewNotes2.this);
                                builder.setMessage("Are you sure want to delete this sub-folder?");
                                builder.setCancelable(false);
                                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(final DialogInterface dialog, int which) {
                                        reference.child(model.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    long count = dataSnapshot.getChildrenCount();
                                                    String cnt = String.valueOf(count);
                                                    if (cnt.equals("2")) {
                                                        reference.child(model.getName()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                dialog.dismiss();
                                                                Toast.makeText(TeacherViewNotes2.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                dialog.dismiss();
                                                                Toast.makeText(TeacherViewNotes2.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    } else {
                                                        Toast.makeText(TeacherViewNotes2.this, "This folder contains data.. remove that first..", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(TeacherViewNotes2.this, "data not exist..", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                                Toast.makeText(TeacherViewNotes2.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();

                            } catch (Exception e) {
                                Toast.makeText(TeacherViewNotes2.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            return true;
                        }
                    });

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            reference.child(model.getName()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        long count = dataSnapshot.getChildrenCount();
                                        String cnt = String.valueOf(count);
                                        if (cnt.equals("2")) {
                                            Toast.makeText(TeacherViewNotes2.this, "Not uploaded any notes yet to this folder.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Intent intent = new Intent(TeacherViewNotes2.this, TeacherViewNotes3.class);
                                            intent.putExtra("subjectName", subjectName);
                                            intent.putExtra("subFolderName", model.getName());
                                            startActivity(intent);
                                            finish();
                                        }
                                    } else {
                                        Toast.makeText(TeacherViewNotes2.this, "data not exist..", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Toast.makeText(TeacherViewNotes2.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });

                }
                else if (model.getFolderType().equals("notes")) {
                    holder.name.setText(model.getName());
                    if (model.getType().equals("Pdf")) {
                        holder.imageView.setImageDrawable(ContextCompat.getDrawable(TeacherViewNotes2.this, R.drawable.pdf));
                    } else if (model.getType().equals("Text")) {
                        holder.imageView.setImageDrawable(ContextCompat.getDrawable(TeacherViewNotes2.this, R.drawable.txt));
                    } else if (model.getType().equals("Ppt")) {
                        holder.imageView.setImageDrawable(ContextCompat.getDrawable(TeacherViewNotes2.this, R.drawable.ppt));
                    } else if (model.getType().equals("Excel")) {
                        holder.imageView.setImageDrawable(ContextCompat.getDrawable(TeacherViewNotes2.this, R.drawable.xls));
                    }

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @SuppressLint("IntentReset")
                        @Override
                        public void onClick(View v) {

                            PopupMenu popupMenu = new PopupMenu(v.getContext(), v);
                            popupMenu.inflate(R.menu.popup_menu);
                            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem item) {
                                    switch (item.getItemId()) {
                                        case R.id.open:
                                            String url = model.getUrl();
                                            if (model.getType().equals("Pdf")) {
                                                urlDownloadFile = url;
                                                nameDownloadFile = model.getName();
                                                downloadFile(TeacherViewNotes2.this, nameDownloadFile,".pdf",DIRECTORY_DOWNLOADS, urlDownloadFile);
                                            } else if (model.getType().equals("Text")) {
                                                urlDownloadFile = url;
                                                nameDownloadFile = model.getName();
                                                downloadFile(TeacherViewNotes2.this, nameDownloadFile,".txt",DIRECTORY_DOWNLOADS, urlDownloadFile);
                                            } else if (model.getType().equals("Ppt")) {
                                                urlDownloadFile = url;
                                                nameDownloadFile = model.getName();
                                                downloadFile(TeacherViewNotes2.this, nameDownloadFile,".pptx",DIRECTORY_DOWNLOADS, urlDownloadFile);
                                            } else if (model.getType().equals("Excel")) {
                                                urlDownloadFile = url;
                                                nameDownloadFile = model.getName();
                                                downloadFile(TeacherViewNotes2.this, nameDownloadFile,".xlsx",DIRECTORY_DOWNLOADS, urlDownloadFile);
                                            }


                                            return true;
                                        case R.id.delete:
                                            StorageReference fileRef = mStorage.getReferenceFromUrl(model.getUrl());
                                            fileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    reference.child(model.getmKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Toast.makeText(TeacherViewNotes2.this, "Deleted..", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(TeacherViewNotes2.this, "Failed.. try again..", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(TeacherViewNotes2.this, "Failed.. try again..", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                            return true;
                                    }
                                    return true;
                                }
                            });
                            popupMenu.show();
                            //Toast.makeText(AdminCheckUserVendor.this, ""+model.getSerialNo(), Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            }

            @NonNull
            @Override
            public FireViewHoldNotes onCreateViewHolder(@NonNull ViewGroup viewGroup,
                                                        int viewType) {
                return new FireViewHoldNotes(LayoutInflater.from(TeacherViewNotes2.this).inflate(R.layout.row_notes, viewGroup, false));
            }
        }

        ;
        adapterHistory.startListening();
        recyclerView.setAdapter(adapterHistory);

    }


    private void downloadFile(Context context, String fileName, String fileExtension, String destinationDirectory,
                              String url) {
        Toast.makeText(context, "Download Started..", Toast.LENGTH_SHORT).show();
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context,destinationDirectory, fileName+fileExtension);

        downloadManager.enqueue(request);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TeacherViewNotes2.this, TeacherViewNotes1.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}