package com.example.osm;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class WEBActivity extends AppCompatActivity {
    WebView webView;
    String fileURL;
    LoadingDialog loadingDialog;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_w_e_b);

        webView = findViewById(R.id.WV);
        fileURL = getIntent().getStringExtra("fileURL");

        loadingDialog = new LoadingDialog(WEBActivity.this);
        webView.getSettings().setJavaScriptEnabled(true);


        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

        webView.setWebChromeClient(new WebChromeClient());

        String url = "";
        try {
            url = URLEncoder.encode(fileURL, "UTF-8"); //Url Convert to UTF-8 It important.
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });

        webView.loadUrl("https://drive.google.com/file/d/1NoU0owK1TK97KdKfwI2XSddQjQv9W2qQ/view?usp=sharing");


    }
}