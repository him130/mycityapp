package com.webandappdevelopment.serviceshop.ServiceProviderList;

import static com.android.volley.Request.Method.POST;
import static com.webandappdevelopment.serviceshop.R.string.api_load_city;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.res.ResourcesCompat;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.ServiceHistory.ServiceHistoryActivity;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookNowActivity extends AppCompatActivity {
    EditText etName, etMobile, etAddress, etService, etDate;
    AppCompatButton btnSubmit;
    ProgressBar progressBar;
    InternetConnectionDetector icd;
    RequestQueue requestQueue, requestQueue1;
    StringRequest request, request1;
    String stId, stPreferredDate;
    int myYear, myMonth, myDate;
    AppCompatSpinner spCity;
    List<String> city = new ArrayList<>();
    int idArray[];
    String cityArray[], stateArray[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_now);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpUI();
    }

    private void setUpUI() {
        /*Bundle extra  = getIntent().getExtras();
        stId = String.valueOf(extra.getInt("spId"));*/

        Bundle extra = getIntent().getExtras();
        if (extra!=null){
            stId = extra.getString("SPID");
            System.out.println("The existing stid : " + stId);
        }


        progressBar = findViewById(R.id.bookNow_progressBar);
        etName = findViewById(R.id.bookNow_etName);
        etMobile = findViewById(R.id.bookNow_etMobile);
        etAddress = findViewById(R.id.bookNow_etAddress);
        spCity = findViewById(R.id.bookNow_spinnerCity);
        spCity.setOnItemSelectedListener(cityListener);
        loadSpinner();
        etService = findViewById(R.id.bookNow_etServiceRequest);
        Calendar calendar = Calendar.getInstance();
        etDate = findViewById(R.id.bookNow_etDate);
        etDate.setOnClickListener(view -> {
            myYear = calendar.get(Calendar.YEAR);
            myMonth = calendar.get(Calendar.MONTH);
            myDate = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(BookNowActivity.this, (datePicker, year, month, dayOfMonth) -> {
                etDate.setText(String.valueOf(dayOfMonth)+"/"+String.valueOf(month+1)+"/"+String.valueOf(year));
                stPreferredDate = String.valueOf(year)+"-"+String.valueOf(month+1)+"-"+String.valueOf(dayOfMonth);
            },myYear,myMonth,myDate);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });


        btnSubmit = findViewById(R.id.bookNow_btnUpdate);
        btnSubmit.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            checkInfo();

        });
    }

    private void checkInfo() {
        String mobilePattern = "[6-9][0-9]{9}";
        if (etName.getText().toString().matches("") || etMobile.getText().toString().matches("") || etAddress.getText().toString().matches("")
                || etService.getText().toString().matches("")) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(BookNowActivity.this,R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
        } else if (!(Patterns.PHONE.matcher(etMobile.getText().toString()).matches()) || !(etMobile.getText().toString().matches(mobilePattern))) {
            progressBar.setVisibility(View.GONE);
            CustomDialog dialog = new CustomDialog(BookNowActivity.this, R.string.msg_invalid_mobile_number);
        } else {
            saveInfo();
        }
    }

    private void saveInfo() {

        icd = new InternetConnectionDetector(this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(Request.Method.POST, getString(R.string.api_add_booking), response -> {

                try {
                    progressBar.setVisibility(View.GONE);
                    System.out.println("Response is : "+response);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_enter_all_details, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("already")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_already_registered, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(getApplicationContext(), "Request Submitted Successfully.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(BookNowActivity.this, ServiceHistoryActivity.class));
                        finish();
                     }
                } catch (Exception e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(BookNowActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BookNowActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sp = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);

                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("name", etName.getText().toString());
                    parameters.put("mobile", etMobile.getText().toString());
                    parameters.put("address", etAddress.getText().toString());
                    parameters.put("city", cityArray[spCity.getSelectedItemPosition()]);
                    parameters.put("state", stateArray[spCity.getSelectedItemPosition()]);
                    parameters.put("service", etService.getText().toString());
                    parameters.put("preferredDate", stPreferredDate);
                    parameters.put("user_id", sp.getString("USER_ID",""));
                    parameters.put("sp_id", stId);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(BookNowActivity.this,R.string.msg_check_internet_connection);
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
                    Toast.makeText(BookNowActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BookNowActivity.this, R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
            });
            requestQueue1.add(request1);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(BookNowActivity.this,R.string.msg_check_internet_connection);
        }
    }

    private AdapterView.OnItemSelectedListener cityListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
            Typeface typeface = ResourcesCompat.getFont(BookNowActivity.this,R.font.abeezee);
            ((TextView) parent.getChildAt(0)).setTypeface(typeface);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };
}