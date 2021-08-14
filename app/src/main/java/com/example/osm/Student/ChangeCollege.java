package com.example.osm.Student;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.osm.R;
import com.example.osm.UserCurrent;

import java.util.ArrayList;

import static android.widget.AdapterView.*;

public class ChangeCollege extends AppCompatActivity {
    ListView listView;
    SearchView mSearchView;
    ArrayAdapter<String> adapter;
    ArrayList<String> DataList;
    String college;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_college);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Change College");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = findViewById(R.id.list_view);
        mSearchView = findViewById(R.id.searchView);
        int id = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText editText = (EditText) mSearchView.findViewById(id);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {

        } else editText.setTextColor(Color.WHITE);

        final String color = "#BCB0B0";
        editText.setHintTextColor(Color.parseColor(color));

        DataList = new ArrayList<>();
        DataList.add("SVERI's College of Engineering(poly.) Pandharpur");
        DataList.add("SVERI's College of Engineering Pandharpur");

        adapter = new ArrayAdapter<String>(ChangeCollege.this, android.R.layout.simple_list_item_1,
                DataList) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {

                } else tv.setTextColor(Color.WHITE);

                return view;
            }
        };
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                college = parent.getItemAtPosition(position).toString();
                new UserCurrent(ChangeCollege.this).setCollegeNameDefault(college);
                startActivity(new Intent(ChangeCollege.this, FindTeacher1.class));
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ChangeCollege.this, FindTeacher1.class));
        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}