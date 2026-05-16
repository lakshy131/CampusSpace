package com.example.campus_space;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.adapters.NotificationAdapter;
import com.example.campus_space.models.NotificationItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView rvNotifications;
    private TextView tvEmpty;
    private NotificationAdapter adapter;
    private List<NotificationItem> notificationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        rvNotifications = findViewById(R.id.rvNotifications);
        tvEmpty = findViewById(R.id.tvEmpty);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationList);
        rvNotifications.setAdapter(adapter);

        loadNotifications();
    }

    private void loadNotifications() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseDatabase.getInstance().getReference("notifications")
            .child(user.getUid())
            .orderByChild("timestamp")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    notificationList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        NotificationItem notification = ds.getValue(NotificationItem.class);
                        if (notification != null) {
                            notification.setId(ds.getKey());
                            // Calculate time ago
                            long diff = System.currentTimeMillis() - notification.getTimestamp();
                            long minutes = diff / (1000 * 60);
                            if (minutes < 60) {
                                notification.setTimeAgo(minutes + " min ago");
                            } else if (minutes < 1440) {
                                notification.setTimeAgo((minutes / 60) + " hours ago");
                            } else {
                                notification.setTimeAgo((minutes / 1440) + " days ago");
                            }
                            notificationList.add(0, notification); // newest first
                        }
                    }
                    adapter.updateData(notificationList);
                    tvEmpty.setVisibility(notificationList.isEmpty() ? View.VISIBLE : View.GONE);
                    rvNotifications.setVisibility(notificationList.isEmpty() ? View.GONE : View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }
}
