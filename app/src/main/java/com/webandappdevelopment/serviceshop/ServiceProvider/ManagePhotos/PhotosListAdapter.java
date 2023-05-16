package com.webandappdevelopment.serviceshop.ServiceProvider.ManagePhotos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import com.webandappdevelopment.serviceshop.R;

import java.util.List;

public class PhotosListAdapter extends RecyclerView.Adapter<PhotosListAdapter.ViewHolder> {
    private Context context;
    private List<PhotosItem> listItem;
    private PhotosListAdapter.ItemClickListener clickListener;

    public PhotosListAdapter(Context context, List<PhotosItem> listItem, ItemClickListener clickListener) {
        this.context = context;
        this.listItem = listItem;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_photos, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        PhotosItem item = listItem.get(position);
        if (item.getImageUrl().matches("")) {
            holder.ivImage.setBackgroundResource(R.mipmap.ic_launcher_new);
        } else {
            Picasso.get().load(item.getImageUrl())
                    .placeholder(R.mipmap.ic_launcher_new)
                    .into(holder.ivImage);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ShapeableImageView ivImage;
        private AppCompatButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.rowPhotos_ivImage);
        }
    }

    public interface ItemClickListener{
        public void onItemClick(PhotosItem item);
    }
}
