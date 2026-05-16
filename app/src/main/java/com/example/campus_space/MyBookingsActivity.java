package com.example.campus_space;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.adapters.MyBookingAdapter;
import com.example.campus_space.models.Booking;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private RecyclerView rvMyBookings;
    private TextView tvEmpty;
    private MyBookingAdapter adapter;
    private List<Booking> bookingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        rvMyBookings = findViewById(R.id.rvMyBookings);
        tvEmpty = findViewById(R.id.tvEmpty);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvMyBookings.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyBookingAdapter(bookingList);
        rvMyBookings.setAdapter(adapter);

        loadMyBookings();
    }

    private void loadMyBookings() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        FirebaseDatabase.getInstance().getReference("bookings")
            .orderByChild("userId").equalTo(user.getUid())
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
                    rvMyBookings.setVisibility(bookingList.isEmpty() ? View.GONE : View.VISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }
}
