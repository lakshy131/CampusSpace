package com.example.campus_space;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.campus_space.fragments.AdminChatsFragment;
import com.example.campus_space.fragments.AdminRequestsFragment;
import com.example.campus_space.fragments.AdminScheduleFragment;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AdminDashboardActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageView btnLogout;
    private ExtendedFloatingActionButton fabAddRoom, fabAddEvent;

    private String[] tabTitles = {"Requests", "Schedule", "Chats"};
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        btnLogout = findViewById(R.id.btnLogout);
        fabAddRoom = findViewById(R.id.fabAddRoom);
        fabAddEvent = findViewById(R.id.fabAddEvent);
        sessionManager = new SessionManager(this);

        // Setup ViewPager2 with adapter
        AdminPagerAdapter pagerAdapter = new AdminPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
            (tab, position) -> tab.setText(tabTitles[position])
        ).attach();

        // FAB Actions
        fabAddRoom.setOnClickListener(v -> showAddRoomDialog());
        fabAddEvent.setOnClickListener(v -> showAddEventDialog());

        // Logout
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void showAddRoomDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_room, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        EditText etRoomName = view.findViewById(R.id.etRoomName);
        EditText etCapacity = view.findViewById(R.id.etCapacity);
        Spinner spinnerRoomType = view.findViewById(R.id.spinnerRoomType);
        Button btnAdd = view.findViewById(R.id.btnAddRoom);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        String[] types = {"Classroom", "Lab", "Conference Room"};
        spinnerRoomType.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types));

        btnAdd.setOnClickListener(v -> {
            String name = etRoomName.getText().toString().trim();
            String capStr = etCapacity.getText().toString().trim();
            String type = spinnerRoomType.getSelectedItem().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(capStr)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> room = new HashMap<>();
            room.put("name", name);
            room.put("type", type);
            room.put("capacity", Integer.parseInt(capStr));
            room.put("status", "Available");

            FirebaseDatabase.getInstance().getReference("rooms").push().setValue(room)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Room Added!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showAddEventDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(view).create();

        EditText etName = view.findViewById(R.id.etEventName);
        EditText etVenue = view.findViewById(R.id.etVenue);
        EditText etOrganizer = view.findViewById(R.id.etOrganizer);
        EditText etDate = view.findViewById(R.id.etDate);
        EditText etTime = view.findViewById(R.id.etTime);
        Button btnAdd = view.findViewById(R.id.btnAddEvent);
        Button btnCancel = view.findViewById(R.id.btnCancel);

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String venue = etVenue.getText().toString().trim();
            String org = etOrganizer.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String time = etTime.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(venue)) {
                Toast.makeText(this, "Name and Venue required", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> event = new HashMap<>();
            event.put("name", name);
            event.put("venue", venue);
            event.put("organizer", org);
            event.put("date", date);
            event.put("time", time);
            event.put("dateBadge", "New");

            FirebaseDatabase.getInstance().getReference("events").push().setValue(event)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Event Added!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private static class AdminPagerAdapter extends FragmentStateAdapter {

        public AdminPagerAdapter(FragmentActivity fa) {
            super(fa);
        }

        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0: return new AdminRequestsFragment();
                case 1: return new AdminScheduleFragment();
                case 2: return new AdminChatsFragment();
                default: return new AdminRequestsFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
}
