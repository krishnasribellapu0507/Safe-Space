package com.safespace.app;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Small visual toolkit shared by the final Safe Space prototype screens. */
final class FinalScreenUi {
    static final int NAVY = Color.rgb(16, 38, 95);
    static final int MUTED = Color.rgb(91, 105, 140);
    static final int PURPLE = Color.rgb(123, 79, 233);

    private FinalScreenUi() {
    }

    static int dp(View view, float value) {
        return Math.round(value * view.getResources().getDisplayMetrics().density);
    }

    static TextView text(Activity activity, String value, float size, int color, boolean bold) {
        TextView text = new TextView(activity);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);
        text.setIncludeFontPadding(false);
        text.setTypeface(Typeface.create("sans-serif-rounded",
                bold ? Typeface.BOLD : Typeface.NORMAL));
        return text;
    }

    static GradientDrawable rounded(int color, float radius, View densityView) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(densityView, radius));
        return drawable;
    }

    static GradientDrawable stroked(int color, float radius, int strokeColor,
                                    float strokeWidth, View densityView) {
        GradientDrawable drawable = rounded(color, radius, densityView);
        drawable.setStroke(dp(densityView, strokeWidth), strokeColor);
        return drawable;
    }

    static GradientDrawable gradient(int[] colors, float radius, View densityView) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, colors);
        drawable.setCornerRadius(dp(densityView, radius));
        return drawable;
    }

    static Drawable ripple(int color, float radius, View densityView) {
        return new RippleDrawable(ColorStateList.valueOf(0x247B4FE9),
                rounded(color, radius, densityView), null);
    }

    static TextView backButton(Activity activity, View densityView, ScreenNavigator navigator) {
        TextView back = text(activity, "‹", 37, NAVY, false);
        back.setGravity(Gravity.CENTER);
        back.setBackground(ripple(0xA8FFFFFF, 23, densityView));
        back.setContentDescription("Back");
        back.setOnClickListener(view -> navigator.goBack());
        return back;
    }

    static LinearLayout titledHeader(Activity activity, View densityView,
                                     ScreenNavigator navigator, String title, String subtitle) {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        row.addView(backButton(activity, densityView, navigator),
                new LinearLayout.LayoutParams(dp(densityView, 46), dp(densityView, 46)));

        LinearLayout labels = new LinearLayout(activity);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setGravity(Gravity.CENTER);
        TextView heading = text(activity, title, 21, NAVY, true);
        heading.setGravity(Gravity.CENTER);
        labels.addView(heading, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (subtitle != null && !subtitle.isEmpty()) {
            TextView detail = text(activity, subtitle, 10, MUTED, false);
            detail.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams detailParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            detailParams.topMargin = dp(densityView, 3);
            labels.addView(detail, detailParams);
        }
        row.addView(labels, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        View spacer = new View(activity);
        row.addView(spacer, new LinearLayout.LayoutParams(
                dp(densityView, 46), dp(densityView, 46)));
        return row;
    }

    static Button purpleButton(Activity activity, View densityView, String label) {
        Button button = new Button(activity);
        button.setAllCaps(false);
        button.setText(label);
        button.setTextColor(Color.WHITE);
        button.setTextSize(16);
        button.setGravity(Gravity.CENTER);
        button.setTypeface(Typeface.create("sans-serif-rounded", Typeface.BOLD));
        button.setElevation(dp(densityView, 5));
        button.setBackground(gradient(new int[]{0xFF9B67F0, 0xFF7042DD}, 28, densityView));
        return button;
    }

    static LinearLayout.LayoutParams margins(int width, int height,
                                             int left, int top, int right, int bottom,
                                             View densityView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(dp(densityView, left), dp(densityView, top),
                dp(densityView, right), dp(densityView, bottom));
        return params;
    }

}
