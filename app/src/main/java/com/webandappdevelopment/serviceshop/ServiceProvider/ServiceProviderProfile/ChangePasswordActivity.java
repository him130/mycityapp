package com.webandappdevelopment.serviceshop.ServiceProvider.ServiceProviderProfile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText etOldPassword, etNewPassword, etConfirmPassword;
    AppCompatButton btnSubmit;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpUI();
    }

    private void setUpUI() {
        icd = new InternetConnectionDetector(this);
        progressBar = findViewById(R.id.changePassword_progressBar);
        etOldPassword = findViewById(R.id.changePassword_etOldPassword);
        etNewPassword = findViewById(R.id.changePassword_etNewPassword);
        etConfirmPassword = findViewById(R.id.changePassword_etConfirmPassword);
        etOldPassword.setHintTextColor(getResources().getColor(R.color.hint_color));
        etNewPassword.setHintTextColor(getResources().getColor(R.color.hint_color));
        etConfirmPassword.setHintTextColor(getResources().getColor(R.color.hint_color));
        btnSubmit= findViewById(R.id.changePassword_btnSubmit);
        btnSubmit.setOnClickListener((View view) -> checkInfo());
    }

    private void checkInfo() {
        if (etOldPassword.getText().toString().matches("") || etNewPassword.getText().toString().matches("") || etConfirmPassword.getText().toString().matches("")) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ChangePasswordActivity.this,R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
        } else if (!(etNewPassword.getText().toString()).matches(etConfirmPassword.getText().toString())) {
            progressBar.setVisibility(View.GONE);
            CustomDialog dialog = new CustomDialog(ChangePasswordActivity.this, R.string.msg_password_doesnot_match);
        } else {
            saveInfo();
        }
    }

    private void saveInfo() {
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(Request.Method.POST, getString(R.string.api_change_password), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        CustomDialog dialog = new CustomDialog(ChangePasswordActivity.this, R.string.msg_enter_all_details);
                    } else if (jsonObject.names().get(0).equals("no")) {
                        CustomDialog dialog = new CustomDialog(ChangePasswordActivity.this, R.string.msg_no_user);
                    } else if (jsonObject.names().get(0).equals("incorrect")) {
                        CustomDialog dialog = new CustomDialog(ChangePasswordActivity.this, R.string.msg_incorrect_password);
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        CustomDialog dialog = new CustomDialog(ChangePasswordActivity.this, R.string.msg_update_fail);
                    } else if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(ChangePasswordActivity.this,R.string.msg_update_success, Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ChangePasswordActivity.this, ManageProfileActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ChangePasswordActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ChangePasswordActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String stUserId = sharedPreferences.getString("SP_ID", "");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("oldPswd", etOldPassword.getText().toString());
                    parameters.put("newPswd", etNewPassword.getText().toString());
                    parameters.put("userId", stUserId);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ChangePasswordActivity.this, R.string.msg_check_internet_connection);
        }
    }
}