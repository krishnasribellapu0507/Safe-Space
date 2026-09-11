package com.safespace.app;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Screen 20: dark, restful dashboard. */
final class NightModeView extends FrameLayout {
    private static final int TEXT = 0xFFF2F4FF;
    private static final int MUTED = 0xFFB9C8EB;
    private static final int PURPLE = 0xFFC69AFF;

    private final Activity activity;
    private final ScreenNavigator navigator;
    private final ScrollView scroll;
    private final LinearLayout bottomBar;
    private final int navHeight;

    NightModeView(Activity activity, ScreenNavigator navigator) {
        super(activity);
        this.activity = activity;
        this.navigator = navigator;
        navHeight = dp(76);
        setBackgroundColor(0xFF071430);

        addNightScenery();

        scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        addView(scroll, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        buildContent();

        bottomBar = buildBottomBar();
        FrameLayout.LayoutParams navParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, navHeight);
        navParams.gravity = Gravity.BOTTOM;
        addView(bottomBar, navParams);

        setOnApplyWindowInsetsListener((view, insets) -> {
            int top = insets.getSystemWindowInsetTop();
            int bottom = insets.getSystemWindowInsetBottom();
            scroll.setPadding(0, top, 0, navHeight + bottom + dp(16));
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bottomBar.getLayoutParams();
            params.height = navHeight + bottom;
            bottomBar.setPadding(dp(5), 0, dp(5), bottom);
            bottomBar.setLayoutParams(params);
            return insets;
        });
        requestApplyInsets();
    }

