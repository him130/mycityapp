package com.webandappdevelopment.serviceshop.ServiceHistory;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.webandappdevelopment.serviceshop.R;
import com.webandappdevelopment.serviceshop.ServiceProviderList.ServiceProviderDetailsActivity;

import java.util.List;

public class ServiceHistoryListAdapter  extends RecyclerView.Adapter<ServiceHistoryListAdapter.ViewHolder> {
    private Context context;
    private List<ServiceHistoryItem> listItem;
    private ServiceHistoryListAdapter.ItemClickListener clickListener;

    public ServiceHistoryListAdapter(Context context, List<ServiceHistoryItem> listItem, ItemClickListener clickListener) {
        this.context = context;
        this.listItem = listItem;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_service_history, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceHistoryListAdapter.ViewHolder holder, int position) {
        ServiceHistoryItem item = listItem.get(position);
        holder.tvName.setText(item.getName());
        holder.tvContact.setText(item.getMobile());
        holder.tvAddress.setText(item.getAddress());
        holder.tvRequest.setText(item.getService());
        holder.tvDate.setText(item.getAppointmentDate());
        holder.tvMonth.setText(item.getAppointmentMonth());
        holder.tvStatus.setText("Status: " +item.getStatus());
        holder.tvCategory.setText(item.getCategory());
        holder.btnViews.setVisibility(View.VISIBLE);
        holder.btnViews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ServiceProviderDetailsActivity.class);
                i.putExtra("Id",item.getId());
                context.startActivity(i);
            }
        });
        if (item.getStatus().matches("Pending")||item.getStatus().matches("Approved")) {
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else{
            holder.btnCancel.setVisibility(View.GONE);
        }
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickListener.onItemClick(item);
            }
        });
        holder.btnViews.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+ holder.tvContact));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return listItem.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvContact;
        private TextView tvAddress;
        private TextView tvRequest;
        private TextView tvDate;
        private TextView tvMonth;
        private TextView tvStatus;
        private TextView tvCategory;
        private AppCompatButton btnCancel;
        private AppCompatButton btnViews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.rowServiceHistory_tvName);
            tvContact = itemView.findViewById(R.id.rowServiceHistory_tvContact);
            tvAddress = itemView.findViewById(R.id.rowServiceHistory_tvAddress);
            tvRequest = itemView.findViewById(R.id.rowServiceHistory_tvService);
            tvDate = itemView.findViewById(R.id.rowServiceHistory_tvDate);
            tvMonth = itemView.findViewById(R.id.rowServiceHistory_tvMonth);
            tvStatus = itemView.findViewById(R.id.rowServiceHistory_tvStatus);
            tvCategory = itemView.findViewById(R.id.rowServiceHistory_tvCategory);
            btnCancel = itemView.findViewById(R.id.rowServiceHistory_btnCancel);
            btnViews = itemView.findViewById(R.id.rowService_btnView);
        }
    }

    public interface ItemClickListener{
        public void onItemClick(ServiceHistoryItem item);

        /*public void onItemClickProfile(ServiceHistoryItem item);*/
    }
}
