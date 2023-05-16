package com.webandappdevelopment.serviceshop;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.webandappdevelopment.serviceshop.databinding.ActivityHomeBinding;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;
    FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_explore, R.id.navigation_about, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        NavigationUI.setupWithNavController(binding.navView, navController);


        int currentVersionCode;

        currentVersionCode = getCurrentVersionCode();

        Log.d("myApp", String.valueOf(currentVersionCode));

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10) // change to 3600 on published app
                .build();


        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        Log.d("Remote", String.valueOf(mFirebaseRemoteConfig));
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {
                    final String new_version_code = mFirebaseRemoteConfig.getString("new_version_code");

                    if(Integer.parseInt(new_version_code) > getCurrentVersionCode())
                        showUpdateDialog();
                }
                else Log.e("MYLOG", "mFirebaseRemoteConfig.fetchAndActivate() NOT Successful");
            }
        });



    }


    private void showUpdateDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("New Update Available")
                .setMessage("Update Now")
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.webandappdevelopment.serviceshop")));
                        }catch (Exception e){
                            Toast.makeText(getApplicationContext(), "Something went wrong. Try again later", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .show();
        dialog.setCancelable(false);
    }


    private int getCurrentVersionCode(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(),0);
        }catch (Exception e){
            Log.d("myApp", e.getMessage());
        }

        return packageInfo.versionCode;
    }

}