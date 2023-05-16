package com.webandappdevelopment.serviceshop.ServiceProvider.ServiceArea;

import static com.android.volley.Request.Method.POST;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceAreaActivity extends AppCompatActivity {
    private List<String> list;
    private List<ServiceAreaItem> listItems;
    private ServiceAreaListAdapter adapter;
    ProgressBar progressBar;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    AppCompatSpinner spinnerItem;
    String areaArray[];
    AppCompatButton btnSubmit;
    LinearLayout llServiceArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_area);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpUI();
    }

    private void setUpUI(){
        llServiceArea = findViewById(R.id.serviceArea_llServiceArea);
        progressBar = findViewById(R.id.serviceArea_progressBar);
        spinnerItem = findViewById(R.id.serviceArea_spinnerArea);
        btnSubmit = findViewById(R.id.serviceArea_btnSubmit);
        btnSubmit.setOnClickListener(view -> addServiceArea());
        loadServiceArea();
        loadSpinner();
    }

    private void loadSpinner() {
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(ServiceAreaActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_area_list), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONArray array = new JSONArray(response);
                    areaArray = new String[array.length()];
                    list = new ArrayList<>();
                    for (int i=0; i<array.length();i++) {
                        JSONObject object = array.getJSONObject(i);
                        list.add(object.getString("area_name"));
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,list);
                    spinnerItem.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ServiceAreaActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceAreaActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String city = sharedPreferences.getString("CITY", "Raipur");
                    String state = sharedPreferences.getString("STATE", "Chhattisgarh");
                    String spId = sharedPreferences.getString("SP_ID", "");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("city", city);
                    parameters.put("state", state);
                    parameters.put("spId", spId);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceAreaActivity.this, R.string.msg_check_internet_connection);
        }
    }

    private void addServiceArea() {
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(ServiceAreaActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_add_area), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_server_not_responding, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_LONG).show();
                        loadServiceArea();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ServiceAreaActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceAreaActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String spId = sharedPreferences.getString("SP_ID", "");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("area", list.get(spinnerItem.getSelectedItemPosition()));
                    parameters.put("sp_id", spId);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceAreaActivity.this, R.string.msg_check_internet_connection);
        }
    }

    private void loadServiceArea(){
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(ServiceAreaActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_my_area), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONArray array = new JSONArray(response);
                    areaArray = new String[array.length()];
                    llServiceArea.removeAllViews();
                    for (int i=0; i<array.length();i++) {
                        JSONObject object = array.getJSONObject(i);
                        showView(object.getInt("area_id"),object.getString("area_name"));
                    }
                    loadSpinner();
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ServiceAreaActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceAreaActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String city = sharedPreferences.getString("CITY", "Raipur");
                    String state = sharedPreferences.getString("STATE", "Chhattisgarh");
                    String spId = sharedPreferences.getString("SP_ID", "");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("city", city);
                    parameters.put("state", state);
                    parameters.put("spId", spId);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceAreaActivity.this, R.string.msg_check_internet_connection);
        }
    }

    private void showView(int id, String services) {
        final View showRow = getLayoutInflater().inflate(R.layout.row_service_area,null,false);
        TextView tvService = showRow.findViewById(R.id.rowServiceArea_tvArea);
        tvService.setText(services);
        ImageView ivServiceRemove = showRow.findViewById(R.id.rowServiceArea_btnRemove);
        ivServiceRemove.setOnClickListener(view -> deleteView(showRow, id));
        llServiceArea.addView(showRow);
    }
    private void deleteView(View view, int id) {
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(ServiceAreaActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_delete_area), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_server_not_responding, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        llServiceArea.removeView(view);
                        loadSpinner();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ServiceAreaActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceAreaActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String spId = sharedPreferences.getString("SP_ID", "");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("areaId", String.valueOf(id));
                    parameters.put("spId", spId);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceAreaActivity.this, R.string.msg_check_internet_connection);
        }
    }
}