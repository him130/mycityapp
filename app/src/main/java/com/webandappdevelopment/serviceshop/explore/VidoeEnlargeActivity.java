package com.webandappdevelopment.serviceshop.explore;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.webandappdevelopment.serviceshop.R;

public class VidoeEnlargeActivity extends AppCompatActivity {


    YouTubePlayerView youTubePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vidoe_enlarge);

        youTubePlayerView = findViewById(R.id.rowEnlarge_youtubePlayer);


        try {

            Intent intent = getIntent();
            String youtubeurl = intent.getStringExtra("url");

            if (youtubeurl != null){
                youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                    @Override
                    public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                        youTubePlayer.cueVideo(youtubeurl,0);
                        /*youTubePlayer.setFullscreen(true);*/
                        youTubePlayer.pause();

                    }
                });
            }

        } catch(Exception e) {
            e.printStackTrace();
        }


    }
}