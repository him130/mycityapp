package com.webandappdevelopment.serviceshop.ServiceProvider.ManagePhotos;

import static com.android.volley.Request.Method.POST;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FullSizeImageActivity extends AppCompatActivity {
    String url,id;
    ShapeableImageView ivImage;
    AppCompatButton btnDelete;
    ProgressBar progressBar;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_size_image);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpUI();
    }

    private void setUpUI(){
        progressBar = findViewById(R.id.fullSizeImage_progressBar);
        Bundle extra = getIntent().getExtras();
        if (extra!=null){
            url = extra.getString("url");
            id = String.valueOf(extra.getInt("id"));
        }
        ivImage = findViewById(R.id.fullSizeImage_ivImage);
        if (url.matches("")) {
            ivImage.setBackgroundResource(R.mipmap.ic_launcher_new);
        } else {
            Picasso.get().load(url)
                    .placeholder(R.mipmap.ic_launcher_new)
                    .into(ivImage);
        }
        if(extra.getBoolean("status")){
            btnDelete = findViewById(R.id.fullSizeImage_btnDelete);
            btnDelete.setVisibility(View.VISIBLE);
            btnDelete.setOnClickListener(view -> deleteImage());
        }
    }

    private void deleteImage() {
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(FullSizeImageActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_delete_image), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_server_not_responding, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        startActivity(new Intent(FullSizeImageActivity.this,ManagePhotosActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(FullSizeImageActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(FullSizeImageActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("id", id);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(FullSizeImageActivity.this, R.string.msg_check_internet_connection);
        }
    }
}