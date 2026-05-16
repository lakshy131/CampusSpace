package com.example.campus_space.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.R;
import com.example.campus_space.models.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> events;
    private int[] borderColors = {
        Color.parseColor("#9C27B0"), // purple
        Color.parseColor("#E91E63"), // pink
        Color.parseColor("#FF9800"), // orange
        Color.parseColor("#2196F3"), // blue
        Color.parseColor("#4CAF50")  // green
    };

    public EventAdapter(List<Event> events) {
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvEventName.setText(event.getName());
        holder.tvVenue.setText(event.getVenue());
        holder.tvTime.setText(event.getTime());
        holder.tvOrganizer.setText(event.getOrganizer());

        // Date badge
        String dateBadge = event.getDateBadge();
        if (dateBadge != null) {
            holder.tvDateBadge.setText(dateBadge);
        } else {
            holder.tvDateBadge.setText(event.getDate());
        }

        // Colored left border
        int colorIndex = position % borderColors.length;
        holder.vBorder.setBackgroundColor(borderColors[colorIndex]);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void updateData(List<Event> newEvents) {
        this.events = newEvents;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View vBorder;
        TextView tvEventName, tvDateBadge, tvVenue, tvTime, tvOrganizer;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            vBorder = itemView.findViewById(R.id.vBorder);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvDateBadge = itemView.findViewById(R.id.tvDateBadge);
            tvVenue = itemView.findViewById(R.id.tvVenue);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvOrganizer = itemView.findViewById(R.id.tvOrganizer);
        }
    }
}
