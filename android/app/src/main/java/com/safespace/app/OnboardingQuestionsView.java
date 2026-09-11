package com.safespace.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lightweight preference questionnaire shown after sign-in/sign-up.
 * Answers are stored locally in SharedPreferences for prototype personalisation.
 */
public final class OnboardingQuestionsView extends FrameLayout {
    private static final int NAVY = 0xFF10265F;
    private static final int PURPLE = 0xFF7550E8;
    private static final int MUTED = 0xFF60739B;
    private static final int BG = 0xFFF3F8FF;

    private final Activity activity;
    private final Runnable onComplete;
    private final SharedPreferences prefs;

    private final String[] questions = {
            "Choose your Safe Space theme",
            "Why did you download Safe Space?",
            "What would you like help with first?",
            "How have you been feeling lately?",
            "When do you usually need support most?",
            "What helps you feel calmer?",
            "How has your sleep been lately?",
            "How often would you like a check-in?",
            "How would you prefer to check in?",
            "Would gentle reminders be useful?",
            "What kind of support feels best for you?",
            "Would you like quick access to a counsellor?",
            "Add an SOS shortcut to your lock screen?",
            "What should Safe Space show first on your home screen?",
            "Which language should feel easiest in Safe Space?",
            "Where are you in your justice / support journey?",
            "Would you like Discreet Mode for extra privacy?"
    };

    private final String[][] options = {
            {"Light blue", "Dark purple"},
            {"Stress or worry", "Sleep better", "Low mood", "A difficult experience", "Build healthier habits", "Just exploring"},
            {"Calm down quickly", "Talk about how I feel", "Journal my thoughts", "Sleep and relax", "Improve focus", "Connect with a counsellor"},
            {"Calm", "Mostly okay", "Tense", "Overwhelmed", "Mixed / up and down", "Prefer not to say"},
            {"Morning", "Afternoon", "Evening", "Night", "It changes day to day"},
            {"Breathing exercises", "Music or nature sounds", "Mindful games", "Writing / journaling", "Talking to someone", "I am not sure yet"},
            {"Good", "Okay", "Hard to fall asleep", "I wake up often", "Irregular", "Prefer not to say"},
            {"Every day", "A few times a week", "Once a week", "Only when I open the app"},
            {"Quick mood emoji", "A few simple questions", "Short chat", "Voice check-in", "A mix of these"},
            {"Yes, gentle reminders", "Only mood check-in reminders", "Only sleep / calm reminders", "No reminders"},
            {"Self-guided exercises", "AI companion", "Professional counsellor", "A trusted person", "A mix depending on the day"},
            {"Yes, keep it on the home screen", "Yes, but only in the menu", "Not right now"},
            {"Yes — add lock-screen SOS", "No — keep SOS inside the app"},
            {"Mood check-in", "Calm / breathing", "Sleep sounds", "Mindful games", "Journal", "Counsellor"},
            {"English", "తెలుగు (Telugu)", "हिन्दी (Hindi)", "Choose later"},
            {"Complaint", "Investigation", "Hearing", "Trial", "Compensation", "Rehabilitation", "Not sure / not applicable"},
            {"Yes — hide sensitive previews", "No — normal privacy settings"}
    };

    private int index = 0;
    private final Map<Integer, String> answers = new LinkedHashMap<>();

    private TextView stepText;
    private ProgressBar progress;
    private TextView questionText;
    private LinearLayout optionContainer;
    private TextView nextButton;

