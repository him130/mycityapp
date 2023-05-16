package com.webandappdevelopment.serviceshop.profile;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.webandappdevelopment.serviceshop.MainActivity;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.ServiceHistory.ServiceHistoryActivity;
import com.webandappdevelopment.serviceshop.ServiceProvider.UploadDocumentActivity;
import com.webandappdevelopment.serviceshop.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private com.webandappdevelopment.serviceshop.databinding.FragmentProfileBinding binding;
    AppCompatButton btnServiceHistory, btnEditProfile, btnSupport, btnPrivacyPolicy, btnLogout;
    LinearLayout llServiceHistory, llEditProfile, llSupport, llPrivacyPolicy, llLogout, llServiceUpload;
    TextView tvName, tvContactDetails;
    ShapeableImageView ivProfileImg;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        setUpUI(root);
        return root;
    }

    private void setUpUI(View view) {
        SharedPreferences sp = getActivity().getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        String stProfileImg = sp.getString("PROFILE_IMAGE","");
        tvName = view.findViewById(R.id.profile_tvName);
        tvName.setText(sp.getString("NAME",""));
        tvContactDetails = view.findViewById(R.id.profile_tvContactDetails);
        tvContactDetails.setText(sp.getString("MOBILE","")+" | "+sp.getString("EMAIL",""));
        ivProfileImg = view.findViewById(R.id.profile_ivProfileImg);
        if (stProfileImg.matches("")) {
            ivProfileImg.setBackgroundResource(R.mipmap.ic_launcher_new);
        } else {
            Picasso.get().load(stProfileImg)
                    .placeholder(R.mipmap.ic_launcher_new)
                    .into(ivProfileImg);
        }

        // Service History
        llServiceHistory = view.findViewById(R.id.profile_llHistory);
        llServiceHistory.setOnClickListener(view1 -> startActivity(new Intent(getActivity(), ServiceHistoryActivity.class)));
        btnServiceHistory = view.findViewById(R.id.profile_btnHistory);
        btnServiceHistory.setOnClickListener(view12 -> startActivity(new Intent(getActivity(), ServiceHistoryActivity.class)));


        //Upload Images and Videos
        llServiceUpload = view.findViewById(R.id.profile_llUpload);
        llServiceUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UploadDocumentActivity.class));
            }
        });

        // Edit Profile
        llEditProfile = view.findViewById(R.id.profile_llEditProfile);
        llEditProfile.setOnClickListener(view13 -> startActivity(new Intent(getActivity(), EditUserProfileActivity.class)));
        btnEditProfile = view.findViewById(R.id.profile_btnEditProfile);
        btnEditProfile.setOnClickListener(view14 -> startActivity(new Intent(getActivity(), EditUserProfileActivity.class)));

        // Support
        llSupport = view.findViewById(R.id.profile_llSupport);
        llSupport.setOnClickListener(view15 -> startActivity(new Intent(getActivity(), SupportActivity.class)));
        btnSupport = view.findViewById(R.id.profile_btnSupport);
        btnSupport.setOnClickListener(view16 -> startActivity(new Intent(getActivity(), SupportActivity.class)));

        // Privacy Policy
        llPrivacyPolicy = view.findViewById(R.id.profile_llPrivacyPolicy);
        llPrivacyPolicy.setOnClickListener(view17 -> startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class)));
        btnPrivacyPolicy = view.findViewById(R.id.profile_btnPrivacyPolicy);
        btnPrivacyPolicy.setOnClickListener(view18 -> startActivity(new Intent(getActivity(), PrivacyPolicyActivity.class)));

        // Logout
        llLogout = view.findViewById(R.id.profile_llLogout);
        llLogout.setOnClickListener(view19 -> {
            SharedPreferences myPrefs = getActivity().getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.clear();
            editor.apply();
            Toast.makeText(getActivity(), R.string.msg_logout, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        });
        btnLogout = view.findViewById(R.id.profile_btnLogout);
        btnLogout.setOnClickListener(view110 -> {
            SharedPreferences myPrefs = getActivity().getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
            SharedPreferences.Editor editor = myPrefs.edit();
            editor.clear();
            editor.apply();
            Toast.makeText(getActivity(), R.string.msg_logout, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        String stName = sharedPreferences.getString("NAME", String.valueOf(R.string.app_name));
        String stMobile = sharedPreferences.getString("MOBILE", "");
        String stEmail = sharedPreferences.getString("EMAIL", "");
        String stProfileImg = sharedPreferences.getString("PROFILE_IMAGE","");
        tvName.setText(stName);
        tvContactDetails.setText(stMobile + " | " + stEmail);
        if (stProfileImg.matches("")) {
            ivProfileImg.setBackgroundResource(R.mipmap.ic_launcher_new);
        } else {
            Picasso.get().load(stProfileImg)
                    .placeholder(R.mipmap.ic_launcher_new)
                    .into(ivProfileImg);
        }
    }
}