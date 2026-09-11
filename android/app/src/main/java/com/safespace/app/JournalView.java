package com.safespace.app;

import android.app.Activity;
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

/** Screen eleven: the user's calm, private journal. */
final class JournalView extends FrameLayout {
    private static final int NAVY = 0xFF10265F;
    private static final int MUTED = 0xB010265F;
    private static final int PURPLE = 0xFF7B4FE9;

    private final Activity activity;
    private final ScreenNavigator navigator;
    private final ScrollView scroll;
    private final LinearLayout entries;
    private final LinearLayout bottomBar;
    private boolean draftAdded;

    JournalView(Activity activity, ScreenNavigator navigator) {
        super(activity);
        this.activity = activity;
        this.navigator = navigator;
        setBackgroundColor(0xFFF8EFF8);
        setClipChildren(false);
        setClipToPadding(false);

        addBackdrop();

        scroll = new ScrollView(activity);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setVerticalScrollBarEnabled(false);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        addView(scroll, match());

        LinearLayout page = new LinearLayout(activity);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(18), dp(10), dp(18), dp(30));
        scroll.addView(page, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        page.addView(header(), lp(ViewGroup.LayoutParams.MATCH_PARENT, dp(75), 0, 0, 0, dp(4)));

        entries = new LinearLayout(activity);
        entries.setOrientation(LinearLayout.VERTICAL);
        page.addView(entries, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        addEntry("▣", "A Brighter Day", "Today I felt more at peace...", "9 Sep 2026",
                0xFFE4E2FF, 0xFF6E5BDC, false);
        addEntry("✎", "Gratitude", "I'm grateful for the little things...", "7 Sep 2026",
                0xFFFFE2F1, 0xFFD85B98, false);
        addEntry("↗", "Overcoming", "I faced a challenge and...", "5 Sep 2026",
                0xFFDDF2FF, 0xFF378CC7, false);
        addEntry("♪", "Self Love", "I choose to be kind to myself...", "2 Sep 2026",
                0xFFFFE2EF, 0xFFCB4B91, false);

        TextView newEntry = text("＋  New Entry", 15, Color.WHITE, true);
        newEntry.setGravity(Gravity.CENTER);
        newEntry.setPadding(dp(20), 0, dp(20), 0);
        newEntry.setBackground(purpleRipple(dp(26)));
        newEntry.setElevation(dp(7));
        newEntry.setContentDescription("Create a new journal entry");
        newEntry.setOnClickListener(v -> createEntry());
        LinearLayout.LayoutParams newParams = lp(dp(154), dp(53), 0, dp(5), 0, dp(4));
        newParams.gravity = Gravity.END;
        page.addView(newEntry, newParams);

        bottomBar = bottomNavigation();
        FrameLayout.LayoutParams navParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(76));
        navParams.gravity = Gravity.BOTTOM;
        addView(bottomBar, navParams);

        setOnApplyWindowInsetsListener((view, insets) -> applyInsets(insets));
        requestApplyInsets();
    }

    private void addBackdrop() {
        ImageView scenery = new ImageView(activity);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inSampleSize = 2;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap scene = BitmapFactory.decodeResource(
                getResources(), R.drawable.serenity_background, options);
        scenery.setImageBitmap(scene);
        scenery.setScaleType(ImageView.ScaleType.CENTER_CROP);
        scenery.setAlpha(.42f);
        scenery.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(scenery, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View wash = new View(activity);
        wash.setBackground(gradient(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0x93E7F3FF, 0xE8FFF4FA, 0xFFFFEEF5}, dp(0)));
        wash.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(wash, match());
    }

    private WindowInsets applyInsets(WindowInsets insets) {
        int bottom = insets.getSystemWindowInsetBottom();
        scroll.setPadding(0, insets.getSystemWindowInsetTop(), 0, dp(82) + bottom);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bottomBar.getLayoutParams();
        params.height = dp(76) + bottom;
        bottomBar.setPadding(dp(5), 0, dp(5), bottom);
        bottomBar.setLayoutParams(params);
        return insets;
    }

