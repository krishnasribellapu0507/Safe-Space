package com.safespace.app;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

final class SosNotification {
    private static final String CHANNEL = "safe_space_sos";
    private static final int ID = 911;
    private SosNotification() {}

    static void refreshIfEnabled(Context context) {
        boolean enabled = context.getSharedPreferences("safe_space_safety", Context.MODE_PRIVATE)
                .getBoolean("lockscreen_sos", false);
        if (!enabled) return;
        if (Build.VERSION.SDK_INT >= 33 && context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "Emergency SOS", NotificationManager.IMPORTANCE_HIGH);
            c.setDescription("Lock-screen Safe Space SOS shortcut");
            c.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(c);
        }
        Intent tap = new Intent(context, SosReceiver.class).setAction("com.safespace.app.SOS_TAP");
        PendingIntent tapPi = PendingIntent.getBroadcast(context, 911, tap,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Intent open = new Intent(context, MainActivity.class);
        PendingIntent openPi = PendingIntent.getActivity(context, 912, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(context, CHANNEL) : new Notification.Builder(context);
        Notification n = b.setSmallIcon(R.drawable.safe_space_logo)
                .setContentTitle("Safe Space SOS ready")
                .setContentText("In danger? Tap SOS 3 times to alert your trusted contact.")
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setOngoing(true)
                .setContentIntent(openPi)
                .addAction(new Notification.Action.Builder(null, "SOS", tapPi).build())
                .build();
        nm.notify(ID, n);
    }

    static void postStatus(Context context, String text) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel c = new NotificationChannel(CHANNEL, "Emergency SOS", NotificationManager.IMPORTANCE_HIGH);
            c.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            nm.createNotificationChannel(c);
        }
        Notification.Builder b = Build.VERSION.SDK_INT >= 26 ? new Notification.Builder(context, CHANNEL) : new Notification.Builder(context);
        nm.notify(ID + 1, b.setSmallIcon(R.drawable.safe_space_logo)
                .setContentTitle("Safe Space SOS")
                .setContentText(text)
                .setVisibility(Notification.VISIBILITY_PUBLIC).build());
    }
}
