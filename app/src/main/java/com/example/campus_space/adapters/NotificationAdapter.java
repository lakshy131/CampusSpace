package com.example.campus_space.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.R;
import com.example.campus_space.models.NotificationItem;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationItem> notifications;

    public NotificationAdapter(List<NotificationItem> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem notification = notifications.get(position);
        holder.tvTitle.setText(notification.getTitle());
        holder.tvMessage.setText(notification.getMessage());
        holder.tvTimeAgo.setText(notification.getTimeAgo());

        // Set border color based on notification type
        String type = notification.getType();
        if ("cancelled".equals(type)) {
            holder.vBorder.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.border_red));
        } else if ("approved".equals(type) || "confirmed".equals(type)) {
            holder.vBorder.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.border_green));
        } else {
            holder.vBorder.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.getContext(), R.color.border_blue));
        }
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public void updateData(List<NotificationItem> newNotifications) {
        this.notifications = newNotifications;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View vBorder;
        TextView tvTitle, tvMessage, tvTimeAgo;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            vBorder = itemView.findViewById(R.id.vBorder);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
        }
    }
}
