package com.example.campus_space.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.R;
import com.example.campus_space.models.Booking;

import java.util.List;

public class BookedRoomAdapter extends RecyclerView.Adapter<BookedRoomAdapter.ViewHolder> {

    private List<Booking> bookings;

    public BookedRoomAdapter(List<Booking> bookings) {
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booked_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Booking booking = bookings.get(position);
        holder.tvRoomName.setText(booking.getRoomName());
        holder.tvEventType.setText(booking.getType());
        holder.tvEventName.setText(booking.getClassName());
        holder.tvFaculty.setText(booking.getFaculty());
        holder.tvTime.setText(booking.getTime());
        holder.tvDate.setText(booking.getDate());
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
        TextView tvRoomName, tvEventType, tvEventName, tvFaculty, tvTime, tvDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tvRoomName);
            tvEventType = itemView.findViewById(R.id.tvEventType);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvFaculty = itemView.findViewById(R.id.tvFaculty);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }
}
