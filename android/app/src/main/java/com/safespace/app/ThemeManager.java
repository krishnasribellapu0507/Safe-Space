package com.safespace.app;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/** Persistent app-wide light-blue / dark-purple appearance support. */
final class ThemeManager {
    static final int LIGHT = 0;
    static final int DARK = 1;
    static final int SYSTEM = 2;
    static final int LIGHT_BG = 0xFFDDF2FF;
    static final int LIGHT_SURFACE = 0xFFF2FAFF;
    static final int DARK_BG = 0xFF241052;
    static final int DARK_SURFACE = 0xFF43227A;
    static final int DARK_CARD = 0xFF6848A4;

    private static final String PREFS = "safe_space_settings";
    private static final String KEY = "appearance";

    private ThemeManager() {}

    static int getMode(Context context) {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getInt(KEY, LIGHT);
    }

    static void setMode(Context context, int mode) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putInt(KEY, mode).apply();
    }

    static boolean isDark(Context context) {
        int mode = getMode(context);
        if (mode == DARK) return true;
        if (mode == LIGHT) return false;
        int night = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return night == Configuration.UI_MODE_NIGHT_YES;
    }

    static void applyToScreen(Activity activity, View root) {
        if (root == null) return;
        boolean dark = isDark(activity);
        if (dark) {
            tintDark(root, 0);
            activity.getWindow().setStatusBarColor(DARK_BG);
            activity.getWindow().setNavigationBarColor(DARK_BG);
        } else {
            root.setBackgroundColor(LIGHT_BG);
            activity.getWindow().setNavigationBarColor(LIGHT_BG);
        }
    }

    private static void tintDark(View view, int depth) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            int c = tv.getCurrentTextColor();
            if (luminance(c) < 0.72) tv.setTextColor(0xFFF8F5FF);
            if (tv.getBackground() != null) {
                tv.setBackgroundTintList(ColorStateList.valueOf(depth <= 3 ? DARK_CARD : 0xFF7655B4));
            }
        } else if (view instanceof ImageView) {
            view.setAlpha(Math.min(view.getAlpha(), .92f));
        } else if (view.getBackground() != null) {
            int tone = depth <= 0 ? DARK_BG : depth <= 2 ? DARK_SURFACE : DARK_CARD;
            view.setBackgroundTintList(ColorStateList.valueOf(tone));
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            group.setClipChildren(false);
            for (int i = 0; i < group.getChildCount(); i++) {
                tintDark(group.getChildAt(i), depth + 1);
            }
        }
    }

    private static double luminance(int color) {
        double r = Color.red(color) / 255.0;
        double g = Color.green(color) / 255.0;
        double b = Color.blue(color) / 255.0;
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }
}
