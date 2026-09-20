package com.safespace.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Safe Space 360 hub: an interactive SIH prototype that connects wellbeing,
 * justice-journey, privacy, offline access and recovery features.
 */
final class SafeSpace360View extends PastelScreenView {
    private final SharedPreferences progress;
    private final SharedPreferences settings;
    private final SharedPreferences onboarding;
    private TextView weatherValue;
    private TextView weatherWhy;
    private TextView justiceValue;
    private TextView recoveryValue;
    private TextView avatarValue;
    private SpeechRecognizer speechRecognizer;

    private static final String[] CASE_STAGES = {
            "Complaint", "Investigation", "Hearing", "Trial", "Compensation", "Rehabilitation"
    };

    SafeSpace360View(Activity activity, ScreenNavigator navigator) {
        super(activity, navigator, 27);
        progress = activity.getSharedPreferences(SupportSignalEngine.PREFS, Context.MODE_PRIVATE);
        settings = activity.getSharedPreferences("safe_space_settings", Context.MODE_PRIVATE);
        onboarding = activity.getSharedPreferences("safe_space_onboarding", Context.MODE_PRIVATE);

        content.addView(header("Safe Space 360", "Support that adapts with you", true, "?", "Every score is explainable and non-diagnostic"),
                marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(68), 0, 0, 0, dp(10)));

        content.addView(buildWeatherCard(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(10)));
        content.addView(buildJusticeCard(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(10)));
        content.addView(buildRecoveryCard(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(14)));

        addSection("Right now", new Object[][]{
                {"◌", "Just Stay With Me", "Minimal calm mode with gentle grounding", 0},
                {"☺", "Emotion Avatar", "A voluntary mood-based companion world", 1},
                {"?", "What do I need right now?", "Answer one question and get a useful next step", 2},
                {"🎙", "Voice Check-In", "Speak instead of typing; consented words only", 3},
                {"◷", "Digital Detox Bubble", "5 / 10 / 20 minute calm session", 4},
                {"!", "I can’t explain it", "Use simple feeling cards to find words", 5}
        });

        addSection("Privacy & access", new Object[][]{
                {"▣", "Discreet Mode", "Hide sensitive previews and block screenshots", 6},
                {"◎", "What Safe Space Remembers", "View or clear user-selected preferences", 7},
                {"⌖", "Support Map", "Nearby mental-health and support services", 8},
                {"♧", "Trusted Circle", "Manage your trusted person and SOS", 9},
                {"⇄", "Offline Safe Space", "Queue check-ins for SMS / IVRS style follow-up", 10},
                {"文", "Language + Simple Mode", "English / Telugu / Hindi and simpler UI", 11}
        });

        addSection("Personalisation & progress", new Object[][]{
                {"✦", "Adaptive Check-In", "Next question changes with recent needs", 12},
                {"▥", "Explain My Trend", "Plain-language reasons behind the support band", 13},
                {"♟", "Counsellor Preparation Card", "Copy a voluntary summary for an appointment", 14},
                {"⌂", "Personalised Home", "Choose what should be easiest to reach", 15},
                {"★", "Gentle Achievements", "Positive milestones without streak pressure", 16},
                {"↗", "My Journey", "Wellbeing + case events + support in one timeline", 17},
                {"☼", "Smart Check-In Timing", "Choose a user-approved reminder window", 18}
        });

        addSection("Learning & engagement", new Object[][]{
                {"▤", "Micro-Learning Stories", "Short coping, legal-process and support stories", 19},
                {"❀", "Cultural Comfort Mode", "Language, visuals and calming style preferences", 20},
                {"▦", "Emotion Quest", "Calm Island, Focus Forest and Grounding Garden", 21},
                {"360", "Safe Space 360 Support Loop", "Check-in → explainable trend → human follow-up", 22}
        });

