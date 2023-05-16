package com.webandappdevelopment.serviceshop.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.webandappdevelopment.serviceshop.R;

import java.util.List;

public class ServiceListAdapter extends RecyclerView.Adapter<ServiceListAdapter.ViewHolder> {
    private Context context;
    private List<ServiceItem> listItem;
    private ServiceListAdapter.ItemClickListener clickListener;

    public ServiceListAdapter(Context context, List<ServiceItem> listItem, ItemClickListener clickListener) {
        this.context = context;
        this.listItem = listItem;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_home, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ServiceItem item = listItem.get(position);
        holder.tvTitle.setText(item.getTitle());
        if (item.getImage().matches("")) {
            holder.ivImage.setBackgroundResource(R.mipmap.ic_launcher_new);
        } else {
            Picasso.get().load(item.getImage())
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
        private TextView tvTitle;
        private ImageView ivImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.rowHome_tvTitle);
            ivImage = itemView.findViewById(R.id.rowHome_ivImage);
        }
    }

    public interface ItemClickListener{
        public void onItemClick(ServiceItem item);
    }
}
