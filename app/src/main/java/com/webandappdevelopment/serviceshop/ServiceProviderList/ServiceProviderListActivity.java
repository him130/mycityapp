package com.webandappdevelopment.serviceshop.ServiceProviderList;

import static com.android.volley.Request.Method.POST;
import static com.webandappdevelopment.serviceshop.R.string.api_area_list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.ServiceProviderList.Slider.SliderAdapter;
import com.webandappdevelopment.serviceshop.ServiceProviderList.Slider.SliderItem;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class ServiceProviderListActivity extends AppCompatActivity implements ServiceProviderListAdapter.ItemClickListener{
    private RecyclerView recyclerView;
    private List<ServiceProviderItem> listItems;
    private ServiceProviderListAdapter adapter;
    private ServiceProviderListAdapter.ItemClickListener clickListener;
    private TextView tvTitle, tvNoServiceProvider;
    ImageView ivNoServiceProvider;
    InternetConnectionDetector icd;
    RequestQueue requestQueue, requestQueueSlider;
    StringRequest request, requestSlider, request1;
    int idArray[], cityidArray[];
    String imageArray[],titleArray[], ServiceListArray[], RatingArray[], DaysArray[], ContactArray[], serviceArray[];
    String areaArray[];
    String stTitle;
    ViewPager vpSlider;
    LinearLayout llSliderDots;
    TextView[] tvDots;
    int NUM_PAGES = 0;
    int currentPage = 0;
    Timer timer;
    final long DELAY_MS = 5000;
    final long PERIOD_MS = 5000;
    SliderAdapter sliderAdapter;
    int sliderIddArray[];
    String sliderImageArray[];
    List<SliderItem> sliderListItems;
    List<String> area = new ArrayList<>();
    ProgressBar progressBar;
    int area_id;
    RequestQueue requestQueue1;
    AppCompatSpinner sArea;
    int aidArray[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_list);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listItems = new ArrayList<>();
        Bundle extra  = getIntent().getExtras();
        stTitle = extra.getString("Title");
        tvTitle = findViewById(R.id.serviceProviderList_tvTitle);
        tvTitle.setText(stTitle);
        setUpUI();

    }

    private void setUpUI(){


        sArea = findViewById(R.id.fragment_spinnerarea);
        sArea.setOnItemSelectedListener(areaListener);
        loadArea();


        vpSlider = findViewById(R.id.serviceProviderList_vpSlider);
        llSliderDots = findViewById(R.id.serviceProviderList_llSliderDots);
        loadSlider();
        addDots(0);
        vpSlider.addOnPageChangeListener(viewListener);
        final Handler handler = new Handler();
        final Runnable Update = () -> {
            if (currentPage == NUM_PAGES) {
                currentPage = 0;
            }
            vpSlider.setCurrentItem(currentPage++, true);
        };
        timer = new Timer();
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        clickListener = this;
        tvNoServiceProvider = findViewById(R.id.serviceProviderList_tvNoServiceProvider);
        ivNoServiceProvider = findViewById(R.id.serviceProviderList_ivNoServiceProvider);
        recyclerView = findViewById(R.id.serviceProviderList_rvList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(ServiceProviderListActivity.this));
        progressBar = findViewById(R.id.serviceProviderList_progressBar);
        /*loadServiceProviderList();*/


    }
    private void loadArea(){
        icd = new InternetConnectionDetector(this.getApplicationContext());
        if(icd.isConnected()) {
            requestQueue1 = Volley.newRequestQueue(this.getApplicationContext());
            requestQueue1.getCache().clear();
            request1 = new StringRequest(POST, getString(api_area_list), response -> {


                try {

                   /*area.clear();*/

                    JSONArray array = new JSONArray(response);
                    aidArray = new int[array.length()];
                    idArray = new int[array.length()];
                    areaArray = new String[array.length()];

                    for (int i=0; i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        aidArray[i] = object.getInt("area_id");
                        idArray[i] = object.getInt("city_id");
                        areaArray[i] = object.getString("area_name");
                    }
                    for (int x=0 ; x<array.length() ; x++){
                        area.add(areaArray[x]);
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(this.getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,area);
                    sArea.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();

                    Toast.makeText(this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String city = sharedPreferences.getString("CITY", "Raipur");
                    String state = sharedPreferences.getString("STATE", "Chhattisgarh");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("city", city);
                    parameters.put("state", state);
                    return parameters;
                }
            };
            requestQueue1.add(request1);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(getApplicationContext(),R.string.msg_check_internet_connection);
        }
    }

    private void loadServiceProviderList(){
        /*progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(ServiceProviderListActivity.this);
        if (icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(ServiceProviderListActivity.this);
            requestQueue.getCache().clear();
            request = new StringRequest(Request.Method.POST, getString(R.string.api_sp_list), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        *//*Log.d("api response" + req);*//*
                        *//*System.out.println("api response : "+ getString(R.string.api_sp_list));*//*
                        progressBar.setVisibility(View.GONE);
                        JSONArray array = new JSONArray(response);
                        tvNoServiceProvider.setVisibility(View.GONE);
                        ivNoServiceProvider.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        idArray = new int[array.length()];
                        cityidArray = new int[array.length()];
                        imageArray = new String[array.length()];
                        titleArray = new String[array.length()];
                        ContactArray = new String[array.length()];
                        ServiceListArray = new String[array.length()];
                        DaysArray = new String[array.length()];
                        for (int i=0; i<array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);

                            idArray[i] = object.getInt("sp_id");
                            cityidArray[i] = object.getInt("mycity_id");
                            imageArray[i] = object.getString("image_url");
                            titleArray[i] = object.getString("full_name");
                            ContactArray[i] = object.getString("mobile_number");
                            DaysArray[i] = calculateDays(object.getString("reg_date"));
                            getServices(i, object.getInt("sp_id"));
                        }
                        listItems = new ArrayList<>();
                        for (int x=0; x<array.length(); x++) {
                            ServiceProviderItem item = new ServiceProviderItem(idArray[x],cityidArray[x],imageArray[x],titleArray[x],ServiceListArray[x],ContactArray[x],DaysArray[x]);
                            listItems.add(item);
                        }
                        adapter = new ServiceProviderListAdapter(ServiceProviderListActivity.this, listItems, clickListener);
                        recyclerView.setAdapter(adapter);
                        *//*System.out.println("api response : "+ getString(R.string.api_sp_list));*//*
                    } catch (JSONException | ParseException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ServiceProviderListActivity.this, R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String city = sharedPreferences.getString("CITY", "Raipur");
                    String state = sharedPreferences.getString("STATE", "Chhattisgarh");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("city", city);
                    parameters.put("state", state);
                    parameters.put("category", stTitle);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceProviderListActivity.this,R.string.msg_check_internet_connection);
        }*/
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(ServiceProviderListActivity.this);
        if (icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(ServiceProviderListActivity.this);
            requestQueue.getCache().clear();
            request = new StringRequest(Request.Method.POST, getString(R.string.api_sp_list2), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {


                    try {
                        listItems.clear();
                        System.out.println("Null or not" + listItems);

                        progressBar.setVisibility(View.GONE);
                        JSONArray array = new JSONArray(response);
                        tvNoServiceProvider.setVisibility(View.GONE);
                        ivNoServiceProvider.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        idArray = new int[array.length()];
                        cityidArray = new int[array.length()];
                        imageArray = new String[array.length()];
                        titleArray = new String[array.length()];
                        ContactArray = new String[array.length()];
                        ServiceListArray = new String[array.length()];
                        DaysArray = new String[array.length()];
                        for (int i=0; i<array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            idArray[i] = object.getInt("sp_id");
                            cityidArray[i] = object.getInt("mycity_id");
                            imageArray[i] = object.getString("image_url");
                            titleArray[i] = object.getString("full_name");
                            ContactArray[i] = object.getString("mobile_number");
                            DaysArray[i] = calculateDays(object.getString("reg_date"));
                            getServices(i, object.getInt("sp_id"));
                        }

                        for (int x=0; x<array.length(); x++) {
                            ServiceProviderItem item = new ServiceProviderItem(idArray[x],cityidArray[x],imageArray[x],titleArray[x],ServiceListArray[x],ContactArray[x],DaysArray[x]);
                            listItems.add(item);
                        }

                        adapter = new ServiceProviderListAdapter(ServiceProviderListActivity.this, listItems, clickListener);
                        recyclerView.setAdapter(adapter);

                    } catch (JSONException | ParseException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(ServiceProviderListActivity.this, R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                    
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String city = sharedPreferences.getString("CITY", "Raipur");
                    String state = sharedPreferences.getString("STATE", "Chhattisgarh");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("area_id", String.valueOf(area_id));
                    /*parameters.put("state", state);*/
                    parameters.put("category", stTitle);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceProviderListActivity.this,R.string.msg_check_internet_connection);
        }
    }

    private void getServices(int position, int spId){

        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(ServiceProviderListActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_service_list), response -> {
                try {
                    progressBar.setVisibility(View.GONE);

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
            }, error -> {
                progressBar.setVisibility(View.GONE);
            }) {
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
            CustomDialog scd = new CustomDialog(ServiceProviderListActivity.this, R.string.msg_check_internet_connection);
        }
    }

    @Override
    public void onItemClick(ServiceProviderItem item) {
        Intent i = new Intent(ServiceProviderListActivity.this, ServiceProviderDetailsActivity.class);
        i.putExtra("Id",item.getId());
        i.putExtra("url", item.getImage());
        startActivity(i);
    }

    private void loadSlider() {
        icd = new InternetConnectionDetector(ServiceProviderListActivity.this);
        if (icd.isConnected()){
            requestQueueSlider = Volley.newRequestQueue(ServiceProviderListActivity.this);
            requestQueueSlider.getCache().clear();
            requestSlider = new StringRequest(Request.Method.POST, getString(R.string.api_random_ads), response -> {
                try {
                    JSONArray array = new JSONArray(response);
                    NUM_PAGES = array.length();
                    sliderIddArray = new int[array.length()];
                    sliderImageArray = new String[array.length()];
                    for (int i=0; i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        sliderIddArray[i] = object.getInt("ad_id");
                        sliderImageArray[i] = object.getString("ad_url");
                    }
                    sliderListItems = new ArrayList<>();
                    for (int x=0 ; x<array.length() ; x++){
                        SliderItem item = new SliderItem(sliderIddArray[x],sliderImageArray[x]);
                        sliderListItems.add(item);
                    }
                    sliderAdapter = new SliderAdapter(ServiceProviderListActivity.this, sliderListItems);
                    vpSlider.setAdapter(sliderAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                CustomDialog scd = new CustomDialog(ServiceProviderListActivity.this,R.string.msg_something_went_wrong);
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String city = sharedPreferences.getString("CITY", "");
                    String state = sharedPreferences.getString("STATE", "");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("city", city);
                    parameters.put("state", state);
                    return parameters;
                }
            };
            requestQueueSlider.add(requestSlider);
        } else {
            CustomDialog scd = new CustomDialog(ServiceProviderListActivity.this,R.string.msg_check_internet_connection);
        }
    }

    private void addDots(int position) {
        tvDots = new TextView[NUM_PAGES];
        llSliderDots.removeAllViews();
        Typeface typeface = ResourcesCompat.getFont(ServiceProviderListActivity.this, R.font.abeezee);
        for(int i=0; i<tvDots.length; i++) {
            tvDots[i] = new TextView(ServiceProviderListActivity.this);
            tvDots[i].setTypeface(typeface);
            tvDots[i].setText(Html.fromHtml("&#8226;"));
            tvDots[i].setTextSize(25);
            tvDots[i].setTextColor(getResources().getColor(R.color.theme_red));
            llSliderDots.addView(tvDots[i]);
        }
        if (tvDots.length > 0) {
            tvDots[position].setTextColor(Color.GRAY);
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }
        @Override
        public void onPageSelected(int position) {
            //addDots(position);
        }
        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };
    private AdapterView.OnItemSelectedListener areaListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
            Typeface typeface = ResourcesCompat.getFont(ServiceProviderListActivity.this,R.font.abeezee);
            ((TextView) parent.getChildAt(0)).setTypeface(typeface);

            area_id = aidArray[position];
            loadServiceProviderList();


        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    private String calculateDays(String regDate) throws ParseException {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        Date startDate = formatter.parse(regDate);
        long diff = todayDate.getTime() - startDate.getTime();
        return String.valueOf(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS))+" Days";
    }
}