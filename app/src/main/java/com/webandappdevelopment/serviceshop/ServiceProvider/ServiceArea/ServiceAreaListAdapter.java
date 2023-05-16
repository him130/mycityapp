package com.webandappdevelopment.serviceshop.ServiceProvider.ServiceArea;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.webandappdevelopment.serviceshop.R;

import java.util.List;

public class ServiceAreaListAdapter extends RecyclerView.Adapter<ServiceAreaListAdapter.ViewHolder> {
    private Context context;
    private List<ServiceAreaItem> listItem;

    public ServiceAreaListAdapter(Context context, List<ServiceAreaItem> listItem) {
        this.context = context;
        this.listItem = listItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_service_area, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ServiceAreaItem item = listItem.get(position);
        holder.tvName.setText(item.getArea());
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.rowServiceArea_tvArea);
        }
    }
}
