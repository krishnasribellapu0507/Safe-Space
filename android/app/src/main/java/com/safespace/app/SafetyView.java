package com.safespace.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/** Emergency support screen with trusted contact, SOS and audible alarm. */
final class SafetyView extends PastelScreenView {
    private final SharedPreferences prefs;
    private final EditText nameField;
    private final EditText phoneField;
    private ToneGenerator alarm;
    private long firstTapAt;
    private int sosTapCount;

    SafetyView(Activity activity, ScreenNavigator navigator) {
        super(activity, navigator, -1);
        prefs = activity.getSharedPreferences("safe_space_safety", Context.MODE_PRIVATE);
        content.addView(header("Safety & SOS", "Trusted contact and emergency shortcuts", true, "", ""),
                marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(68), 0, 0, 0, dp(10)));

        TextView note = text("Save one trusted person. SOS uses your current location when Android location access is available.", 12, MUTED_NAVY, false);
        note.setPadding(dp(12), dp(12), dp(12), dp(12));
        note.setBackground(rounded(0xEFFFFFFF, dp(16), dp(1), 0x227B5CE6));
        content.addView(note, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(12)));

        LinearLayout form = glassCard();
        form.setPadding(dp(14), dp(14), dp(14), dp(14));
        nameField = field("Trusted person name", InputType.TYPE_CLASS_TEXT);
        nameField.setText(prefs.getString("trusted_name", ""));
        form.addView(nameField, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(54), 0, 0, 0, dp(10)));
        phoneField = field("Trusted person phone number", InputType.TYPE_CLASS_PHONE);
        phoneField.setText(prefs.getString("trusted_phone", ""));
        form.addView(phoneField, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(54), 0, 0, 0, dp(10)));
        TextView save = action("Save trusted contact", 0xFF7652E8);
        save.setOnClickListener(v -> saveContact());
        form.addView(save, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(52)));
        content.addView(form, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(12)));

        TextView sos = action("SOS — TAP 3 TIMES", 0xFFE33C65);
        sos.setTextSize(17);
        sos.setOnClickListener(v -> handleSosTap());
        content.addView(sos, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(64), 0, 0, 0, dp(10)));

        TextView sound = action("🔊  Sound alarm", 0xFF4C77C8);
        sound.setOnClickListener(v -> toggleAlarm(sound));
        content.addView(sound, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(56), 0, 0, 0, dp(10)));

        LinearLayout callTrusted = bottomRowCard("☎", 0xFFE8E6FF, PURPLE,
                "Call trusted person", "Uses the number saved above", "Call", "");
        callTrusted.setOnClickListener(v -> {
            saveContact();
            String phone = prefs.getString("trusted_phone", "");
            if (phone.trim().isEmpty()) Toast.makeText(activity, "Save a trusted contact first.", Toast.LENGTH_SHORT).show();
            else EmergencyHelper.callTrusted(activity, phone);
        });
        content.addView(callTrusted, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(74), 0, 0, 0, dp(9)));

        LinearLayout emergency = bottomRowCard("!", 0xFFFFE7E7, 0xFFD63E4D,
                "Emergency services", "India ERSS emergency number", "112", "");
        emergency.setOnClickListener(v -> EmergencyHelper.dial(activity, "112"));
        content.addView(emergency, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(74), 0, 0, 0, dp(9)));

        LinearLayout she = bottomRowCard("♀", 0xFFFFE7F2, 0xFFD84F8A,
                "Telangana SHE Teams", "Official Women Safety Wing helpline", "Open", "");
        she.setOnClickListener(v -> {
            android.content.Intent i = new android.content.Intent(android.content.Intent.ACTION_VIEW,
                    android.net.Uri.parse("https://womensafetywing.telangana.gov.in/she-module/she-teams/"));
            activity.startActivity(i);
        });
        content.addView(she, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(74), 0, 0, 0, dp(9)));

        boolean lock = prefs.getBoolean("lockscreen_sos", false);
        TextView lockButton = action(lock ? "Lock-screen SOS: ON" : "Enable lock-screen SOS", 0xFF6645B6);
        lockButton.setOnClickListener(v -> {
            boolean enabled = !prefs.getBoolean("lockscreen_sos", false);
            prefs.edit().putBoolean("lockscreen_sos", enabled).apply();
            lockButton.setText(enabled ? "Lock-screen SOS: ON" : "Enable lock-screen SOS");
            if (enabled) SosNotification.refreshIfEnabled(activity);
        });
        content.addView(lockButton, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(54), 0, 0, 0, dp(12)));

        TextView info = text("For the lock screen, Android allows Safe Space to show a public SOS notification. Tap its SOS action 3 times to send the trusted-contact alert. Emergency-service calls stay user-initiated to prevent accidental calls.", 11, MUTED_NAVY, false);
        info.setLineSpacing(dp(2), 1f);
        content.addView(info, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, dp(4), 0, dp(4), dp(10)));
    }

    private EditText field(String hint, int type) {
        EditText e = new EditText(activity);
        e.setHint(hint);
        e.setInputType(type);
        e.setSingleLine(true);
        e.setTextColor(NAVY);
        e.setHintTextColor(0x8870809F);
        e.setPadding(dp(14), 0, dp(14), 0);
        e.setBackground(rounded(0xFFF5FAFF, dp(15), dp(1), 0x337B5CE6));
        return e;
    }

    private TextView action(String label, int fill) {
        TextView t = text(label, 14, Color.WHITE, true);
        t.setGravity(Gravity.CENTER);
        t.setBackground(rounded(fill, dp(18), 0, Color.TRANSPARENT));
        t.setElevation(dp(3));
        return t;
    }

    private void saveContact() {
        String name = nameField.getText().toString().trim();
        String phone = phoneField.getText().toString().replaceAll("[^0-9+]", "").trim();
        if (name.length() < 2 || phone.length() < 8) {
            Toast.makeText(activity, "Enter a valid trusted name and phone number.", Toast.LENGTH_LONG).show();
            return;
        }
        prefs.edit().putString("trusted_name", name).putString("trusted_phone", phone).apply();
        Toast.makeText(activity, "Trusted contact saved.", Toast.LENGTH_SHORT).show();
    }

    private void handleSosTap() {
        long now = System.currentTimeMillis();
        if (now - firstTapAt > 6000L) {
            firstTapAt = now;
            sosTapCount = 0;
        }
        sosTapCount++;
        if (sosTapCount >= 3) {
            sosTapCount = 0;
            firstTapAt = 0;
            saveContact();
            EmergencyHelper.sendTrustedSos(activity, true);
        } else {
            Toast.makeText(activity, "SOS " + sosTapCount + "/3", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleAlarm(TextView button) {
        if (alarm == null) {
            alarm = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
            alarm.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 10000);
            button.setText("■  Stop alarm");
        } else {
            alarm.stopTone();
            alarm.release();
            alarm = null;
            button.setText("🔊  Sound alarm");
        }
    }

    @Override protected void onDetachedFromWindow() {
        if (alarm != null) {
            alarm.stopTone();
            alarm.release();
            alarm = null;
        }
        super.onDetachedFromWindow();
    }
}
