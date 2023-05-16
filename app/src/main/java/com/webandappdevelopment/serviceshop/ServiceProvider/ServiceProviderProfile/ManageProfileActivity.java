package com.webandappdevelopment.serviceshop.ServiceProvider.ServiceProviderProfile;

import static com.android.volley.Request.Method.POST;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
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
import com.webandappdevelopment.serviceshop.MainActivity;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ManageProfileActivity extends AppCompatActivity {
    TextView tvName, tvId, tvContact, tvEmail, tvAddress, tvAge, tvMarital, tvExperience, tvCategory, tvAbout;
    ShapeableImageView ivProfileImg;
    AppCompatButton btnLogout, btnEditProfile, btnChangePassword;
    ProgressBar progressBar;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    String stAddress, stCity, stState, stAge, stExperience, stId, stImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_profile);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpUI();
    }

    private void setUpUI(){
        progressBar = findViewById(R.id.manageProfile_progressBar);
        tvName = findViewById(R.id.manageProfile_tvName);
        tvId = findViewById(R.id.manageProfile_tvId);
        tvContact = findViewById(R.id.manageProfile_tvContact);
        tvEmail = findViewById(R.id.manageProfile_tvEmail);
        tvAddress = findViewById(R.id.manageProfile_tvAddress);
        tvAge = findViewById(R.id.manageProfile_tvAge);
        tvExperience = findViewById(R.id.manageProfile_tvExperience);
        tvMarital = findViewById(R.id.manageProfile_tvMaritalStatus);
        tvCategory = findViewById(R.id.manageProfile_tvCategory);
        tvAbout = findViewById(R.id.manageProfile_tvAbout);
        ivProfileImg = findViewById(R.id.manageProfile_ivProfileImg);
        fetchData();
        
        btnLogout = findViewById(R.id.manageProfile_btnLogout);
        btnLogout.setOnClickListener(view -> {
            SharedPreferences myPrefs = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.clear();
            editor.apply();
            Toast.makeText(ManageProfileActivity.this, R.string.msg_logout, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(ManageProfileActivity.this, MainActivity.class));
            finish();
        });

        btnChangePassword = findViewById(R.id.manageProfile_btnChangePassword);
        btnChangePassword.setOnClickListener(view -> startActivity(new Intent(ManageProfileActivity.this, ChangePasswordActivity.class)));

        btnEditProfile = findViewById(R.id.manageProfile_btnEditProfile);
        btnEditProfile.setOnClickListener(view -> {
            Intent i = new Intent(ManageProfileActivity.this,EditProfileActivity.class);
            i.putExtra("id",stId);
            i.putExtra("name",tvName.getText());
            i.putExtra("contact",tvContact.getText());
            i.putExtra("email",tvEmail.getText());
            i.putExtra("address",stAddress);
            i.putExtra("city",stCity);
            i.putExtra("state",stState);
            i.putExtra("age",stAge);
            i.putExtra("experience",stExperience);
            i.putExtra("marital",tvMarital.getText());
            i.putExtra("category",tvCategory.getText());
            i.putExtra("image",stImage);
            i.putExtra("about", tvAbout.getText());
            startActivity(i);
        });
    }

    private void fetchData() {
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(ManageProfileActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_sp_details), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_invalid_mobile_number, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("sp_id")) {
                        stId = jsonObject.getString("sp_id");
                        tvName.setText(myStringFormatter(jsonObject.getString("full_name")));
                        tvContact.setText(jsonObject.getString("mobile_number"));
                        tvEmail.setText(jsonObject.getString("email"));
                        stAddress = jsonObject.getString("address");
                        stCity = jsonObject.getString("city_name");
                        stState = jsonObject.getString("state_name");
                        tvAddress.setText(myStringFormatter(stAddress)+" "+myStringFormatter(stCity)+" "+myStringFormatter(stState));
                        tvId.setText("ID: "+jsonObject.getString("mycity_id"));
                        stAge = jsonObject.getString("age");
                        tvAge.setText(stAge + " yrs old");
                        stExperience = jsonObject.getString("experience");
                        tvExperience.setText(stExperience + " yrs of experience");
                        tvMarital.setText(jsonObject.getString("marital_status"));
                        tvCategory.setText(jsonObject.getString("category"));
                        tvAbout.setText(jsonObject.getString("about"));
                        stImage = jsonObject.getString("image_url");
                        if (stImage.matches("")) {
                            ivProfileImg.setBackgroundResource(R.mipmap.ic_launcher_new);
                        } else {
                            Picasso.get().load(stImage)
                                    .placeholder(R.mipmap.ic_launcher_new)
                                    .into(ivProfileImg);
                        }
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ManageProfileActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageProfileActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String stId = sharedPreferences.getString("SP_ID","");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("spId", stId);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ManageProfileActivity.this,R.string.msg_check_internet_connection);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchData();
    }

    private String myStringFormatter(String myString) {
        String[] strArray = myString.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        return builder.toString();
    }
}