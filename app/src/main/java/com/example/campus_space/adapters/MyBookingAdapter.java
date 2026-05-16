package com.example.campus_space.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.R;
import com.example.campus_space.models.Booking;

import java.util.List;

public class MyBookingAdapter extends RecyclerView.Adapter<MyBookingAdapter.ViewHolder> {

    private List<Booking> bookings;

    public MyBookingAdapter(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_booking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.tvRoomName.setText(booking.getRoomName());
        holder.tvRoomType.setText(booking.getRoomType() != null ? booking.getRoomType() : "Classroom");
        holder.tvCapacity.setText("Capacity: " + booking.getCapacity() + " seats");
        holder.tvClassName.setText(booking.getClassName());
        holder.tvFaculty.setText(booking.getFaculty());
        holder.tvTime.setText(booking.getTime());
        holder.tvDate.setText(booking.getDate());

        // Status badge color
        String status = booking.getStatus();
        if ("Confirmed".equals(status)) {
            holder.tvStatus.setText("Confirmed");
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.badge_green_text));
            holder.tvStatus.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_badge_green));
        } else if ("Pending".equals(status)) {
            holder.tvStatus.setText("Pending");
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.badge_yellow_text));
            holder.tvStatus.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_badge_yellow));
        } else {
            holder.tvStatus.setText("Cancelled");
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.badge_red_text));
            holder.tvStatus.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_badge_red));
        }
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public void updateData(List<Booking> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoomName, tvRoomType, tvStatus, tvCapacity, tvClassName, tvFaculty, tvTime, tvDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            tvClassName = itemView.findViewById(R.id.tvClassName);
            tvFaculty = itemView.findViewById(R.id.tvFaculty);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
