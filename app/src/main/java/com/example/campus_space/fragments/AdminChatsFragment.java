package com.example.campus_space.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.ChatActivity;
import com.example.campus_space.R;
import com.example.campus_space.adapters.AdminChatListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminChatsFragment extends Fragment implements AdminChatListAdapter.OnChatClickListener {

    private RecyclerView rvChats;
    private AdminChatListAdapter adapter;
    private List<Map<String, String>> chatPreviews = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_chats, container, false);

        rvChats = view.findViewById(R.id.rvChats);
        rvChats.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminChatListAdapter(chatPreviews, this);
        rvChats.setAdapter(adapter);

        loadChats();

        return view;
    }

    private void loadChats() {
        FirebaseDatabase.getInstance().getReference("chats")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatPreviews.clear();
                    for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                        String chatId = chatSnapshot.getKey();

                        // Get user name
                        String userId = chatId; // chat ID is the user ID

                        // Get last message
                        DataSnapshot messagesSnapshot = chatSnapshot.child("messages");
                        String lastMessage = "";
                        long lastTimestamp = 0;

                        for (DataSnapshot msgSnapshot : messagesSnapshot.getChildren()) {
                            String text = msgSnapshot.child("text").getValue(String.class);
                            Long timestamp = msgSnapshot.child("timestamp").getValue(Long.class);
                            if (timestamp != null && timestamp > lastTimestamp) {
                                lastTimestamp = timestamp;
                                lastMessage = text != null ? text : "";
                            }
                        }

                        Map<String, String> preview = new HashMap<>();
                        preview.put("chatId", chatId);
                        preview.put("name", "User"); // Will be updated
                        preview.put("lastMessage", lastMessage);

                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                        preview.put("time", sdf.format(new Date(lastTimestamp)));
                        preview.put("isNew", "true");

                        chatPreviews.add(preview);

                        // Load user name
                        final int index = chatPreviews.size() - 1;
                        FirebaseDatabase.getInstance().getReference("users").child(userId)
                            .child("name").get().addOnSuccessListener(nameSnapshot -> {
                                String name = nameSnapshot.getValue(String.class);
                                if (name != null && index < chatPreviews.size()) {
                                    chatPreviews.get(index).put("name", name);
                                    adapter.notifyItemChanged(index);
                                }
                            });
                    }
                    adapter.updateData(chatPreviews);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }

    @Override
    public void onChatClick(String chatId, String userName) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra("chatId", chatId);
        intent.putExtra("userName", userName);
        startActivity(intent);
    }
}
