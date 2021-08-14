package com.example.osm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutUs extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Contact Us");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void openTejasInstagram(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://www.instagram.com/_.wrecker._/"));
        startActivity(intent);
    }

    public void openTejasGmail(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "tejas.coder@gmail.com"));
        startActivity(intent);
    }

    public void openTejasWhatsapp(View view) {
        Uri uri = Uri.parse("smsto:" + "8669160351");
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));
    }

    public void openYashInstagram(View view) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse("https://www.instagram.com/mr.wolwax/"));
        startActivity(intent);
    }

    public void openYashGmail(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:" + "yashrajchavan55@gmail.com"));
        startActivity(intent);
    }

    public void openYashWhatsapp(View view) {
        Uri uri = Uri.parse("smsto:" + "9404049739");
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.setPackage("com.whatsapp");
        startActivity(Intent.createChooser(i, ""));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}