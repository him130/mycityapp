package com.webandappdevelopment.serviceshop.ServiceHistory;

import static com.android.volley.Request.Method.POST;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class ServiceHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private List<ServiceHistoryItem> listItems;
    private ServiceHistoryListAdapter adapter;
    private ServiceHistoryListAdapter.ItemClickListener clickListener;
    ProgressBar progressBar;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    int idArray[];
    String spNameArray[], contactArray[], serviceRequired[], addressArray[], dateArray[], monthArray[], categoryArray[], statusArray[];
    TextView tvNoRequestMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_history);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpUI();
    }
    public void setUpUI(){
        clickListener = this::onItemClick;
        tvNoRequestMsg = findViewById(R.id.serviceHistory_tvNoRequestMsg);
        progressBar = findViewById(R.id.serviceHistory_progressBar);
        icd = new InternetConnectionDetector(ServiceHistoryActivity.this);
        recyclerView = findViewById(R.id.serviceHistory_rvList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ServiceHistoryActivity.this));
        loadServiceRequestList();
    }

    private void loadServiceRequestList() {
        progressBar.setVisibility(View.VISIBLE);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_my_request), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    tvNoRequestMsg.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    JSONArray array = new JSONArray(response);
                    idArray = new int[array.length()];
                    spNameArray = new String[array.length()];
                    contactArray = new String[array.length()];
                    serviceRequired = new String[array.length()];
                    addressArray = new String[array.length()];
                    dateArray = new String[array.length()];
                    monthArray = new String[array.length()];
                    categoryArray = new String[array.length()];
                    statusArray = new String[array.length()];
                    for (int i=0; i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        idArray[i] = object.getInt("request_id");
                        spNameArray[i] = object.getString("full_name");
                        contactArray[i] = object.getString("mobile_number");
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
                        categoryArray[i] = object.getString("category");
                        statusArray[i] = object.getString("request_status");
                    }
                    listItems = new ArrayList<>();
                    for (int x=0 ; x<array.length() ; x++){
                        ServiceHistoryItem item = new ServiceHistoryItem(idArray[x],spNameArray[x],addressArray[x],serviceRequired[x],dateArray[x],monthArray[x],contactArray[x],categoryArray[x],statusArray[x]);
                        listItems.add(item);
                    }
                    adapter = new ServiceHistoryListAdapter(ServiceHistoryActivity.this, listItems, clickListener);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ServiceHistoryActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceHistoryActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String userId = sharedPreferences.getString("USER_ID", "");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("id", userId);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceHistoryActivity.this,R.string.msg_check_internet_connection);
        }
    }

    //cancel service request on cancel button click
    public void onItemClick(ServiceHistoryItem item) {
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
                    Toast.makeText(ServiceHistoryActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceHistoryActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("id", String.valueOf(item.getId()));
                    parameters.put("status", "Cancel");
                    return parameters;
                }
            };
            requestQueue.add(request);
        }
        else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceHistoryActivity.this,R.string.msg_check_internet_connection);
        }
    }

   /* public void onItemClickProfile(ServiceHistoryItem item){
        Intent i = new Intent(getApplicationContext(), ServiceProviderDetailsActivity.class);
        i.putExtra("Id",item.getId());
        startActivity(i);
    }*/

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