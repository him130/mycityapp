package com.webandappdevelopment.serviceshop.ServiceProviderList;

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

public class ServiceProviderListAdapter extends RecyclerView.Adapter<ServiceProviderListAdapter.ViewHolder> {
    private Context context;
    private List<ServiceProviderItem> listItem;
    private ServiceProviderListAdapter.ItemClickListener clickListener;

    public ServiceProviderListAdapter(Context context, List<ServiceProviderItem> listItem, ItemClickListener clickListener) {
        this.context = context;
        this.listItem = listItem;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_service_provider_list, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ServiceProviderItem item = listItem.get(position);
        if (item.getImage().matches("")) {
            holder.ivImage.setBackgroundResource(R.drawable.ic_service_provider);
        } else {
            Picasso.get().load(item.getImage())
                    .placeholder(R.drawable.ic_service_provider)
                    .into(holder.ivImage);
        }

        holder.tvId.setText("ID: "+item.getCityId());
        holder.tvRating.setText("Rating: "+item.getRating());
        holder.tvName.setText(item.getName());
        holder.tvServices.setText(item.getServiceList());
        holder.tvDays.setText("My City Days: "+item.getDays());
        holder.itemView.setOnClickListener(v -> clickListener.onItemClick(item));
        /*holder.btnBookNow.setOnClickListener(view -> {
            Intent intent = new Intent(context, BookNowActivity.class);
            intent.putExtra("spid", item.getId());
            context.startActivity(intent);
        });
        holder.btnCallNow.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+ item.getContact()));
            context.startActivity(intent);
        });*/

    }

    @Override
    public int getItemCount() {
        if (listItem.size()==0){
            return 0;
        }else {
            return listItem.size();
        }


    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView ivImage;
        private TextView tvId;
        private TextView tvRating;
        private TextView tvName;
        private TextView tvServices;
        private TextView tvDays;
        /*private Button btnBookNow;
        private Button btnCallNow;*/

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.rowServiceProviderList_ivImage);
            tvId = itemView.findViewById(R.id.rowServiceProviderList_tvId);
            tvRating = itemView.findViewById(R.id.rowServiceProviderList_tvRating);
            tvName = itemView.findViewById(R.id.rowServiceProviderList_tvName);
            tvServices = itemView.findViewById(R.id.rowServiceProviderList_tvServices);
            tvDays = itemView.findViewById(R.id.rowServiceProviderList_tvMyCityDays);
            /*btnBookNow = itemView.findViewById(R.id.rowServiceProviderList_btnBookNow);
            btnCallNow = itemView.findViewById(R.id.rowServiceProviderList_btnCallNow);*/
        }
    }

    public interface ItemClickListener{
        public void onItemClick(ServiceProviderItem item);
    }
}
