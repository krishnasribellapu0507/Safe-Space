package com.safespace.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Screen twelve: breathing, meditation and soothing-sound exercises. */
final class RelaxView extends FrameLayout {
    private static final int NAVY = 0xFF10265F;
    private static final int MUTED = 0xB010265F;
    private static final int PURPLE = 0xFF7650E4;

    private final Activity activity;
    private final ScreenNavigator navigator;
    private final ScrollView scroll;
    private final LinearLayout bottomBar;
    private final TextView[] tabViews = new TextView[3];
    private final TextView instruction;
    private final TextView detail;
    private final FrameLayout orb;
    private ValueAnimator breathingAnimator;
    private CountDownTimer sessionTimer;
    private TextView timerText;
    private TextView actionButton;
    private boolean sessionRunning;
    private int mode;
    private int step;

    private static final String[][] TITLES = {
            {"Breathe In", "Hold Gently", "Breathe Out", "Rest"},
            {"Settle In", "Notice", "Let It Pass", "Return"},
            {"Gentle Rain", "Ocean Waves", "Forest Birds", "Soft Night"}
    };
    private static final String[][] DETAILS = {
            {"4 seconds", "4 seconds", "6 seconds", "2 seconds"},
            {"Relax your shoulders", "Follow one breath", "No need to judge", "Come back to now"},
            {"Tap arrows to explore", "Slow and steady", "Breathe with nature", "Quiet and calm"}
    };

