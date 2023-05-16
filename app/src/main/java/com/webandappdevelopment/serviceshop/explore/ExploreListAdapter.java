package com.webandappdevelopment.serviceshop.explore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.ServiceProviderList.Slider.SliderItem;

import java.util.List;

public class ExploreListAdapter extends RecyclerView.Adapter<ExploreListAdapter.ViewHolder> {
    private Context context;
    private List<ExploreItem> listItem;
    private List<SliderItem> adsItem;
    private ExploreListAdapter.ItemClickListener clickListener;
    private static final int CONTENT = 0;
    private static final int AD = 1;
    private int i=0;
    private int j=0;

    public ExploreListAdapter(Context context, List<ExploreItem> listItem, List<SliderItem> adsItem, ItemClickListener clickListener) {
        this.context = context;
        this.listItem = listItem;
        this.adsItem = adsItem;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == AD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_slider, parent,
                    false);
        } else if (viewType == CONTENT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_explore, parent,
                    false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try{
            if (getItemViewType(position) == CONTENT) {
                ExploreItem item = listItem.get(i);
                holder.tvTitle.setText(item.getTitle());
                if (item.getSPId()==0) {
                    holder.btnView.setVisibility(View.GONE);
                } else {
                    holder.btnView.setVisibility(View.VISIBLE);
                    holder.btnView.setOnClickListener(view -> clickListener.onItemClick(item));
                }
                if (item.getType().matches("image")){
                    holder.llYoutubePlayer.setVisibility(View.GONE);
                    holder.ivImage.setVisibility(View.VISIBLE);
                    if (item.getVideoUrl().matches("")) {
                        holder.ivImage.setBackgroundResource(R.mipmap.ic_launcher_new);
                    } else {
                        Picasso.get().load(item.getVideoUrl())
                                .placeholder(R.mipmap.ic_launcher_new)
                                .into(holder.ivImage);
                    }
                } else if (item.getType().matches("video")){
                    holder.llYoutubePlayer.setVisibility(View.VISIBLE);
                    holder.ivImage.setVisibility(View.GONE);
                    if (!(item.getVideoUrl().matches(""))) {
                        holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                            @Override
                            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                                youTubePlayer.cueVideo(item.getVideoUrl(),0);
                                /*youTubePlayer.setFullscreen(true);*/
                                youTubePlayer.pause();



                            }
                        });
                    }
                    holder.enlargeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            /*Fragment eFragment = new EnlargeVideoFragment();
                            FragmentManager mFragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                            androidx.fragment.app.FragmentTransaction eFragmentTransaction  = mFragmentManager.beginTransaction();

                            Bundle eBundle = new Bundle();
                            eBundle.putString("url", item.getVideoUrl());

                            eFragment.setArguments(eBundle);

                            eFragmentTransaction
                                    .replace(R.id.enlargeframeLayout, eFragment)
                                    .addToBackStack(null)
                                    .commit();*/
                            Intent intent = new Intent(context, VidoeEnlargeActivity.class);
                            intent.putExtra("url", item.getVideoUrl());
                            context.startActivity(intent);

                        }
                    });
                }
                i+=1;
            } else if (getItemViewType(position) == AD) {
                SliderItem item = adsItem.get(j);
                holder.imageView.setVisibility(View.VISIBLE);
                if (item.getImg_url().matches("")) {
                    holder.imageView.setBackgroundResource(R.mipmap.ic_launcher_new);
                } else {
                    Picasso.get().load(item.getImg_url())
                            .placeholder(R.mipmap.ic_launcher_new)
                            .into(holder.imageView);
                }
                j+=1;
            }
        }catch (Exception e){

        }


    }


    @Override
    public int getItemCount() {
        return listItem.size();
    }

    @Override
    public int getItemViewType(int position) {
        if ((position > 0) && (position % 10 == 0))
            return AD;
        return CONTENT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        //content
        private TextView tvTitle;
        private AppCompatButton btnView;
        private ImageView ivImage;
        private YouTubePlayerView youTubePlayerView;
        private LinearLayout llYoutubePlayer;
        //ads
        private ImageView imageView;
        private ImageView enlargeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.rowExplore_tvTitle);
            btnView = itemView.findViewById(R.id.rowExplore_btnView);
            ivImage = itemView.findViewById(R.id.rowExplore_ivImage);
            youTubePlayerView = itemView.findViewById(R.id.rowExplore_youtubePlayer);
            llYoutubePlayer = itemView.findViewById(R.id.rowExplore_llYoutubePlayer);
            imageView = itemView.findViewById(R.id.sliderRow_ivSlider);
            enlargeButton = itemView.findViewById(R.id.enlargebtn);
        }
    }

    public interface ItemClickListener{
        public void onItemClick(ExploreItem item);
    }
}
