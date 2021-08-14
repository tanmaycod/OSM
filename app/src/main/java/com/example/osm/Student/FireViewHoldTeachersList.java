package com.example.osm.Student;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.osm.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FireViewHoldTeachersList extends RecyclerView.ViewHolder {
    TextView teacherName;
    CircleImageView teacherProfile;

    public FireViewHoldTeachersList(@NonNull View itemView) {
        super(itemView);
        teacherName = itemView.findViewById(R.id.teacherNameRecycler);
        teacherProfile = itemView.findViewById(R.id.nameImageView);
    }
}
