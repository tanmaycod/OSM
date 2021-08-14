package com.example.osm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

public class Credits extends AppCompatActivity {
    TextView tvFlat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        tvFlat = findViewById(R.id.tv_flat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Credits");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SpannableString content2 = new SpannableString("www.flaticon.com");
        content2.setSpan(new UnderlineSpan(), 0, content2.length(), 0);
        tvFlat.setText(content2);
    }

    public void onFlatIcon(View view) {
        String url = "https://www.flaticon.com/";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}