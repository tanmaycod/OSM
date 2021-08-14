package com.example.osm.Uploads;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.osm.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class zCollegeName extends AppCompatActivity {
    DatabaseReference reference;
    EditText mCollegeName;
    Button mAddCollegeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_z_college_name);

        reference = FirebaseDatabase.getInstance().getReference("colleges");
        mCollegeName = findViewById(R.id.college_name_add);
        mAddCollegeName = findViewById(R.id.btnAddCollegeName);

        mAddCollegeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCollegeName.getText().toString().trim().isEmpty())
                {
                    mCollegeName.setError("Enter College Name..");
                }
                else {
                    reference.child(mCollegeName.getText().toString()).child("collegeName").setValue(mCollegeName.getText().toString().trim());
                }
            }
        });
    }
}
