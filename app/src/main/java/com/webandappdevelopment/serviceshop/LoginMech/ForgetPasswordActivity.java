package com.webandappdevelopment.serviceshop.LoginMech;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

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

public class ForgetPasswordActivity extends AppCompatActivity {
    AppCompatButton btnLogin, btnSubmit;
    EditText etMobile;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ConstraintLayout layout = findViewById(R.id.forgetPassword_rootLayout);
        layout.setBackground(getDrawable(R.color.theme_yellow));
        setUpUI();
    }

    private void setUpUI() {
        icd = new InternetConnectionDetector(ForgetPasswordActivity.this);
        progressBar = findViewById(R.id.forgetPassword_progressBar);
        etMobile = findViewById(R.id.forgetPassword_etMobile);
        btnLogin = findViewById(R.id.forgetPassword_btnLogin);
        btnLogin.setOnClickListener((View v) -> {
            startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
            finish();
        });
        btnSubmit = findViewById(R.id.forgetPassword_btnSubmit);
        btnSubmit.setOnClickListener((View v) -> checkInfo());
    }

    private void checkInfo() {
        String mobilePattern = "[6-9][0-9]{9}";
        if(etMobile.getText().toString().matches("")) {
            Toast.makeText(ForgetPasswordActivity.this, R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
        } else if (!(Patterns.PHONE.matcher(etMobile.getText().toString()).matches()) || !(etMobile.getText().toString().matches(mobilePattern))){
            CustomDialog dialog = new CustomDialog(ForgetPasswordActivity.this, R.string.msg_invalid_mobile_number);
        } else {
            updateInfo();
        }
    }

    private void updateInfo() {
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(Request.Method.POST, getString(R.string.api_forget_password), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(ForgetPasswordActivity.this, R.string.msg_email_success, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(ForgetPasswordActivity.this, LoginActivity.class));
                        finish();
                    } else if (jsonObject.names().get(0).equals("no")) {
                        CustomDialog dialog = new CustomDialog(ForgetPasswordActivity.this, R.string.msg_no_user);
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        etMobile.setText("");
                        CustomDialog dialog = new CustomDialog(ForgetPasswordActivity.this, R.string.msg_something_went_wrong);
                    } else if (jsonObject.names().get(0).equals("error")) {
                        etMobile.setText("");
                        CustomDialog dialog = new CustomDialog(ForgetPasswordActivity.this, R.string.msg_enter_all_details);
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ForgetPasswordActivity.this, R.string.msg_server_not_responding, Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("mobile", etMobile.getText().toString());
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            CustomDialog cd = new CustomDialog(ForgetPasswordActivity.this, R.string.msg_check_internet_connection);
        }
    }
}