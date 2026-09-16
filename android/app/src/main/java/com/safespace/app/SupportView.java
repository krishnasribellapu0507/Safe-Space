package com.safespace.app;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Screen 15: calm, clearly separated prototype support choices. */
final class SupportView extends PastelScreenView {
    SupportView(Activity activity, ScreenNavigator navigator) {
        super(activity, navigator, -1);
        setContentDescription("You're Not Alone support screen");

        content.addView(header("", "", true, "", ""), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(52), 0, 0, 0, 0));
        content.addView(buildHero(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(118), 0, 0, 0, dp(18)));

        LinearLayout counsellor = supportCard("♟", 0xFFDDEEFF, 0xFF397FC5,
                "Talk to a Counsellor", "Connect with professional support",
                "Open counsellor directory");
        counsellor.setOnClickListener(v -> navigator.openScreen(22));
        content.addView(counsellor, cardParams());
        content.addView(supportCard("☎", 0xFFDFF5E8, 0xFF359E67,
                "Helpline Numbers", "Get immediate support",
                "Helpline directory is a prototype"), cardParams());
        content.addView(supportCard("♥", 0xFFFFE4ED, 0xFFE34F80,
                "Emergency Help", "In case of urgent need",
                "If you are in immediate danger, contact local emergency services"),
                cardParams());
        content.addView(supportCard("⌕", 0xFFFFEBDD, 0xFFDF7A42,
                "Find Resources", "Legal, financial and more",
                "Resource finder is a prototype"), cardParams());

        TextView note = text("You deserve support, care, and kindness.", 11,
                MUTED_NAVY, false);
        note.setGravity(Gravity.CENTER);
        content.addView(note, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(35), 0, dp(2), 0, 0));
    }

    private LinearLayout.LayoutParams cardParams() {
        return marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(78),
                0, 0, 0, dp(11));
    }

    private View buildHero() {
        LinearLayout hero = new LinearLayout(activity);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setGravity(Gravity.CENTER);

        FrameLayout iconShell = new FrameLayout(activity);
        iconShell.setBackground(gradientRounded(0xFFEAE2FF, 0xFFFFE5F0,
                dp(28), dp(1), 0x34A26BE2));
        iconShell.setElevation(dp(3));
        TextView heart = text("♥", 29, PURPLE, true);
        heart.setGravity(Gravity.CENTER);
        iconShell.addView(heart, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        hero.addView(iconShell, new LinearLayout.LayoutParams(dp(57), dp(57)));

        TextView title = text("You’re Not Alone", 21, NAVY, true);
        title.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.topMargin = dp(8);
        hero.addView(title, titleParams);

        TextView subtitle = text("Help is always available", 11, MUTED_NAVY, false);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = dp(3);
        hero.addView(subtitle, subtitleParams);
        return hero;
    }

    private LinearLayout supportCard(String glyph, int fill, int color,
                                     String title, String subtitle, String message) {
        LinearLayout card = bottomRowCard(glyph, fill, color, title, subtitle,
                null, message);
        if ("Emergency Help".equals(title)) {
            card.setBackground(rippleRounded(0xF7FFF9FB, dp(19), 0x45F06E98));
        }
        return card;
    }
}
