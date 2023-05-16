package com.webandappdevelopment.serviceshop.ServiceProvider.ServiceRequest;

import android.annotation.SuppressLint;
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

import java.util.List;

public class ServiceRequestListAdapter extends RecyclerView.Adapter<ServiceRequestListAdapter.ViewHolder> {
    private Context context;
    private List<ServiceRequestItem> listItem;
    private ServiceRequestListAdapter.RejectClickListener RejectClickListener;
    private ServiceRequestListAdapter.UpdateClickListener UpdateClickListener;

    public ServiceRequestListAdapter(Context context, List<ServiceRequestItem> listItem, RejectClickListener RejectClickListener, UpdateClickListener UpdateClickListener) {
        this.context = context;
        this.listItem = listItem;
        this.RejectClickListener = RejectClickListener;
        this.UpdateClickListener = UpdateClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_service_list, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ServiceRequestItem item = listItem.get(position);
        holder.tvName.setText(item.getCustomerName());
        holder.tvContact.setText(item.getContact());
        holder.tvAddress.setText(item.getAddress());
        holder.tvRequest.setText(item.getServiceRequired());
        holder.tvDate.setText(item.getAppointmentDate());
        holder.tvMonth.setText(item.getAppointmentMonth());
        holder.tvStatus.setText("Status: " +item.getStatus());
        if (!(item.getStatus().matches("Pending")||item.getStatus().matches("Approved"))) {
            holder.btnReject.setVisibility(View.GONE);
            holder.btnUpdate.setVisibility(View.GONE);
        }
        holder.btnReject.setOnClickListener(view -> RejectClickListener.onRejectClick(item));
        if (item.getStatus().matches("Approved")){
            holder.btnUpdate.setText(R.string.mark_completed);
        }
        holder.btnUpdate.setOnClickListener(view -> {
            String status = "";
            if(holder.btnUpdate.getText().toString().matches("Mark Approved")){
                status = "Approved";
            } else if (holder.btnUpdate.getText().toString().matches("Mark Completed")){
                status = "Completed";
            }
            UpdateClickListener.onUpdateClick(item, status);
        });
        holder.btnCall.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:"+ holder.tvContact));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tvName;
        private TextView tvContact;
        private TextView tvAddress;
        private TextView tvRequest;
        private TextView tvDate;
        private TextView tvMonth;
        private TextView tvStatus;
        private AppCompatButton btnReject, btnUpdate, btnCall;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.rowServiceRequest_tvName);
            tvContact = itemView.findViewById(R.id.rowServiceRequest_tvContact);
            tvAddress = itemView.findViewById(R.id.rowServiceRequest_tvAddress);
            tvRequest = itemView.findViewById(R.id.rowServiceRequest_tvService);
            tvDate = itemView.findViewById(R.id.rowServiceList_tvDate);
            tvMonth = itemView.findViewById(R.id.rowServiceList_tvMonth);
            tvStatus = itemView.findViewById(R.id.rowServiceRequest_tvStatus);
            btnReject = itemView.findViewById(R.id.rowServiceRequest_btnReject);
            btnUpdate = itemView.findViewById(R.id.rowServiceRequest_btnUpdate);
            btnCall = itemView.findViewById(R.id.rowService_list_btnView);
        }
    }

    public interface RejectClickListener{
        public void onRejectClick(ServiceRequestItem item);
    }

    public interface UpdateClickListener{
        public void onUpdateClick(ServiceRequestItem item, String status);
    }
}
