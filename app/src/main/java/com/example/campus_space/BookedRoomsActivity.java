package com.example.campus_space;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.adapters.BookedRoomAdapter;
import com.example.campus_space.models.Booking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookedRoomsActivity extends AppCompatActivity {

    private RecyclerView rvBookedRooms;
    private TextView tvEmpty;
    private BookedRoomAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_rooms);

        rvBookedRooms = findViewById(R.id.rvBookedRooms);
        tvEmpty = findViewById(R.id.tvEmpty);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvBookedRooms.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookedRoomAdapter(bookingList);
        rvBookedRooms.setAdapter(adapter);

        loadBookedRooms();
    }

    private void loadBookedRooms() {
        FirebaseDatabase.getInstance().getReference("bookings")
            .orderByChild("status").equalTo("Confirmed")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    bookingList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Booking booking = ds.getValue(Booking.class);
                        if (booking != null) {
                            booking.setId(ds.getKey());
                            bookingList.add(booking);
                        }
                    }
                    adapter.updateData(bookingList);
                    tvEmpty.setVisibility(bookingList.isEmpty() ? View.VISIBLE : View.GONE);
                    rvBookedRooms.setVisibility(bookingList.isEmpty() ? View.GONE : View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }
}
