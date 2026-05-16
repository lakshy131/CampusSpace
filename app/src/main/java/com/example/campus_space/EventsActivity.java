package com.example.campus_space;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.adapters.EventAdapter;
import com.example.campus_space.models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private TextView tvEmpty;
    private EventAdapter adapter;
    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        rvEvents = findViewById(R.id.rvEvents);
        tvEmpty = findViewById(R.id.tvEmpty);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EventAdapter(eventList);
        rvEvents.setAdapter(adapter);

        loadEvents();
    }

    private void loadEvents() {
        FirebaseDatabase.getInstance().getReference("events")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    eventList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Event event = ds.getValue(Event.class);
                        if (event != null) {
                            event.setId(ds.getKey());
                            eventList.add(event);
                        }
                    }
                    adapter.updateData(eventList);
                    tvEmpty.setVisibility(eventList.isEmpty() ? View.VISIBLE : View.GONE);
                    rvEvents.setVisibility(eventList.isEmpty() ? View.GONE : View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }
}
