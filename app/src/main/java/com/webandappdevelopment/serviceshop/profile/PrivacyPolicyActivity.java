package com.webandappdevelopment.serviceshop.profile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebView;

import com.webandappdevelopment.serviceshop.R;

public class PrivacyPolicyActivity extends AppCompatActivity {
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        webView = findViewById(R.id.privacyPolicy_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://app.mycityindia.in/privacy-policy.html");
    }
}