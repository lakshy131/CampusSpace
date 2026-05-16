package com.example.campus_space;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Spinner spinnerRole;
    private ImageView ivTogglePassword, ivToggleConfirmPassword;
    private Button btnRegister;
    private TextView tvGoToLogin, tvError;
    private ProgressBar progressBar;

    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    private FirebaseAuth mAuth;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);
        ivToggleConfirmPassword = findViewById(R.id.ivToggleConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        tvError = findViewById(R.id.tvError);
        progressBar = findViewById(R.id.progressBar);

        setupRoleSpinner();
        setupPasswordToggles();

        btnRegister.setOnClickListener(v -> registerUser());

        tvGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void setupRoleSpinner() {
        String[] roles = {"Student", "Faculty", "Admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);
    }

    private void setupPasswordToggles() {
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

        ivToggleConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye);
            } else {
                etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                ivToggleConfirmPassword.setImageResource(R.drawable.ic_eye_off);
            }
            etConfirmPassword.setSelection(etConfirmPassword.getText().length());
        });
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String role = spinnerRole.getSelectedItem().toString();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            showError("All fields are required");
            return;
        }

        if (password.length() < 6) {
            showError("Password too short");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords mismatch");
            return;
        }

        tvError.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        btnRegister.setEnabled(false);

        Toast.makeText(this, "Creating Auth Account...", Toast.LENGTH_SHORT).show();

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        saveUserToDatabase(user.getUid(), name, email, role);
                    }
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        // Account already exists in Auth. Sign them in and force update the Database.
                        Toast.makeText(this, "Account exists. Updating database...", Toast.LENGTH_SHORT).show();
                        mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(signInTask -> {
                                if (signInTask.isSuccessful() && mAuth.getCurrentUser() != null) {
                                    saveUserToDatabase(mAuth.getCurrentUser().getUid(), name, email, role);
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    btnRegister.setEnabled(true);
                                    showError("Auth Failed: Could not sign in to existing account.");
                                }
                            });
                    } else {
                        progressBar.setVisibility(View.GONE);
                        btnRegister.setEnabled(true);
                        showError("Auth Failed: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                }
            });
    }

    private void saveUserToDatabase(String uid, String name, String email, String role) {
        Toast.makeText(this, "Storing user details...", Toast.LENGTH_SHORT).show();
        
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("role", role);
        userMap.put("uid", uid);

        // Explicitly using the default instance
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        
        usersRef.setValue(userMap)
            .addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                btnRegister.setEnabled(true);

                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Success! Welcome " + name, Toast.LENGTH_SHORT).show();
                    sessionManager.createSession(uid, name, email, role);
                    
                    if ("Admin".equals(role)) {
                        startActivity(new Intent(RegisterActivity.this, AdminDashboardActivity.class));
                    } else {
                        startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                    }
                    finish();
                } else {
                    String errorMsg = task.getException() != null ? task.getException().getMessage() : "Database Write Failed";
                    showError("DB Error: " + errorMsg);
                    Log.e("FirebaseRegister", "Error writing to DB", task.getException());
                    
                    // Fallback: If DB write fails but Auth succeeded, we still have session but DB is out of sync
                    Toast.makeText(RegisterActivity.this, "Auth ok, but DB failed. Please check Rules.", Toast.LENGTH_LONG).show();
                }
            });
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
    }
}
