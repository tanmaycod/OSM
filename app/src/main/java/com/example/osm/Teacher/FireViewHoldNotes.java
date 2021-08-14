package com.example.osm.Teacher;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.osm.R;

public class FireViewHoldNotes extends RecyclerView.ViewHolder {
    TextView name;
    ImageView imageView;

    public FireViewHoldNotes(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.notesNameRecycler);
        imageView = itemView.findViewById(R.id.nameImageView);
    }
}
