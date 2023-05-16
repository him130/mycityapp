package com.webandappdevelopment.serviceshop.LoginMech;

import static com.android.volley.Request.Method.POST;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;

import com.webandappdevelopment.serviceshop.HomeActivity;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.ServiceProvider.ServiceProviderHomeActivity;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class OtpVerifyActivity extends AppCompatActivity {

    AppCompatButton submit;
    EditText[] otpETs = new EditText[6];
    EditText etB1, etB2, etB3, etB4, etB5, etB6;
    TextView tMobile, resend;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    private String userid;
    private String spid;
    private String phonenum;
    PinView pinView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        submit = findViewById(R.id.otp_submit);
        pinView = findViewById(R.id.firstPinView);
        tMobile = findViewById(R.id.send_mobile);
        resend = findViewById(R.id.resendbtn);


        phonenum = getIntent().getStringExtra("phone");
        tMobile.setText(String.format(
                "+91-%s", phonenum
        ));

        userid = getIntent().getStringExtra("userID");
        spid = getIntent().getStringExtra("spID");

        icd = new InternetConnectionDetector(OtpVerifyActivity.this);
        progressBar = findViewById(R.id.login_progressBar);


        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpresend();
                Toast.makeText(OtpVerifyActivity.this, "Otp resend Successfully", Toast.LENGTH_SHORT).show();
            }
        });

        verifyOtp();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(icd.isConnected()) {
                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                    requestQueue.getCache().clear();
                    request = new StringRequest(POST, getString(R.string.api_varify), response -> {

                        try {
                            progressBar.setVisibility(View.GONE);

                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(OtpVerifyActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                            if (pinView.getText().toString().equals(jsonObject.getString("varification_code"))) {
                                if (userid != null){
                                    Intent intent = new Intent(OtpVerifyActivity.this, HomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }else if (spid != null){
                                    Intent intent = new Intent(OtpVerifyActivity.this, ServiceProviderHomeActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                                else{
                                    Toast.makeText(OtpVerifyActivity.this, "User not exist", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(OtpVerifyActivity.this, "No Found oid", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            progressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                            Toast.makeText(OtpVerifyActivity.this,"Enter valid OTP",Toast.LENGTH_LONG).show();
                        }


                    }, error -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(OtpVerifyActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> parameters = new HashMap<>();
                            parameters.put("otp", pinView.getText().toString());
                            parameters.put("mobile", phonenum);
                            return parameters;
                        }
                    };
                    requestQueue.add(request);
                }
                else {
                    progressBar.setVisibility(View.GONE);
                    CustomDialog scd = new CustomDialog(OtpVerifyActivity.this,R.string.msg_check_internet_connection);
                }
            }


        });

    }

    private void verifyOtp() {



    }

    private void otpresend() {

        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_otp_send), response -> {
                try {
                    JSONArray array = new JSONArray(response);

                } catch (JSONException e) {

                    e.printStackTrace();
                }
            }, error -> {

                Toast.makeText(OtpVerifyActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("mobile", phonenum);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            CustomDialog scd = new CustomDialog(OtpVerifyActivity.this,R.string.msg_check_internet_connection);
        }

    }
}