package com.example.osm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;

import com.github.barteksc.pdfviewer.PDFView;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PDFActivity extends AppCompatActivity {
   // private AdView mAdView;
    LoadingDialog loadingDialog;
    String fileURL="";
    PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_d_f);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
//
//        MobileAds.initialize(this, new OnInitializationCompleteListener() {
//            @Override
//            public void onInitializationComplete(InitializationStatus initializationStatus) {
//            }
//        });
//        mAdView = findViewById(R.id.adView);
       // AdRequest adRequest = new AdRequest.Builder().build();
        //mAdView.loadAd(adRequest);

        loadingDialog = new LoadingDialog(PDFActivity.this);
        pdfView = findViewById(R.id.pdfv);
        fileURL = getIntent().getStringExtra("fileURL");

        new PDFActivity.RetrivePDFStream().execute(fileURL);
    }

    class RetrivePDFStream extends AsyncTask<String, Void, InputStream>{

        @Override
        protected void onPreExecute() {
            loadingDialog.startLoadingDialog();
            loadingDialog.setText("Please wait...");
        }

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;

            try {
                URL urlx = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) urlx.openConnection();
                if(urlConnection.getResponseCode() == 200){
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }
            }catch (IOException e){
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
            loadingDialog.dismissDialog();
        }
    }

}
