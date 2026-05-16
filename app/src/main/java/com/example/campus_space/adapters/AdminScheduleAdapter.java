package com.example.campus_space.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.R;
import com.example.campus_space.models.Booking;

import java.util.List;

public class AdminScheduleAdapter extends RecyclerView.Adapter<AdminScheduleAdapter.ViewHolder> {

    private List<Booking> scheduleItems;
    private int[] barColors = {
        Color.parseColor("#2196F3"), // blue
        Color.parseColor("#4CAF50"), // green
        Color.parseColor("#9C27B0"), // purple
        Color.parseColor("#FF9800"), // orange
        Color.parseColor("#E91E63")  // pink
    };

    public AdminScheduleAdapter(List<Booking> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_schedule, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking item = scheduleItems.get(position);
        holder.tvRoomName.setText(item.getRoomName());
        holder.tvEventTime.setText(item.getClassName() + " - " + item.getTime());
        holder.vColorBar.setBackgroundColor(barColors[position % barColors.length]);
    }

    @Override
    public int getItemCount() {
        return scheduleItems.size();
    }

    public void updateData(List<Booking> newItems) {
        this.scheduleItems = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View vColorBar;
        TextView tvRoomName, tvEventTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            vColorBar = itemView.findViewById(R.id.vColorBar);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvEventTime = itemView.findViewById(R.id.tvEventTime);
        }
    }
}
