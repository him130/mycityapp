package com.webandappdevelopment.serviceshop.LoginMech;

import static com.android.volley.Request.Method.POST;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    FirebaseRemoteConfig mFirebaseRemoteConfig;

    private static final int MY_REQUEST_CODE = 50;
    AppUpdateManager appUpdateManager;
    AppCompatButton btnLogin, btnForgetPassword, btnRegisterNow;
    EditText etMobile, etPassword;
    RadioGroup rgUserType;
    String stUserType="" , otpArray[];
    ProgressBar progressBar;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        int currentVersionCode;

        currentVersionCode = getCurrentVersionCode();

        Log.d("myApp", String.valueOf(currentVersionCode));

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10) // change to 3600 on published app
                .build();


        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        Log.d("Remote", String.valueOf(mFirebaseRemoteConfig));
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if (task.isSuccessful()) {
                        final String new_version_code = mFirebaseRemoteConfig.getString("new_version_code");

                        if(Integer.parseInt(new_version_code) > getCurrentVersionCode())
                            showUpdateDialog();
                    }
                    else Log.e("MYLOG", "mFirebaseRemoteConfig.fetchAndActivate() NOT Successful");
            }
        });

        ConstraintLayout layout = findViewById(R.id.login_rootLayout);
        layout.setBackground(getDrawable(R.color.theme_yellow));
        /*checkappupdate();*/
        setUpUI();


    }

    private void showUpdateDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New Update Available")
                .setMessage("Update Now")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.webandappdevelopment.serviceshop")));
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "Something went wrong. Try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
        dialog.setCancelable(false);
    }


    private int getCurrentVersionCode(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
        }catch (Exception e){
            Log.d("myApp", e.getMessage());
        }

        return packageInfo.versionCode;
    }



    private void checkappupdate(){

        System.out.println("New Method is call");


        appUpdateManager = AppUpdateManagerFactory.create(this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE

                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo, AppUpdateType.FLEXIBLE, this, MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Try new", Toast.LENGTH_SHORT).show();
                }

            }
        });
        appUpdateManager.registerListener(installStateUpdatedListener);

    }

    private InstallStateUpdatedListener installStateUpdatedListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(@NonNull InstallState installState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED){
                popup();
            }

        }
    };
    private void popup() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"App Update almost Done", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Reload", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appUpdateManager.completeUpdate();

            }
        });
        snackbar.setTextColor(Color.parseColor("#FF0000"));
        snackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                Log.w("LoginActivity", "Update flow failed! Result code: " + resultCode);

            }
        }
    }


    private void setUpUI() {
        icd = new InternetConnectionDetector(LoginActivity.this);
        progressBar = findViewById(R.id.login_progressBar);
        etMobile = findViewById(R.id.login_etMobile);
        etPassword = findViewById(R.id.login_etPassword);
        rgUserType = findViewById(R.id.login_radioGroup);
        rgUserType.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.login_rbCustomer:
                    stUserType = "APP USER";
                    etMobile.setVisibility(View.VISIBLE);
                    etPassword.setVisibility(View.GONE);
                    btnForgetPassword.setVisibility(View.GONE);
                    break;

                case R.id.login_rbServiceProvider:
                    stUserType = "SERVICE PROVIDER";
                    etMobile.setVisibility(View.VISIBLE);
                    etPassword.setVisibility(View.VISIBLE);
                    btnForgetPassword.setVisibility(View.VISIBLE);
                    break;
            }
        });
        btnLogin = findViewById(R.id.login_btnLogin);
        btnLogin.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            checkInfo();
        });
        btnRegisterNow = findViewById(R.id.login_btnRegisterNow);
        btnRegisterNow.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
        btnForgetPassword = findViewById(R.id.login_btnForgetPassword);
        btnForgetPassword.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class)));
    }

    private void checkInfo() {
        String mobilePattern = "[6-9][0-9]{9}";
        if (etMobile.getText().toString().matches("")) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this,R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
        } else if (!(Patterns.PHONE.matcher(etMobile.getText().toString()).matches()) || !(etMobile.getText().toString().matches(mobilePattern))) {
            progressBar.setVisibility(View.GONE);
            CustomDialog dialog = new CustomDialog(LoginActivity.this, R.string.msg_invalid_mobile_number);
        } else if (stUserType.matches("SERVICE PROVIDER") && etPassword.getText().toString().matches("")) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this,R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
        } else {
            saveInfo();
        }
    }

    private void otpsend() {

        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_otp_send), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONArray array = new JSONArray(response);
                    /*otpArray = new String[array.length()];
                    for (int i = 0; i<array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        otpArray[i] = object.getString("service");
                    }
                    stServices = TextUtils.join(",", serviceArray);
                    tvServices.setText(myStringFormatter(stServices));*/
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
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
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(LoginActivity.this,R.string.msg_check_internet_connection);
        }

    }

    private void saveInfo() {
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_login), response -> {
                try {

                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_please_register, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("incorrect")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_incorrect_password, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("user_id")) {
                        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("USER_ID", jsonObject.getString("user_id"));
                        editor.putString("NAME", jsonObject.getString("full_name"));
                        editor.putString("MOBILE", jsonObject.getString("mobile_number"));
                        editor.putString("EMAIL", jsonObject.getString("email"));
                        editor.putString("CITY", jsonObject.getString("city_name"));
                        editor.putString("STATE", jsonObject.getString("state_name"));
                        editor.putString("PROFILE_IMAGE", jsonObject.getString("img_url"));
                        editor.putString("STATUS","ALREADY REGISTERED");
                        editor.putString("USER_TYPE", stUserType);
                        editor.apply();
                        /*startActivity(new Intent(LoginActivity.this, HomeActivity.class));*/

                        otpsend();
                        Intent intent = new Intent(LoginActivity.this, OtpVerifyActivity.class);
                        intent.putExtra("userID", jsonObject.getString("user_id"));
                        intent.putExtra("phone", etMobile.getText().toString().trim());
                        startActivity(intent);
                        finish();
                    } else if (jsonObject.names().get(0).equals("sp_id")) {
                        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("SP_ID", jsonObject.getString("sp_id"));
                        editor.putString("NAME", jsonObject.getString("full_name"));
                        editor.putString("MOBILE", jsonObject.getString("mobile_number"));
                        editor.putString("EMAIL", jsonObject.getString("email"));
                        editor.putString("CITY", jsonObject.getString("city_name"));
                        editor.putString("STATE", jsonObject.getString("state_name"));
                        editor.putString("PROFILE_IMG", jsonObject.getString("image_url"));
                        editor.putString("WORK_STATUS", jsonObject.getString("work_status"));
                        editor.putString("STATUS","ALREADY REGISTERED");
                        editor.putString("USER_TYPE", stUserType);
                        editor.putString("MYCITY_ID", jsonObject.getString("mycity_id"));
                        editor.apply();
                        /*startActivity(new Intent(LoginActivity.this, ServiceProviderHomeActivity.class));*/
                        otpsend();
                        Intent intent = new Intent(LoginActivity.this, OtpVerifyActivity.class);
                        intent.putExtra("spID", jsonObject.getString("sp_id"));
                        intent.putExtra("phone", etMobile.getText().toString().trim());
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    if (stUserType.matches("SERVICE PROVIDER"))
                        parameters.put("pswd", etPassword.getText().toString());
                    parameters.put("mobile", etMobile.getText().toString());
                    parameters.put("type", stUserType);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(LoginActivity.this,R.string.msg_check_internet_connection);
        }
    }
}