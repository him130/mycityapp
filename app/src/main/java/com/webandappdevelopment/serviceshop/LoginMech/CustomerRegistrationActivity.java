package com.webandappdevelopment.serviceshop.LoginMech;

import static com.android.volley.Request.Method.POST;
import static com.webandappdevelopment.serviceshop.R.string.api_load_city;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.webandappdevelopment.serviceshop.HomeActivity;
import com.webandappdevelopment.serviceshop.R;
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

public class CustomerRegistrationActivity extends AppCompatActivity {
    private AppCompatButton btnLogin, btnRegister;
    EditText etName, etMobile, etEmail;
    InternetConnectionDetector icd;
    RequestQueue requestQueue, requestQueue1;
    StringRequest request, request1;
    ProgressBar progressBar;
    AppCompatSpinner spCity;
    List<String> city = new ArrayList<>();
    int idArray[];
    String cityArray[], stateArray[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_registration);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ConstraintLayout layout = findViewById(R.id.customerRegistration_rootLayout);
        layout.setBackground(AppCompatResources.getDrawable(this, R.color.theme_yellow));
        setUpUI();
    }

    private void setUpUI() {
        icd = new InternetConnectionDetector(CustomerRegistrationActivity.this);
        etName = findViewById(R.id.customerRegistration_etName);
        etMobile = findViewById(R.id.customerRegistration_etMobile);
        etEmail = findViewById(R.id.customerRegistration_etEmail);
        spCity = findViewById(R.id.customerRegistration_spinnerCity);
        spCity.setOnItemSelectedListener(listener);
        loadSpinner();
        progressBar = findViewById(R.id.customerRegistration_progressBar);
        btnLogin = findViewById(R.id.customerRegistration_btnLogin);
        btnLogin.setOnClickListener(view -> {
            startActivity(new Intent(CustomerRegistrationActivity.this, LoginActivity.class));
            finish();
        });
        btnRegister = findViewById(R.id.customerRegistration_btnRegister);
        btnRegister.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            checkInfo();
        });
    }

    private void checkInfo() {
        String mobilePattern = "[6-9][0-9]{9}";
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (etName.getText().toString().matches("") || etMobile.getText().toString().matches("") || etEmail.getText().toString().matches("")) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(CustomerRegistrationActivity.this,R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
        } else if (!(Patterns.PHONE.matcher(etMobile.getText().toString()).matches()) || !(etMobile.getText().toString().matches(mobilePattern))) {
            progressBar.setVisibility(View.GONE);
            CustomDialog dialog = new CustomDialog(CustomerRegistrationActivity.this, R.string.msg_invalid_mobile_number);
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) || !(etEmail.getText().toString().matches(emailPattern))) {
            progressBar.setVisibility(View.GONE);
            CustomDialog dialog = new CustomDialog(CustomerRegistrationActivity.this, R.string.msg_invalid_email);
        } else {
            saveInfo();
        }
    }

    private void saveInfo() {
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_reg_user), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("already")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_already_registered, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        ArrayList aList= new ArrayList(Arrays.asList(city.get(spCity.getSelectedItemPosition()).split(", ")));
                        SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("NAME", etName.getText().toString());
                        editor.putString("MOBILE", etMobile.getText().toString());
                        editor.putString("EMAIL", etEmail.getText().toString());
                        editor.putString("CITY", aList.get(0).toString());
                        editor.putString("STATE", aList.get(1).toString());
                        editor.putString("STATUS","ALREADY REGISTERED");
                        editor.putString("USER_TYPE", "APP USER");
                        editor.apply();
                        startActivity(new Intent(CustomerRegistrationActivity.this, HomeActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(CustomerRegistrationActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerRegistrationActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("name", etName.getText().toString());
                    parameters.put("mobile", etMobile.getText().toString());
                    parameters.put("email", etEmail.getText().toString());
                    parameters.put("city", city.get(spCity.getSelectedItemPosition()));
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(CustomerRegistrationActivity.this,R.string.msg_check_internet_connection);
        }
    }

    private void loadSpinner() {
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
                    Toast.makeText(CustomerRegistrationActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CustomerRegistrationActivity.this, R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
            });
            requestQueue1.add(request1);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(CustomerRegistrationActivity.this,R.string.msg_check_internet_connection);
        }
    }

    private AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
            Typeface typeface = ResourcesCompat.getFont(CustomerRegistrationActivity.this,R.font.abeezee);
            ((TextView) parent.getChildAt(0)).setTypeface(typeface);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
}