    private View header() {
        FrameLayout box = new FrameLayout(activity);

        TextView menu = text("☰", 24, NAVY, true);
        menu.setGravity(Gravity.CENTER);
        menu.setBackground(softRipple(dp(22)));
        menu.setContentDescription("Back");
        menu.setOnClickListener(v -> navigator.goBack());
        FrameLayout.LayoutParams menuParams = new FrameLayout.LayoutParams(dp(44), dp(44));
        menuParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        box.addView(menu, menuParams);

        LinearLayout words = new LinearLayout(activity);
        words.setOrientation(LinearLayout.VERTICAL);
        words.setGravity(Gravity.CENTER);
        TextView title = text("My Journal", 23, NAVY, true);
        title.setGravity(Gravity.CENTER);
        words.addView(title);
        TextView subtitle = text("A space for your thoughts", 11, MUTED, false);
        subtitle.setGravity(Gravity.CENTER);
        words.addView(subtitle);
        FrameLayout.LayoutParams wordParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wordParams.gravity = Gravity.CENTER;
        wordParams.leftMargin = dp(48);
        wordParams.rightMargin = dp(48);
        box.addView(words, wordParams);

        TextView share = text("⇧", 23, NAVY, true);
        share.setGravity(Gravity.CENTER);
        share.setBackground(softRipple(dp(22)));
        share.setContentDescription("Journal options");
        share.setOnClickListener(v -> Toast.makeText(activity,
                "Your journal stays private on this device", Toast.LENGTH_SHORT).show());
        FrameLayout.LayoutParams shareParams = new FrameLayout.LayoutParams(dp(44), dp(44));
        shareParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        box.addView(share, shareParams);
        return box;
    }

    private void createEntry() {
        if (!draftAdded) {
            addEntry("✦", "A New Beginning", "One gentle thought at a time...", "Just now",
                    0xFFE8E1FF, PURPLE, true);
            draftAdded = true;
            SupportSignalEngine.recordJournal(activity);
            Toast.makeText(activity, "New entry added", Toast.LENGTH_SHORT).show();
            scroll.post(() -> scroll.smoothScrollTo(0, 0));
        } else {
            navigator.openScreen(21);
        }
    }

