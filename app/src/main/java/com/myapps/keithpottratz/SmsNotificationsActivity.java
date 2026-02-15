package com.myapps.keithpottratz;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

public class SmsNotificationsActivity extends AppCompatActivity {
    private static final int REQ_SEND_SMS = 1001;

    private MaterialButton btnRequest;
    private MaterialButton btnSendTest;
    private TextView       tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_notifications);

        btnRequest  = findViewById(R.id.requestSmsPermission);
        btnSendTest = findViewById(R.id.sendTestSms);
        tvStatus    = findViewById(R.id.permissionStatus);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
        == PackageManager.PERMISSION_GRANTED){
            getSharedPreferences("prefs", MODE_PRIVATE)
                    .edit().putBoolean("seenSmsScreen", true).apply();
            setResult(RESULT_OK);
            finish();
            return;
        }

        // initial UI state
        updateUi();

        btnRequest.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{ Manifest.permission.SEND_SMS },
                        REQ_SEND_SMS
                );
            } else {
                // already granted, just go back
                navigateBack(true);
            }
        });

        btnSendTest.setOnClickListener(v -> {
            // send a test SMS
            SmsManager.getDefault().sendTextMessage(
                    "1234567890", null,
                    "This is a test SMS from InventoryApp", null, null
            );
            tvStatus.setText("Test SMS sent!");
            // after sending, return to inventory
            navigateBack(true);
        });
    }

    private void updateUi() {
        boolean granted = ContextCompat.checkSelfPermission(
                this, Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED;

        tvStatus.setText("Permission: " + (granted ? "granted" : "denied"));
        btnSendTest.setEnabled(granted);
        btnRequest.setText(granted
                ? "SMS Already Enabled"
                : "Enable SMS Notifications"
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_SEND_SMS) {
            boolean granted = grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED;

            tvStatus.setText(granted
                    ? "SMS permission granted!"
                    : "SMS permission denied."
            );
            // after either response, return to inventory
            navigateBack(granted);
        }
    }

    /**
     * Navigate back to the inventory screen
     */
    private void navigateBack(boolean granted) {
        if (granted){
            getSharedPreferences("prefs",MODE_PRIVATE)
                    .edit().putBoolean("seenSmsScreen", true).apply();
            setResult(RESULT_OK);
        }else{
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
