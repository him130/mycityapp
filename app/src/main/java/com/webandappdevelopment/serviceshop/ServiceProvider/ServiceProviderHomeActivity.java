package com.webandappdevelopment.serviceshop.ServiceProvider;

import static com.android.volley.Request.Method.POST;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
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
import com.webandappdevelopment.serviceshop.ServiceProvider.ManagePhotos.ManagePhotosActivity;
import com.webandappdevelopment.serviceshop.ServiceProvider.ServiceArea.ServiceAreaActivity;
import com.webandappdevelopment.serviceshop.ServiceProvider.ServiceProviderProfile.ManageProfileActivity;
import com.webandappdevelopment.serviceshop.ServiceProvider.ServiceProviderProfile.VerifyProfileActivity;
import com.webandappdevelopment.serviceshop.ServiceProvider.ServiceRequest.ServiceRequestActivity;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ServiceProviderHomeActivity extends AppCompatActivity {
    AppCompatButton btnServiceRequest, btnAddCategory, btnAddServiceArea,
            btnManagePhotos, btnManageProfile, btnVerifyProfile, btnUpload;
    Switch switchStatus;
    TextView tvStatus, tvName, tvContact, tvCityState;
    ShapeableImageView ivProfileImg;
    String stName, stContact, stCity, stState, stId, stStatus, stImage;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    ProgressBar progressBar;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_home);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpUI();
    }

    private void setUpUI() {
        sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        stName = sharedPreferences.getString("NAME", "My City");
        stContact = sharedPreferences.getString("MOBILE", "00000-00000");
        stCity = sharedPreferences.getString("CITY", "Raipur");
        stState = sharedPreferences.getString("STATE", "Chhattisgarh");
        stId = sharedPreferences.getString("MYCITY_ID","0000");
        stStatus = sharedPreferences.getString("WORK_STATUS","0");
        stImage = sharedPreferences.getString("PROFILE_IMG","");

        tvName = findViewById(R.id.spHome_tvName);
        tvName.setText(myStringFormatter(stName));
        tvContact = findViewById(R.id.spHome_tvContact);
        tvContact.setText(myStringFormatter(stContact));
        tvCityState = findViewById(R.id.spHome_tvCityState);
        tvCityState.setText(myStringFormatter(stCity)+" ("+myStringFormatter(stState)+")");
        ivProfileImg = findViewById(R.id.spHome_ivProfileImg);
        btnUpload = findViewById(R.id.spHome_btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ServiceProviderHomeActivity.this,UploadDocumentActivity.class));
            }
        });
        if (stImage.matches("")) {
            ivProfileImg.setBackgroundResource(R.mipmap.ic_launcher_new);
        } else {
            Picasso.get().load(stImage)
                    .placeholder(R.mipmap.ic_launcher_new)
                    .into(ivProfileImg);
        }
        progressBar = findViewById(R.id.spHome_progressBar);
        tvStatus = findViewById(R.id.spHome_tvStatus);
        switchStatus = findViewById(R.id.spHome_switchStatus);
        if (stStatus.matches("0"))
            switchStatus.setChecked(true);
        else
            switchStatus.setChecked(false);
        switchStatus.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                progressBar.setVisibility(View.VISIBLE);
                updateStatus("0");
            } else {
                progressBar.setVisibility(View.VISIBLE);
                updateStatus("1");
            }
        });

        btnServiceRequest = findViewById(R.id.spHome_btnServiceRequest);
        btnServiceRequest.setOnClickListener(view -> startActivity(new Intent(ServiceProviderHomeActivity.this, ServiceRequestActivity.class)));
        btnAddCategory = findViewById(R.id.spHome_btnAddCategory);
        btnAddCategory.setOnClickListener(view -> startActivity(new Intent(ServiceProviderHomeActivity.this, AddServicesActivity.class)));
        btnAddServiceArea = findViewById(R.id.spHome_btnAddServiceArea);
        btnAddServiceArea.setOnClickListener(view -> startActivity(new Intent(ServiceProviderHomeActivity.this, ServiceAreaActivity.class)));
        btnManagePhotos = findViewById(R.id.spHome_btnManagePhotos);
        btnManagePhotos.setOnClickListener(view -> startActivity(new Intent(ServiceProviderHomeActivity.this, ManagePhotosActivity.class)));
        btnManageProfile = findViewById(R.id.spHome_btnManageProfile);
        btnManageProfile.setOnClickListener(view -> startActivity(new Intent(ServiceProviderHomeActivity.this, ManageProfileActivity.class)));
        btnVerifyProfile = findViewById(R.id.spHome_btnVerifyProfile);
        btnVerifyProfile.setOnClickListener(view -> startActivity(new Intent(ServiceProviderHomeActivity.this, VerifyProfileActivity.class)));
    }

    private void updateStatus(String status) {
        icd = new InternetConnectionDetector(ServiceProviderHomeActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_update_status), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_server_not_responding, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        if (status.matches("0")) {
                            editor = sharedPreferences.edit();
                            editor.putString("WORK_STATUS", "0");
                            tvStatus.setText("online");
                        } else if (status.matches("1")) {
                            editor = sharedPreferences.edit();
                            editor.putString("WORK_STATUS", "1");
                            tvStatus.setText("offline");
                        }
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ServiceProviderHomeActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceProviderHomeActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("status", status);
                    parameters.put("id", sharedPreferences.getString("SP_ID",""));
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceProviderHomeActivity.this, R.string.msg_check_internet_connection);
        }
    }

    private String myStringFormatter (String myString) {
        String[] strArray = myString.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0,1).toUpperCase() + s.substring(1);
            builder.append(cap+" ");
        }
        return builder.toString();
    }
}