        refreshSummary();
    }

    private View buildWeatherCard() {
        LinearLayout card = glassCard();
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        TextView title = text("📈 Emotional Weather", 16, NAVY, true);
        card.addView(title);
        weatherValue = text("", 23, PURPLE, true);
        LinearLayout.LayoutParams valueLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        valueLp.topMargin = dp(7);
        card.addView(weatherValue, valueLp);
        weatherWhy = text("", 11, MUTED_NAVY, false);
        LinearLayout.LayoutParams whyLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        whyLp.topMargin = dp(7);
        card.addView(weatherWhy, whyLp);
        TextView explain = actionButton("Why did this change?");
        explain.setOnClickListener(v -> showMessage("Explainable support trend", SupportSignalEngine.explanation(activity)));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(46));
        lp.topMargin = dp(10);
        card.addView(explain, lp);
        return card;
    }

    private View buildJusticeCard() {
        LinearLayout card = glassCard();
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.addView(text("⚖️ Justice-Journey Timeline", 16, NAVY, true));
        justiceValue = text("", 13, NAVY, true);
        justiceValue.setLineSpacing(dp(3), 1.08f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(10);
        card.addView(justiceValue, lp);
        TextView update = actionButton("Update current stage");
        update.setOnClickListener(v -> chooseCaseStage());
        LinearLayout.LayoutParams btn = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(46));
        btn.topMargin = dp(10);
        card.addView(update, btn);
        return card;
    }

    private View buildRecoveryCard() {
        LinearLayout card = glassCard();
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.addView(text("🌱 Recovery / Progress Map", 16, NAVY, true));
        recoveryValue = text("", 13, NAVY, false);
        recoveryValue.setLineSpacing(dp(4), 1.08f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.topMargin = dp(9);
        card.addView(recoveryValue, lp);
        avatarValue = text("", 32, PURPLE, true);
        avatarValue.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams avatarLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(54));
        avatarLp.topMargin = dp(5);
        card.addView(avatarValue, avatarLp);
        TextView garden = actionButton("Visit Healing Garden");
        garden.setOnClickListener(v -> showGarden());
        card.addView(garden, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(46)));
        return card;
    }

    private void addSection(String title, Object[][] rows) {
        TextView heading = text(title, 16, NAVY, true);
        content.addView(heading, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                dp(2), dp(6), 0, dp(8)));
        for (Object[] row : rows) {
            int action = (Integer) row[3];
            LinearLayout card = bottomRowCard((String) row[0], 0xFFE9E7FF, PURPLE,
                    (String) row[1], (String) row[2], null, "");
            card.setOnClickListener(v -> handleAction(action));
            content.addView(card, marginParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, dp(74), 0, 0, 0, dp(8)));
        }
    }

    private TextView actionButton(String label) {
        TextView button = text(label, 13, Color.WHITE, true);
        button.setGravity(Gravity.CENTER);
        button.setBackground(gradientRounded(0xFF8D66EA, 0xFF6842D5, dp(18), 0, Color.TRANSPARENT));
        button.setClickable(true);
        button.setFocusable(true);
        return button;
    }

    private void handleAction(int action) {
        switch (action) {
            case 0: justStayWithMe(); break;
            case 1: showAvatar(); break;
            case 2: needRightNow(); break;
            case 3: startVoiceCheckIn(); break;
            case 4: navigator.openScreen(28); break;
            case 5: cantExplain(); break;
            case 6: toggleDiscreetMode(); break;
            case 7: showMemoryControl(); break;
            case 8: navigator.openScreen(26); break;
            case 9: navigator.openScreen(25); break;
            case 10: queueOfflineCheckIn(); break;
            case 11: languageAndSimpleMode(); break;
            case 12: adaptiveCheckIn(); break;
            case 13: showMessage("Explain My Trend", SupportSignalEngine.explanation(activity)); break;
            case 14: counsellorPrep(); break;
            case 15: personalisedHome(); break;
            case 16: achievements(); break;
            case 17: myJourney(); break;
            case 18: smartTiming(); break;
            case 19: microLearning(); break;
            case 20: culturalComfort(); break;
            case 21: navigator.openScreen(24); break;
            case 22: supportLoop(); break;
            default: break;
        }
    }

    private void refreshSummary() {
        int score = SupportSignalEngine.wellbeingScore(activity);
        weatherValue.setText(SupportSignalEngine.band(activity) + "  •  " + score + "/100");
        weatherWhy.setText(SupportSignalEngine.explanation(activity));

        int stage = Math.max(0, Math.min(CASE_STAGES.length - 1, progress.getInt("case_stage", 0)));
        StringBuilder timeline = new StringBuilder();
        for (int i = 0; i < CASE_STAGES.length; i++) {
            if (i > 0) timeline.append("  →  ");
            timeline.append(i < stage ? "✓ " : i == stage ? "● " : "○ ").append(CASE_STAGES[i]);
        }
        justiceValue.setText(timeline.toString());

        int checks = progress.getInt("checkin_count", 0);
        int calm = progress.getInt("calm_count", 0);
        int journals = progress.getInt("journal_count", 0);
        int appts = progress.getInt("appointment_count", 0);
        int points = SupportSignalEngine.recoveryPoints(activity);
        recoveryValue.setText("Progress " + points + "/100\n"
                + "Check-ins " + checks + "  •  Calm activities " + calm
                + "  •  Journal " + journals + "  •  Appointments " + appts
                + "\nGarden: " + SupportSignalEngine.gardenStage(activity));
        avatarValue.setText(avatarForMood(progress.getString("last_mood", "Okay")));
    }

    private String avatarForMood(String mood) {
        if ("Happy".equals(mood)) return "☺  ✨  🌿";
        if ("Calm".equals(mood)) return "◡  ☁  🌱";
        if ("Stressed".equals(mood)) return "•︵•  🌧  →  🌤";
        if ("Sad".equals(mood)) return "•́︿•̀  🌧";
        if ("Angry".equals(mood)) return "•̀ᴗ•́  🔥 → 🌿";
        return "☺  🌱";
    }

    private void chooseCaseStage() {
        new AlertDialog.Builder(activity)
                .setTitle("Current justice stage")
                .setItems(CASE_STAGES, (dialog, which) -> {
                    progress.edit()
                            .putInt("case_stage", which)
                            .putBoolean("upcoming_case_event", which >= 1 && which <= 3)
                            .apply();
                    refreshSummary();
                    toast("Stage updated. Supportive check-ins can adapt around this event.");
                })
                .show();
    }

    private void showGarden() {
        int p = SupportSignalEngine.recoveryPoints(activity);
        String garden = p < 15 ? "🌱\nYour first seed is growing."
                : p < 35 ? "🌱  🌿  🌱\nYour garden is taking root."
                : p < 65 ? "🌿  🌷  🌱  🌼\nYour healthy actions are blooming."
                : "🌳  🌷  🌼  🌿  🪷\nYour garden reflects the care you have practiced.";
        showMessage("Healing Garden", garden + "\n\nCheck-ins, calm sessions, journaling and appointments add progress. No streaks, no punishment.");
    }

    private void justStayWithMe() {
        showMessage("Just Stay With Me", "No advice needed.\n\nNotice your feet on the floor.\nLet your shoulders soften.\nBreathe at your own comfortable pace.\n\nYou can stay on this screen as long as you want.");
    }

    private void showAvatar() {
        showMessage("Emotion Avatar", "Your avatar uses only moods you voluntarily record.\n\nCurrent world: "
                + avatarForMood(progress.getString("last_mood", "Okay"))
                + "\n\nIt never reads your face or judges your appearance.");
    }

    private void needRightNow() {
        String[] needs = {"Calm my body", "Put feelings into words", "Distract me gently", "Talk / journal", "Find human support"};
        new AlertDialog.Builder(activity).setTitle("What do you need right now?")
                .setItems(needs, (d, which) -> {
                    if (which == 0) navigator.openScreen(12);
                    else if (which == 1) cantExplain();
                    else if (which == 2) navigator.openScreen(24);
                    else if (which == 3) navigator.openScreen(10);
                    else navigator.openScreen(15);
                }).show();
    }

    private void startVoiceCheckIn() {
        if (!SpeechRecognizer.isRecognitionAvailable(activity)) {
            toast("Speech recognition is not available on this device");
            return;
        }
        if (Build.VERSION.SDK_INT >= 23
                && activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 733);
            toast("Allow microphone, then tap Voice Check-In again");
            return;
        }
        if (speechRecognizer != null) speechRecognizer.destroy();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override public void onReadyForSpeech(android.os.Bundle params) { toast("Listening…"); }
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float rmsdB) {}
            @Override public void onBufferReceived(byte[] buffer) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onError(int error) { toast("Voice check-in ended. You can try again."); }
            @Override public void onResults(android.os.Bundle results) {
                ArrayList<String> heard = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String text = heard != null && !heard.isEmpty() ? heard.get(0) : "";
                progress.edit().putString("last_voice_checkin", text).apply();
                showMessage("Voice Check-In saved", text.isEmpty() ? "No words were captured."
                        : "“" + text + "”\n\nOnly the captured words are stored in this prototype. This is not a diagnosis.");
            }
            @Override public void onPartialResults(android.os.Bundle partialResults) {}
            @Override public void onEvent(int eventType, android.os.Bundle params) {}
        });
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "How are you feeling today?");
        speechRecognizer.startListening(intent);
    }

    private void cantExplain() {
        String[] feelings = {"Worried", "Lonely", "Overwhelmed", "Angry", "Confused", "Numb / unsure"};
        new AlertDialog.Builder(activity).setTitle("I can’t explain it")
                .setItems(feelings, (d, which) -> {
                    progress.edit().putString("feeling_words", feelings[which]).apply();
                    showMessage("You can start with this", "“I’m feeling " + feelings[which].toLowerCase(Locale.ROOT)
                            + ", and I’m not sure how to explain the rest yet.”\n\nYou can copy this into Talk or your Journal.");
                }).show();
    }

    private void toggleDiscreetMode() {
        boolean next = !settings.getBoolean("discreet_mode", false);
        settings.edit().putBoolean("discreet_mode", next).apply();
        if (next) activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        else activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        showMessage("Discreet Mode", next
                ? "On. Screenshots are blocked and sensitive notification wording should stay neutral. Use Home for a quick exit."
                : "Off. Normal screen capture behaviour restored.");
    }

    private void showMemoryControl() {
        String language = settings.getString("comfort_language", "English");
        String timing = settings.getString("smart_timing", "Not chosen");
        String home = settings.getString("preferred_home", onboarding.getString("q13", "Mood check-in"));
        new AlertDialog.Builder(activity)
                .setTitle("What Safe Space Remembers")
                .setMessage("Language: " + language + "\nReminder timing: " + timing
                        + "\nPreferred home tool: " + home
                        + "\n\nThese are user-selected preferences stored on this device.")
                .setPositiveButton("Keep", null)
                .setNegativeButton("Clear preferences", (d, w) -> {
                    settings.edit().remove("comfort_language").remove("smart_timing")
                            .remove("preferred_home").remove("simple_mode").apply();
                    toast("Selected preferences cleared");
                }).show();
    }

    private void queueOfflineCheckIn() {
        SupportSignalEngine.recordOfflineQueued(activity);
        int queued = progress.getInt("offline_queue", 0);
        showMessage("Offline Safe Space", "Queued check-in #" + queued
                + ".\n\nBreathing, grounding, journal, emergency information and selected games remain available locally. In a production deployment, queued check-ins can sync later or be routed through approved SMS / IVRS infrastructure.");
    }

    private void languageAndSimpleMode() {
        String[] languages = {"English", "తెలుగు (Telugu)", "हिन्दी (Hindi)"};
        new AlertDialog.Builder(activity).setTitle("Choose language")
                .setItems(languages, (d, which) -> {
                    settings.edit().putString("comfort_language", languages[which]).apply();
                    new AlertDialog.Builder(activity).setTitle("Simple-language mode")
                            .setItems(new String[]{"Standard", "Simple / large-button mode"}, (d2, choice) -> {
                                settings.edit().putBoolean("simple_mode", choice == 1).apply();
                                toast("Preferences saved");
                            }).show();
                }).show();
    }

    private void adaptiveCheckIn() {
        String mood = progress.getString("last_mood", "Okay");
        String q;
        String[] answers;
        if ("Stressed".equals(mood) || "Angry".equals(mood)) {
            q = "What would make the next hour feel a little easier?";
            answers = new String[]{"Breathing / calm", "Quiet space", "Talk to someone", "Write it down", "Not sure"};
        } else if (progress.getBoolean("upcoming_case_event", false)) {
            q = "You have a justice-stage event ahead. What support would help around it?";
            answers = new String[]{"Reminder", "Counsellor prep", "Trusted person", "Calm exercise", "Skip"};
        } else {
            q = "What should today’s check-in focus on?";
            answers = new String[]{"Mood", "Sleep", "Stress", "Support", "Just a quick check-in"};
        }
        new AlertDialog.Builder(activity).setTitle(q).setItems(answers, (d, which) -> {
            progress.edit().putString("adaptive_answer", answers[which]).apply();
            toast("Saved. Future check-ins can adapt to this choice.");
        }).show();
    }

    private void counsellorPrep() {
        String summary = "Safe Space counsellor preparation\n"
                + "Current support band: " + SupportSignalEngine.band(activity) + "\n"
                + "Recent mood: " + progress.getString("last_mood", "Not recorded") + "\n"
                + "Justice stage: " + CASE_STAGES[Math.max(0, Math.min(CASE_STAGES.length - 1, progress.getInt("case_stage", 0)))] + "\n"
                + "What I want help with: " + progress.getString("adaptive_answer", "Not selected") + "\n"
                + "My words: " + progress.getString("feeling_words", "Not selected") + "\n"
                + "Generated only from information I chose to record.";
        ClipboardManager cm = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) cm.setPrimaryClip(ClipData.newPlainText("Safe Space counsellor prep", summary));
        showMessage("Counsellor Preparation Card", summary + "\n\nCopied to clipboard.");
    }

    private void personalisedHome() {
        String[] choices = {"Mood check-in", "Calm / breathing", "Journal", "Games", "Counsellor", "Safety"};
        new AlertDialog.Builder(activity).setTitle("Put this first on Home")
                .setItems(choices, (d, which) -> {
                    settings.edit().putString("preferred_home", choices[which]).apply();
                    toast(choices[which] + " saved as your preferred quick action");
                }).show();
    }

    private void achievements() {
        int c = progress.getInt("checkin_count", 0);
        int calm = progress.getInt("calm_count", 0);
        int journal = progress.getInt("journal_count", 0);
        StringBuilder a = new StringBuilder("✓ First step\n");
        if (c > 0) a.append("✓ First Check-In\n");
        if (calm > 0) a.append("✓ Tried a Calm Activity\n");
        if (journal > 0) a.append("✓ Put Something Into Words\n");
        if (c + calm + journal >= 10) a.append("✓ Ten Supportive Actions\n");
        a.append("\nNo streaks and no ‘failure’ messages.");
        showMessage("Gentle Achievements", a.toString());
    }

    private void myJourney() {
        int stage = Math.max(0, Math.min(CASE_STAGES.length - 1, progress.getInt("case_stage", 0)));
        String text = "Justice: " + CASE_STAGES[stage]
                + "\nLast mood: " + progress.getString("last_mood", "Not recorded")
                + "\nCheck-ins: " + progress.getInt("checkin_count", 0)
                + "\nCalm activities: " + progress.getInt("calm_count", 0)
                + "\nJournal actions: " + progress.getInt("journal_count", 0)
                + "\nRecovery points: " + SupportSignalEngine.recoveryPoints(activity) + "/100";
        showMessage("My Journey", text);
    }

    private void smartTiming() {
        String[] choices = {"Morning", "Afternoon", "Evening", "Let Safe Space suggest from my approved check-in times"};
        new AlertDialog.Builder(activity).setTitle("Smart Check-In Timing")
                .setItems(choices, (d, which) -> {
                    settings.edit().putString("smart_timing", choices[which]).apply();
                    toast("Timing preference saved");
                }).show();
    }

    private void microLearning() {
        String[] stories = {
                "Before a difficult appointment: write 1 question you want answered, 1 person you can contact, and 1 calming action for afterward.",
                "If a legal process feels confusing, ask the relevant support professional to explain the next step in simple language. You do not need to memorise everything at once.",
                "A coping tool does not need to remove every difficult feeling. Even making the next few minutes more manageable can be useful."
        };
        int next = settings.getInt("story_index", 0) % stories.length;
        settings.edit().putInt("story_index", next + 1).apply();
        showMessage("60-second Micro-Learning", stories[next]);
    }

    private void culturalComfort() {
        String[] styles = {"Soft blue", "Calm purple", "Nature / garden", "Minimal"};
        new AlertDialog.Builder(activity).setTitle("Cultural Comfort Mode")
                .setItems(styles, (d, which) -> {
                    settings.edit().putString("comfort_style", styles[which]).apply();
                    toast("Comfort style saved. Language can be changed in Language + Simple Mode.");
                }).show();
    }

    private void supportLoop() {
        showMessage("Safe Space 360 Support Loop",
                "Victim / user\n↓\nApp • SMS • IVRS • Helpline\n↓\nConsented check-in signals\n↓\nPersonal baseline\n↓\nDynamic, explainable distress trend\n↓\nCounsellor dashboard / human review\n↓\nFollow-up outcome\n↓\nFuture support adapts\n\nHuman follow-up stays in the loop.");
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
    }
}
