package com.webandappdevelopment.serviceshop.about;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.webandappdevelopment.serviceshop.R;

import java.util.List;

public class AboutListAdapter extends RecyclerView.Adapter<AboutListAdapter.ViewHolder> {
    private Context context;
    private List<AboutItem> listItem;
    public AboutListAdapter(Context context, List<AboutItem> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_about_fragment, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AboutItem item = listItem.get(position);
        holder.tvTitle.setText(item.getTitle());
        if (!(item.getVideoUrl().matches(""))) {
            holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    youTubePlayer.cueVideo(item.getVideoUrl(),0);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvTitle;
        private YouTubePlayerView youTubePlayerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.about_tvServices);
            youTubePlayerView = itemView.findViewById(R.id.about_youtubePlayer);
        }

    }
}
