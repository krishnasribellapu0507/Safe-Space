package com.safespace.app;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/** Screen 17: reminder setup. */
final class RemindersView extends FrameLayout {
    private final Activity activity;
    private final ScreenNavigator navigator;
    private final LinearLayout content;

    RemindersView(Activity activity, ScreenNavigator navigator) {
        super(activity);
        this.activity = activity;
        this.navigator = navigator;
        setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFFDCEEFF, 0xFFF2F3FF, 0xFFFFEFF5}));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        addView(scroll, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(dp(20), dp(16), dp(20), dp(28));
        scroll.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(FinalScreenUi.titledHeader(activity, this, navigator,
                "Set a Reminder", "A little nudge for your well-being"),
                FinalScreenUi.margins(ViewGroup.LayoutParams.MATCH_PARENT, dp(58),
                        0, 0, 0, 18, this));

        LinearLayout rowOne = tileRow();
        rowOne.addView(reminderTile("☻", "Check-in", 0xFFE8E9FF, 0xFF6657D9),
                weighted(false));
        rowOne.addView(reminderTile("✦", "Meditation", 0xFFE2F7FF, 0xFF318FBF),
                weighted(false));
        rowOne.addView(reminderTile("▤", "Journal", 0xFFFFE7F1, 0xFFE0609B),
                weighted(true));
        content.addView(rowOne, FinalScreenUi.margins(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(105), 0, 0, 0, 11, this));

        LinearLayout rowTwo = tileRow();
        rowTwo.addView(reminderTile("♒", "Water", 0xFFDFF4FF, 0xFF3797C9),
                weighted(false));
        rowTwo.addView(reminderTile("♧", "Walk", 0xFFE4F6E8, 0xFF48A46B),
                weighted(false));
        rowTwo.addView(reminderTile("☾", "Sleep", 0xFFECE6FF, 0xFF8062D9),
                weighted(true));
        content.addView(rowTwo, FinalScreenUi.margins(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(105), 0, 0, 0, 17, this));

        content.addView(customReminder(), FinalScreenUi.margins(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(60), 0, 0, 0, 16, this));
        content.addView(toggleCard("Daily Reminder", "Check in with yourself", true),
                FinalScreenUi.margins(ViewGroup.LayoutParams.MATCH_PARENT, dp(68),
                        0, 0, 0, 10, this));
        content.addView(toggleCard("Motivational Notifications",
                        "Gentle encouragement through the day", true),
                FinalScreenUi.margins(ViewGroup.LayoutParams.MATCH_PARENT, dp(68),
                        0, 0, 0, 24, this));

        Button save = FinalScreenUi.purpleButton(activity, this, "Save");
        save.setContentDescription("Save reminders");
        save.setOnClickListener(view -> navigator.openScreen(21));
        content.addView(save, FinalScreenUi.margins(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(56), 0, 0, 0, 8, this));

        setOnApplyWindowInsetsListener((view, insets) -> {
            scroll.setPadding(0, insets.getSystemWindowInsetTop(), 0,
                    insets.getSystemWindowInsetBottom());
            return insets;
        });
        requestApplyInsets();
    }

    private LinearLayout tileRow() {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        return row;
    }

    private LinearLayout.LayoutParams weighted(boolean last) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(105), 1f);
        if (!last) {
            params.setMarginEnd(dp(10));
        }
        return params;
    }

    private View reminderTile(String icon, String label, int fill, int accent) {
        LinearLayout tile = new LinearLayout(activity);
        tile.setOrientation(LinearLayout.VERTICAL);
        tile.setGravity(Gravity.CENTER);
        tile.setPadding(dp(7), dp(9), dp(7), dp(7));
        tile.setElevation(dp(3));
        tile.setBackground(FinalScreenUi.ripple(0xEDFFFFFF, 18, this));
        tile.setContentDescription(label + " reminder");
        tile.setOnClickListener(view -> Toast.makeText(activity,
                label + " reminder selected", Toast.LENGTH_SHORT).show());

        TextView iconView = FinalScreenUi.text(activity, icon, 27, accent, true);
        iconView.setGravity(Gravity.CENTER);
        iconView.setBackground(FinalScreenUi.rounded(fill, 15, this));
        tile.addView(iconView, new LinearLayout.LayoutParams(dp(53), dp(53)));
        TextView title = FinalScreenUi.text(activity, label, 11, FinalScreenUi.NAVY, true);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.topMargin = dp(7);
        tile.addView(title, titleParams);
        return tile;
    }

    private View customReminder() {
        LinearLayout row = new LinearLayout(activity);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), 0, dp(15), 0);
        row.setElevation(dp(2));
        row.setBackground(FinalScreenUi.ripple(0xEEFFFFFF, 17, this));
        row.setOnClickListener(view -> Toast.makeText(activity,
                "Custom reminder ready to edit", Toast.LENGTH_SHORT).show());
        TextView plus = FinalScreenUi.text(activity, "+", 25, FinalScreenUi.PURPLE, false);
        plus.setGravity(Gravity.CENTER);
        row.addView(plus, new LinearLayout.LayoutParams(dp(34), dp(42)));
        TextView title = FinalScreenUi.text(activity, "Custom Reminder", 14,
                FinalScreenUi.NAVY, true);
        title.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(title, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        TextView arrow = FinalScreenUi.text(activity, "›", 25, FinalScreenUi.MUTED, false);
        arrow.setGravity(Gravity.CENTER);
        row.addView(arrow, new LinearLayout.LayoutParams(dp(30), dp(42)));
        return row;
    }

    private View toggleCard(String title, String subtitle, boolean enabled) {
        LinearLayout row = new LinearLayout(activity);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(9), dp(12), dp(9));
        row.setElevation(dp(2));
        row.setBackground(FinalScreenUi.rounded(0xEDFFFFFF, 17, this));

        TextView icon = FinalScreenUi.text(activity, "▣", 19, FinalScreenUi.PURPLE, true);
        icon.setGravity(Gravity.CENTER);
        row.addView(icon, new LinearLayout.LayoutParams(dp(34), dp(42)));

        LinearLayout labels = new LinearLayout(activity);
        labels.setOrientation(LinearLayout.VERTICAL);
        TextView heading = FinalScreenUi.text(activity, title, 13, FinalScreenUi.NAVY, true);
        TextView detail = FinalScreenUi.text(activity, subtitle, 10, FinalScreenUi.MUTED, false);
        labels.addView(heading);
        labels.addView(detail);
        row.addView(labels, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        Switch toggle = new Switch(activity);
        toggle.setChecked(enabled);
        toggle.setShowText(false);
        toggle.setThumbTintList(new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{Color.WHITE, 0xFFF1F1F4}));
        toggle.setTrackTintList(new ColorStateList(
                new int[][]{new int[]{android.R.attr.state_checked}, new int[]{}},
                new int[]{0xFF70B9DD, 0xFFBBC6D1}));
        toggle.setContentDescription(title);
        row.addView(toggle, new LinearLayout.LayoutParams(dp(54), dp(44)));
        return row;
    }

    private int dp(float value) {
        return FinalScreenUi.dp(this, value);
    }
}
