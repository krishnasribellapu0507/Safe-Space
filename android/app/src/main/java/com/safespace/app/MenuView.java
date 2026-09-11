package com.safespace.app;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Professional all-features navigation menu. */
final class MenuView extends PastelScreenView {
    private static final Object[][] ITEMS = {
            {"⌂", "Home", "Your dashboard", 8},
            {"☺", "Check-in", "Track how you feel", 9},
            {"✦", "Companion", "Supportive conversation", 10},
            {"▤", "Journal", "Private reflections", 11},
            {"◌", "Calm", "Breathing, meditation & sounds", 12},
            {"▦", "Play", "Mindful calming games", 13},
            {"▥", "Insights", "See your patterns", 14},
            {"♡", "Support", "Help and resources", 15},
            {"♟", "Counsellors", "Professional support directory", 22},
            {"!", "Safety & SOS", "Trusted contact, SOS and alarm", 25},
            {"⌖", "Nearby care", "Psychiatrists, psychologists and hospitals", 26},
            {"360", "Safe Space 360", "Emotional weather, justice journey, recovery and adaptive support", 27},
            {"◷", "Reminders", "Your routines", 17},
            {"♧", "Community", "Safe community space", 18},
            {"⚙", "Settings", "Appearance and privacy", 19},
            {"●", "Profile", "Your account", 16}
    };

    MenuView(Activity activity, ScreenNavigator navigator) {
        super(activity, navigator, -1);
        setContentDescription("Safe Space navigation menu");
        content.addView(header("Menu", "Everything in Safe Space", true, "", ""),
                marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(70), 0, 0, 0, dp(10)));

        for (Object[] item : ITEMS) {
            int screen = (Integer) item[3];
            LinearLayout card = bottomRowCard((String) item[0], 0xFFE8E6FF, PURPLE,
                    (String) item[1], (String) item[2], null, "");
            card.setOnClickListener(v -> navigator.openScreen(screen));
            content.addView(card, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(70),
                    0, 0, 0, dp(8)));
        }
    }
}
