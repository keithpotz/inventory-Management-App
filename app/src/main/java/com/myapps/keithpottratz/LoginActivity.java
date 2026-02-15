package com.myapps.keithpottratz;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText usernameInput;
    private TextInputEditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Edge-to-edge padding hook
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginButton), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(v -> {
            String username = safeText(usernameInput);
            String password = safeText(passwordInput);
            handleLogin(username, password);
        });
    }

    private void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        UserDao userDao = AppDatabase.getInstance(this).userDao();
        User existing = userDao.findByUsername(username);

        String inputHash = sha256(password);

        if (existing == null) {
            // Create new account
            long id = userDao.insert(new User(username, inputHash));
            if (id > 0) {
                Toast.makeText(this, "Account created. Welcome, " + username + "!", Toast.LENGTH_SHORT).show();
                goToInventory();
            } else {
                Toast.makeText(this, "Unable to create account.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Login existing
            if (inputHash.equals(existing.passwordHash)) {
                Toast.makeText(this, "Welcome back, " + username + "!", Toast.LENGTH_SHORT).show();
                goToInventory();
            } else {
                Toast.makeText(this, "Incorrect password.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void goToInventory() {
        startActivity(new Intent(this, InventoryActivity.class));
        finish();
    }

    private static String safeText(TextInputEditText editText) {
        CharSequence cs = editText.getText();
        return cs == null ? "" : cs.toString().trim();
    }
//hashing passwords
    private static String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            // Convert to hex
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(e);
        }
    }
}
