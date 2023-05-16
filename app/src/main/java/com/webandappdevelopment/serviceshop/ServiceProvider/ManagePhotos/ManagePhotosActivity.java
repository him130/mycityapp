package com.webandappdevelopment.serviceshop.ServiceProvider.ManagePhotos;

import static com.android.volley.Request.Method.POST;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagePhotosActivity extends AppCompatActivity {
    RelativeLayout rlImage;
    ImageView ivImage;
    RecyclerView recyclerView;
    private List<PhotosItem> listItems;
    private PhotosListAdapter adapter;
    PhotosListAdapter.ItemClickListener clickListener;
    AppCompatButton btnUpload, btnUpdate;
    TextView tvNoImage;
    ProgressBar progressBar;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    int idArray[];
    String urlArray[];
    //image upload
    private final int PICK_IMAGE_RESULT = 1;
    Uri filepath;
    Bitmap bitmap;
    String encodedImgString;
    Boolean checkImage=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_photos);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpUI();

    }

    private void setUpUI() {
        clickListener = this::onItemClick;
        rlImage = findViewById(R.id.managePhotos_rlImage);
        ivImage = findViewById(R.id.managePhotos_ivImage);
        tvNoImage = findViewById(R.id.managePhotos_tvNoImage);
        progressBar = findViewById(R.id.managePhotos_progressBar);
        btnUpload = findViewById(R.id.managePhotos_btnUpload);
        btnUpload.setOnClickListener(view -> showFileChooser());
        btnUpdate = findViewById(R.id.managePhotos_btnUpdate);
        btnUpdate.setOnClickListener(view -> updateImages());
        recyclerView = findViewById(R.id.managePhotos_rvList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(ManagePhotosActivity.this,3));
        loadPhotos();
    }

    private void loadPhotos() {
        icd = new InternetConnectionDetector(this);
        progressBar.setVisibility(View.VISIBLE);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_my_photos), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    tvNoImage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    JSONArray array = new JSONArray(response);
                    idArray = new int[array.length()];
                    urlArray = new String[array.length()];
                    for (int i=0; i<array.length();i++) {
                        JSONObject object = array.getJSONObject(i);
                        idArray[i] = object.getInt("img_id");
                        urlArray[i] = object.getString("img_url");
                    }
                    listItems = new ArrayList<>();
                    for (int x=0 ; x<array.length() ; x++){
                        PhotosItem item = new PhotosItem(idArray[x],urlArray[x]);
                        listItems.add(item);
                    }
                    adapter = new PhotosListAdapter(ManagePhotosActivity.this, listItems, clickListener);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    tvNoImage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                tvNoImage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(ManagePhotosActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String userId = sharedPreferences.getString("SP_ID", "");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("spId", userId);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ManagePhotosActivity.this,R.string.msg_check_internet_connection);
        }
    }

    private void updateImages(){
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(ManagePhotosActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_photos), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("no") || jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(ManagePhotosActivity.this,"Updated Successfully.",Toast.LENGTH_LONG).show();
                        loadPhotos();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ManagePhotosActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManagePhotosActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("spId", sharedPreferences.getString("SP_ID",""));
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
            CustomDialog scd = new CustomDialog(ManagePhotosActivity.this,R.string.msg_check_internet_connection);
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
                    rlImage.setVisibility(View.VISIBLE);
                    ivImage.setVisibility(View.VISIBLE);
                    ivImage.setImageBitmap(bitmap);
                    encodedBitmapImg(bitmap);
                } else {
                    Toast.makeText(ManagePhotosActivity.this,"Your Image size is too big to upload",Toast.LENGTH_LONG).show();
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

    public void onItemClick(PhotosItem item) {
        Intent i = new Intent(ManagePhotosActivity.this, FullSizeImageActivity.class);
        i.putExtra("url",item.getImageUrl());
        i.putExtra("id",item.getImageId());
        i.putExtra("status",true);
        startActivity(i);
        finish();
    }
}