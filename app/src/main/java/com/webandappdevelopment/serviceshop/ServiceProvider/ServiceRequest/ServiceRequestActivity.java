package com.webandappdevelopment.serviceshop.ServiceProvider.ServiceRequest;

import static com.android.volley.Request.Method.POST;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceRequestActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ServiceRequestListAdapter adapter;
    ServiceRequestListAdapter.RejectClickListener RejectClickListener;
    ServiceRequestListAdapter.UpdateClickListener UpdateClickListener;
    ProgressBar progressBar;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    int idArray[];
    String customerNameArray[], contactArray[], serviceRequired[], addressArray[], dateArray[], monthArray[], statusArray[];
    List<ServiceRequestItem> requestItemList = new ArrayList<>();
    TextView tvNoRequestMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpUI();
    }

    public void setUpUI(){
        tvNoRequestMsg = findViewById(R.id.serviceRequest_tvNoRequestMsg);
        progressBar = findViewById(R.id.serviceRequest_progressBar);
        RejectClickListener = this::onRejectItemClick;
        UpdateClickListener = this::onUpdateItemClick;
        icd = new InternetConnectionDetector(ServiceRequestActivity.this);
        recyclerView = findViewById(R.id.serviceRequest_rvList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ServiceRequestActivity.this));
        loadServiceRequestList();
    }

    private void loadServiceRequestList() {
        progressBar.setVisibility(View.VISIBLE);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_service_request), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    tvNoRequestMsg.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    JSONArray array = new JSONArray(response);
                    idArray = new int[array.length()];
                    customerNameArray = new String[array.length()];
                    contactArray = new String[array.length()];
                    serviceRequired = new String[array.length()];
                    addressArray = new String[array.length()];
                    dateArray = new String[array.length()];
                    monthArray = new String[array.length()];
                    statusArray = new String[array.length()];
                    for (int i=0; i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        idArray[i] = object.getInt("request_id");
                        customerNameArray[i] = object.getString("customer_name");
                        contactArray[i] = object.getString("contact");
                        serviceRequired[i] = object.getString("service_required");
                        addressArray[i] = object.getString("customer_address");
                        dateArray[i] = formatDateFromString("yyyy-MM-dd", "dd", object.getString("appointment_date"));
                        String stMonth = formatDateFromString("yyyy-MM-dd", "M", object.getString("appointment_date"));
                        switch (stMonth) {
                            case "1": stMonth = "Jan"; break;
                            case "2": stMonth = "Feb"; break;
                            case "3": stMonth = "Mar"; break;
                            case "4": stMonth = "Apr"; break;
                            case "5": stMonth = "May"; break;
                            case "6": stMonth = "Jun"; break;
                            case "7": stMonth = "Jul"; break;
                            case "8": stMonth = "Aug"; break;
                            case "9": stMonth = "Sep"; break;
                            case "10": stMonth = "Oct"; break;
                            case "11": stMonth = "Nov"; break;
                            case "12": stMonth = "Dec"; break;
                        }
                        String stYear = formatDateFromString("yyyy-MM-dd", "yyyy", object.getString("appointment_date"));
                        monthArray[i] = stMonth+" "+stYear;
                        statusArray[i] = object.getString("request_status");
                    }
                    requestItemList = new ArrayList<>();
                    for (int x=0 ; x<array.length() ; x++){
                        ServiceRequestItem item = new ServiceRequestItem(idArray[x],customerNameArray[x],contactArray[x],serviceRequired[x],addressArray[x],dateArray[x],monthArray[x],statusArray[x]);
                        requestItemList.add(item);
                    }
                    adapter = new ServiceRequestListAdapter(ServiceRequestActivity.this, requestItemList,RejectClickListener,UpdateClickListener);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ServiceRequestActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceRequestActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String spId = sharedPreferences.getString("SP_ID", "0");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("id", spId);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceRequestActivity.this,R.string.msg_check_internet_connection);
        }
    }

    //reject service request on reject button click
    public void onRejectItemClick(ServiceRequestItem item) {
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_edit_status), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_update_fail, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_update_success, Toast.LENGTH_LONG).show();
                        loadServiceRequestList();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ServiceRequestActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceRequestActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("id", String.valueOf(item.getRequestId()));
                    parameters.put("status", "Rejected");
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceRequestActivity.this,R.string.msg_check_internet_connection);
        }
    }

    //update service request on update button click
    public void onUpdateItemClick(ServiceRequestItem item, String status) {
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_edit_status), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_update_fail, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_update_success, Toast.LENGTH_LONG).show();
                        loadServiceRequestList();
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ServiceRequestActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceRequestActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("id", String.valueOf(item.getRequestId()));
                    parameters.put("status", status);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceRequestActivity.this,R.string.msg_check_internet_connection);
        }
    }

    // Setting date format
    public static String formatDateFromString(String inputFormat, String outputFormat, String inputDate){
        Date parsed = null;
        String outputDate = "";
        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());
        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (ParseException e) {}
        return outputDate;
    }
}