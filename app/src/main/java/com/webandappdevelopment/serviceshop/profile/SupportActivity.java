package com.webandappdevelopment.serviceshop.profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.webandappdevelopment.serviceshop.R;

public class SupportActivity extends AppCompatActivity {
    TextView tvSupport, tvSupport2, tvEmail1, tvEmail2, tvEmail3;
    AppCompatButton btnCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        String stMsg = "If you need our help, please feel free to contact us at number below:";
        tvSupport = findViewById(R.id.support_tvMsg);
        tvSupport.setText(stMsg);

        btnCall = findViewById(R.id.support_btnCall);
        btnCall.setText("+91-787-944-5321");
        btnCall.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+917879445321"));
            startActivity(intent);
        });

        tvEmail1 = findViewById(R.id.support_tvEmail1);
        tvEmail1.setText("businessinfo@mycityindia.in");
        tvEmail2 = findViewById(R.id.support_tvEmail2);
        tvEmail2.setText("complain@mycityindia.in");
        tvEmail3 = findViewById(R.id.support_tvEmail3);
        tvEmail3.setText("feedback@mycityindia.in");

        String stMsg2 = "Or you can mail us at below mail ids:";
        tvSupport2 = findViewById(R.id.support_tvMsg2);
        tvSupport2.setText(stMsg2);
    }
}