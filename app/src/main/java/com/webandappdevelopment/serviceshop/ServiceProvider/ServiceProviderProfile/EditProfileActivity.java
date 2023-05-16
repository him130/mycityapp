package com.webandappdevelopment.serviceshop.ServiceProvider.ServiceProviderProfile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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

public class EditProfileActivity extends AppCompatActivity {
    AppCompatButton btnUpdate,btnUpload;
    EditText etAbout;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    ProgressBar progressBar;
    String stId;
    ShapeableImageView ivProfileImage;
    //image upload
    private final int PICK_IMAGE_RESULT = 1;
    Uri filepath;
    Bitmap bitmap;
    String encodedImgString;
    Boolean checkImage=false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ScrollView layout = findViewById(R.id.editProfile_rootLayout);
        layout.setBackground(getDrawable(R.color.theme_yellow));
        setUpUI();
    }

    private void setUpUI() {
        progressBar = findViewById(R.id.editProfile_progressBar);
        etAbout = findViewById(R.id.editProfile_etAbout);
        etAbout.setHintTextColor(getResources().getColor(R.color.hint_color));
        ivProfileImage = findViewById(R.id.editProfile_ivProfileImg);
        btnUpload = findViewById(R.id.editProfile_btnOpenGallery);
        btnUpload.setOnClickListener(view -> showFileChooser());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            stId = extras.getString("id");
            etAbout.setText(extras.getString("about"));
            if (extras.getString("image").matches("")) {
                ivProfileImage.setBackgroundResource(R.mipmap.ic_launcher_new);
            } else {
                Picasso.get().load(extras.getString("image"))
                        .placeholder(R.mipmap.ic_launcher_new)
                        .into(ivProfileImage);
                btnUpload.setVisibility(View.GONE);
            }
        }
        progressBar = findViewById(R.id.editProfile_progressBar);
        icd = new InternetConnectionDetector(EditProfileActivity.this);

        btnUpdate = findViewById(R.id.editProfile_btnUpdate);
        btnUpdate.setOnClickListener(view -> checkInfo());
    }

    private void checkInfo() {
        progressBar.setVisibility(View.VISIBLE);
        saveInfo();
    }

    private void saveInfo() {
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(Request.Method.POST, getString(R.string.api_sp_update), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("already")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_already_registered, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(getApplicationContext(), "Updated Successfully.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(EditProfileActivity.this,ManageProfileActivity.class));
                        finish();
                    } else if (jsonObject.names().get(0).equals("image")) {
                        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("PROFILE_IMG", jsonObject.getString("image"));
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Updated Successfully.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(EditProfileActivity.this,ManageProfileActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(EditProfileActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(EditProfileActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("about", etAbout.getText().toString());
                    parameters.put("spId", stId);
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
            CustomDialog scd = new CustomDialog(EditProfileActivity.this,R.string.msg_check_internet_connection);
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
                    Toast.makeText(EditProfileActivity.this,"Your Image size is too big to upload",Toast.LENGTH_LONG).show();
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