package com.safespace.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public final class SosReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        SharedPreferences p = context.getSharedPreferences("safe_space_safety", Context.MODE_PRIVATE);
        long now = System.currentTimeMillis();
        long last = p.getLong("sos_last_tap", 0L);
        int count = (now - last <= 8000L) ? p.getInt("sos_tap_count", 0) + 1 : 1;
        p.edit().putLong("sos_last_tap", now).putInt("sos_tap_count", count).apply();
        if (count >= 3) {
            p.edit().putInt("sos_tap_count", 0).apply();
            EmergencyHelper.sendFromBackground(context);
        } else {
            Toast.makeText(context, "SOS " + count + "/3 — tap " + (3 - count) + " more time(s)", Toast.LENGTH_SHORT).show();
        }
    }
}
