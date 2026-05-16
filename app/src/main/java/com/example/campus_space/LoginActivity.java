package com.example.campus_space;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private ImageView ivTogglePassword;
    private Button btnLogin;
    private TextView tvResetPassword, tvError, tvGoToRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private SessionManager sessionManager;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvResetPassword = findViewById(R.id.tvResetPassword);
        tvGoToRegister = findViewById(R.id.tvGoToRegister);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);

        setupPasswordToggle();

        btnLogin.setOnClickListener(v -> loginUser());
        tvResetPassword.setOnClickListener(v -> resetPassword());
        tvGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void setupPasswordToggle() {
        ivTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye);
            } else {
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivTogglePassword.setImageResource(R.drawable.ic_eye_off);
            }
            etPassword.setSelection(etPassword.getText().length());
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            showError(getString(R.string.email_required));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showError(getString(R.string.password_required));
            return;
        }

        tvError.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        // Timeout Fallback (15 seconds)
        new Handler().postDelayed(() -> {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                showError("Request timed out. Please check your internet.");
            }
        }, 15000);

        Toast.makeText(this, "Authenticating...", Toast.LENGTH_SHORT).show();

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        fetchUserRoleAndSession(user);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    String errorMsg = task.getException() != null ? task.getException().getMessage() : "Login Failed";
                    showError(errorMsg);
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            });
    }

    private void fetchUserRoleAndSession(FirebaseUser user) {
        Toast.makeText(this, "Checking user role...", Toast.LENGTH_SHORT).show();
        
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(user.getUid())
            .get()
            .addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (task.isSuccessful() && task.getResult() != null) {
                    com.google.firebase.database.DataSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        String name = snapshot.child("name").getValue(String.class);
                        String role = snapshot.child("role").getValue(String.class);
                        String email = snapshot.child("email").getValue(String.class);

                        sessionManager.createSession(user.getUid(), 
                            name != null ? name : "User", 
                            email != null ? email : user.getEmail(), 
                            role != null ? role : "Student");

                        if ("Admin".equals(role)) {
                            startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                        } else {
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        }
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Profile not found in database.", Toast.LENGTH_LONG).show();
                        mAuth.signOut();
                    }
                } else {
                    String error = task.getException() != null ? task.getException().getMessage() : "Failed to fetch role";
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                    // If role fetch fails, we still have auth, but can't route safely.
                }
            });
    }

    private void resetPassword() {
        String email = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            showError(getString(R.string.email_required));
            return;
        }

        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, R.string.password_reset_sent, Toast.LENGTH_SHORT).show();
                } else {
                    showError("Failed to send reset email");
                }
            });
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}
