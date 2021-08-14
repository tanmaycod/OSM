package com.example.osm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.example.osm.Uploads.UserTheme;

public class LoadingScreen extends AppCompatActivity {
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        checkSession();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        setContentView(R.layout.activity_loading_screen);
        iv = (ImageView) findViewById(R.id.iv);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    private void checkSession() {
        if (new UserTheme(LoadingScreen.this).getTheme() != "") {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
