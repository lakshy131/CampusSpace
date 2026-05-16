package com.example.campus_space.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.R;
import com.example.campus_space.models.Room;

import java.util.List;

public class AvailableRoomAdapter extends RecyclerView.Adapter<AvailableRoomAdapter.ViewHolder> {

    private List<Room> rooms;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Room room);
    }

    public AvailableRoomAdapter(List<Room> rooms, OnBookClickListener listener) {
        this.rooms = rooms;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_available_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.tvRoomName.setText(room.getName());
        holder.tvRoomType.setText(room.getType());
        holder.tvCapacity.setText("Capacity: " + room.getCapacity() + " seats");

        // Status dot and text
        if ("Available".equals(room.getStatus())) {
            GradientDrawable dot = new GradientDrawable();
            dot.setShape(GradientDrawable.OVAL);
            dot.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_available));
            dot.setSize(20, 20);
            holder.vStatusDot.setBackground(dot);
            holder.tvStatus.setText("Status: Available");
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_available));
            holder.btnBookRoom.setText("Book Room");
            holder.btnBookRoom.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_blue_button));
            holder.btnBookRoom.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.btnBookRoom.setEnabled(true);
            holder.btnBookRoom.setOnClickListener(v -> listener.onBookClick(room));
        } else if ("Booked".equals(room.getStatus())) {
            GradientDrawable dot = new GradientDrawable();
            dot.setShape(GradientDrawable.OVAL);
            dot.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_booked));
            dot.setSize(20, 20);
            holder.vStatusDot.setBackground(dot);
            holder.tvStatus.setText("Status: Booked");
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_booked));
            holder.btnBookRoom.setText("Currently Booked");
            holder.btnBookRoom.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_disabled_button));
            holder.btnBookRoom.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary));
            holder.btnBookRoom.setEnabled(false);
        } else {
            GradientDrawable dot = new GradientDrawable();
            dot.setShape(GradientDrawable.OVAL);
            dot.setColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_pending));
            dot.setSize(20, 20);
            holder.vStatusDot.setBackground(dot);
            holder.tvStatus.setText("Status: Pending approval");
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_pending));
            holder.btnBookRoom.setText("Awaiting Approval");
            holder.btnBookRoom.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_disabled_button));
            holder.btnBookRoom.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary));
            holder.btnBookRoom.setEnabled(false);
        }
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public void updateData(List<Room> newRooms) {
        this.rooms = newRooms;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View vStatusDot;
        TextView tvRoomName, tvRoomType, tvCapacity, tvStatus;
        Button btnBookRoom;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            vStatusDot = itemView.findViewById(R.id.vStatusDot);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnBookRoom = itemView.findViewById(R.id.btnBookRoom);
        }
    }
}
