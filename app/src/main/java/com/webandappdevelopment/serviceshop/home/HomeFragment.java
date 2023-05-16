package com.webandappdevelopment.serviceshop.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.ServiceProviderList.ServiceProviderListActivity;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;
import com.webandappdevelopment.serviceshop.databinding.FragmentHomeBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment implements ServiceListAdapter.ItemClickListener{
    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private ServiceListAdapter adapter;
    private ServiceListAdapter.ItemClickListener clickListener;
    InternetConnectionDetector icd;
    RequestQueue requestQueue, requestQueue1;
    StringRequest request, request1;
    AppCompatSpinner sArea;
    List<String> area = new ArrayList<>();
    String areaArray[],cityArray[],stateArray[];
    ProgressBar progressBar;
    int aidArray[],cidArray[];
    String imageArray[],titleArray[];
    List<ServiceItem> serviceList = new ArrayList<>();
    AppCompatButton etSearch;
    int area_id;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        clickListener = this;
        setUpUI(root);
        return root;
    }

    private void setUpUI(View view){
        icd = new InternetConnectionDetector(getActivity());
        progressBar = view.findViewById(R.id.fragmentHome_progressBar);
        recyclerView = view.findViewById(R.id.fragmentHome_rvList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),2, GridLayoutManager.VERTICAL, false));
        loadServiceItem();
        etSearch = view.findViewById(R.id.fragmentHome_etSearch);
        etSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity().getApplicationContext(), SearchActivity.class);
                startActivity(i);
            }
        });

       /* sArea = view.findViewById(R.id.fragment_spinnerarea);
        sArea.setOnItemSelectedListener(areaListener);
        loadArea();*/

    }

    private void loadServiceItem() {
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(getActivity().getApplicationContext());
        if (icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(Request.Method.POST, getString(R.string.api_category_list), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        serviceList.clear();
                        progressBar.setVisibility(View.GONE);
                        JSONArray array = new JSONArray(response);
                        cidArray = new int[array.length()];
                        imageArray = new String[array.length()];
                        titleArray = new String[array.length()];
                        for (int i = 0; i<array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            cidArray[i] = object.getInt("category_id");
                            imageArray[i] = object.getString("img_url");
                            titleArray[i] = myStringFormatter(object.getString("category_name"));
                        }
                        for (int x=0; x<array.length(); x++) {
                            ServiceItem item = new ServiceItem(cidArray[x],imageArray[x],titleArray[x]);
                            serviceList.add(item);
                        }
                        adapter = new ServiceListAdapter(getContext(), serviceList, clickListener);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                        Toast.makeText(getActivity(),R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String city = sharedPreferences.getString("CITY", "Raipur");
                    String state = sharedPreferences.getString("STATE", "Chhattisgarh");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("city", city);
                    parameters.put("state", state);
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(getActivity(),R.string.msg_check_internet_connection);
        }
        /*progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(getActivity().getApplicationContext());
        if (icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(Request.Method.POST, getString(R.string.api_category_list2), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        serviceList.clear();
                        progressBar.setVisibility(View.GONE);
                        JSONArray array = new JSONArray(response);
                        cidArray = new int[array.length()];
                        imageArray = new String[array.length()];
                        titleArray = new String[array.length()];
                        for (int i = 0; i<array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            cidArray[i] = object.getInt("category_id");
                            imageArray[i] = object.getString("img_url");
                            titleArray[i] = myStringFormatter(object.getString("category_name"));
                        }
                        for (int x=0; x<array.length(); x++) {
                            ServiceItem item = new ServiceItem(cidArray[x],imageArray[x],titleArray[x]);
                            serviceList.add(item);
                        }
                        adapter = new ServiceListAdapter(getContext(), serviceList, clickListener);
                        recyclerView.setAdapter(adapter);
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                        Toast.makeText(getActivity(),R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String city = sharedPreferences.getString("CITY", "Raipur");
                    String state = sharedPreferences.getString("STATE", "Chhattisgarh");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("city", city);
                    parameters.put("state", state);
                    parameters.put("area_id", String.valueOf(area_id));
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(getActivity(),R.string.msg_check_internet_connection);
        }*/
        
    }

    /*private void loadArea(){
        icd = new InternetConnectionDetector(getActivity().getApplicationContext());
        if(icd.isConnected()) {
            requestQueue1 = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue1.getCache().clear();
            request1 = new StringRequest(POST, getString(api_area_list), response -> {
                try {

                    JSONArray array = new JSONArray(response);
                    aidArray = new int[array.length()];
                    cidArray = new int[array.length()];
                    areaArray = new String[array.length()];

                    for (int i=0; i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        aidArray[i] = object.getInt("area_id");
                        cidArray[i] = object.getInt("city_id");
                        areaArray[i] = object.getString("area_name");
                    }
                    for (int x=0 ; x<array.length() ; x++){
                        area.add(areaArray[x]);
                    }
                    ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity().getApplicationContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,area);
                    sArea.setAdapter(arrayAdapter);
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(getActivity(),R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
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
            CustomDialog scd = new CustomDialog(getActivity(),R.string.msg_check_internet_connection);
        }
    }*/

    /*private AdapterView.OnItemSelectedListener areaListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
            Typeface typeface = ResourcesCompat.getFont(getActivity(),R.font.abeezee);
            ((TextView) parent.getChildAt(0)).setTypeface(typeface);

            area_id = aidArray[position];
            *//*loadServiceItem();*//*

        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onItemClick(ServiceItem item) {
        Intent i = new Intent(getActivity().getApplicationContext(), ServiceProviderListActivity.class);
        i.putExtra("Title",item.getTitle());
        startActivity(i);
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
}