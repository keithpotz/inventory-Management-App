// SmsNotifier.java
package com.myapps.keithpottratz;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

import androidx.core.content.ContextCompat;

public class SmsNotifier {

    public static final String ALERT_PHONE = "1234567890";
    public static final int LOW_STOCK_THRESHOLD = 3;

    public static boolean canSend(Context ctx) {
        return ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void sendLowStockAlert(Context ctx, InventoryItem item) {
        if (!canSend(ctx)) return; // gracefully no-op if denied
        String msg = "Low stock alert: " + item.getName() + " is at Qty " + item.getQuantity();
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(ALERT_PHONE, null, msg, null, null);
    }
}
