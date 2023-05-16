package com.webandappdevelopment.serviceshop.ServiceProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.webandappdevelopment.serviceshop.R;

public class UploadDocumentActivity extends AppCompatActivity {

    TextView contact;
    TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_document);



        contact = findViewById(R.id.spHome_tvContactNum);
        email = findViewById(R.id.spHome_tvEmail);


        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+ "7879445321"));
                startActivity(intent);
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + "mycityapp.ind@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");
                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });



    }
}