package com.example.campus_space.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.R;
import com.example.campus_space.adapters.AdminRequestAdapter;
import com.example.campus_space.models.BookingRequest;
import com.example.campus_space.models.Booking;
import com.example.campus_space.models.NotificationItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminRequestsFragment extends Fragment implements AdminRequestAdapter.OnRequestActionListener {

    private RecyclerView rvRequests;
    private AdminRequestAdapter adapter;
    private List<BookingRequest> requestList = new ArrayList<>();
    private DatabaseReference requestsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_requests, container, false);

        rvRequests = view.findViewById(R.id.rvRequests);
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdminRequestAdapter(requestList, this);
        rvRequests.setAdapter(adapter);

        requestsRef = FirebaseDatabase.getInstance().getReference("admin_requests");
        loadRequests();

        return view;
    }

    private void loadRequests() {
        requestsRef.orderByChild("status").equalTo("Pending")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    requestList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        BookingRequest request = ds.getValue(BookingRequest.class);
                        if (request != null) {
                            request.setId(ds.getKey());
                            requestList.add(request);
                        }
                    }
                    adapter.updateData(requestList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }

    @Override
    public void onApprove(BookingRequest request) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        // Update request status
        db.child("admin_requests").child(request.getId()).child("status").setValue("Approved");

        // Update room status to Booked
        if (request.getRoomId() != null) {
            db.child("rooms").child(request.getRoomId()).child("status").setValue("Booked");
        }

        // Create a booking
        DatabaseReference bookingRef = db.child("bookings").push();
        Booking booking = new Booking(
            bookingRef.getKey(), request.getRoomName(), request.getRoomName(),
            request.getUserId(), request.getPurpose(), request.getRequestedBy(),
            request.getTime(), request.getDate(), "Confirmed", "Class",
            "Classroom", 50
        );
        bookingRef.setValue(booking);

        // Send notification to user
        if (request.getUserId() != null) {
            DatabaseReference notifRef = db.child("notifications")
                .child(request.getUserId()).push();
            Map<String, Object> notif = new HashMap<>();
            notif.put("title", "Room " + request.getRoomName() + " booking approved");
            notif.put("message", "Your booking for " + request.getPurpose() + " has been successfully approved.");
            notif.put("type", "approved");
            notif.put("timestamp", System.currentTimeMillis());
            notifRef.setValue(notif);
        }

        Toast.makeText(getContext(), "Request approved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReject(BookingRequest request) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        // Update request status
        db.child("admin_requests").child(request.getId()).child("status").setValue("Rejected");

        // Update room status back to Available
        if (request.getRoomId() != null) {
            db.child("rooms").child(request.getRoomId()).child("status").setValue("Available");
        }

        // Send notification to user
        if (request.getUserId() != null) {
            DatabaseReference notifRef = db.child("notifications")
                .child(request.getUserId()).push();
            Map<String, Object> notif = new HashMap<>();
            notif.put("title", "Room " + request.getRoomName() + " booking rejected");
            notif.put("message", "Your booking request for " + request.getPurpose() + " was not approved.");
            notif.put("type", "cancelled");
            notif.put("timestamp", System.currentTimeMillis());
            notifRef.setValue(notif);
        }

        Toast.makeText(getContext(), "Request rejected", Toast.LENGTH_SHORT).show();
    }
}