    private void addEntry(String icon, String title, String preview, String date,
                          int iconFill, int iconColor, boolean first) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(12), dp(11), dp(11), dp(11));
        card.setBackground(cardRipple());
        card.setElevation(dp(3));
        card.setContentDescription(title + ". " + preview + ". " + date);
        card.setOnClickListener(v -> Toast.makeText(activity,
                title + " opened", Toast.LENGTH_SHORT).show());

        TextView mark = text(icon, 25, iconColor, true);
        mark.setGravity(Gravity.CENTER);
        mark.setBackground(rounded(iconFill, dp(14), 0, Color.TRANSPARENT));
        card.addView(mark, new LinearLayout.LayoutParams(dp(53), dp(53)));

        LinearLayout copy = new LinearLayout(activity);
        copy.setOrientation(LinearLayout.VERTICAL);
        copy.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams copyParams = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        copyParams.setMargins(dp(13), 0, dp(5), 0);
        card.addView(copy, copyParams);

        TextView heading = text(title, 15, NAVY, true);
        copy.addView(heading);
        TextView excerpt = text(preview, 11, MUTED, false);
        excerpt.setSingleLine(true);
        copy.addView(excerpt);
        TextView when = text(date, 10, 0x8A10265F, false);
        copy.addView(when);

        TextView arrow = text("›", 29, 0x8A10265F, false);
        arrow.setGravity(Gravity.CENTER);
        card.addView(arrow, new LinearLayout.LayoutParams(dp(28), dp(45)));

        LinearLayout.LayoutParams params = lp(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(86), 0, 0, 0, dp(11));
        if (first) {
            entries.addView(card, 0, params);
        } else {
            entries.addView(card, params);
        }
    }

    private LinearLayout bottomNavigation() {
        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        bar.setClipChildren(false);
        bar.setClipToPadding(false);
        bar.setElevation(dp(15));
        bar.setBackground(gradient(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFCFFFFFF, 0xFFF4F7FF}, 0));
        bar.addView(nav("⌂", "Home", 8, false), navParams());
        bar.addView(nav("☺", "Check-in", 9, false), navParams());
        bar.addView(lotus(), navParams());
        bar.addView(nav("◌", "Calm", 12, false), navParams());
        bar.addView(nav("●", "Profile", 16, false), navParams());
        return bar;
    }

    private View nav(String icon, String label, int screen, boolean selected) {
        LinearLayout item = new LinearLayout(activity);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(0, dp(5), 0, dp(3));
        item.setBackground(softRipple(dp(13)));
        item.setContentDescription(label);
        item.setOnClickListener(v -> navigator.openScreen(screen));
        int color = selected ? PURPLE : 0xFF627095;
        TextView iconView = text(icon, 21, color, true);
        iconView.setGravity(Gravity.CENTER);
        item.addView(iconView, new LinearLayout.LayoutParams(dp(32), dp(30)));
        TextView labelView = text(label, 10, color, selected);
        labelView.setGravity(Gravity.CENTER);
        item.addView(labelView);
        return item;
    }

    private View lotus() {
        FrameLayout item = new FrameLayout(activity);
        item.setClipChildren(false);
        item.setClipToPadding(false);
        item.setTranslationY(-dp(18));
        ImageView image = new ImageView(activity);
        BitmapFactory.Options logoOptions = new BitmapFactory.Options();
        logoOptions.inScaled = false;
        logoOptions.inSampleSize = 4;
        image.setImageBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.safe_space_logo, logoOptions));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setBackground(rounded(Color.WHITE, dp(36), dp(3), 0x66C798FF));
        image.setElevation(dp(12));
        image.setContentDescription("Safe Space home");
        image.setOnClickListener(v -> navigator.openScreen(8));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp(72), dp(72));
        params.gravity = Gravity.CENTER;
        item.addView(image, params);
        return item;
    }

    private LinearLayout.LayoutParams navParams() {
        return new LinearLayout.LayoutParams(0, dp(76), 1f);
    }

    private TextView text(String value, float size, int color, boolean bold) {
        TextView view = new TextView(activity);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setIncludeFontPadding(false);
        view.setTypeface(Typeface.create("sans-serif-rounded",
                bold ? Typeface.BOLD : Typeface.NORMAL));
        return view;
    }

    private Drawable cardRipple() {
        return new RippleDrawable(ColorStateList.valueOf(0x207B4FE9),
                rounded(0xF3FFFFFF, dp(19), dp(1), 0x247B4FE9), null);
    }

    private Drawable softRipple(int radius) {
        return new RippleDrawable(ColorStateList.valueOf(0x267B4FE9),
                rounded(0x28FFFFFF, radius, 0, Color.TRANSPARENT), null);
    }

    private Drawable purpleRipple(int radius) {
        GradientDrawable fill = gradient(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0xFF9B68EF, 0xFF7444E2}, radius);
        return new RippleDrawable(ColorStateList.valueOf(0x44FFFFFF), fill, null);
    }

    private GradientDrawable rounded(int color, int radius, int stroke, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        if (stroke > 0) drawable.setStroke(stroke, strokeColor);
        return drawable;
    }

    private GradientDrawable gradient(GradientDrawable.Orientation orientation,
                                      int[] colors, int radius) {
        GradientDrawable drawable = new GradientDrawable(orientation, colors);
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private LinearLayout.LayoutParams lp(int width, int height, int l, int t, int r, int b) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(l, t, r, b);
        return params;
    }

    private FrameLayout.LayoutParams match() {
        return new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
