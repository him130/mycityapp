package com.webandappdevelopment.serviceshop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnlargeVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnlargeVideoFragment extends Fragment {


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    YouTubePlayerView youTubePlayerView;

    public EnlargeVideoFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EnlargeVideoFragment newInstance(String param1, String param2) {
        EnlargeVideoFragment fragment = new EnlargeVideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_enlarge_video, container, false);



        return view;

    }
}