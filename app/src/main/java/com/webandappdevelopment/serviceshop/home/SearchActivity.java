package com.webandappdevelopment.serviceshop.home;

import static com.android.volley.Request.Method.POST;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.webandappdevelopment.serviceshop.ServiceProviderList.ServiceProviderDetailsActivity;
import com.webandappdevelopment.serviceshop.ServiceProviderList.ServiceProviderItem;
import com.webandappdevelopment.serviceshop.ServiceProviderList.ServiceProviderListAdapter;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SearchActivity extends AppCompatActivity {private RecyclerView recyclerView;
    List<ServiceProviderItem> listItems;
    ServiceProviderListAdapter adapter;
    ServiceProviderListAdapter.ItemClickListener clickListener;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    int idArray[], cityidArray[];
    String imageArray[],titleArray[], ServiceListArray[], RatingArray[], DaysArray[], ContactArray[],serviceArray[];
    String stSearchText;
    EditText etSearch;
    ProgressBar progressBar;
    TextView tvNoResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpUI();
    }

    private void setUpUI() {
        etSearch = findViewById(R.id.search_etSearch);
        etSearch.setText(stSearchText);
        etSearch.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == EditorInfo.IME_ACTION_DONE) {
                fetchServices(etSearch.getText().toString());
                return true;
            }
            return false;
        });
        tvNoResult = findViewById(R.id.search_tvNoResult);
        progressBar = findViewById(R.id.search_progressBar);
        recyclerView = findViewById(R.id.search_rvList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        clickListener = this::onItemClick;
    }

    public void fetchServices(String stSearchText){
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(SearchActivity.this);
        if (icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(SearchActivity.this);
            requestQueue.getCache().clear();
            request = new StringRequest(Request.Method.POST, getString(R.string.api_search), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    tvNoResult.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    JSONArray array = new JSONArray(response);
                    idArray = new int[array.length()];
                    cityidArray = new int[array.length()];
                    imageArray = new String[array.length()];
                    titleArray = new String[array.length()];
                    ContactArray = new String[array.length()];
                    ServiceListArray = new String[array.length()];
                    DaysArray = new String[array.length()];
                    for (int i = 0; i<array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        getServices(i, object.getInt("sp_id"));
                        idArray[i] = object.getInt("sp_id");
                        cityidArray[i] = object.getInt("mycity_id");
                        imageArray[i] = object.getString("image_url");
                        titleArray[i] = object.getString("full_name");
                        ContactArray[i] = object.getString("mobile_number");
                        ServiceListArray[i] = "";
                        DaysArray[i] = calculateDays(object.getString("reg_date"));
                    }
                    listItems = new ArrayList<>();
                    for (int x=0; x<array.length(); x++) {
                        ServiceProviderItem item = new ServiceProviderItem(idArray[x],cityidArray[x],imageArray[x],titleArray[x],ServiceListArray[x],ContactArray[x],DaysArray[x]);
                        listItems.add(item);
                    }
                    adapter = new ServiceProviderListAdapter(SearchActivity.this, listItems, clickListener);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException | ParseException e) {
                    progressBar.setVisibility(View.GONE);
                    tvNoResult.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(SearchActivity.this, R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String city = sharedPreferences.getString("CITY", "Raipur");
                    String state = sharedPreferences.getString("STATE", "Chhattisgarh");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("city", city);
                    parameters.put("state", state);
                    parameters.put("searchText", stSearchText);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(SearchActivity.this,R.string.msg_check_internet_connection);
        }
    }

    private void getServices(int position, int spId){
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(SearchActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_service_list), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    String stServices="";
                    JSONArray array = new JSONArray(response);
                    serviceArray = new String[array.length()];
                    for (int i = 0; i<array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        serviceArray[i] = object.getString("service");
                    }
                    //convert array to string
                    ServiceListArray[position] = TextUtils.join(",", serviceArray);
                  } catch (JSONException e) {
                    ServiceListArray[position] ="No services registered yet";
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }, error -> progressBar.setVisibility(View.GONE)) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("sp_id", String.valueOf(spId));
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(SearchActivity.this, R.string.msg_check_internet_connection);
        }
    }

    public void onItemClick(ServiceProviderItem item) {
        Intent i = new Intent(SearchActivity.this, ServiceProviderDetailsActivity.class);;
        i.putExtra("Id",item.getId());
        startActivity(i);
    }

    private String calculateDays(String regDate) throws ParseException {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        Date startDate = formatter.parse(regDate);
        long diff = todayDate.getTime() - startDate.getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)+" Days";
    }
}