    RelaxView(Activity activity, ScreenNavigator navigator) {
        super(activity);
        this.activity = activity;
        this.navigator = navigator;
        setBackgroundColor(0xFFE7F3FF);
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
        page.setGravity(Gravity.CENTER_HORIZONTAL);
        page.setPadding(dp(18), dp(8), dp(18), dp(26));
        scroll.addView(page, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        page.addView(header(), lp(ViewGroup.LayoutParams.MATCH_PARENT, dp(55), 0, 0, 0, dp(8)));

        TextView title = text("Take a Deep Breath", 27, NAVY, true);
        title.setGravity(Gravity.CENTER);
        page.addView(title, lp(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(4)));
        TextView sub = text("You are enough.", 14, MUTED, false);
        sub.setGravity(Gravity.CENTER);
        page.addView(sub, lp(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(18)));

        page.addView(tabBar(), lp(ViewGroup.LayoutParams.MATCH_PARENT, dp(46),
                0, 0, 0, dp(24)));

        orb = breathingOrb();
        page.addView(orb, lp(dp(246), dp(246), 0, 0, 0, dp(16)));

        instruction = text(TITLES[0][0], 18, NAVY, true);
        instruction.setGravity(Gravity.CENTER);
        page.addView(instruction, lp(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(4)));
        detail = text(DETAILS[0][0], 12, MUTED, false);
        detail.setGravity(Gravity.CENTER);
        page.addView(detail, lp(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(10)));

        timerText = text("1:00", 31, NAVY, true);
        timerText.setGravity(Gravity.CENTER);
        page.addView(timerText, lp(ViewGroup.LayoutParams.MATCH_PARENT, dp(42),
                0, 0, 0, dp(8)));

        actionButton = text("Start", 16, Color.WHITE, true);
        actionButton.setGravity(Gravity.CENTER);
        actionButton.setElevation(dp(5));
        actionButton.setBackground(new RippleDrawable(ColorStateList.valueOf(0x55FFFFFF),
                gradient(GradientDrawable.Orientation.LEFT_RIGHT,
                        new int[]{0xFF567DE6, 0xFF6D49D8}, dp(27)), null));
        actionButton.setOnClickListener(v -> toggleSession());
        page.addView(actionButton, lp(dp(230), dp(54), 0, 0, 0, dp(12)));

        page.addView(controls(), lp(ViewGroup.LayoutParams.MATCH_PARENT, dp(57),
                0, 0, 0, dp(8)));

        bottomBar = bottomNavigation();
        FrameLayout.LayoutParams navParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(76));
        navParams.gravity = Gravity.BOTTOM;
        addView(bottomBar, navParams);

        setOnApplyWindowInsetsListener((view, insets) -> applyInsets(insets));
        requestApplyInsets();
        startBreathingAnimation();
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
        scenery.setAlpha(.74f);
        scenery.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(scenery, match());

        View wash = new View(activity);
        wash.setBackground(gradient(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0x42E4F3FF, 0x82EFF6FF, 0xECF8F2FA, 0xFFFFEFF5}, 0));
        wash.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(wash, match());
    }

    private WindowInsets applyInsets(WindowInsets insets) {
        int bottom = insets.getSystemWindowInsetBottom();
        scroll.setPadding(0, insets.getSystemWindowInsetTop(), 0, dp(80) + bottom);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bottomBar.getLayoutParams();
        params.height = dp(76) + bottom;
        bottomBar.setPadding(dp(5), 0, dp(5), bottom);
        bottomBar.setLayoutParams(params);
        return insets;
    }

    private View header() {
        FrameLayout row = new FrameLayout(activity);
        TextView back = text("‹", 34, NAVY, false);
        back.setGravity(Gravity.CENTER);
        back.setBackground(softRipple(dp(24)));
        back.setContentDescription("Back");
        back.setOnClickListener(v -> navigator.goBack());
        FrameLayout.LayoutParams backParams = new FrameLayout.LayoutParams(dp(46), dp(46));
        backParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        row.addView(back, backParams);

        TextView plus = text("＋", 24, Color.WHITE, false);
        plus.setGravity(Gravity.CENTER);
        plus.setBackground(new RippleDrawable(ColorStateList.valueOf(0x44FFFFFF),
                rounded(0xAA7E83EB, dp(24), dp(1), 0x88FFFFFF), null));
        plus.setContentDescription("Open mindful activities");
        plus.setOnClickListener(v -> navigator.openScreen(13));
        FrameLayout.LayoutParams plusParams = new FrameLayout.LayoutParams(dp(43), dp(43));
        plusParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        row.addView(plus, plusParams);
        return row;
    }

    private View tabBar() {
        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER);
        bar.setPadding(dp(4), dp(4), dp(4), dp(4));
        bar.setBackground(rounded(0xBFFFFFFF, dp(23), dp(1), 0x33FFFFFF));
        bar.setElevation(dp(3));
        String[] labels = {"Breathing", "Meditation", "Sounds"};
        for (int i = 0; i < labels.length; i++) {
            final int index = i;
            TextView tab = text(labels[i], 12, i == 0 ? Color.WHITE : NAVY, i == 0);
            tab.setGravity(Gravity.CENTER);
            tab.setContentDescription(labels[i] + " tab");
            tab.setBackground(i == 0 ? selectedTab() : transparentRipple());
            tab.setOnClickListener(v -> selectTab(index));
            tabViews[i] = tab;
            bar.addView(tab, new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        }
        return bar;
    }

    private FrameLayout breathingOrb() {
        FrameLayout outer = new FrameLayout(activity);
        outer.setBackground(rounded(0x28FFFFFF, dp(123), dp(2), 0x90BFEAFF));
        outer.setElevation(dp(5));
        outer.setContentDescription("Calming lotus breathing guide");

        View ring = new View(activity);
        ring.setBackground(rounded(0x4AFFFFFF, dp(98), dp(3), 0xAA9DDFFF));
        FrameLayout.LayoutParams ringParams = new FrameLayout.LayoutParams(dp(196), dp(196));
        ringParams.gravity = Gravity.CENTER;
        outer.addView(ring, ringParams);

        View glow = new View(activity);
        glow.setBackground(gradient(GradientDrawable.Orientation.TL_BR,
                new int[]{0x88BDEEFF, 0x99CFB8FF, 0x88FFE1F1}, dp(80)));
        FrameLayout.LayoutParams glowParams = new FrameLayout.LayoutParams(dp(160), dp(160));
        glowParams.gravity = Gravity.CENTER;
        outer.addView(glow, glowParams);

        ImageView lotus = new ImageView(activity);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inSampleSize = 4;
        Bitmap logo = BitmapFactory.decodeResource(
                getResources(), R.drawable.safe_space_logo, options);
        lotus.setImageBitmap(logo);
        lotus.setScaleType(ImageView.ScaleType.CENTER_CROP);
        lotus.setContentDescription(null);
        lotus.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        FrameLayout.LayoutParams lotusParams = new FrameLayout.LayoutParams(dp(118), dp(118));
        lotusParams.gravity = Gravity.CENTER;
        outer.addView(lotus, lotusParams);
        return outer;
    }

    private View controls() {
        FrameLayout controls = new FrameLayout(activity);
        TextView previous = arrow("‹", "Previous exercise");
        previous.setOnClickListener(v -> {
            step = (step + TITLES[mode].length - 1) % TITLES[mode].length;
            updateExercise();
            if (sessionRunning && mode == 2) restartSound();
        });
        FrameLayout.LayoutParams previousParams = new FrameLayout.LayoutParams(dp(54), dp(54));
        previousParams.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        controls.addView(previous, previousParams);

        TextView dots = text("●  ○  ○  ○", 12, 0xBB7650E4, true);
        dots.setGravity(Gravity.CENTER);
        dots.setTag("progress");
        FrameLayout.LayoutParams dotsParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dotsParams.gravity = Gravity.CENTER;
        controls.addView(dots, dotsParams);

        TextView next = arrow("›", "Next exercise");
        next.setOnClickListener(v -> {
            step = (step + 1) % TITLES[mode].length;
            updateExercise();
            if (sessionRunning && mode == 2) restartSound();
        });
        FrameLayout.LayoutParams nextParams = new FrameLayout.LayoutParams(dp(54), dp(54));
        nextParams.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        controls.addView(next, nextParams);
        return controls;
    }

    private TextView arrow(String value, String description) {
        TextView arrow = text(value, 33, NAVY, false);
        arrow.setGravity(Gravity.CENTER);
        arrow.setBackground(new RippleDrawable(ColorStateList.valueOf(0x307B4FE9),
                rounded(0xD9FFFFFF, dp(28), dp(1), 0x33A3A9E9), null));
        arrow.setElevation(dp(4));
        arrow.setContentDescription(description);
        return arrow;
    }

    private void selectTab(int index) {
        if (sessionRunning) stopSession();
        mode = index;
        step = 0;
        for (int i = 0; i < tabViews.length; i++) {
            boolean selected = i == index;
            tabViews[i].setTextColor(selected ? Color.WHITE : NAVY);
            tabViews[i].setTypeface(Typeface.create("sans-serif-rounded",
                    selected ? Typeface.BOLD : Typeface.NORMAL));
            tabViews[i].setBackground(selected ? selectedTab() : transparentRipple());
            tabViews[i].setSelected(selected);
        }
        updateExercise();
        if (timerText != null) timerText.setText(index == 2 ? "Background audio" : "1:00");
        if (actionButton != null) actionButton.setText("Start");
    }

    private void toggleSession() {
        if (sessionRunning) {
            stopSession();
            return;
        }
        sessionRunning = true;
        SupportSignalEngine.recordCalm(activity);
        actionButton.setText("Stop");
        if (mode == 2) {
            Intent play = new Intent(activity, CalmAudioService.class)
                    .setAction(CalmAudioService.ACTION_PLAY)
                    .putExtra(CalmAudioService.EXTRA_TRACK, step);
            if (Build.VERSION.SDK_INT >= 26) activity.startForegroundService(play);
            else activity.startService(play);
            timerText.setText("Playing • notification controls on");
        } else {
            sessionTimer = new CountDownTimer(60000L, 1000L) {
                @Override public void onTick(long millisUntilFinished) {
                    long secs = (millisUntilFinished + 999) / 1000;
                    timerText.setText(String.format("0:%02d", secs));
                }
                @Override public void onFinish() {
                    sessionRunning = false;
                    actionButton.setText("Start again");
                    timerText.setText("Done ✓");
                }
            }.start();
        }
    }

    private void stopSession() {
        if (sessionTimer != null) { sessionTimer.cancel(); sessionTimer = null; }
        if (mode == 2) {
            activity.startService(new Intent(activity, CalmAudioService.class)
                    .setAction(CalmAudioService.ACTION_STOP));
            timerText.setText("Background audio");
        } else {
            timerText.setText("1:00");
        }
        sessionRunning = false;
        if (actionButton != null) actionButton.setText("Start");
    }

    private void restartSound() {
        Intent play = new Intent(activity, CalmAudioService.class)
                .setAction(CalmAudioService.ACTION_PLAY)
                .putExtra(CalmAudioService.EXTRA_TRACK, step);
        if (Build.VERSION.SDK_INT >= 26) activity.startForegroundService(play);
        else activity.startService(play);
    }

    private void updateExercise() {
        instruction.setText(TITLES[mode][step]);
        detail.setText(DETAILS[mode][step]);
        StringBuilder dots = new StringBuilder();
        for (int i = 0; i < TITLES[mode].length; i++) {
            if (i > 0) dots.append("  ");
            dots.append(i == step ? "●" : "○");
        }
        View parent = (View) instruction.getParent();
        View progress = parent.findViewWithTag("progress");
        if (progress instanceof TextView) ((TextView) progress).setText(dots.toString());
        orb.announceForAccessibility(instruction.getText() + ", " + detail.getText());
    }

    private void startBreathingAnimation() {
        if (MotionSystem.reducedMotion(activity)) {
            orb.setScaleX(1f);
            orb.setScaleY(1f);
            return;
        }
        breathingAnimator = ValueAnimator.ofFloat(.97f, 1.035f);
        breathingAnimator.setDuration(3200L);
        breathingAnimator.setRepeatCount(ValueAnimator.INFINITE);
        breathingAnimator.setRepeatMode(ValueAnimator.REVERSE);
        breathingAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        breathingAnimator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            orb.setScaleX(value);
            orb.setScaleY(value);
        });
        breathingAnimator.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationCancel(Animator animation) {
                orb.setScaleX(1f);
                orb.setScaleY(1f);
            }
        });
        breathingAnimator.start();
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
        bar.addView(nav("▤", "Journal", 11, false), navParams());
        bar.addView(nav("◌", "Calm", 12, true), navParams());
        bar.addView(nav("▥", "Insights", 14, false), navParams());
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

    private Drawable selectedTab() {
        GradientDrawable selected = gradient(GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0xFF6683E6, 0xFF7953E8}, dp(20));
        return new RippleDrawable(ColorStateList.valueOf(0x55FFFFFF), selected, null);
    }

    private Drawable transparentRipple() {
        return new RippleDrawable(ColorStateList.valueOf(0x267B4FE9),
                rounded(Color.TRANSPARENT, dp(20), 0, Color.TRANSPARENT), null);
    }

    private Drawable softRipple(int radius) {
        return new RippleDrawable(ColorStateList.valueOf(0x267B4FE9),
                rounded(0x31FFFFFF, radius, 0, Color.TRANSPARENT), null);
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

    @Override
    protected void onDetachedFromWindow() {
        if (breathingAnimator != null) breathingAnimator.cancel();
        if (sessionTimer != null) sessionTimer.cancel();
        super.onDetachedFromWindow();
    }
}