    public OnboardingQuestionsView(Activity activity, Runnable onComplete) {
        super(activity);
        this.activity = activity;
        this.onComplete = onComplete;
        this.prefs = activity.getSharedPreferences("safe_space_onboarding", Context.MODE_PRIVATE);
        setBackgroundColor(BG);
        buildUi();
        renderQuestion();
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        addView(scroll, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout root = new LinearLayout(activity);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(22), dp(22), dp(22), dp(30));
        scroll.addView(root, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView title = text("Let’s make Safe Space yours", 25, NAVY, true);
        title.setGravity(Gravity.START);
        root.addView(title);

        TextView subtitle = text("A few quick questions help us personalise your experience. You can change these later.", 13, MUTED, false);
        subtitle.setGravity(Gravity.START);
        LinearLayout.LayoutParams subLp = lp(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = dp(6);
        root.addView(subtitle, subLp);

        stepText = text("", 12, MUTED, true);
        stepText.setGravity(Gravity.START);
        LinearLayout.LayoutParams stepLp = lp(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        stepLp.topMargin = dp(20);
        root.addView(stepText, stepLp);

        progress = new ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(questions.length);
        progress.setProgressTintList(android.content.res.ColorStateList.valueOf(PURPLE));
        progress.setProgressBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFDCE5F5));
        LinearLayout.LayoutParams progressLp = lp(ViewGroup.LayoutParams.MATCH_PARENT, dp(7));
        progressLp.topMargin = dp(7);
        root.addView(progress, progressLp);

        questionText = text("", 23, NAVY, true);
        questionText.setGravity(Gravity.START);
        LinearLayout.LayoutParams qLp = lp(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        qLp.topMargin = dp(28);
        root.addView(questionText, qLp);

        optionContainer = new LinearLayout(activity);
        optionContainer.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams optionsLp = lp(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        optionsLp.topMargin = dp(18);
        root.addView(optionContainer, optionsLp);

        LinearLayout actions = new LinearLayout(activity);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams actionsLp = lp(ViewGroup.LayoutParams.MATCH_PARENT, dp(58));
        actionsLp.topMargin = dp(24);
        root.addView(actions, actionsLp);

        TextView back = text("Back", 15, NAVY, true);
        back.setGravity(Gravity.CENTER);
        back.setBackground(outlineButton());
        back.setOnClickListener(v -> previousQuestion());
        actions.addView(back, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.38f));

        nextButton = text("Next", 16, Color.WHITE, true);
        nextButton.setGravity(Gravity.CENTER);
        nextButton.setBackground(primaryButton());
        nextButton.setAlpha(.45f);
        nextButton.setEnabled(false);
        nextButton.setOnClickListener(v -> nextQuestion());
        LinearLayout.LayoutParams nextLp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.62f);
        nextLp.setMarginStart(dp(12));
        actions.addView(nextButton, nextLp);

        TextView privacy = text("Your answers stay on this device in this prototype. Skip any question by choosing ‘Prefer not to say’ where available.", 11, 0xFF7A89A6, false);
        privacy.setGravity(Gravity.START);
        LinearLayout.LayoutParams privacyLp = lp(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        privacyLp.topMargin = dp(16);
        root.addView(privacy, privacyLp);

        setOnApplyWindowInsetsListener((view, insets) -> {
            scroll.setPadding(0, insets.getSystemWindowInsetTop(), 0,
                    insets.getSystemWindowInsetBottom());
            return insets;
        });
    }

    private void renderQuestion() {
        stepText.setText("Question " + (index + 1) + " of " + questions.length);
        progress.setProgress(index + 1);
        questionText.setText(questions[index]);
        optionContainer.removeAllViews();

        String selected = answers.get(index);
        for (String option : options[index]) {
            TextView row = text(option, 15, NAVY, true);
            row.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            row.setPadding(dp(18), 0, dp(16), 0);
            row.setMinHeight(dp(56));
            row.setClickable(true);
            row.setFocusable(true);
            boolean isSelected = option.equals(selected);
            row.setBackground(optionBackground(isSelected));
            row.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            row.setOnClickListener(v -> {
                answers.put(index, option);
                if (index == 0) {
                    ThemeManager.setMode(activity, "Dark purple".equals(option)
                            ? ThemeManager.DARK : ThemeManager.LIGHT);
                }
                renderQuestion();
            });
            LinearLayout.LayoutParams rowLp = lp(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rowLp.bottomMargin = dp(10);
            optionContainer.addView(row, rowLp);
        }

        boolean hasAnswer = answers.containsKey(index);
        nextButton.setEnabled(hasAnswer);
        nextButton.setAlpha(hasAnswer ? 1f : .45f);
        nextButton.setText(index == questions.length - 1 ? "Finish" : "Next");
    }

    private void nextQuestion() {
        if (!answers.containsKey(index)) return;
        if (index < questions.length - 1) {
            index++;
            renderQuestion();
        } else {
            saveAndFinish();
        }
    }

    private void previousQuestion() {
        if (index > 0) {
            index--;
            renderQuestion();
        }
    }

    /** Returns true when back was handled inside this questionnaire. */
    public boolean handleBack() {
        if (index > 0) {
            previousQuestion();
            return true;
        }
        return false;
    }

    private void saveAndFinish() {
        SharedPreferences.Editor editor = prefs.edit();
        for (Map.Entry<Integer, String> entry : answers.entrySet()) {
            editor.putString("q" + entry.getKey(), entry.getValue());
        }
        editor.putBoolean("questionnaire_complete", true).apply();
        String appearance = answers.get(0);
        ThemeManager.setMode(activity, "Dark purple".equals(appearance)
                ? ThemeManager.DARK : ThemeManager.LIGHT);
        String sosChoice = answers.get(12);
        boolean lockscreen = "Yes — add lock-screen SOS".equals(sosChoice);
        activity.getSharedPreferences("safe_space_safety", Context.MODE_PRIVATE)
                .edit().putBoolean("lockscreen_sos", lockscreen).apply();

        String language = answers.get(14);
        String journey = answers.get(15);
        String discreet = answers.get(16);
        activity.getSharedPreferences("safe_space_settings", Context.MODE_PRIVATE).edit()
                .putString("comfort_language", language == null ? "English" : language)
                .putString("preferred_home", answers.get(13) == null ? "Mood check-in" : answers.get(13))
                .putBoolean("discreet_mode", "Yes — hide sensitive previews".equals(discreet))
                .apply();
        if (journey != null && !journey.startsWith("Not sure")) {
            int stage = 0;
            String[] stages = {"Complaint", "Investigation", "Hearing", "Trial", "Compensation", "Rehabilitation"};
            for (int i = 0; i < stages.length; i++) if (stages[i].equals(journey)) stage = i;
            activity.getSharedPreferences(SupportSignalEngine.PREFS, Context.MODE_PRIVATE).edit()
                    .putInt("case_stage", stage)
                    .putBoolean("upcoming_case_event", stage >= 1 && stage <= 3)
                    .apply();
        }
        if ("Yes — hide sensitive previews".equals(discreet)) {
            activity.getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE);
        }
        if (onComplete != null) onComplete.run();
    }

    private TextView text(String value, float sizeSp, int color, boolean bold) {
        TextView view = new TextView(activity);
        view.setText(value);
        view.setTextSize(sizeSp);
        view.setTextColor(color);
        view.setTypeface(Typeface.create("sans-serif-rounded", bold ? Typeface.BOLD : Typeface.NORMAL));
        return view;
    }

    private GradientDrawable optionBackground(boolean selected) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(selected ? 0xFFEDE7FF : Color.WHITE);
        gd.setCornerRadius(dp(17));
        gd.setStroke(dp(selected ? 2 : 1), selected ? PURPLE : 0xFFD9E3F1);
        return gd;
    }

    private GradientDrawable primaryButton() {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0xFF9B6AF2, 0xFF7042DD});
        gd.setCornerRadius(dp(29));
        return gd;
    }

    private GradientDrawable outlineButton() {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.WHITE);
        gd.setCornerRadius(dp(29));
        gd.setStroke(dp(1), 0xFFD4DFF0);
        return gd;
    }

    private LinearLayout.LayoutParams lp(int width, int height) {
        return new LinearLayout.LayoutParams(width, height);
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
