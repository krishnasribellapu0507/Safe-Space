package com.safespace.app;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Transparent, deterministic wellbeing-change engine for the demo build.
 *
 * It compares recent user-entered signals with that user's own earlier pattern.
 * It does not diagnose a condition and does not use a population/clinical cutoff.
 */
final class SupportSignalEngine {
    static final String PREFS = "safe_space_progress";
    private static final int HISTORY_LIMIT = 30;

    private SupportSignalEngine() {}

    static void recordMood(Context context, String mood, String note) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        int value = moodValue(mood);
        int count = p.getInt("checkin_count", 0) + 1;
        int positive = p.getInt("positive_checkins", 0);
        if (value >= 4) positive++;
        p.edit()
                .putString("last_mood", mood == null ? "Okay" : mood)
                .putInt("last_mood_value", value)
                .putString("last_note", note == null ? "" : note.trim())
                .putLong("last_checkin_at", System.currentTimeMillis())
                .putInt("checkin_count", count)
                .putInt("positive_checkins", positive)
                .putString("mood_history", append(p.getString("mood_history", ""), value))
                .apply();
    }

    static void recordDetailedWellbeing(Context context, int mood, int sleep, int stress,
                                        int energy, int safety, int connection) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor e = p.edit();
        putSignal(e, p, "mood", mood);
        putSignal(e, p, "sleep", sleep);
        putSignal(e, p, "stress", stress);
        putSignal(e, p, "energy", energy);
        putSignal(e, p, "safety", safety);
        putSignal(e, p, "connection", connection);
        e.putLong("last_detailed_checkin_at", System.currentTimeMillis()).apply();
    }

    private static void putSignal(SharedPreferences.Editor e, SharedPreferences p,
                                  String key, int value) {
        int safe = clamp(value, 1, 5);
        e.putInt("last_" + key, safe);
        e.putString(key + "_history", append(p.getString(key + "_history", ""), safe));
    }

    static void recordJournal(Context context) { increment(context, "journal_count"); }
    static void recordCalm(Context context) { increment(context, "calm_count"); }
    static void recordAppointment(Context context) { increment(context, "appointment_count"); }
    static void recordOfflineQueued(Context context) { increment(context, "offline_queue"); }

    private static void increment(Context context, String key) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        p.edit().putInt(key, p.getInt(key, 0) + 1).apply();
    }

    static boolean hasBaseline(Context context) {
        return values(context, "mood_history").length >= 4;
    }

    /** Higher means the recent self-reported pattern is doing better. Non-clinical. */
    static int wellbeingScore(Context context) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        double mood = recentOr(p, "mood", 3.0);
        double sleep = recentOr(p, "sleep", 3.0);
        double stress = recentOr(p, "stress", 3.0);
        double energy = recentOr(p, "energy", 3.0);
        double safety = recentOr(p, "safety", 4.0);
        double connection = recentOr(p, "connection", 3.0);
        double score = normalize(mood) * .25
                + normalize(sleep) * .15
                + (100 - normalize(stress)) * .20
                + normalize(energy) * .15
                + normalize(safety) * .15
                + normalize(connection) * .10;
        return clamp((int) Math.round(score), 0, 100);
    }

    /** Legacy API retained for existing screens: this is concern intensity, not a diagnosis. */
    static int supportScore(Context context) {
        return 100 - wellbeingScore(context);
    }

    static String band(Context context) {
        if (!hasBaseline(context)) return "Building baseline";
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        double delta = wellbeingDelta(p);
        double safety = recentOr(p, "safety", 4.0);
        double stress = recentOr(p, "stress", 3.0);
        if (safety <= 2.0 || stress >= 4.6 || delta <= -25) return "Consider additional support";
        if (delta <= -14) return "Needs attention";
        if (delta <= -6) return "Slight change";
        return "Stable";
    }

    static String explanation(Context context) {
        if (!hasBaseline(context)) {
            return "Keep checking in and Safe Space will compare new entries with your own pattern over time. "
                    + "This is a wellbeing reflection, not a clinical score.";
        }
        String signals = contributingSignals(context);
        return "Compared with your personal baseline: " + signals
                + ". This is an explainable wellbeing signal, not a diagnosis.";
    }

    static String contributingSignals(Context context) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        List<String> reasons = new ArrayList<>();
        addChange(reasons, p, "mood", false, "lower mood");
        addChange(reasons, p, "sleep", false, "less sleep");
        addChange(reasons, p, "stress", true, "higher stress");
        addChange(reasons, p, "energy", false, "lower energy");
        addChange(reasons, p, "safety", false, "lower sense of safety");
        addChange(reasons, p, "connection", false, "less social connection");
        if (p.getBoolean("reduced_engagement", false)) reasons.add("fewer recent check-ins");
        if (p.getBoolean("upcoming_case_event", false)) reasons.add("an upcoming high-stress case event");
        if (reasons.isEmpty()) return "recent check-ins are close to your usual pattern";
        return join(reasons);
    }

    static String thingsHelping(Context context) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        List<String> items = new ArrayList<>();
        if (delta(p, "sleep") >= .45) items.add("sleep has improved");
        if (delta(p, "energy") >= .45) items.add("energy is improving");
        if (p.getInt("calm_count", 0) > 0) items.add("you have used calming tools");
        if (p.getInt("journal_count", 0) > 0) items.add("you have been reflecting in your journal");
        return items.isEmpty() ? "regular check-ins help build a clearer picture" : join(items);
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

    static void clearDemoData(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply();
    }

    private static void addChange(List<String> out, SharedPreferences p, String key,
                                  boolean higherIsConcern, String label) {
        int[] v = parse(p.getString(key + "_history", ""));
        if (v.length < 4) return;
        double d = delta(v);
        if ((!higherIsConcern && d <= -.55) || (higherIsConcern && d >= .55)) out.add(label);
    }

    private static double wellbeingDelta(SharedPreferences p) {
        int[] mood = parse(p.getString("mood_history", ""));
        if (mood.length < 4) return 0;
        double recent = normalize(recent(mood));
        double base = normalize(baseline(mood));
        double delta = (recent - base) * .35;

        delta += normalizedDelta(p, "sleep", false) * .15;
        delta += normalizedDelta(p, "stress", true) * .20;
        delta += normalizedDelta(p, "energy", false) * .10;
        delta += normalizedDelta(p, "safety", false) * .12;
        delta += normalizedDelta(p, "connection", false) * .08;
        return delta;
    }

    private static double normalizedDelta(SharedPreferences p, String key, boolean inverse) {
        int[] v = parse(p.getString(key + "_history", ""));
        if (v.length < 4) return 0;
        double d = normalize(recent(v)) - normalize(baseline(v));
        return inverse ? -d : d;
    }

    private static double recentOr(SharedPreferences p, String key, double fallback) {
        int[] v = parse(p.getString(key + "_history", ""));
        if (v.length == 0 && "mood".equals(key)) v = parse(p.getString("mood_history", ""));
        return v.length == 0 ? fallback : recent(v);
    }

    private static double delta(SharedPreferences p, String key) {
        int[] v = parse(p.getString(key + "_history", ""));
        return v.length < 4 ? 0 : delta(v);
    }

    private static double delta(int[] values) { return recent(values) - baseline(values); }

    private static double recent(int[] values) {
        int take = Math.min(3, values.length);
        double sum = 0;
        for (int i = values.length - take; i < values.length; i++) sum += values[i];
        return sum / take;
    }

    private static double baseline(int[] values) {
        int end = Math.max(1, values.length - Math.min(3, values.length));
        double sum = 0;
        for (int i = 0; i < end; i++) sum += values[i];
        return sum / end;
    }

    private static int[] values(Context context, String key) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return parse(p.getString(key, ""));
    }

    private static int moodValue(String mood) {
        if (mood == null) return 3;
        String m = mood.trim().toLowerCase();
        if (m.equals("great") || m.equals("happy")) return 5;
        if (m.equals("good") || m.equals("calm")) return 4;
        if (m.equals("okay")) return 3;
        if (m.equals("low") || m.equals("sad")) return 2;
        if (m.equals("overwhelmed") || m.equals("stressed") || m.equals("angry")) return 1;
        return 3;
    }

    private static String append(String current, int value) {
        String next = current == null || current.trim().isEmpty()
                ? String.valueOf(value) : current + "," + value;
        String[] parts = next.split(",");
        if (parts.length <= HISTORY_LIMIT) return next;
        StringBuilder b = new StringBuilder();
        for (int i = parts.length - HISTORY_LIMIT; i < parts.length; i++) {
            if (b.length() > 0) b.append(',');
            b.append(parts[i]);
        }
        return b.toString();
    }

    private static int[] parse(String csv) {
        if (csv == null || csv.trim().isEmpty()) return new int[0];
        String[] parts = csv.split(",");
        int[] out = new int[parts.length];
        int count = 0;
        for (String part : parts) {
            try { out[count++] = clamp(Integer.parseInt(part.trim()), 1, 5); }
            catch (NumberFormatException ignored) { }
        }
        if (count == out.length) return out;
        int[] compact = new int[count];
        System.arraycopy(out, 0, compact, 0, count);
        return compact;
    }

    private static double normalize(double value) { return (value - 1.0) * 25.0; }
    private static int clamp(int value, int min, int max) { return Math.max(min, Math.min(max, value)); }

    private static String join(List<String> values) {
        StringBuilder b = new StringBuilder();
        for (String value : values) {
            if (b.length() > 0) b.append(" • ");
            b.append(value);
        }
        return b.toString();
    }
}
