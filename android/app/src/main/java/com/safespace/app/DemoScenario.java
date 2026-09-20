package com.safespace.app;

import android.content.Context;
import android.content.SharedPreferences;

/** Repeatable synthetic judge-demo scenario. Never mixes with production/user records. */
final class DemoScenario {
    private DemoScenario() {}

    static void reset(Context context) {
        SharedPreferences p = context.getSharedPreferences(SupportSignalEngine.PREFS, Context.MODE_PRIVATE);
        p.edit().clear()
                .putBoolean("demo_mode", true)
                .putString("mood_history", "4,3,2,2,1")
                .putString("sleep_history", "4,4,3,2,2")
                .putString("stress_history", "2,2,3,4,5")
                .putString("energy_history", "4,4,3,2,2")
                .putString("safety_history", "5,5,4,4,4")
                .putString("connection_history", "4,4,3,3,2")
                .putString("last_mood", "Overwhelmed")
                .putInt("last_mood_value", 1)
                .putInt("last_sleep", 2)
                .putInt("last_stress", 5)
                .putInt("last_energy", 2)
                .putInt("last_safety", 4)
                .putInt("last_connection", 2)
                .putInt("checkin_count", 5)
                .putInt("positive_checkins", 1)
                .putInt("journal_count", 1)
                .putInt("calm_count", 1)
                .putBoolean("reduced_engagement", false)
                .putBoolean("upcoming_case_event", false)
                .putLong("last_checkin_at", System.currentTimeMillis())
                .apply();

        context.getSharedPreferences("safe_space_profile", Context.MODE_PRIVATE).edit()
                .putString("display_name", "Krishnasri").apply();
    }

    static boolean isEnabled(Context context) {
        return context.getSharedPreferences(SupportSignalEngine.PREFS, Context.MODE_PRIVATE)
                .getBoolean("demo_mode", false);
    }
}
