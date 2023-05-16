package com.webandappdevelopment.serviceshop.about;

import static android.content.Context.MODE_PRIVATE;

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
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.Utility.CustomDialog;
import com.webandappdevelopment.serviceshop.Utility.InternetConnectionDetector;
import com.webandappdevelopment.serviceshop.databinding.FragmentAboutBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AboutFragment extends Fragment {
    private FragmentAboutBinding binding;
    private RecyclerView recyclerView;
    private AboutListAdapter adapter;
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    ProgressBar progressBar;
    int idArray[];
    String urlArray[],titleArray[];
    List<AboutItem> aboutItems = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setUpUI(root);
        return root;
    }

    public void setUpUI(View view){
        icd = new InternetConnectionDetector(getActivity());
        progressBar = view.findViewById(R.id.fragmentAbout_progressBar);
        recyclerView = view.findViewById(R.id.fragmentAbout_rvList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        loadAboutItem();
    }

    public void loadAboutItem() {
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(getActivity().getApplicationContext());
        if (icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(Request.Method.POST, getString(R.string.api_know_us), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONArray array = new JSONArray(response);
                    idArray = new int[array.length()];
                    urlArray = new String[array.length()];
                    titleArray = new String[array.length()];
                    for (int i = 0; i<array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        idArray[i] = object.getInt("content_id");
                        urlArray[i] = object.getString("content_url");
                        titleArray[i] = object.getString("title");
                    }
                    for (int x=0; x<array.length(); x++) {
                        AboutItem item = new AboutItem(idArray[x],urlArray[x],titleArray[x]);
                        aboutItems.add(item);
                    }
                    adapter = new AboutListAdapter(getContext(), aboutItems);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}