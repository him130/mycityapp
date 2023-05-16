package com.webandappdevelopment.serviceshop.LoginMech;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.webandappdevelopment.serviceshop.R;

public class RegistrationActivity extends AppCompatActivity {
    private AppCompatButton btnCustomer, btnServiceProvider, btnLogin;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ConstraintLayout layout = findViewById(R.id.registration_rootLayout);
        layout.setBackground(getDrawable(R.color.theme_yellow));
        setUpUI();
    }

    public void setUpUI() {
        btnCustomer = findViewById(R.id.registration_btnCustomer);
        btnCustomer.setOnClickListener(view -> {
            startActivity(new Intent(RegistrationActivity.this, CustomerRegistrationActivity.class));
            finish();
        });

        btnServiceProvider = findViewById(R.id.registration_btnServiceProvider);
        btnServiceProvider.setOnClickListener(view -> {
            startActivity(new Intent(RegistrationActivity.this, ServiceProviderRegistrationActivity.class));
            finish();
        });

        btnLogin = findViewById(R.id.registration_btnLogin);
        btnLogin.setOnClickListener(view -> {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        });
    }
}