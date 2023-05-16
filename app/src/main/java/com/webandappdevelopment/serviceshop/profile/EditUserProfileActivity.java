package com.webandappdevelopment.serviceshop.profile;

import static com.android.volley.Request.Method.POST;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class EditUserProfileActivity extends AppCompatActivity {
    EditText etName, etMobile, etEmail;
    Button btnUpdate, btnUpload;
    ProgressBar progressBar;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    ShapeableImageView ivProfileImage;
    private final int PICK_IMAGE_RESULT = 1;
    Uri filepath;
    Bitmap bitmap;
    String encodedImgString;
    Boolean checkImage=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpUI();
    }

    private void setUpUI() {
        progressBar = findViewById(R.id.editUserProfile_progressBar);
        SharedPreferences sp = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        etName = findViewById(R.id.editUserProfile_etName);
        etName.setText(sp.getString("NAME",""));
        etMobile = findViewById(R.id.editUserProfile_etContact);
        etMobile.setText(sp.getString("MOBILE",""));
        etEmail = findViewById(R.id.editUserProfile_etEmail);
        etEmail.setText(sp.getString("EMAIL",""));
        ivProfileImage = findViewById(R.id.editUserProfile_ivProfileImg);
        if (sp.getString("PROFILE_IMAGE","").matches("")) {
            ivProfileImage.setBackgroundResource(R.mipmap.ic_launcher_new);
        } else {
            Picasso.get().load(sp.getString("PROFILE_IMAGE",""))
                    .placeholder(R.mipmap.ic_launcher_new)
                    .into(ivProfileImage);
        }
        btnUpdate = findViewById(R.id.editUserProfile_btnUpdate);
        btnUpdate.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            checkInfo();
        });
        btnUpload = findViewById(R.id.editUserProfile_btnOpenGallery);
        btnUpload.setOnClickListener(view -> showFileChooser());
    }

    private void checkInfo() {
        String mobilePattern = "[6-9][0-9]{9}";
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (etName.getText().toString().matches("") || etMobile.getText().toString().matches("") || etEmail.getText().toString().matches("")) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(EditUserProfileActivity.this,R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
        } else if (!(Patterns.PHONE.matcher(etMobile.getText().toString()).matches()) || !(etMobile.getText().toString().matches(mobilePattern))) {
            progressBar.setVisibility(View.GONE);
            CustomDialog dialog = new CustomDialog(EditUserProfileActivity.this, R.string.msg_invalid_mobile_number);
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) || !(etEmail.getText().toString().matches(emailPattern))) {
            progressBar.setVisibility(View.GONE);
            CustomDialog dialog = new CustomDialog(EditUserProfileActivity.this, R.string.msg_invalid_email);
        } else {
            saveInfo();
        }
    }

    private void saveInfo() {
        icd = new InternetConnectionDetector(EditUserProfileActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_edit_profile), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("already")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_already_registered, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("NAME", etName.getText().toString());
                        editor.putString("MOBILE", etMobile.getText().toString());
                        editor.putString("EMAIL", etEmail.getText().toString());
                        editor.apply();
                        Toast.makeText(EditUserProfileActivity.this,"Updated Successfully.",Toast.LENGTH_LONG).show();
                        finish();
                    } else if (jsonObject.names().get(0).equals("image")) {
                        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("NAME", etName.getText().toString());
                        editor.putString("MOBILE", etMobile.getText().toString());
                        editor.putString("EMAIL", etEmail.getText().toString());
                        editor.putString("PROFILE_IMAGE", jsonObject.getString("image"));
                        editor.apply();
                        Toast.makeText(EditUserProfileActivity.this,"Updated Successfully.",Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(EditUserProfileActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditUserProfileActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sp = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("name", etName.getText().toString());
                    parameters.put("mobile", etMobile.getText().toString());
                    parameters.put("email", etEmail.getText().toString());
                    parameters.put("id", sp.getString("USER_ID",""));
                    if (checkImage)
                        parameters.put("image", encodedImgString);
                    else
                        parameters.put("image", "");
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(EditUserProfileActivity.this,R.string.msg_check_internet_connection);
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
            filepath = data.getData();
            try{
                InputStream inputStream = getContentResolver().openInputStream(filepath);
                bitmap= BitmapFactory.decodeStream(inputStream);
                //checking for image size
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] imageInByte = stream.toByteArray();
                double lengthbmp = (imageInByte.length)/1024.0;
                if (lengthbmp<=7000.0) {
                    ivProfileImage.setImageBitmap(bitmap);
                    encodedBitmapImg(bitmap);
                } else {
                    Toast.makeText(EditUserProfileActivity.this,"Your Image size is too big to upload",Toast.LENGTH_LONG).show();
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