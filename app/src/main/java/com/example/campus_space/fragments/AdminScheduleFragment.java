package com.example.campus_space.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.R;
import com.example.campus_space.adapters.AdminScheduleAdapter;
import com.example.campus_space.models.Booking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminScheduleFragment extends Fragment {

    private RecyclerView rvSchedule;
    private AdminScheduleAdapter adapter;
    private List<Booking> scheduleList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_schedule, container, false);

        rvSchedule = view.findViewById(R.id.rvSchedule);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminScheduleAdapter(scheduleList);
        rvSchedule.setAdapter(adapter);

        loadSchedule();

        return view;
    }

    private void loadSchedule() {
        FirebaseDatabase.getInstance().getReference("bookings")
            .orderByChild("status").equalTo("Confirmed")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    scheduleList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Booking booking = ds.getValue(Booking.class);
                        if (booking != null) {
                            booking.setId(ds.getKey());
                            scheduleList.add(booking);
                        }
                    }
                    adapter.updateData(scheduleList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }
}
