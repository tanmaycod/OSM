package com.example.osm.Student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.osm.AboutUs;
import com.example.osm.Credits;
import com.example.osm.R;
import com.example.osm.Uploads.UserTheme;


public class SettingsStudent extends AppCompatActivity {
    private Switch mySwitch;
   // private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.LightTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_student);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//
//        mInterstitialAd = new InterstitialAd(this);
        //mInterstitialAd.setAdUnitId("ca-app-pub-9166191804845649/4894089753");
        //mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mySwitch = findViewById(R.id.myswitch);
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            mySwitch.setChecked(true);
        }
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    new UserTheme(SettingsStudent.this).setTheme("dark");
                    //restartApp();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    new UserTheme(SettingsStudent.this).removeTheme();
                    //restartApp();
                }
            }
        });

    }

    private void restartApp() {
        Intent i = new Intent(getApplicationContext(), SettingsStudent.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SettingsStudent.this, StudentMainActivity.class));
        finish();
    }

    public void onSettingsChangePassword(View view) {
        Intent intent = new Intent(SettingsStudent.this, SettingsStudentChangePassword.class);
        startActivity(intent);
        finish();
    }

    public void onSettingsDeleteAccount(View view) {
        Intent intent = new Intent(SettingsStudent.this, SettingsStudentDeleteAccount.class);
        startActivity(intent);
        finish();
    }

    public void onSettingsContactUs(View view) {
        startActivity(new Intent(SettingsStudent.this, AboutUs.class));
    }

    public void onSettingsFeedback(View view) {
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }
        String[] address = {"lrnwell@gmail.com"};
        composeEmail(address, "Feedback for OSM");
    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void onCredits(View view) {
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }
        startActivity(new Intent(SettingsStudent.this, Credits.class));
    }

    public void onPrivacyPolicy(View view) {
//        if (mInterstitialAd.isLoaded()) {
//            mInterstitialAd.show();
//        }
        String url = "https://osmprivacypolicy.blogspot.com/2020/07/osm.html";
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