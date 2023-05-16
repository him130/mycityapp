package com.webandappdevelopment.serviceshop.ServiceProviderList;

import static com.android.volley.Request.Method.POST;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.ServiceProvider.ManagePhotos.FullSizeImageActivity;
import com.webandappdevelopment.serviceshop.ServiceProvider.ManagePhotos.PhotosItem;
import com.webandappdevelopment.serviceshop.ServiceProvider.ManagePhotos.PhotosListAdapter;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ServiceProviderDetailsActivity extends AppCompatActivity {
    ImageView ivImage;
    TextView tvMyCityId,tvName,tvAge,tvMaritalStatus,tvExperience,tvMyCityDays,tvContact,tvEmail,tvAddress,tvAbout;
    TextView tvServices, tvNoImage;
    Button btnCall,btnViewAllReview;
    ImageFragment mFragment;
    androidx.fragment.app.FragmentTransaction mFragmentTransaction;
    androidx.fragment.app.FragmentManager mFragmentManager;
    private Button btnBookNow;
    private Button btnCallNow;
    String spId, stContact, stServices="", serviceArray[];
    InternetConnectionDetector icd;
    RequestQueue requestQueue, requestQueue1;
    StringRequest request, request1;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    private List<PhotosItem> listItems;
    private PhotosListAdapter adapter;
    PhotosListAdapter.ItemClickListener clickListener;
    int idArray[];
    String urlArray[];
    private String img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_provider_details);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);


        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragment = new ImageFragment();


        setUpUI();
    }

    private void setUpUI() {
        ivImage = findViewById(R.id.rowServiceProviderDetails_ivImage);
        /*fragmentimg();*/
        tvMyCityId = findViewById(R.id.rowServiceProviderDetails_tvId);
        tvName = findViewById(R.id.rowServiceProviderDetails_tvName);
        tvAge = findViewById(R.id.rowServiceProviderDetails_tvAge);
        tvMaritalStatus = findViewById(R.id.rowServiceProviderDetails_tvMaritalStatus);
        tvExperience = findViewById(R.id.rowServiceProviderDetails_tvExperience);
        tvMyCityDays = findViewById(R.id.rowServiceProviderDetails_tvMyCityDays);
        tvContact = findViewById(R.id.rowServiceProviderDetails_tvContact);
        tvEmail = findViewById(R.id.rowServiceProviderDetails_tvEmail);
        tvAddress = findViewById(R.id.rowServiceProviderDetails_tvAddress);
        tvServices = findViewById(R.id.rowServiceProviderDetails_tvServices);
        tvAbout = findViewById(R.id.rowServiceProviderDetails_tvAbout);
        tvNoImage = findViewById(R.id.spDetails_tvNoImage);
        clickListener = this::onItemClick;
        recyclerView = findViewById(R.id.spDetails_rvImages);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(ServiceProviderDetailsActivity.this,3));
        /*btnCall = findViewById(R.id.rowServiceProviderDetails_btnCall);
        btnCall.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+ stContact));
            startActivity(intent);
        });*/

        btnViewAllReview = findViewById(R.id.rowServiceProviderDetails_btnViewAll);
        progressBar = findViewById(R.id.rowServiceProviderDetails_progressBar);

        Bundle extra = getIntent().getExtras();
        if (extra!=null){
            spId = String.valueOf(extra.getInt("Id"));
            String img = extra.getString("url");
            System.out.println("The existing spid : " + spId);
        }

        loadDetails();
        btnBookNow = findViewById(R.id.rowServiceProviderList_btnBookNow);
        btnCallNow = findViewById(R.id.rowServiceProviderList_btnCallNow);

        btnBookNow.setOnClickListener(view -> {
            Intent intent = new Intent(ServiceProviderDetailsActivity.this, BookNowActivity.class);
            // intent.putExtra("SPID", spId);
            Bundle bundle = new Bundle();
            bundle.putString("SPID", spId);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        btnCallNow.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+ stContact));
            startActivity(intent);
        });

        loadServices();
        loadPhotos();
    }

    /*private void fragmentimg() {

        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle mBundle = new Bundle();
                mBundle.putString("image", getString(Integer.parseInt("image_url")));
                mFragment.setArguments(mBundle);
                mFragmentTransaction
                        .add(R.id.frameLayout, mFragment)
                        .addToBackStack("close")
                        .commit();
            }
        });

    }*/

    private void loadDetails() {
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_sp_details), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_no_user, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_no_user, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("sp_id")) {
                        tvMyCityId.setText("ID: " + jsonObject.getString("mycity_id"));
                        if (jsonObject.getString("image_url").matches("")) {
                            ivImage.setBackgroundResource(R.drawable.ic_service_provider);
                        } else {
                            Picasso.get().load(jsonObject.getString("image_url"))
                                    .placeholder(R.drawable.ic_service_provider)
                                    .into(ivImage);
                        }
                        tvName.setText(myStringFormatter(jsonObject.getString("full_name")));
                        tvAge.setText(jsonObject.getString("age")+ " yrs old");
                        tvMaritalStatus.setText(jsonObject.getString("marital_status"));
                        tvExperience.setText("Adhaar : "+jsonObject.getString("experience"));
                        stContact = jsonObject.getString("mobile_number");
                        tvContact.setText(jsonObject.getString("mobile_number"));
                        tvEmail.setText(jsonObject.getString("email"));
                        tvAbout.setText(jsonObject.getString("about"));
                        tvAddress.setText(myStringFormatter(jsonObject.getString("address"))+", "+myStringFormatter(jsonObject.getString("city_name"))+", "+myStringFormatter(jsonObject.getString("state_name")));
                        tvMyCityDays.setText(calculateDays(jsonObject.getString("reg_date")));
                    }

                    ivImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle mBundle = new Bundle();
                            try {
                                mBundle.putString("image", jsonObject.getString("image_url"));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            mFragment.setArguments(mBundle);
                            mFragmentTransaction
                                    .add(R.id.frameLayout, mFragment)
                                    .addToBackStack("close")
                                    .commit();
                        }
                    });
                } catch (JSONException | ParseException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(ServiceProviderDetailsActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ServiceProviderDetailsActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("spId", spId);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceProviderDetailsActivity.this,R.string.msg_check_internet_connection);
        }
    }

    private void loadServices() {
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(this);
        if(icd.isConnected()) {
            requestQueue1 = Volley.newRequestQueue(getApplicationContext());
            requestQueue1.getCache().clear();
            request1 = new StringRequest(POST, getString(R.string.api_service_list), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONArray array = new JSONArray(response);
                    serviceArray = new String[array.length()];
                    for (int i = 0; i<array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        serviceArray[i] = object.getString("service");
                    }
                    //convert array to string
                    stServices = TextUtils.join(",", serviceArray);
                    tvServices.setText(myStringFormatter(stServices));
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("sp_id", spId);
                    return parameters;
                }
            };
            requestQueue1.add(request1);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceProviderDetailsActivity.this,R.string.msg_check_internet_connection);
        }
    }

    private void loadPhotos() {
        
        /*icd = new InternetConnectionDetector(this);
        progressBar.setVisibility(View.VISIBLE);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_my_photos), response -> {
                System.out.println("The response is : "+response);
                try {
                    progressBar.setVisibility(View.GONE);
                    tvNoImage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    JSONArray array = new JSONArray(response);
                    idArray = new int[array.length()];
                    urlArray = new String[array.length()];

                    for (int i=0; i<array.length();i++) {
                        JSONObject object = array.getJSONObject(i);
                        System.out.println("The new image of json object" + object.getString("img_url"));
                        idArray[i] = object.getInt("img_id");
                        urlArray[i] = object.getString("img_url");
                    }
                    listItems = new ArrayList<>();
                    for (int x=0 ; x<array.length() ; x++){
                        PhotosItem item = new PhotosItem(idArray[x],urlArray[x]);
                        listItems.add(item);
                    }
                    adapter = new PhotosListAdapter(ServiceProviderDetailsActivity.this, listItems, clickListener);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    tvNoImage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(this, "Something Wrong in api", Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                tvNoImage.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(ServiceProviderDetailsActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("spId", spId);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(ServiceProviderDetailsActivity.this,R.string.msg_check_internet_connection);
        }*/
    }

    public void onItemClick(PhotosItem item) {
        Intent i = new Intent(ServiceProviderDetailsActivity.this, FullSizeImageActivity.class);
        i.putExtra("url",item.getImageUrl());
        i.putExtra("id",item.getImageId());
        i.putExtra("status",false);
        startActivity(i);
        finish();
    }

    private String myStringFormatter(String myString) {
        String[] strArray = myString.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap + " ");
        }
        return builder.toString();
    }

    private String calculateDays(String regDate) throws ParseException {
        Date todayDate = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        Date startDate = formatter.parse(regDate);
        long diff = todayDate.getTime() - startDate.getTime();
        return "My City Days: "+TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)+" Days";
    }
}