    private void addNightScenery() {
        ImageView background = new ImageView(activity);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inSampleSize = 2;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        background.setImageBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.serenity_background, options));
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);
        background.setColorFilter(0xFF243767, PorterDuff.Mode.MULTIPLY);
        background.setAlpha(.72f);
        background.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(background, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View wash = new View(activity);
        wash.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0x9A04102B, 0xC20A1B3D, 0xFA07142F}));
        wash.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(wash, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void buildContent() {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(dp(16), dp(12), dp(16), dp(22));
        scroll.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout top = new LinearLayout(activity);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView menu = text("☰", 24, TEXT, true);
        menu.setGravity(Gravity.CENTER);
        menu.setBackground(darkRipple(0x381C3767, 22));
        menu.setContentDescription("Open settings");
        menu.setOnClickListener(view -> navigator.openScreen(19));
        top.addView(menu, new LinearLayout.LayoutParams(dp(44), dp(44)));
        View spacer = new View(activity);
        top.addView(spacer, new LinearLayout.LayoutParams(0, dp(44), 1f));
        TextView moon = text("☾", 34, 0xFFFFEAB0, false);
        moon.setGravity(Gravity.CENTER);
        moon.setShadowLayer(dp(9), 0, 0, 0x99FFE6A1);
        top.addView(moon, new LinearLayout.LayoutParams(dp(56), dp(56)));
        content.addView(top, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(60)));

        TextView title = text("Good night,", 27, TEXT, true);
        title.setGravity(Gravity.CENTER);
        content.addView(title, margins(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 18, 0, 6));
        TextView subtitle = text("Rest today\nfor a brighter tomorrow", 14, MUTED, false);
        subtitle.setGravity(Gravity.CENTER);
        addWithMargins(content, subtitle, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, 24);

        LinearLayout first = featureRow();
        first.addView(feature("✦", "Talk", 10), weighted(false));
        first.addView(feature("▤", "Journal", 11), weighted(false));
        first.addView(feature("☾", "Relax", 12), weighted(true));
        content.addView(first, margins(ViewGroup.LayoutParams.MATCH_PARENT,
                dp(112), 0, 0, 0, 12));

        LinearLayout second = featureRow();
        second.addView(feature("☺", "Check-in", 9), weighted(false));
        second.addView(feature("▥", "Insights", 14), weighted(false));
        second.addView(feature("♡", "Support", 15), weighted(true));
        content.addView(second, margins(ViewGroup.LayoutParams.MATCH_PARENT,
                dp(112), 0, 0, 0, 14));
    }

    private LinearLayout featureRow() {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        return row;
    }

    private LinearLayout.LayoutParams weighted(boolean last) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(112), 1f);
        if (!last) {
            params.setMarginEnd(dp(10));
        }
        return params;
    }

    private View feature(String icon, String label, int target) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setElevation(dp(5));
        GradientDrawable fill = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0xD82C4479, 0xD8182D5D});
        fill.setCornerRadius(dp(19));
        fill.setStroke(dp(1), 0x557FA8EB);
        card.setBackground(new RippleDrawable(ColorStateList.valueOf(0x33C69AFF), fill, null));
        card.setContentDescription(label);
        card.setOnClickListener(view -> navigator.openScreen(target));

        TextView iconView = text(icon, 27, 0xFFD6B4FF, true);
        iconView.setGravity(Gravity.CENTER);
        iconView.setBackground(rounded(0x4FBA8BF5, 16));
        card.addView(iconView, new LinearLayout.LayoutParams(dp(53), dp(53)));
        TextView name = text(label, 11, TEXT, true);
        name.setGravity(Gravity.CENTER);
        card.addView(name, margins(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 8, 0, 0));
        return card;
    }

    private LinearLayout buildBottomBar() {
        LinearLayout bar = new LinearLayout(activity);
        bar.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xF0172B55, 0xFF091832}));
        bar.setElevation(dp(14));
        bar.addView(navItem("⌂", "Home", 8), navParams());
        bar.addView(navItem("☺", "Check-in", 9), navParams());
        bar.addView(lotusHome(), navParams());
        bar.addView(navItem("◌", "Calm", 12), navParams());
        bar.addView(navItem("●", "Profile", 16), navParams());
        return bar;
    }

    private View navItem(String icon, String label, int target) {
        LinearLayout item = new LinearLayout(activity);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setOnClickListener(view -> navigator.openScreen(target));
        TextView symbol = text(icon, 20, MUTED, true);
        symbol.setGravity(Gravity.CENTER);
        item.addView(symbol, new LinearLayout.LayoutParams(dp(31), dp(30)));
        TextView name = text(label, 9, MUTED, false);
        name.setGravity(Gravity.CENTER);
        item.addView(name);
        return item;
    }

    private View lotusHome() {
        FrameLayout holder = new FrameLayout(activity);
        holder.setOnClickListener(view -> navigator.openScreen(8));
        ImageView logo = new ImageView(activity);
        logo.setImageResource(R.drawable.safe_space_logo);
        logo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        logo.setBackground(rounded(0xFF263E72, 29));
        logo.setElevation(dp(8));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp(58), dp(58));
        params.gravity = Gravity.CENTER;
        holder.addView(logo, params);
        return holder;
    }

    private LinearLayout.LayoutParams navParams() {
        return new LinearLayout.LayoutParams(0, navHeight, 1f);
    }

    private TextView text(String value, float size, int color, boolean bold) {
        TextView text = new TextView(activity);
        text.setText(value);
        text.setTextSize(size);
        text.setTextColor(color);
        text.setIncludeFontPadding(false);
        text.setTypeface(Typeface.create("sans-serif-rounded",
                bold ? Typeface.BOLD : Typeface.NORMAL));
        return text;
    }

    private GradientDrawable rounded(int color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        return drawable;
    }

    private RippleDrawable darkRipple(int color, float radius) {
        return new RippleDrawable(ColorStateList.valueOf(0x33C69AFF),
                rounded(color, radius), null);
    }

    private LinearLayout.LayoutParams margins(int width, int height,
                                              int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom));
        return params;
    }

    private void addWithMargins(LinearLayout parent, View child, int width, int height,
                                int left, int top, int right, int bottom) {
        parent.addView(child, margins(width, height, left, top, right, bottom));
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
