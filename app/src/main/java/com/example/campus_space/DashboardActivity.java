package com.example.campus_space;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvUserName, tvUserRole, tvNoResults;
    private EditText etSearch;
    private ImageView btnLogout;
    private FrameLayout flNotification;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        sessionManager = new SessionManager(this);

        tvUserName = findViewById(R.id.tvUserName);
        tvUserRole = findViewById(R.id.tvUserRole);
        tvNoResults = findViewById(R.id.tvNoResults);
        etSearch = findViewById(R.id.etSearch);
        btnLogout = findViewById(R.id.btnLogout);
        flNotification = findViewById(R.id.flNotification);

        Toast.makeText(this, "Welcome to CampusSpace Dashboard", Toast.LENGTH_SHORT).show();

        // Load user info
        loadUserInfo();

        // Setup click listeners
        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            sessionManager.logout();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        flNotification.setOnClickListener(v -> {
            startActivity(new Intent(this, NotificationsActivity.class));
        });

        // Quick Access Card Clicks
        findViewById(R.id.cardAvailableRooms).setOnClickListener(v ->
            startActivity(new Intent(this, AvailableRoomsActivity.class)));

        findViewById(R.id.cardBookedRooms).setOnClickListener(v ->
            startActivity(new Intent(this, BookedRoomsActivity.class)));

        findViewById(R.id.cardMyBookings).setOnClickListener(v ->
            startActivity(new Intent(this, MyBookingsActivity.class)));

        findViewById(R.id.cardEvents).setOnClickListener(v ->
            startActivity(new Intent(this, EventsActivity.class)));

        findViewById(R.id.cardContact).setOnClickListener(v ->
            startActivity(new Intent(this, ChatActivity.class)));

        // Search functionality
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    // Navigate to search results
                    Intent intent = new Intent(DashboardActivity.this, SearchResultsActivity.class);
                    intent.putExtra("query", s.toString());
                    startActivity(intent);
                    etSearch.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadUserInfo() {
        String name = sessionManager.getUserName();
        String role = sessionManager.getUserRole();
        
        if (!name.isEmpty()) {
            tvUserName.setText(name);
        }
        if (!role.isEmpty()) {
            tvUserRole.setText(role);
        }
    }
}
