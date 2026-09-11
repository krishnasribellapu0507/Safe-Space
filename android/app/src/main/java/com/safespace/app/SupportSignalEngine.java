package com.safespace.app;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Small, transparent prototype signal engine used for the SIH demo.
 * It does not diagnose a condition. It only turns locally stored, user-entered
 * wellbeing signals into an explainable support band.
 */
final class SupportSignalEngine {
    static final String PREFS = "safe_space_progress";

    private SupportSignalEngine() {}

    static void recordMood(Context context, String mood, String note) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int count = p.getInt("checkin_count", 0) + 1;
        int positive = p.getInt("positive_checkins", 0);
        if ("Happy".equals(mood) || "Calm".equals(mood) || "Okay".equals(mood)) positive++;
        p.edit()
                .putString("last_mood", mood == null ? "Okay" : mood)
                .putString("last_note", note == null ? "" : note.trim())
                .putLong("last_checkin_at", System.currentTimeMillis())
                .putInt("checkin_count", count)
                .putInt("positive_checkins", positive)
                .apply();
    }

    static void recordJournal(Context context) {
        increment(context, "journal_count");
    }

    static void recordCalm(Context context) {
        increment(context, "calm_count");
    }

    static void recordAppointment(Context context) {
        increment(context, "appointment_count");
    }

    static void recordOfflineQueued(Context context) {
        increment(context, "offline_queue");
    }

    private static void increment(Context context, String key) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        p.edit().putInt(key, p.getInt(key, 0) + 1).apply();
    }

    static int supportScore(Context context) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int score = 24;
        String mood = p.getString("last_mood", "Okay");
        if ("Stressed".equals(mood)) score += 28;
        else if ("Sad".equals(mood) || "Angry".equals(mood)) score += 18;
        else if ("Calm".equals(mood) || "Happy".equals(mood)) score -= 7;

        int missed = p.getInt("missed_checkins", 0);
        score += Math.min(18, missed * 6);
        if (p.getBoolean("upcoming_case_event", false)) score += 18;
        if (p.getBoolean("reduced_engagement", false)) score += 12;

        int calm = p.getInt("calm_count", 0);
        int journals = p.getInt("journal_count", 0);
        int positive = p.getInt("positive_checkins", 0);
        score -= Math.min(14, calm + journals + positive / 2);
        return Math.max(0, Math.min(100, score));
    }

    static String band(Context context) {
        int score = supportScore(context);
        if (score <= 35) return "Stable";
        if (score <= 65) return "Watch";
        return "Elevated support need";
    }

    static String explanation(Context context) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String mood = p.getString("last_mood", "Okay");
        StringBuilder why = new StringBuilder();
        if ("Stressed".equals(mood) || "Sad".equals(mood) || "Angry".equals(mood)) {
            why.append("recent check-in mood changed");
        }
        if (p.getInt("missed_checkins", 0) > 0) appendReason(why, "fewer recent check-ins");
        if (p.getBoolean("reduced_engagement", false)) appendReason(why, "reduced engagement");
        if (p.getBoolean("upcoming_case_event", false)) appendReason(why, "an upcoming justice event");
        if (why.length() == 0) {
            why.append("recent check-ins and engagement are close to your current baseline");
        }
        return "Support level is based on " + why + ". This is not a clinical diagnosis.";
    }

    private static void appendReason(StringBuilder builder, String reason) {
        if (builder.length() > 0) builder.append(" + ");
        builder.append(reason);
    }

    static int recoveryPoints(Context context) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int points = p.getInt("checkin_count", 0) * 2
                + p.getInt("journal_count", 0) * 3
                + p.getInt("calm_count", 0) * 2
                + p.getInt("appointment_count", 0) * 5;
        return Math.min(100, points);
    }

    static String gardenStage(Context context) {
        int p = recoveryPoints(context);
        if (p < 15) return "Seedling";
        if (p < 35) return "Growing garden";
        if (p < 65) return "Blooming garden";
        return "Flourishing garden";
    }
}
