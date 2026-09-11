package com.safespace.app;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The pastel home screen shown after the permissions flow.
 *
 * <p>This view deliberately has no dependency on AndroidX or an XML layout so it can be
 * dropped into the prototype's existing, programmatic UI. All actions are safe prototype
 * affordances and currently acknowledge the tap with a short message.</p>
 */
final class HomeDashboardView extends FrameLayout {
    private static final int NAVY = Color.rgb(18, 43, 98);
    private static final int MUTED_NAVY = Color.rgb(88, 102, 137);
    private static final int PURPLE = Color.rgb(125, 78, 232);
    private static final int CARD_STROKE = 0x307B4FE9;

    private final Activity activity;
    private final ScreenNavigator navigator;
    private final ScrollView scrollView;
    private final LinearLayout bottomBar;
    private final int navigationHeight;

    HomeDashboardView(Activity activity, ScreenNavigator navigator) {
        super(activity);
        this.activity = activity;
        this.navigator = navigator;
        navigationHeight = dp(76);

        setBackgroundColor(ThemeManager.isDark(activity) ? ThemeManager.DARK_BG : ThemeManager.LIGHT_BG);
        setClipChildren(false);
        setClipToPadding(false);
        setFocusable(true);
        setContentDescription("Safe Space home dashboard");

        addScenicBackground();

        scrollView = new ScrollView(activity);
        scrollView.setFillViewport(true);
        scrollView.setClipToPadding(false);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        addView(scrollView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        buildDashboardContent();

        bottomBar = buildBottomNavigation();
        FrameLayout.LayoutParams navParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, navigationHeight);
        navParams.gravity = Gravity.BOTTOM;
        addView(bottomBar, navParams);

        setOnApplyWindowInsetsListener((view, insets) -> applyInsets(insets));
        requestApplyInsets();
    }

    private void addScenicBackground() {
        ImageView scenery = new ImageView(activity);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inSampleSize = 2;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap scene = BitmapFactory.decodeResource(
                getResources(), R.drawable.serenity_background, options);
        scenery.setImageBitmap(scene);
        scenery.setScaleType(ImageView.ScaleType.CENTER_CROP);
        scenery.setAlpha(.78f);
        scenery.setContentDescription(null);
        scenery.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(scenery, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(322)));

