package com.webandappdevelopment.serviceshop.ServiceProvider.ServiceProviderProfile;

import static com.android.volley.Request.Method.POST;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class VerifyProfileActivity extends AppCompatActivity {
    TextView tvStatus, tvNoImage;
    ImageView ivAadharCard;
    Button btnUpload, btnUpdate;
    ProgressBar progressBar;
    InternetConnectionDetector icd;
    RequestQueue requestQueue, updateRequestQueue;
    StringRequest request, updateRequest;
    //image upload
    private final int PICK_IMAGE_RESULT = 1;
    Uri filepath;
    Bitmap bitmap;
    String encodedImgString;
    Boolean checkImage=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpUI();
    }

    private void setUpUI(){
        tvStatus = findViewById(R.id.verifyProfile_tvStatus);
        tvNoImage = findViewById(R.id.verifyProfile_tvNoImage);
        ivAadharCard = findViewById(R.id.verifyProfile_ivAadharCard);
        btnUpload = findViewById(R.id.verifyProfile_btnUpload);
        btnUpload.setOnClickListener(view -> showFileChooser());
        progressBar = findViewById(R.id.verifyProfile_progressBar);
        fetchData();
        btnUpdate = findViewById(R.id.verifyProfile_btnUpdate);
        btnUpdate.setEnabled(false);
        btnUpdate.setBackgroundResource(R.drawable.search_grey_bg);
        btnUpdate.setOnClickListener(view -> updateAadhar());
    }

    private void updateAadhar(){
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(VerifyProfileActivity.this);
        if(icd.isConnected()) {
            updateRequestQueue = Volley.newRequestQueue(getApplicationContext());
            updateRequestQueue.getCache().clear();
            updateRequest = new StringRequest(POST, getString(R.string.api_aadhar), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("no") || jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        btnUpload.setEnabled(false);
                        btnUpdate.setBackgroundResource(R.drawable.search_grey_bg);
                        Toast.makeText(VerifyProfileActivity.this,"Updated Successfully.",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(VerifyProfileActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(VerifyProfileActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("mobile", sharedPreferences.getString("MOBILE",""));
                    if (checkImage)
                        parameters.put("image", encodedImgString);
                    else
                        parameters.put("image", "");
                    return parameters;
                }
            };
            updateRequestQueue.add(updateRequest);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(VerifyProfileActivity.this,R.string.msg_check_internet_connection);
        }
    }

    private void fetchData() {
        progressBar.setVisibility(View.GONE);
        icd = new InternetConnectionDetector(VerifyProfileActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_login), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_no_user, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("sp_id")) {
                        tvStatus.setText("Status: "+jsonObject.getString("status"));
                        if(jsonObject.getString("aadhar_link").matches("")) {
                            tvNoImage.setVisibility(View.VISIBLE);
                            ivAadharCard.setVisibility(View.GONE);
                        } else {
                            tvNoImage.setVisibility(View.GONE);
                            ivAadharCard.setVisibility(View.VISIBLE);
                            Picasso.get().load(jsonObject.getString("aadhar_link"))
                                .placeholder(R.mipmap.ic_launcher_new)
                                .into(ivAadharCard);
                        }
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(VerifyProfileActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(VerifyProfileActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("mobile", sharedPreferences.getString("MOBILE",""));
                    parameters.put("type", sharedPreferences.getString("USER_TYPE",""));
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(VerifyProfileActivity.this,R.string.msg_check_internet_connection);
        }
    }

    //code for image upload
    private void showFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"SELECT IMAGE"),PICK_IMAGE_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_RESULT && data!=null && data.getData()!=null){
            btnUpdate.setEnabled(true);
            btnUpdate.setBackgroundResource(R.drawable.red_round_bg);
            filepath = data.getData();
            try{
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                //checking for image size
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                double lengthbmp = (imageInByte.length)/1024.0;
                if (lengthbmp<=800.0) {
                    tvNoImage.setVisibility(View.GONE);
                    ivAadharCard.setVisibility(View.VISIBLE);
                    ivAadharCard.setImageBitmap(bitmap);
                    encodedBitmapImg(bitmap);
                } else {
                    Toast.makeText(VerifyProfileActivity.this,"Your Image size is too big to upload",Toast.LENGTH_LONG).show();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void encodedBitmapImg(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] bytesOfImg = byteArrayOutputStream.toByteArray();
        encodedImgString = android.util.Base64.encodeToString(bytesOfImg, Base64.DEFAULT);
        if (!(encodedImgString.matches("")))
            checkImage = true;
    }
}