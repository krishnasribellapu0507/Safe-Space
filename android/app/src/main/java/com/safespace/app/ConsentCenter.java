package com.safespace.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/** Human-readable consent controls. Sensitive analysis is opt-in in the demo build. */
final class ConsentCenter {
    private static final String PREFS = "safe_space_consent";
    private static final String[] KEYS = {
            "journal_ai", "analytics", "location", "voice_transcription", "notifications", "support_sharing"
    };
    private static final String[] LABELS = {
            "Journal intelligence\nAnalyze only opted-in journal signals; full entries stay private.",
            "Product analytics\nShare non-journal usage events to improve the product.",
            "Location\nUse location only when I explicitly open nearby support.",
            "Voice transcription\nTranscribe a voice journal only when I request it.",
            "Gentle notifications\nAllow non-sensitive wellbeing reminders.",
            "Support personnel sharing\nShare permitted wellbeing trends with authorized support staff."
    };

    private ConsentCenter() {}

    static boolean allowed(Context context, String key) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        return p.getBoolean(key, "notifications".equals(key));
    }

    static void show(Activity activity) {
        SharedPreferences p = activity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        boolean[] checked = new boolean[KEYS.length];
        for (int i = 0; i < KEYS.length; i++) checked[i] = allowed(activity, KEYS[i]);

        new AlertDialog.Builder(activity)
                .setTitle("Consent Center")
                .setMessage("You choose what Safe Space may use. Turning a permission off does not block your private journal or saved calming tools.")
                .setMultiChoiceItems(LABELS, checked, (dialog, which, isChecked) ->
                        p.edit().putBoolean(KEYS[which], isChecked).apply())
                .setPositiveButton("Done", (dialog, which) ->
                        Toast.makeText(activity, "Privacy choices saved", Toast.LENGTH_SHORT).show())
                .show();
    }
}
