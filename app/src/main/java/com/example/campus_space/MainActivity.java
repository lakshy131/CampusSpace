package com.example.campus_space;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        // Check if user is already logged in using SessionManager
        new Handler().postDelayed(() -> {
            if (sessionManager.isLoggedIn()) {
                String role = sessionManager.getUserRole();
                if ("Admin".equals(role)) {
                    startActivity(new Intent(this, AdminDashboardActivity.class));
                } else {
                    startActivity(new Intent(this, DashboardActivity.class));
                }
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 1500); // Give 1.5 seconds to show splash screen
    }
}
