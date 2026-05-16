package com.example.campus_space;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space.adapters.AvailableRoomAdapter;
import com.example.campus_space.models.BookingRequest;
import com.example.campus_space.models.Room;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AvailableRoomsActivity extends AppCompatActivity implements AvailableRoomAdapter.OnBookClickListener {

    private RecyclerView rvAvailableRooms;
    private TextView tvEmpty;
    private AvailableRoomAdapter adapter;
    private List<Room> roomList = new ArrayList<>();
    private DatabaseReference roomsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_rooms);

        rvAvailableRooms = findViewById(R.id.rvAvailableRooms);
        tvEmpty = findViewById(R.id.tvEmpty);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvAvailableRooms.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AvailableRoomAdapter(roomList, this);
        rvAvailableRooms.setAdapter(adapter);

        roomsRef = FirebaseDatabase.getInstance().getReference("rooms");
        loadRooms();
    }

    private void loadRooms() {
        roomsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                roomList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Room room = ds.getValue(Room.class);
                    if (room != null) {
                        room.setId(ds.getKey());
                        if ("Available".equals(room.getStatus())) {
                            roomList.add(room);
                        }
                    }
                }
                adapter.updateData(roomList);
                tvEmpty.setVisibility(roomList.isEmpty() ? View.VISIBLE : View.GONE);
                rvAvailableRooms.setVisibility(roomList.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    @Override
    public void onBookClick(Room room) {
        showBookingDialog(room);
    }

    private void showBookingDialog(Room room) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_book_room, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        TextView tvRoomName = dialogView.findViewById(R.id.tvRoomName);
        EditText etPurpose = dialogView.findViewById(R.id.etPurpose);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etStartTime = dialogView.findViewById(R.id.etStartTime);
        EditText etEndTime = dialogView.findViewById(R.id.etEndTime);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);

        tvRoomName.setText(room.getName());

        // Date picker
        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                etDate.setText(String.format(Locale.getDefault(), "%d %s %d",
                    day, getMonthName(month), year));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Time pickers
        etStartTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(this, (view, hour, minute) -> {
                etStartTime.setText(formatTime(hour, minute));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();
        });

        etEndTime.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new TimePickerDialog(this, (view, hour, minute) -> {
                etEndTime.setText(formatTime(hour, minute));
            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSubmit.setOnClickListener(v -> {
            String purpose = etPurpose.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String startTime = etStartTime.getText().toString().trim();
            String endTime = etEndTime.getText().toString().trim();

            if (purpose.isEmpty() || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            submitBookingRequest(room, purpose, date, startTime + " – " + endTime);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void submitBookingRequest(Room room, String purpose, String date, String time) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference requestRef = FirebaseDatabase.getInstance()
                .getReference("admin_requests").push();

        // Get user name for the request
        FirebaseDatabase.getInstance().getReference("users").child(user.getUid())
            .get().addOnSuccessListener(snapshot -> {
                String userName = snapshot.child("name").getValue(String.class);
                String userRole = snapshot.child("role").getValue(String.class);

                BookingRequest request = new BookingRequest(
                    requestRef.getKey(),
                    room.getId(),
                    room.getName(),
                    userName != null ? userName : "Unknown",
                    userRole != null ? userRole : "Faculty",
                    purpose,
                    date,
                    time,
                    "Pending",
                    user.getUid()
                );

                requestRef.setValue(request).addOnSuccessListener(aVoid -> {
                    // Update room status to "Pending" so others can't book it
                    FirebaseDatabase.getInstance().getReference("rooms")
                        .child(room.getId()).child("status").setValue("Pending");

                    Toast.makeText(this,
                        String.format(getString(R.string.booking_success), room.getName()),
                        Toast.LENGTH_LONG).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, R.string.booking_error, Toast.LENGTH_SHORT).show();
                });
            });
    }

    private String formatTime(int hour, int minute) {
        String amPm = hour >= 12 ? "PM" : "AM";
        int displayHour = hour % 12;
        if (displayHour == 0) displayHour = 12;
        return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm);
    }

    private String getMonthName(int month) {
        String[] months = {"January", "February", "March", "April", "May", "June",
                           "July", "August", "September", "October", "November", "December"};
        return months[month];
    }
}
