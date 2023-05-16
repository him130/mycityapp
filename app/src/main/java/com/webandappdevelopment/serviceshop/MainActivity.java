package com.webandappdevelopment.serviceshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import com.webandappdevelopment.serviceshop.LoginMech.LoginActivity;
import com.webandappdevelopment.serviceshop.ServiceProvider.ServiceProviderHomeActivity;

public class MainActivity extends AppCompatActivity {
    private static final int splash = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ConstraintLayout layout = findViewById(R.id.main_rootLayout);
        layout.setBackground(getDrawable(R.color.theme_yellow));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            checkUser();
            }
        }, splash);
    }

    public void checkUser() {
        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        String result = sharedPreferences.getString("STATUS", "NOT REGISTERED");
        String userType = sharedPreferences.getString("USER_TYPE", "UNKNOWN");

        if (result.matches("ALREADY REGISTERED")) {
            if (userType.matches("APP USER")) {
                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(i);
                finish();
            } else if (userType.matches("SERVICE PROVIDER")) {
                Intent i = new Intent(MainActivity.this, ServiceProviderHomeActivity.class);
                startActivity(i);
                finish();
            } else {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        } else {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }
    }
}