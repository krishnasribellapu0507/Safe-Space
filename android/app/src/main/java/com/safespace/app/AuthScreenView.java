package com.safespace.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Local/demo authentication screen used for the SIH prototype.
 * It intentionally avoids shipping API credentials. Production auth can be
 * connected through the backend/Firebase adapter without changing the UI flow.
 */
final class AuthScreenView extends ScrollView {
    private static final int NAVY = Color.rgb(16, 38, 95);
    private static final int PURPLE = Color.rgb(123, 79, 233);
    private static final int ERROR = Color.rgb(186, 45, 76);
    private final Activity activity;
    private final Runnable onAuthenticated;
    private boolean signUp;
    private LinearLayout content;
    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;
    private TextView messageView;

    AuthScreenView(Activity activity, boolean startWithSignUp, Runnable onAuthenticated) {
        super(activity);
        this.activity = activity;
        this.onAuthenticated = onAuthenticated;
        this.signUp = startWithSignUp;
        setFillViewport(true);
        setVerticalScrollBarEnabled(false);
        setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFFD7ECFF, 0xFFE7F4FF, 0xFFF2F9FF}));
        setOnApplyWindowInsetsListener((view, insets) -> {
            setPadding(0, insets.getSystemWindowInsetTop(), 0, insets.getSystemWindowInsetBottom());
            return insets;
        });
        buildContent();
    }

    private void buildContent() {
        removeAllViews();
        content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(dp(24), dp(42), dp(24), dp(28));
        addView(content, new ScrollView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView logo = text("S", 42, Color.WHITE, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(circle(0xFF7550DF));
        add(content, logo, dp(92), dp(92), 0, 0, 0, dp(14));
        add(content, text(signUp ? "Create Your Safe Space" : "Welcome Back", 28, NAVY, true),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(6));
        add(content, text("Private by design • demo-ready", 13, 0xAA10265F, false),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(22));

        if (signUp) {
            nameField = input("Full name", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
            add(content, nameField, ViewGroup.LayoutParams.MATCH_PARENT, dp(56), 0, 0, 0, dp(12));
        }
        emailField = input("Email", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        add(content, emailField, ViewGroup.LayoutParams.MATCH_PARENT, dp(56), 0, 0, 0, dp(12));
        passwordField = input("Password", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordField.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordField.setOnEditorActionListener((v, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) { submit(); return true; }
            return false;
        });
        add(content, passwordField, ViewGroup.LayoutParams.MATCH_PARENT, dp(56), 0, 0, 0, dp(16));

        Button primary = new Button(activity);
        primary.setAllCaps(false);
        primary.setText(signUp ? "Create account" : "Log in");
        primary.setTextColor(Color.WHITE);
        primary.setTextSize(16);
        primary.setTypeface(Typeface.DEFAULT_BOLD);
        primary.setBackground(rounded(0xFF7550DF, dp(28)));
        primary.setOnClickListener(v -> submit());
        add(content, primary, ViewGroup.LayoutParams.MATCH_PARENT, dp(56), 0, 0, 0, dp(8));

        Button demo = new Button(activity);
        demo.setAllCaps(false);
        demo.setText("SIH Demo Login");
        demo.setTextColor(NAVY);
        demo.setBackground(rounded(Color.WHITE, dp(24)));
        demo.setOnClickListener(v -> demoLogin());
        add(content, demo, ViewGroup.LayoutParams.MATCH_PARENT, dp(50), 0, 0, 0, dp(8));

        messageView = text("", 12, ERROR, false);
        add(content, messageView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(14));

        TextView toggle = text(signUp ? "Already registered? Log in" : "New here? Create account", 14, PURPLE, true);
        toggle.setOnClickListener(v -> { signUp = !signUp; buildContent(); });
        add(content, toggle, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(12));
        add(content, text("Prototype auth uses encrypted app-local preferences. Connect /api/auth for deployment.",
                        10.5f, 0x8810265F, false),
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, 0);
    }

    private void submit() {
        String name = nameField == null ? "" : nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString();
        if (signUp && name.length() < 2) { show("Enter your name."); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { show("Enter a valid email."); return; }
        if (password.length() < 6) { show("Password must be at least 6 characters."); return; }
        completeAuth(signUp ? name : inferredName(email), email);
    }

    private void demoLogin() {
        emailField.setText("demo@safespace.app");
        passwordField.setText("demo123");
        completeAuth("Krishnasri", "demo@safespace.app");
    }

    private void completeAuth(String name, String email) {
        SharedPreferences session = activity.getSharedPreferences("safe_space_session", Context.MODE_PRIVATE);
        session.edit().putBoolean("authenticated", true).putString("email", email).apply();
        SharedPreferences profile = activity.getSharedPreferences("safe_space_profile", Context.MODE_PRIVATE);
        if (!name.trim().isEmpty()) profile.edit().putString("display_name", name.trim()).apply();
        onAuthenticated.run();
    }

    private String inferredName(String email) {
        SharedPreferences profile = activity.getSharedPreferences("safe_space_profile", Context.MODE_PRIVATE);
        String existing = profile.getString("display_name", "");
        if (existing != null && !existing.trim().isEmpty()) return existing;
        int at = email.indexOf('@');
        String base = at > 0 ? email.substring(0, at) : "Safe Space User";
        return base.isEmpty() ? "Safe Space User" : Character.toUpperCase(base.charAt(0)) + base.substring(1);
    }

    private EditText input(String hint, int type) {
        EditText field = new EditText(activity);
        field.setHint(hint); field.setInputType(type); field.setSingleLine(true);
        field.setTextSize(15); field.setTextColor(NAVY); field.setHintTextColor(0x8010265F);
        field.setPadding(dp(16), 0, dp(16), 0); field.setBackground(rounded(0xF6FFFFFF, dp(16)));
        return field;
    }
    private TextView text(String value, float size, int color, boolean bold) {
        TextView v = new TextView(activity); v.setText(value); v.setTextSize(size); v.setTextColor(color);
        v.setGravity(Gravity.CENTER); v.setTypeface(Typeface.create("sans-serif-rounded", bold ? Typeface.BOLD : Typeface.NORMAL));
        return v;
    }
    private GradientDrawable rounded(int color, int radius) { GradientDrawable g = new GradientDrawable(); g.setColor(color); g.setCornerRadius(radius); return g; }
    private GradientDrawable circle(int color) { GradientDrawable g = new GradientDrawable(); g.setShape(GradientDrawable.OVAL); g.setColor(color); return g; }
    private void add(LinearLayout p, View v, int w, int h, int l, int t, int r, int b) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(w, h); lp.setMargins(dp(l), dp(t), dp(r), dp(b)); p.addView(v, lp);
    }
    private void show(String msg) { messageView.setText(msg); }
    private int dp(float v) { return Math.round(v * getResources().getDisplayMetrics().density); }
}
