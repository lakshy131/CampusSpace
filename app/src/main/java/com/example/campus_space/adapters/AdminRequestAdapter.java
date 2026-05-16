package com.example.campus_space.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.R;
import com.example.campus_space.models.BookingRequest;

import java.util.List;

public class AdminRequestAdapter extends RecyclerView.Adapter<AdminRequestAdapter.ViewHolder> {

    private List<BookingRequest> requests;
    private OnRequestActionListener listener;

    public interface OnRequestActionListener {
        void onApprove(BookingRequest request);
        void onReject(BookingRequest request);
    }

    public AdminRequestAdapter(List<BookingRequest> requests, OnRequestActionListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingRequest request = requests.get(position);
        holder.tvRoomName.setText(request.getRoomName());
        holder.tvRequestedBy.setText("Requested by: " + request.getRequestedBy() + " (" + request.getRequesterRole() + ")");
        holder.tvPurpose.setText("Purpose: " + request.getPurpose());
        holder.tvDate.setText("Date: " + request.getDate());
        holder.tvTime.setText("Time: " + request.getTime());

        holder.btnApprove.setOnClickListener(v -> listener.onApprove(request));
        holder.btnReject.setOnClickListener(v -> listener.onReject(request));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public void updateData(List<BookingRequest> newRequests) {
        this.requests = newRequests;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvStatus, tvRequestedBy, tvPurpose, tvDate, tvTime;
        Button btnApprove, btnReject;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvRequestedBy = itemView.findViewById(R.id.tvRequestedBy);
            tvPurpose = itemView.findViewById(R.id.tvPurpose);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }
}
