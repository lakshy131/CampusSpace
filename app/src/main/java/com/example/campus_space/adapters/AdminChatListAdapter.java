package com.example.campus_space.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.R;
import com.example.campus_space.models.ChatMessage;

import java.util.List;
import java.util.Map;

public class AdminChatListAdapter extends RecyclerView.Adapter<AdminChatListAdapter.ViewHolder> {

    private List<Map<String, String>> chatPreviews;
    private OnChatClickListener listener;

    public interface OnChatClickListener {
        void onChatClick(String chatId, String userName);
    }

    public AdminChatListAdapter(List<Map<String, String>> chatPreviews, OnChatClickListener listener) {
        this.chatPreviews = chatPreviews;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_chat_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> preview = chatPreviews.get(position);
        holder.tvName.setText(preview.get("name"));
        holder.tvPreview.setText(preview.get("lastMessage"));
        holder.tvTime.setText(preview.get("time"));

        boolean isNew = "true".equals(preview.get("isNew"));
        holder.tvNewBadge.setVisibility(isNew ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onChatClick(preview.get("chatId"), preview.get("name"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatPreviews.size();
    }

    public void updateData(List<Map<String, String>> newPreviews) {
        this.chatPreviews = newPreviews;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPreview, tvTime, tvNewBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPreview = itemView.findViewById(R.id.tvPreview);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvNewBadge = itemView.findViewById(R.id.tvNewBadge);
        }
    }
}
