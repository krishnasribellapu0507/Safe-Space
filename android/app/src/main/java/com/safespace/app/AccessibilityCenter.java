package com.safespace.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

final class AccessibilityCenter {
    static final String PREFS = "safe_space_accessibility";
    static final String REDUCED_MOTION = "reduced_motion";
    static final String HIGH_CONTRAST = "high_contrast";

    private AccessibilityCenter() {}

    static boolean reducedMotion(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getBoolean(REDUCED_MOTION, false);
    }

    static void show(Activity activity) {
        SharedPreferences p = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String[] items = {"Reduced motion", "Higher contrast"};
        boolean[] checked = {
                p.getBoolean(REDUCED_MOTION, false),
                p.getBoolean(HIGH_CONTRAST, false)
        };
        new AlertDialog.Builder(activity)
                .setTitle("Accessibility")
                .setMultiChoiceItems(items, checked, (dialog, which, enabled) ->
                        p.edit().putBoolean(which == 0 ? REDUCED_MOTION : HIGH_CONTRAST, enabled).apply())
                .setPositiveButton("Done", (dialog, which) ->
                        Toast.makeText(activity, "Accessibility settings saved", Toast.LENGTH_SHORT).show())
                .show();
    }
}
