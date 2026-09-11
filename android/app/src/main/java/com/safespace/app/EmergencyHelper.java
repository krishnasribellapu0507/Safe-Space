package com.safespace.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

final class EmergencyHelper {
    static final int REQUEST_SOS_PERMISSIONS = 906;
    private EmergencyHelper() {}

    static boolean ensurePermissions(Activity activity) {
        List<String> missing = new ArrayList<>();
        if (activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            missing.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (activity.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            missing.add(Manifest.permission.SEND_SMS);
        }
        if (activity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            missing.add(Manifest.permission.CALL_PHONE);
        }
        if (missing.isEmpty()) return true;
        activity.requestPermissions(missing.toArray(new String[0]), REQUEST_SOS_PERMISSIONS);
        Toast.makeText(activity, "Allow location, SMS and phone permissions, then tap SOS again.", Toast.LENGTH_LONG).show();
        return false;
    }

    static void sendTrustedSos(Activity activity, boolean callAfter) {
        SharedPreferences prefs = activity.getSharedPreferences("safe_space_safety", Context.MODE_PRIVATE);
        String phone = prefs.getString("trusted_phone", "").trim();
        String name = prefs.getString("trusted_name", "Trusted contact");
        if (phone.isEmpty()) {
            Toast.makeText(activity, "Save a trusted contact first.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!ensurePermissions(activity)) return;

        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        AtomicBoolean sent = new AtomicBoolean(false);
        Handler handler = new Handler(Looper.getMainLooper());
        Location fallback = bestLastKnown(activity, lm);

        LocationListener listener = location -> {
            if (sent.compareAndSet(false, true)) {
                try { lm.removeUpdates(thisListenerHolder[0]); } catch (Exception ignored) {}
                sendMessage(activity, phone, location);
                if (callAfter) callTrusted(activity, phone);
            }
        };
        thisListenerHolder[0] = listener;

        try {
            lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, Looper.getMainLooper());
        } catch (Exception ignored) {
            try { lm.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, Looper.getMainLooper()); }
            catch (Exception ignored2) {}
        }

        handler.postDelayed(() -> {
            if (sent.compareAndSet(false, true)) {
                try { lm.removeUpdates(thisListenerHolder[0]); } catch (Exception ignored) {}
                sendMessage(activity, phone, fallback);
                if (callAfter) callTrusted(activity, phone);
            }
        }, 4500L);
        Toast.makeText(activity, "Getting your location and alerting " + name + "…", Toast.LENGTH_SHORT).show();
    }

    private static final LocationListener[] thisListenerHolder = new LocationListener[1];

    static void sendFromBackground(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("safe_space_safety", Context.MODE_PRIVATE);
        String phone = prefs.getString("trusted_phone", "").trim();
        if (phone.isEmpty()) return;
        if (context.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) return;
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Location loc = bestLastKnown(context, lm);
        sendMessage(context, phone, loc);
        SosNotification.postStatus(context, "SOS message sent to your trusted contact");
    }

    private static Location bestLastKnown(Context context, LocationManager lm) {
        if (lm == null || context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) return null;
        try {
            Location gps = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location net = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (gps == null) return net;
            if (net == null) return gps;
            return gps.getTime() >= net.getTime() ? gps : net;
        } catch (Exception e) {
            return null;
        }
    }

    private static void sendMessage(Context context, String phone, Location location) {
        String profile = context.getSharedPreferences("safe_space_profile", Context.MODE_PRIVATE)
                .getString("display_name", "Safe Space user");
        StringBuilder body = new StringBuilder("Emergency SOS from ").append(profile)
                .append(". Please contact me immediately.");
        if (location != null) {
            body.append(" My location: https://maps.google.com/?q=")
                    .append(location.getLatitude()).append(',').append(location.getLongitude());
        } else {
            body.append(" Location could not be refreshed; please call me now.");
        }
        try {
            SmsManager.getDefault().sendTextMessage(phone, null, body.toString(), null, null);
            Toast.makeText(context, "SOS message sent.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Could not send SMS. Check SIM/SMS permission.", Toast.LENGTH_LONG).show();
        }
    }

    static void callTrusted(Activity activity, String phone) {
        if (phone == null || phone.trim().isEmpty()) return;
        Intent call = new Intent(activity.checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                ? Intent.ACTION_CALL : Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phone.trim())));
        activity.startActivity(call);
    }

    static void dial(Activity activity, String phone) {
        activity.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
    }
}
