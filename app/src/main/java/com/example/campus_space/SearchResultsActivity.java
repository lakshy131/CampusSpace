package com.example.campus_space;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

public class SearchResultsActivity extends AppCompatActivity implements AvailableRoomAdapter.OnBookClickListener {

    private RecyclerView rvSearchResults;
    private EditText etSearch;
    private TextView tvUserName, tvUserRole, tvNoResults;
    private LinearLayout llBookingSuccess;
    private TextView tvBookingSuccess;
    private AvailableRoomAdapter adapter;
    private List<Room> allRooms = new ArrayList<>();
    private List<Room> filteredRooms = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        rvSearchResults = findViewById(R.id.rvSearchResults);
        etSearch = findViewById(R.id.etSearch);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserRole = findViewById(R.id.tvUserRole);
        tvNoResults = findViewById(R.id.tvNoResults);
        llBookingSuccess = findViewById(R.id.llBookingSuccess);
        tvBookingSuccess = findViewById(R.id.tvBookingSuccess);

        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AvailableRoomAdapter(filteredRooms, this);
        rvSearchResults.setAdapter(adapter);

        // Load user info
        loadUserInfo();

        // Load all rooms
        loadAllRooms();

        // Get initial query
        String initialQuery = getIntent().getStringExtra("query");
        if (initialQuery != null) {
            etSearch.setText(initialQuery);
        }

        // Search filter
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterRooms(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseDatabase.getInstance().getReference("users").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("name").getValue(String.class);
                        String role = snapshot.child("role").getValue(String.class);
                        if (name != null) tvUserName.setText(name);
                        if (role != null) tvUserRole.setText(role);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
        }
    }

    private void loadAllRooms() {
        FirebaseDatabase.getInstance().getReference("rooms")
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    allRooms.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Room room = ds.getValue(Room.class);
                        if (room != null) {
                            room.setId(ds.getKey());
                            allRooms.add(room);
                        }
                    }
                    // Apply initial filter
                    String query = etSearch.getText().toString();
                    if (!query.isEmpty()) {
                        filterRooms(query);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
    }

    private void filterRooms(String query) {
        filteredRooms.clear();
        if (query.isEmpty()) {
            filteredRooms.addAll(allRooms);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Room room : allRooms) {
                if (room.getName().toLowerCase().contains(lowerQuery) ||
                    room.getType().toLowerCase().contains(lowerQuery)) {
                    filteredRooms.add(room);
                }
            }
        }
        adapter.updateData(filteredRooms);
        tvNoResults.setVisibility(filteredRooms.isEmpty() ? View.VISIBLE : View.GONE);
        rvSearchResults.setVisibility(filteredRooms.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onBookClick(Room room) {
        showBookingDialog(room);
    }

    private void showBookingDialog(Room room) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_book_room, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(dialogView).create();

        TextView tvRoomName = dialogView.findViewById(R.id.tvRoomName);
        EditText etPurpose = dialogView.findViewById(R.id.etPurpose);
        EditText etDate = dialogView.findViewById(R.id.etDate);
        EditText etStartTime = dialogView.findViewById(R.id.etStartTime);
        EditText etEndTime = dialogView.findViewById(R.id.etEndTime);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSubmit = dialogView.findViewById(R.id.btnSubmit);

        tvRoomName.setText(room.getName());

        etDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                etDate.setText(String.format(Locale.getDefault(), "%d %s %d",
                    day, getMonthName(month), year));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });

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

        FirebaseDatabase.getInstance().getReference("users").child(user.getUid())
            .get().addOnSuccessListener(snapshot -> {
                String userName = snapshot.child("name").getValue(String.class);
                String userRole = snapshot.child("role").getValue(String.class);

                BookingRequest request = new BookingRequest(
                    requestRef.getKey(), room.getName(),
                    userName != null ? userName : "Unknown",
                    userRole != null ? userRole : "Faculty",
                    purpose, date, time, "Pending", user.getUid()
                );

                requestRef.setValue(request).addOnSuccessListener(aVoid -> {
                    tvBookingSuccess.setText(String.format(getString(R.string.booking_success), room.getName()));
                    llBookingSuccess.setVisibility(View.VISIBLE);
                    // Hide after 3 seconds
                    llBookingSuccess.postDelayed(() -> llBookingSuccess.setVisibility(View.GONE), 3000);
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
