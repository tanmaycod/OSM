package com.example.osm.Teacher;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.osm.R;

public class FireViewHoldSubjects extends RecyclerView.ViewHolder {
    TextView subjectName;

    public FireViewHoldSubjects(@NonNull View itemView) {
        super(itemView);
        subjectName = itemView.findViewById(R.id.subjectNameRecycler);
    }
}