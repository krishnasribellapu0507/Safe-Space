package com.safespace.app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/** Screen 19: app settings. */
final class SettingsView extends FrameLayout {
    private final Activity activity;
    private final ScreenNavigator navigator;
    private final TextView[] appearanceOptions = new TextView[3];

    SettingsView(Activity activity, ScreenNavigator navigator) {
        super(activity);
        this.activity = activity;
        this.navigator = navigator;
        setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFFDCEEFF, 0xFFF4F3FF, 0xFFFFF1F5}));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        addView(scroll, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(16), dp(18), dp(28));
        scroll.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(FinalScreenUi.titledHeader(activity, this, navigator,
                        "Settings", "Customize your experience"),
                FinalScreenUi.margins(ViewGroup.LayoutParams.MATCH_PARENT, dp(60),
                        0, 0, 0, 15, this));

        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(8), dp(4), dp(8), dp(4));
        card.setElevation(dp(3));
        card.setBackground(FinalScreenUi.stroked(
                0xEFFFFFFF, 20, 0x286A83C2, 1, this));

        card.addView(settingRow("♧", "Notifications", "›", () -> toast("Notification settings")));
        addDivider(card);
        card.addView(settingRow("▣", "Privacy & Security", "Consent Center  ›", () -> ConsentCenter.show(activity)));
        addDivider(card);
        card.addView(settingRow("◐", "Accessibility", "Reduced motion & contrast  ›", () -> AccessibilityCenter.show(activity)));
        addDivider(card);
        card.addView(settingRow("▶", "Presentation Demo", "Reset scenario  ›", () -> {
            DemoScenario.reset(activity);
            toast("Demo scenario reset: 5-day wellbeing change ready");
        }));
        addDivider(card);
        card.addView(appearanceRow());
        addDivider(card);
        card.addView(settingRow("◎", "Language", "English  ›", () -> toast("Language: English")));
        addDivider(card);
        card.addView(settingRow("▱", "App Lock", "›", () -> toast("App lock settings")));
        addDivider(card);
        card.addView(settingRow("◈", "Data & Storage", "›", () -> toast("Data and storage")));
        addDivider(card);
        card.addView(settingRow("ⓘ", "About", "›", () -> toast("Safe Space 2.0.0 Demo")));
        content.addView(card, FinalScreenUi.margins(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                0, 0, 0, 16, this));

        TextView logout = FinalScreenUi.text(activity, "⇥  Log Out", 14, 0xFFE14E79, true);
        logout.setGravity(Gravity.CENTER_VERTICAL);
        logout.setPadding(dp(18), 0, dp(18), 0);
        logout.setElevation(dp(2));
        logout.setBackground(FinalScreenUi.ripple(0xEEFFFFFF, 17, this));
        logout.setContentDescription("Log out");
        logout.setOnClickListener(view -> {
            activity.getSharedPreferences("safe_space_session", android.content.Context.MODE_PRIVATE).edit().clear().apply();
            Toast.makeText(activity, "Logged out safely", Toast.LENGTH_SHORT).show();
            activity.recreate();
        });
        content.addView(logout, FinalScreenUi.margins(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(58), 0, 0, 0, 0, this));

        setOnApplyWindowInsetsListener((view, insets) -> {
            scroll.setPadding(0, insets.getSystemWindowInsetTop(), 0,
                    insets.getSystemWindowInsetBottom());
            return insets;
        });
        requestApplyInsets();
    }

    private View settingRow(String iconText, String titleText, String valueText,
                            Runnable action) {
        LinearLayout row = new LinearLayout(activity);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(8), 0, dp(8), 0);
        row.setClickable(true);
        row.setFocusable(true);
        row.setBackground(FinalScreenUi.ripple(Color.TRANSPARENT, 14, this));
        row.setOnClickListener(view -> action.run());

        TextView icon = FinalScreenUi.text(activity, iconText, 19, FinalScreenUi.PURPLE, true);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(FinalScreenUi.rounded(0xFFEAE7FF, 12, this));
        row.addView(icon, new LinearLayout.LayoutParams(dp(39), dp(39)));

        TextView title = FinalScreenUi.text(activity, titleText, 14, FinalScreenUi.NAVY, true);
        title.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0, dp(58), 1f);
        titleParams.setMarginStart(dp(13));
        row.addView(title, titleParams);

        TextView value = FinalScreenUi.text(activity, valueText, 12,
                FinalScreenUi.MUTED, false);
        value.setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        row.addView(value, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dp(58)));
        return row;
    }

    private View appearanceRow() {
        LinearLayout holder = new LinearLayout(activity);
        holder.setOrientation(LinearLayout.VERTICAL);
        holder.setPadding(dp(8), dp(9), dp(8), dp(11));

        LinearLayout heading = new LinearLayout(activity);
        heading.setGravity(Gravity.CENTER_VERTICAL);
        TextView icon = FinalScreenUi.text(activity, "◇", 19, FinalScreenUi.PURPLE, true);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(FinalScreenUi.rounded(0xFFEAE7FF, 12, this));
        heading.addView(icon, new LinearLayout.LayoutParams(dp(39), dp(39)));
        TextView title = FinalScreenUi.text(activity, "Appearance", 14,
                FinalScreenUi.NAVY, true);
        title.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0, dp(39), 1f);
        titleParams.setMarginStart(dp(13));
        heading.addView(title, titleParams);
        holder.addView(heading, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(42)));

        LinearLayout chooser = new LinearLayout(activity);
        chooser.setPadding(dp(3), dp(3), dp(3), dp(3));
        chooser.setBackground(FinalScreenUi.rounded(0xFFDDE3F1, 14, this));
        String[] labels = {"Light", "Dark", "System"};
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            appearanceOptions[i] = FinalScreenUi.text(activity, labels[i], 11,
                    FinalScreenUi.MUTED, false);
            appearanceOptions[i].setGravity(Gravity.CENTER);
            appearanceOptions[i].setOnClickListener(view -> selectAppearance(index));
            chooser.addView(appearanceOptions[i], new LinearLayout.LayoutParams(0, dp(34), 1f));
        }
        holder.addView(chooser, FinalScreenUi.margins(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(40), 46, 3, 0, 0, this));
        paintAppearance(navigator.getAppearance());
        return holder;
    }

    private void selectAppearance(int index) {
        paintAppearance(index);
        navigator.setAppearance(index);
        toast(index == 0 ? "Light theme on" : index == 1 ? "Dark theme on" : "Following system theme");
    }

    private void paintAppearance(int selected) {
        for (int i = 0; i < appearanceOptions.length; i++) {
            TextView option = appearanceOptions[i];
            if (option == null) {
                continue;
            }
            boolean active = i == selected;
            option.setTextColor(active ? FinalScreenUi.NAVY : FinalScreenUi.MUTED);
            option.setTypeface(android.graphics.Typeface.create("sans-serif-rounded",
                    active ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL));
            option.setBackground(active
                    ? FinalScreenUi.rounded(0xF8FFFFFF, 11, this)
                    : FinalScreenUi.rounded(Color.TRANSPARENT, 11, this));
            option.setElevation(active ? dp(2) : 0);
        }
    }

    private void addDivider(LinearLayout parent) {
        View divider = new View(activity);
        divider.setBackgroundColor(0x18758BB8);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
        params.setMargins(dp(55), 0, dp(8), 0);
        parent.addView(divider, params);
    }

    private void toast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    private int dp(float value) {
        return FinalScreenUi.dp(this, value);
    }
}
