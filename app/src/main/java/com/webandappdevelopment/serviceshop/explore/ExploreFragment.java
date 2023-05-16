package com.webandappdevelopment.serviceshop.explore;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.webandappdevelopment.serviceshop.EnlargeVideoFragment;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.ServiceProviderList.ServiceProviderDetailsActivity;
import com.webandappdevelopment.serviceshop.ServiceProviderList.Slider.SliderItem;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;
import com.webandappdevelopment.serviceshop.databinding.FragmentExploreBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreFragment extends Fragment implements ExploreListAdapter.ItemClickListener{
    private FragmentExploreBinding binding;
    private RecyclerView recyclerView;
    private List<ExploreItem> listItems;
    private List<SliderItem> adsItems;
    private ExploreListAdapter adapter;
    ExploreFragment eFragment;
    EnlargeVideoFragment enlargeVideoFragment;
    ExploreListAdapter.ItemClickListener clickListener;
    InternetConnectionDetector icd;
    RequestQueue requestQueue, requestQueue1;
    StringRequest request, request1;
    ProgressBar progressBar;
    int idArray[], spIdArray[], sliderIdArray[];
    String titleArray[], urlArray[], typeArray[],sliderImageArray[];
    ExploreFragment mFragment;
    androidx.fragment.app.FragmentTransaction mFragmentTransaction;
    androidx.fragment.app.FragmentManager mFragmentManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        icd = new InternetConnectionDetector(getActivity());
        progressBar = root.findViewById(R.id.fragmentExplore_progressBar);
        recyclerView = root.findViewById(R.id.fragmentExplore_rvList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        getAds();
        loadItem();
        clickListener = this;
        return root;
    }

    private void loadItem() {
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(getActivity().getApplicationContext());
        if (icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(Request.Method.POST, getString(R.string.api_my_city_list), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONArray array = new JSONArray(response);
                    idArray = new int[array.length()];
                    titleArray = new String[array.length()];
                    urlArray = new String[array.length()];
                    typeArray = new String[array.length()];
                    spIdArray = new int[array.length()];
                    for (int i = 0; i<array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        idArray[i] = object.getInt("content_id");
                        urlArray[i] = object.getString("content_url");
                        titleArray[i] = object.getString("title");
                        typeArray[i] = object.getString("type");
                        spIdArray[i] = object.getInt("sp_id");
                    }
                    listItems = new ArrayList<>();
                    for (int x=0; x<array.length(); x++) {
                        ExploreItem item = new ExploreItem(idArray[x],titleArray[x],urlArray[x],typeArray[x],spIdArray[x]);
                        listItems.add(item);
                    }
                    adapter = new ExploreListAdapter(getContext(), listItems, adsItems, clickListener);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(getActivity(),R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
            }){
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
    }

    @Override
    public void onItemClick(ExploreItem item) {
        Intent i = new Intent(getActivity().getApplicationContext(), ServiceProviderDetailsActivity.class);
        i.putExtra("Id",item.getSPId());
        startActivity(i);
    }


    private void getAds() {
        icd = new InternetConnectionDetector(getActivity().getApplicationContext());
        if (icd.isConnected()){
            requestQueue1 = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue1.getCache().clear();
            request1 = new StringRequest(Request.Method.POST, getString(R.string.api_random_ads), response -> {
                try {
                    JSONArray array = new JSONArray(response);
                    sliderIdArray = new int[array.length()];
                    sliderImageArray = new String[array.length()];
                    for (int i=0; i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        sliderIdArray[i] = object.getInt("ad_id");
                        sliderImageArray[i] = object.getString("ad_url");
                    }
                    adsItems = new ArrayList<>();
                    for (int x=0 ; x<array.length() ; x++){
                        SliderItem item = new SliderItem(sliderIdArray[x],sliderImageArray[x]);
                        adsItems.add(item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> {
                CustomDialog scd = new CustomDialog(getActivity().getApplicationContext(),R.string.msg_something_went_wrong);
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    String city = sharedPreferences.getString("CITY", "");
                    String state = sharedPreferences.getString("STATE", "");
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("city", city);
                    parameters.put("state", state);
                    return parameters;
                }
            };
            requestQueue1.add(request1);
        } else {
            CustomDialog scd = new CustomDialog(getActivity().getApplicationContext(),R.string.msg_check_internet_connection);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}