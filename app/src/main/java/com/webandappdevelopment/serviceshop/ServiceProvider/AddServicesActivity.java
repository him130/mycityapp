package com.webandappdevelopment.serviceshop.ServiceProvider;

import static com.android.volley.Request.Method.POST;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class AddServicesActivity extends AppCompatActivity {
    LinearLayout layoutList, serviceList;
    AppCompatButton btnAdd, btnUpdate;
    int idArray[];
    String servicesArray[];
    InternetConnectionDetector icd;
    RequestQueue requestQueue;
    StringRequest request;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_services);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        layoutList = findViewById(R.id.addCategory_llCategoryList);
        serviceList = findViewById(R.id.addCategory_llServiceList);
        btnAdd = findViewById(R.id.addCategory_btnAdd);
        btnAdd.setOnClickListener(view -> addView());

        progressBar = findViewById(R.id.addCategory_progressBar);
        btnUpdate = findViewById(R.id.addCategory_btnUpdate);
        btnUpdate.setOnClickListener(view -> {
            if (checkIfValidAndRead()){
                progressBar.setVisibility(View.VISIBLE);
                for (int i=0; i<servicesArray.length; i++) {
                    updateServices(i);
                }
            }
        });
        getServices();
    }

    private void addView() {
        final View addRow = getLayoutInflater().inflate(R.layout.row_add_category,null,false);
        EditText etName = addRow.findViewById(R.id.row_etName);
        ImageView ivRemove = addRow.findViewById(R.id.row_ivDelete);
        ivRemove.setOnClickListener(view -> removeView(addRow));
        layoutList.addView(addRow);
    }
    private void removeView(View view) {
        layoutList.removeView(view);
    }

    private boolean checkIfValidAndRead() {
        boolean result = true;
        servicesArray = new String[layoutList.getChildCount()];
        for (int i=0; i<layoutList.getChildCount(); i++){
            View addRow = layoutList.getChildAt(i);
            EditText etItemName = addRow.findViewById(R.id.row_etName);
            if (!etItemName.getText().toString().matches("")){
                servicesArray[i] = etItemName.getText().toString();
            } else {
                result = false;
                break;
            }
        }
        return result;
    }

    private void updateServices(int index){
        icd = new InternetConnectionDetector(AddServicesActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_add_services), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(AddServicesActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddServicesActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("services", servicesArray[index]);
                    parameters.put("sp_id", sharedPreferences.getString("SP_ID",""));
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(AddServicesActivity.this, R.string.msg_check_internet_connection);
        }
    }

    private void getServices(){
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(AddServicesActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_service_list), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONArray array = new JSONArray(response);
                    for (int i=0; i<array.length();i++) {
                        JSONObject object = array.getJSONObject(i);
                        showView(object.getInt("service_id"), object.getString("service"));
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(AddServicesActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddServicesActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("sp_id", sharedPreferences.getString("SP_ID",""));
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(AddServicesActivity.this, R.string.msg_check_internet_connection);
        }
    }

    private void showView(int id, String services) {
        final View showRow = getLayoutInflater().inflate(R.layout.row_category,null,false);
        TextView tvService = showRow.findViewById(R.id.row_tvService);
        tvService.setText(services);
        ImageView ivServiceRemove = showRow.findViewById(R.id.row_ivServiceDelete);
        ivServiceRemove.setOnClickListener(view -> deleteView(showRow, id));
        serviceList.addView(showRow);
    }
    private void deleteView(View view, int id) {
        progressBar.setVisibility(View.VISIBLE);
        icd = new InternetConnectionDetector(AddServicesActivity.this);
        if(icd.isConnected()) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            request = new StringRequest(POST, getString(R.string.api_delete_service), response -> {
                try {
                    progressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.names().get(0).equals("error")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_something_went_wrong, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), R.string.msg_server_not_responding, Toast.LENGTH_LONG).show();
                    } else if (jsonObject.names().get(0).equals("success")) {
                        serviceList.removeView(view);
                    }
                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();
                    Toast.makeText(AddServicesActivity.this,R.string.msg_server_not_responding,Toast.LENGTH_LONG).show();
                }
            }, error -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddServicesActivity.this,R.string.msg_something_went_wrong,Toast.LENGTH_LONG).show();
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    SharedPreferences sharedPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
                    Map<String,String> parameters = new HashMap<>();
                    parameters.put("serviceId", String.valueOf(id));
                    return parameters;
                }
            };
            requestQueue.add(request);
        } else {
            progressBar.setVisibility(View.GONE);
            CustomDialog scd = new CustomDialog(AddServicesActivity.this, R.string.msg_check_internet_connection);
        }
    }
}