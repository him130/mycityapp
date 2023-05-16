package com.webandappdevelopment.serviceshop.LoginMech;

import static com.android.volley.Request.Method.POST;
import static com.webandappdevelopment.serviceshop.R.string.api_load_category;
import static com.webandappdevelopment.serviceshop.R.string.api_load_city;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.ServiceProvider.ServiceProviderHomeActivity;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceProviderRegistrationActivity extends AppCompatActivity {
    AppCompatButton btnRegister, btnLogin;
    EditText etName, etMobile, etEmail, etAge, etExperience, etAddress, etPassword, etConfirmPassword;
    RadioGroup rbMaritalStatus;
    InternetConnectionDetector icd;
    RequestQueue requestQueue, requestQueue1;
    StringRequest request, request1;
    ProgressBar progressBar;
    String stMaritalStatus="";
    AppCompatSpinner spCity, spCategory;
    List<String> city = new ArrayList<>();
    List<String> category = new ArrayList<>();
    int[] idArray;
    String[] cityArray, stateArray, categoryNameArray, categoryIdArray;
    int city_id;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"SourceLockedOrientationActivity", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_registration);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ScrollView layout = findViewById(R.id.serviceProviderRegistration_rootLayout);
        layout.setBackground(getDrawable(R.color.theme_yellow));
        setUpUI();
    }

    private void setUpUI() {
        etName = findViewById(R.id.serviceProviderRegistration_etName);
        etMobile = findViewById(R.id.serviceProviderRegistration_etMobile);
        etEmail = findViewById(R.id.serviceProviderRegistration_etEmail);
        etAge = findViewById(R.id.serviceProviderRegistration_etAge);
        etExperience = findViewById(R.id.serviceProviderRegistration_etExperience);
        etAddress = findViewById(R.id.serviceProviderRegistration_etAddress);
        spCity = findViewById(R.id.serviceProviderRegistration_spinnerCity);
        spCity.setOnItemSelectedListener(cityListener);
        loadSpinner();
        spCategory = findViewById(R.id.serviceProviderRegistration_spinnerCategory);
        spCategory.setOnItemSelectedListener(categoryListener);

        etPassword = findViewById(R.id.serviceProviderRegistration_etPassword);
        etConfirmPassword = findViewById(R.id.serviceProviderRegistration_etConfirmPassword);
        rbMaritalStatus = findViewById(R.id.serviceProviderRegistration_radioGroup);
        rbMaritalStatus.setOnCheckedChangeListener((radioGroup, i) -> {
            switch (i) {
                case R.id.spRegistration_rbSingle:
                    stMaritalStatus = "Single";
                    break;
                case R.id.spRegistration_rbMarried:
                    stMaritalStatus = "Married";
                    break;
            }
        });
        progressBar = findViewById(R.id.serviceProviderRegistration_progressBar);
        icd = new InternetConnectionDetector(ServiceProviderRegistrationActivity.this);

        btnRegister = findViewById(R.id.serviceProviderRegistration_btnRegister);
        btnRegister.setOnClickListener(view -> checkInfo());

        btnLogin = findViewById(R.id.serviceProviderRegistration_btnLogin);
        btnLogin.setOnClickListener(view -> startActivity(new Intent(ServiceProviderRegistrationActivity.this, LoginActivity.class)));
    }

    private void checkInfo() {
        String mobilePattern = "[6-9][0-9]{9}";
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (etName.getText().toString().matches("") || etMobile.getText().toString().matches("") || etEmail.getText().toString().matches("") || etPassword.getText().toString().matches("") || etConfirmPassword
                .getText().toString().matches("")) {
            progressBar.setVisibility(View.GONE);
//            R.string.msg_enter_all_details
            Toast.makeText(ServiceProviderRegistrationActivity.this,"here", Toast.LENGTH_LONG).show();
        } else if (!(Patterns.PHONE.matcher(etMobile.getText().toString()).matches()) || !(etMobile.getText().toString().matches(mobilePattern))) {
            progressBar.setVisibility(View.GONE);
            CustomDialog dialog = new CustomDialog(ServiceProviderRegistrationActivity.this, R.string.msg_invalid_mobile_number);
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) || !(etEmail.getText().toString().matches(emailPattern))) {
            progressBar.setVisibility(View.GONE);
            CustomDialog dialog = new CustomDialog(ServiceProviderRegistrationActivity.this, R.string.msg_invalid_email);
        } else if (!(etPassword.getText().toString().matches(etConfirmPassword.getText().toString()))) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(ServiceProviderRegistrationActivity.this,R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
        } else {
            saveInfo();
        }
    }

    private void saveInfo() {
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(Request.Method.POST, getString(R.string.api_sp_registration), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("already")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_already_registered, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        ArrayList aList= new ArrayList(Arrays.asList(city.get(spCity.getSelectedItemPosition()).split(",")));
                        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("NAME", etName.getText().toString());
                        editor.putString("MOBILE", etMobile.getText().toString());
                        editor.putString("EMAIL", etEmail.getText().toString());
                        editor.putString("CITY", aList.get(0).toString());
                        editor.putString("STATE", aList.get(1).toString());
                        editor.putString("WORK_STATUS", "0");
                        editor.putString("STATUS","ALREADY REGISTERED");
                        editor.putString("USER_TYPE", "SERVICE PROVIDER");
                        editor.apply();
                        startActivity(new Intent(ServiceProviderRegistrationActivity.this, ServiceProviderHomeActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ServiceProviderRegistrationActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceProviderRegistrationActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("name", etName.getText().toString());
                    parameters.put("mobile", etMobile.getText().toString());
                    parameters.put("email", etEmail.getText().toString());
                    parameters.put("age", etAge.getText().toString());
                    parameters.put("experience", etExperience.getText().toString());
                    parameters.put("address", etAddress.getText().toString());
                    parameters.put("city", city.get(spCity.getSelectedItemPosition()));
                    parameters.put("category", category.get(spCategory.getSelectedItemPosition()));
                    parameters.put("marital", stMaritalStatus);
                    parameters.put("password", etPassword.getText().toString());
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceProviderRegistrationActivity.this,R.string.msg_check_internet_connection);
        }
    }

    private void loadSpinner() {
        icd = new InternetConnectionDetector(this);
        if(icd.isConnected()) {
            requestQueue1 = Volley.newRequestQueue(getApplicationContext());
            requestQueue1.getCache().clear();
            request1 = new StringRequest(POST, getString(api_load_city), response -> {
                try {
                    JSONArray array = new JSONArray(response);
                    idArray = new int[array.length()];
                    cityArray = new String[array.length()];
                    stateArray = new String[array.length()];
                    for (int i=0; i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        idArray[i] = object.getInt("city_id");
                        cityArray[i] = object.getString("city_name");
                        stateArray[i] = object.getString("state_name");

                    }

                    for (int x=0 ; x<array.length() ; x++){
                        city.add(cityArray[x]+", "+stateArray[x]);
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,city);
                    spCity.setAdapter(arrayAdapter);

                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ServiceProviderRegistrationActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceProviderRegistrationActivity.this, R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
            });
            requestQueue1.add(request1);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceProviderRegistrationActivity.this,R.string.msg_check_internet_connection);
        }
    }

    private void loadCategory() {

        icd = new InternetConnectionDetector(this);
        if(icd.isConnected()) {
            requestQueue1 = Volley.newRequestQueue(getApplicationContext());
            requestQueue1.getCache().clear();
            request1 = new StringRequest(POST, getString(api_load_category), response -> {
                try {
                    category.clear();
                    JSONArray array = new JSONArray(response);
                    categoryIdArray = new String[array.length()];
                    categoryNameArray = new String[array.length()];
                    for (int i=0; i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        categoryIdArray[i] = object.getString("category_id");
                        categoryNameArray[i] = object.getString("category_name");
                    }
                    for (int x=0 ; x<array.length() ; x++){
                        category.add(categoryNameArray[x]);
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,category);
                    spCategory.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    
                    // Toast.makeText(ServiceProviderRegistrationActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceProviderRegistrationActivity.this, R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("city_id", String.valueOf(city_id));

                    return parameters;
                }
            };
            requestQueue1.add(request1);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceProviderRegistrationActivity.this,R.string.msg_check_internet_connection);
        }
    }


    private AdapterView.OnItemSelectedListener cityListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
            Typeface typeface = ResourcesCompat.getFont(ServiceProviderRegistrationActivity.this,R.font.abeezee);
            ((TextView) parent.getChildAt(0)).setTypeface(typeface);

            city_id = idArray[position];
            loadCategory();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private AdapterView.OnItemSelectedListener categoryListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
            Typeface typeface = ResourcesCompat.getFont(ServiceProviderRegistrationActivity.this,R.font.abeezee);
            ((TextView) parent.getChildAt(0)).setTypeface(typeface);
            /*String parentid = city.get(position);*/

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}