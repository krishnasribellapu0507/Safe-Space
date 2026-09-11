package com.safespace.app;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/** Screen 18: optional positive community feed. */
final class CommunityView extends FrameLayout {
    private final Activity activity;
    private final TextView[] tabs = new TextView[4];
    private int selectedTab;

    CommunityView(Activity activity, ScreenNavigator navigator) {
        super(activity);
        this.activity = activity;
        setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFFDDEFFF, 0xFFF6F1FF, 0xFFFFEFF4}));

        ScrollView scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        addView(scroll, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(18), dp(16), dp(18), dp(30));
        scroll.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(FinalScreenUi.titledHeader(activity, this, navigator,
                        "A Kinder Community", "Share. Support. Grow."),
                FinalScreenUi.margins(ViewGroup.LayoutParams.MATCH_PARENT, dp(58),
                        0, 0, 0, 16, this));

        LinearLayout tabBar = new LinearLayout(activity);
        tabBar.setOrientation(LinearLayout.HORIZONTAL);
        tabBar.setPadding(dp(4), dp(4), dp(4), dp(4));
        tabBar.setBackground(FinalScreenUi.rounded(0xAFFFFFFF, 18, this));
        String[] labels = {"All", "Stories", "Tips", "Events"};
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            tabs[i] = FinalScreenUi.text(activity, labels[i], 12,
                    FinalScreenUi.MUTED, i == 0);
            tabs[i].setGravity(Gravity.CENTER);
            tabs[i].setClickable(true);
            tabs[i].setFocusable(true);
            tabs[i].setOnClickListener(view -> selectTab(index));
            tabBar.addView(tabs[i], new LinearLayout.LayoutParams(0, dp(40), 1f));
        }
        selectTab(0);
        content.addView(tabBar, FinalScreenUi.margins(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(48), 0, 0, 0, 15, this));

        content.addView(post("A", "You're stronger\nthan you think.  💜",
                        "24", "3", 0xFFFFE3EF, 0xFFD75E92),
                FinalScreenUi.margins(ViewGroup.LayoutParams.MATCH_PARENT, dp(126),
                        0, 0, 0, 13, this));
        content.addView(post("M", "Small steps, big changes!  ✨",
                        "18", "2", 0xFFE4ECFF, 0xFF675AD6),
                FinalScreenUi.margins(ViewGroup.LayoutParams.MATCH_PARENT, dp(116),
                        0, 0, 0, 13, this));
        content.addView(post("S", "Self-care is not selfish  🌸",
                        "31", "5", 0xFFE2F6ED, 0xFF449E73),
                FinalScreenUi.margins(ViewGroup.LayoutParams.MATCH_PARENT, dp(116),
                        0, 0, 0, 22, this));

        Button share = FinalScreenUi.purpleButton(activity, this, "▣  Share Something");
        share.setOnClickListener(view -> Toast.makeText(activity,
                "Your kind thought is ready to share", Toast.LENGTH_SHORT).show());
        content.addView(share, FinalScreenUi.margins(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(55), 0, 0, 0, 0, this));

        setOnApplyWindowInsetsListener((view, insets) -> {
            scroll.setPadding(0, insets.getSystemWindowInsetTop(), 0,
                    insets.getSystemWindowInsetBottom());
            return insets;
        });
        requestApplyInsets();
    }

    private void selectTab(int selected) {
        selectedTab = selected;
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i] == null) {
                continue;
            }
            boolean active = i == selectedTab;
            tabs[i].setTextColor(active ? Color.WHITE : FinalScreenUi.MUTED);
            tabs[i].setBackground(active
                    ? FinalScreenUi.gradient(new int[]{0xFF9D72EF, 0xFF6E57D7}, 15, this)
                    : FinalScreenUi.rounded(Color.TRANSPARENT, 15, this));
        }
    }

    private View post(String initial, String message, String hearts, String replies,
                      int avatarFill, int avatarText) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(13), dp(14), dp(10));
        card.setElevation(dp(3));
        card.setBackground(FinalScreenUi.stroked(
                0xF0FFFFFF, 19, 0x286D86C6, 1, this));

        LinearLayout top = new LinearLayout(activity);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView avatar = FinalScreenUi.text(activity, initial, 18, avatarText, true);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(FinalScreenUi.rounded(avatarFill, 24, this));
        top.addView(avatar, new LinearLayout.LayoutParams(dp(46), dp(46)));

        TextView body = FinalScreenUi.text(activity, message, 14, FinalScreenUi.NAVY, true);
        body.setGravity(Gravity.CENTER_VERTICAL);
        body.setLineSpacing(dp(2), 1f);
        LinearLayout.LayoutParams bodyParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        bodyParams.setMarginStart(dp(13));
        top.addView(body, bodyParams);
        card.addView(top, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        TextView stats = FinalScreenUi.text(activity,
                "♥  " + hearts + "      ◌  " + replies, 11, 0xFF8B4DB7, false);
        stats.setGravity(Gravity.CENTER);
        stats.setOnClickListener(view -> Toast.makeText(activity,
                "Kindness added", Toast.LENGTH_SHORT).show());
        card.addView(stats, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(28)));
        return card;
    }

    private int dp(float value) {
        return FinalScreenUi.dp(this, value);
    }
}
