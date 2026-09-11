package com.safespace.app;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/** Screen 13: mindful play catalogue with functional games. */
final class ActivitiesView extends PastelScreenView {
    ActivitiesView(Activity activity, ScreenNavigator navigator) {
        super(activity, navigator, -1);
        setContentDescription("Mindful Activities screen");
        content.addView(header("Play", "Calming, mindful play for the present moment", true,
                "", ""), marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(76), 0, 0, 0, dp(10)));

        addGame("○", "Bubble Pop", "Tap soft bubbles and slow down", 0xFFDDF7FF, 0xFF3F9BCB);
        addGame("≋", "Zen Garden", "Draw quiet patterns in the sand", 0xFFFFEDDF, 0xFFD1884E);
        addGame("✦", "Firefly Glow", "Follow small lights with your attention", 0xFFE4E7FF, 0xFF6757CF);
        addGame("▦", "Calm Blocks", "Build a simple steady tower", 0xFFFFE5ED, 0xFFD86188);

        LinearLayout grounding = bottomRowCard("5", 0xFFE4F5E8, 0xFF3B9364,
                "5-4-3-2-1 Grounding", "A guided senses exercise", "Open", "");
        grounding.setOnClickListener(v -> navigator.openScreen(12));
        content.addView(grounding, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(78), 0, dp(4), 0, 0));
    }

    private void addGame(String glyph, String title, String subtitle, int fill, int color) {
        LinearLayout card = bottomRowCard(glyph, fill, color, title, subtitle, "Play", "");
        card.setOnClickListener(v -> navigator.openScreen(24));
        content.addView(card, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(82), 0, 0, 0, dp(10)));
    }
}