        View softWash = new View(activity);
        softWash.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0x2EFFFFFF, 0xC6ECF3FF, 0xFFF8EFF8}));
        softWash.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(softWash, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private WindowInsets applyInsets(WindowInsets insets) {
        int top = insets.getSystemWindowInsetTop();
        int bottom = insets.getSystemWindowInsetBottom();
        scrollView.setPadding(0, top + dp(6), 0, navigationHeight + bottom + dp(18));

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bottomBar.getLayoutParams();
        params.height = navigationHeight + bottom;
        bottomBar.setPadding(dp(5), 0, dp(5), bottom);
        bottomBar.setLayoutParams(params);
        return insets;
    }

    private void buildDashboardContent() {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(9), dp(16), dp(18));
        scrollView.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        content.addView(buildHeader(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(104), 0, 0, 0, dp(7)));
        content.addView(buildMoodPrompt(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(57), 0, 0, 0, dp(9)));
        content.addView(buildPersonalisedQuickAction(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(62), 0, 0, 0, dp(13)));

        LinearLayout firstRow = featureRow();
        firstRow.addView(featureCard("✦", "Talk", 0xFFE5E8FF, 0xFF6655DD,
                10), weightedFeatureParams(false));
        firstRow.addView(featureCard("▤", "Journal", 0xFFFFE8F2, 0xFFE6659E,
                11), weightedFeatureParams(false));
        firstRow.addView(featureCard("❀", "Relax", 0xFFE0F5FF, 0xFF3D9BCD,
                12), weightedFeatureParams(true));
        content.addView(firstRow, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(111), 0, 0, 0, dp(11)));

        LinearLayout secondRow = featureRow();
        secondRow.addView(featureCard("☺", "Check-in", 0xFFFFEBD9, 0xFFE48745,
                9), weightedFeatureParams(false));
        secondRow.addView(featureCard("▥", "Insights", 0xFFF0E2FF, 0xFF8B51DE,
                14), weightedFeatureParams(false));
        secondRow.addView(featureCard("♡", "Support", 0xFFFFE2EE, 0xFFDB5C8B,
                15), weightedFeatureParams(true));
        content.addView(secondRow, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(111), 0, 0, 0, dp(11)));

        LinearLayout safetyRow = featureRow();
        safetyRow.addView(featureCard("SOS", "Safety", 0xFFFFE5EA, 0xFFE34F6F,
                25), weightedFeatureParams(false));
        safetyRow.addView(featureCard("⌖", "Nearby care", 0xFFDFF4FF, 0xFF2D85C6,
                26), weightedFeatureParams(true));
        content.addView(safetyRow, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(106), 0, 0, 0, dp(10)));

        LinearLayout sahaayRow = featureRow();
        sahaayRow.addView(featureCard("360", "Safe Space 360", 0xFFE9E2FF, 0xFF7047D7,
                27), weightedFeatureParams(false));
        sahaayRow.addView(featureCard("🌱", "My progress", 0xFFE2F7EA, 0xFF2F9462,
                27), weightedFeatureParams(true));
        content.addView(sahaayRow, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(106), 0, 0, 0, dp(17)));

        content.addView(buildThoughtCard(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(118), 0, 0, 0, dp(8)));
    }

    private View buildHeader() {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView menu = text("☰", 25, NAVY, true);
        menu.setGravity(Gravity.CENTER);
        menu.setBackground(rippleCircle(0xB8FFFFFF));
        menu.setContentDescription("Open menu");
        menu.setOnClickListener(view -> navigator.openScreen(23));
        row.addView(menu, new LinearLayout.LayoutParams(dp(44), dp(44)));

        LinearLayout greeting = new LinearLayout(activity);
        greeting.setOrientation(LinearLayout.VERTICAL);
        greeting.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = text("Good morning,", 22, NAVY, true);
        title.setGravity(Gravity.CENTER);
        greeting.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView subtitle = text("A brighter you\nstarts today  ✿", 12, NAVY, false);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setLineSpacing(0, 1.05f);
        greeting.addView(subtitle, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout.LayoutParams greetingParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        greetingParams.setMargins(dp(5), 0, dp(5), 0);
        row.addView(greeting, greetingParams);

        SharedPreferences profilePrefs = activity.getSharedPreferences("safe_space_profile", Context.MODE_PRIVATE);
        String displayName = profilePrefs.getString("display_name", "Safe Space");
        String initial = "S";
        if (displayName != null && !displayName.trim().isEmpty()) {
            initial = displayName.trim().substring(0, 1).toUpperCase();
        }
        TextView avatar = text(initial, 22, Color.WHITE, true);
        avatar.setGravity(Gravity.CENTER);
        avatar.setBackground(circleGradient(0xFF7F6AE8, 0xFFE07FC8, dp(2), Color.WHITE));
        avatar.setElevation(dp(4));
        avatar.setContentDescription("Open profile");
        avatar.setOnClickListener(view -> navigator.openScreen(16));
        row.addView(avatar, new LinearLayout.LayoutParams(dp(48), dp(48)));
        return row;
    }

    private View buildMoodPrompt() {
        LinearLayout prompt = new LinearLayout(activity);
        prompt.setOrientation(LinearLayout.HORIZONTAL);
        prompt.setGravity(Gravity.CENTER_VERTICAL);
        prompt.setPadding(dp(16), 0, dp(8), 0);
        prompt.setElevation(dp(3));
        prompt.setBackground(rippleRounded(0xF4FFFFFF, dp(18), CARD_STROKE));
        prompt.setContentDescription("How are you feeling today? Start a mood check-in");
        prompt.setOnClickListener(view -> navigator.openScreen(9));

        TextView question = text("How are you feeling today?", 14, NAVY, false);
        question.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        prompt.addView(question, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        TextView plus = text("+", 24, Color.WHITE, false);
        plus.setGravity(Gravity.CENTER);
        plus.setBackground(circleGradient(0xFF98A7F5, 0xFF7452DE, 0, Color.TRANSPARENT));
        prompt.addView(plus, new LinearLayout.LayoutParams(dp(40), dp(40)));
        return prompt;
    }

    private View buildPersonalisedQuickAction() {
        SharedPreferences prefs = activity.getSharedPreferences("safe_space_settings", Context.MODE_PRIVATE);
        String preferred = prefs.getString("preferred_home", "Mood check-in");
        int screen = 9;
        if (preferred != null) {
            if (preferred.contains("Calm")) screen = 12;
            else if (preferred.contains("Journal")) screen = 11;
            else if (preferred.contains("Games")) screen = 24;
            else if (preferred.contains("Counsellor")) screen = 22;
            else if (preferred.contains("Safety")) screen = 25;
        }
        final int target = screen;
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), 0, dp(12), 0);
        card.setElevation(dp(2));
        card.setBackground(rippleRounded(0xEFFFFFFF, dp(18), 0x227B4FE9));
        TextView label = text("For you: " + preferred, 12.5f, NAVY, true);
        card.addView(label, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        TextView arrow = text("›", 25, PURPLE, true);
        arrow.setGravity(Gravity.CENTER);
        card.addView(arrow, new LinearLayout.LayoutParams(dp(35), ViewGroup.LayoutParams.MATCH_PARENT));
        card.setOnClickListener(v -> navigator.openScreen(target));
        card.setContentDescription("Personalised quick action: " + preferred);
        return card;
    }

    private LinearLayout featureRow() {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        return row;
    }

    private LinearLayout.LayoutParams weightedFeatureParams(boolean last) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        if (!last) {
            params.setMarginEnd(dp(10));
        }
        return params;
    }

    private View featureCard(String icon, String label, int iconBackground, int iconColor,
                             int targetScreen) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(5), dp(10), dp(5), dp(8));
        card.setElevation(dp(3));
        card.setBackground(rippleRounded(0xF2FFFFFF, dp(20), 0x227B4FE9));
        card.setContentDescription(label);
        card.setOnClickListener(view -> navigator.openScreen(targetScreen));

        TextView iconView = text(icon, 27, iconColor, true);
        iconView.setGravity(Gravity.CENTER);
        iconView.setBackground(rounded(iconBackground, dp(15), 0, Color.TRANSPARENT));
        card.addView(iconView, new LinearLayout.LayoutParams(dp(52), dp(52)));

        TextView labelView = text(label, 12, NAVY, true);
        labelView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        labelParams.topMargin = dp(5);
        card.addView(labelView, labelParams);
        return card;
    }

    private View buildThoughtCard() {
        FrameLayout card = new FrameLayout(activity);
        card.setElevation(dp(3));
        card.setPadding(dp(18), dp(13), dp(18), dp(12));
        card.setBackground(rippleRounded(0xEFFFF8FC, dp(22), 0x28E07FC8));
        card.setContentDescription("Today's thought: Small steps make big progress");
        card.setOnClickListener(view -> toast("One small step is enough for today"));

        TextView petalLeft = text("❀", 36, 0x55EF85B9, false);
        petalLeft.setGravity(Gravity.BOTTOM | Gravity.START);
        FrameLayout.LayoutParams petalLeftParams = new FrameLayout.LayoutParams(dp(58), dp(58));
        petalLeftParams.gravity = Gravity.BOTTOM | Gravity.START;
        petalLeftParams.leftMargin = -dp(10);
        petalLeftParams.bottomMargin = -dp(10);
        card.addView(petalLeft, petalLeftParams);

        TextView petalRight = text("✦", 28, 0x66795BE3, false);
        petalRight.setGravity(Gravity.TOP | Gravity.END);
        FrameLayout.LayoutParams petalRightParams = new FrameLayout.LayoutParams(dp(45), dp(45));
        petalRightParams.gravity = Gravity.TOP | Gravity.END;
        petalRightParams.rightMargin = -dp(6);
        card.addView(petalRight, petalRightParams);

        LinearLayout words = new LinearLayout(activity);
        words.setOrientation(LinearLayout.VERTICAL);
        words.setGravity(Gravity.CENTER);

        TextView heading = text("Today's Thought", 12, PURPLE, true);
        heading.setGravity(Gravity.CENTER);
        words.addView(heading, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView quote = text("“Small steps make big\nprogress.”", 17, NAVY, true);
        quote.setGravity(Gravity.CENTER);
        quote.setLineSpacing(dp(2), 1f);
        LinearLayout.LayoutParams quoteParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        quoteParams.topMargin = dp(6);
        words.addView(quote, quoteParams);
        card.addView(words, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return card;
    }

    private LinearLayout buildBottomNavigation() {
        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        bar.setClipChildren(false);
        bar.setClipToPadding(false);
        bar.setPadding(dp(5), 0, dp(5), 0);
        bar.setElevation(dp(14));
        bar.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xF8FFFFFF, 0xFFF4F7FF}));

        bar.addView(navItem("⌂", "Home", true, 8), navItemParams());
        bar.addView(navItem("☺", "Check-in", false, 9), navItemParams());
        bar.addView(centerLotus(), navItemParams());
        bar.addView(navItem("◌", "Calm", false, 12), navItemParams());
        bar.addView(navItem("●", "Profile", false, 16), navItemParams());
        return bar;
    }

    private LinearLayout.LayoutParams navItemParams() {
        return new LinearLayout.LayoutParams(0, navigationHeight, 1f);
    }

    private View navItem(String icon, String label, boolean selected, int targetScreen) {
        LinearLayout item = new LinearLayout(activity);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(0, dp(6), 0, dp(3));
        item.setBackground(new RippleDrawable(
                ColorStateList.valueOf(0x187B4FE9),
                rounded(Color.TRANSPARENT, dp(12), 0, Color.TRANSPARENT), null));
        item.setContentDescription(label);
        item.setOnClickListener(view -> navigator.openScreen(targetScreen));

        int color = selected ? PURPLE : MUTED_NAVY;
        TextView iconView = text(icon, selected ? 23 : 20, color, true);
        iconView.setGravity(Gravity.CENTER);
        item.addView(iconView, new LinearLayout.LayoutParams(dp(32), dp(30)));

        TextView labelView = text(label, 10, color, selected);
        labelView.setGravity(Gravity.CENTER);
        item.addView(labelView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return item;
    }

    private View centerLotus() {
        LinearLayout item = new LinearLayout(activity);
        item.setGravity(Gravity.CENTER);
        item.setClipChildren(false);
        item.setClipToPadding(false);
        item.setTranslationY(-dp(18));
        item.setContentDescription("Safe Space home");
        item.setOnClickListener(view -> toast("You're already home"));

        ImageView lotus = new ImageView(activity);
        lotus.setImageResource(R.drawable.safe_space_logo);
        lotus.setScaleType(ImageView.ScaleType.CENTER_CROP);
        lotus.setBackground(rounded(Color.WHITE, dp(36), dp(3), 0x66C798FF));
        lotus.setElevation(dp(12));
        item.addView(lotus, new LinearLayout.LayoutParams(dp(72), dp(72)));
        return item;
    }

    private TextView text(String value, float sizeSp, int color, boolean bold) {
        TextView view = new TextView(activity);
        view.setText(value);
        view.setTextSize(sizeSp);
        view.setTextColor(color);
        view.setIncludeFontPadding(false);
        view.setTypeface(Typeface.create("sans-serif-rounded",
                bold ? Typeface.BOLD : Typeface.NORMAL));
        return view;
    }

    private Drawable rippleRounded(int fill, int radius, int stroke) {
        return new RippleDrawable(ColorStateList.valueOf(0x247B4FE9),
                rounded(fill, radius, stroke == 0 ? 0 : dp(1), stroke), null);
    }

    private Drawable rippleCircle(int fill) {
        return new RippleDrawable(ColorStateList.valueOf(0x247B4FE9),
                rounded(fill, dp(24), dp(1), 0x20FFFFFF), null);
    }

    private GradientDrawable rounded(int fill, int radius, int strokeWidth, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) {
            drawable.setStroke(strokeWidth, strokeColor);
        }
        return drawable;
    }

    private GradientDrawable circleGradient(int start, int end, int strokeWidth,
                                            int strokeColor) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, new int[]{start, end});
        drawable.setShape(GradientDrawable.OVAL);
        if (strokeWidth > 0) {
            drawable.setStroke(strokeWidth, strokeColor);
        }
        return drawable;
    }

    private LinearLayout.LayoutParams marginParams(int width, int height,
                                                    int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(left, top, right, bottom);
        return params;
    }

    private void toast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
