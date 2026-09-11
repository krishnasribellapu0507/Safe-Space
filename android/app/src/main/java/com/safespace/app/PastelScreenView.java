package com.safespace.app;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
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

/** Shared native, dependency-free chrome for the pastel Safe Space prototype screens. */
abstract class PastelScreenView extends FrameLayout {
    protected static final int NAVY = 0xFF142C63;
    protected static final int MUTED_NAVY = 0xFF687594;
    protected static final int PURPLE = 0xFF7652E8;
    protected static final int PINK = 0xFFE26EA8;
    protected static final int GLASS = 0xF2FFFFFF;

    protected final Activity activity;
    protected final ScreenNavigator navigator;
    protected final ScrollView scrollView;
    protected final LinearLayout content;

    private final LinearLayout bottomBar;
    private final int navigationHeight;

    PastelScreenView(Activity activity, ScreenNavigator navigator, int selectedScreen) {
        super(activity);
        this.activity = activity;
        this.navigator = navigator;
        navigationHeight = dp(72);

        setClipChildren(false);
        setClipToPadding(false);
        setFocusable(true);
        setBackgroundColor(0xFFF8F3FB);

        addView(new PastelBackdrop(activity), new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        scrollView = new ScrollView(activity);
        scrollView.setFillViewport(true);
        scrollView.setClipToPadding(false);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        addView(scrollView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(17), dp(5), dp(17), dp(22));
        scrollView.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        bottomBar = buildBottomNavigation(selectedScreen);
        FrameLayout.LayoutParams navParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, navigationHeight);
        navParams.gravity = Gravity.BOTTOM;
        addView(bottomBar, navParams);

        setOnApplyWindowInsetsListener((view, insets) -> applyInsets(insets));
        requestApplyInsets();
    }

    private WindowInsets applyInsets(WindowInsets insets) {
        int top = insets.getSystemWindowInsetTop();
        int bottom = insets.getSystemWindowInsetBottom();
        scrollView.setPadding(0, top + dp(3), 0, navigationHeight + bottom + dp(13));

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) bottomBar.getLayoutParams();
        params.height = navigationHeight + bottom;
        bottomBar.setPadding(dp(4), 0, dp(4), bottom);
        bottomBar.setLayoutParams(params);
        return insets;
    }

    protected View header(String title, String subtitle, boolean showBack,
                          String actionGlyph, String actionMessage) {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, dp(3), 0, dp(3));

        TextView back = text(showBack ? "‹" : "", 38, NAVY, false);
        back.setGravity(Gravity.CENTER);
        if (showBack) {
            back.setContentDescription("Go back");
            back.setBackground(circleRipple(0x75FFFFFF));
            back.setOnClickListener(view -> navigator.goBack());
        }
        row.addView(back, new LinearLayout.LayoutParams(dp(45), dp(45)));

        LinearLayout labels = new LinearLayout(activity);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setGravity(Gravity.CENTER);

        TextView titleView = text(title, 21, NAVY, true);
        titleView.setGravity(Gravity.CENTER);
        labels.addView(titleView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        if (subtitle != null && !subtitle.isEmpty()) {
            TextView subtitleView = text(subtitle, 11, MUTED_NAVY, false);
            subtitleView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            subtitleParams.topMargin = dp(2);
            labels.addView(subtitleView, subtitleParams);
        }
        row.addView(labels, new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView action = text(actionGlyph == null ? "" : actionGlyph, 22, PURPLE, true);
        action.setGravity(Gravity.CENTER);
        if (actionGlyph != null && !actionGlyph.isEmpty()) {
            action.setContentDescription(actionMessage);
            action.setBackground(circleRipple(0x75FFFFFF));
            action.setOnClickListener(view -> toast(actionMessage));
        }
        row.addView(action, new LinearLayout.LayoutParams(dp(45), dp(45)));
        return row;
    }

    protected LinearLayout segmentedControl(String[] labels, int selected,
                                             View.OnClickListener listener) {
        LinearLayout control = new LinearLayout(activity);
        control.setOrientation(LinearLayout.HORIZONTAL);
        control.setGravity(Gravity.CENTER);
        control.setPadding(dp(4), dp(4), dp(4), dp(4));
        control.setBackground(rounded(0xCFFFFFFF, dp(18), dp(1), 0x227B5CE6));
        control.setElevation(dp(2));
        for (int i = 0; i < labels.length; i++) {
            boolean active = i == selected;
            TextView item = text(labels[i], 12, active ? Color.WHITE : NAVY, active);
            item.setGravity(Gravity.CENTER);
            item.setTag(i);
            item.setBackground(active
                    ? gradientRounded(0xFF6F58DE, 0xFF8B5FEF, dp(14), 0, Color.TRANSPARENT)
                    : new RippleDrawable(ColorStateList.valueOf(0x187B4FE9),
                            rounded(Color.TRANSPARENT, dp(14), 0, Color.TRANSPARENT), null));
            item.setOnClickListener(listener);
            control.addView(item, new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        }
        return control;
    }

    protected LinearLayout glassCard() {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setElevation(dp(3));
        card.setBackground(rounded(GLASS, dp(20), dp(1), 0x247A61D9));
        return card;
    }

    protected LinearLayout bottomRowCard(String icon, int iconFill, int iconColor,
                                         String title, String subtitle, String end,
                                         String toastMessage) {
        LinearLayout card = new LinearLayout(activity);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(13), dp(10), dp(11), dp(10));
        card.setElevation(dp(3));
        card.setBackground(rippleRounded(GLASS, dp(19), 0x247A61D9));
        card.setContentDescription(title + ". " + subtitle);
        card.setOnClickListener(view -> toast(toastMessage));

        TextView iconView = text(icon, 21, iconColor, true);
        iconView.setGravity(Gravity.CENTER);
        iconView.setBackground(rounded(iconFill, dp(13), 0, Color.TRANSPARENT));
        card.addView(iconView, new LinearLayout.LayoutParams(dp(47), dp(47)));

        LinearLayout labels = new LinearLayout(activity);
        labels.setOrientation(LinearLayout.VERTICAL);
        labels.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams labelsParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        labelsParams.setMarginStart(dp(12));
        card.addView(labels, labelsParams);

        TextView titleView = text(title, 14, NAVY, true);
        labels.addView(titleView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView subtitleView = text(subtitle, 10.5f, MUTED_NAVY, false);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = dp(3);
        labels.addView(subtitleView, subtitleParams);

        TextView endView = text(end == null ? "›" : end, end != null ? 11 : 26,
                end != null ? MUTED_NAVY : 0xFF7B83A2, end != null);
        endView.setGravity(Gravity.CENTER);
        card.addView(endView, new LinearLayout.LayoutParams(
                end != null ? dp(68) : dp(25), ViewGroup.LayoutParams.MATCH_PARENT));
        return card;
    }

    protected TextView text(String value, float sizeSp, int color, boolean bold) {
        TextView view = new TextView(activity);
        view.setText(value);
        view.setTextSize(sizeSp);
        view.setTextColor(color);
        view.setIncludeFontPadding(false);
        view.setTypeface(Typeface.create("sans-serif-rounded",
                bold ? Typeface.BOLD : Typeface.NORMAL));
        return view;
    }

    protected Drawable rippleRounded(int fill, int radius, int strokeColor) {
        return new RippleDrawable(ColorStateList.valueOf(0x247B4FE9),
                rounded(fill, radius, strokeColor == Color.TRANSPARENT ? 0 : dp(1), strokeColor),
                null);
    }

    protected Drawable circleRipple(int fill) {
        return new RippleDrawable(ColorStateList.valueOf(0x247B4FE9),
                rounded(fill, dp(23), dp(1), 0x28FFFFFF), null);
    }

    protected GradientDrawable rounded(int fill, int radius, int strokeWidth, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) {
            drawable.setStroke(strokeWidth, strokeColor);
        }
        return drawable;
    }

    protected GradientDrawable gradientRounded(int start, int end, int radius,
                                                int strokeWidth, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR, new int[]{start, end});
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) {
            drawable.setStroke(strokeWidth, strokeColor);
        }
        return drawable;
    }

    protected LinearLayout.LayoutParams marginParams(int width, int height,
                                                      int left, int top,
                                                      int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(left, top, right, bottom);
        return params;
    }

    protected void toast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }

    protected int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private LinearLayout buildBottomNavigation(int selectedScreen) {
        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        bar.setElevation(dp(14));
        bar.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFAFFFFFF, 0xFFF5F7FF}));

        bar.addView(navItem("⌂", "Home", selectedScreen == 8, 8), navParams());
        bar.addView(navItem("☺", "Check-in", selectedScreen == 9, 9), navParams());
        bar.addView(centerLotus(selectedScreen == 8), navParams());
        bar.addView(navItem("◌", "Calm", selectedScreen == 12, 12), navParams());
        bar.addView(navItem("●", "Profile", selectedScreen == 16, 16), navParams());
        return bar;
    }

    private LinearLayout.LayoutParams navParams() {
        return new LinearLayout.LayoutParams(0, navigationHeight, 1f);
    }

    private View navItem(String icon, String label, boolean selected, int destination) {
        LinearLayout item = new LinearLayout(activity);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(0, dp(5), 0, dp(2));
        item.setBackground(new RippleDrawable(ColorStateList.valueOf(0x187B4FE9),
                rounded(Color.TRANSPARENT, dp(12), 0, Color.TRANSPARENT), null));
        item.setContentDescription(label);
        item.setOnClickListener(view -> navigator.openScreen(destination));

        int color = selected ? PURPLE : MUTED_NAVY;
        TextView iconView = text(icon, selected ? 23 : 20, color, true);
        iconView.setGravity(Gravity.CENTER);
        item.addView(iconView, new LinearLayout.LayoutParams(dp(32), dp(29)));

        TextView labelView = text(label, 9.5f, color, selected);
        labelView.setGravity(Gravity.CENTER);
        item.addView(labelView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return item;
    }

    private View centerLotus(boolean selected) {
        FrameLayout holder = new FrameLayout(activity);
        holder.setContentDescription("Safe Space home");
        holder.setForeground(new RippleDrawable(ColorStateList.valueOf(0x207B4FE9),
                rounded(Color.TRANSPARENT, dp(31), 0, Color.TRANSPARENT), null));
        holder.setOnClickListener(view -> navigator.openScreen(8));

        ImageView lotus = new ImageView(activity);
        lotus.setImageResource(R.drawable.safe_space_logo);
        lotus.setScaleType(ImageView.ScaleType.CENTER_CROP);
        lotus.setBackground(gradientRounded(0xFFFFFFFF, 0xFFF5E9FF, dp(29),
                dp(selected ? 3 : 2), selected ? 0xCCB678FF : 0x669B8BF0));
        lotus.setElevation(dp(8));
        FrameLayout.LayoutParams logoParams = new FrameLayout.LayoutParams(dp(57), dp(57));
        logoParams.gravity = Gravity.CENTER;
        holder.addView(lotus, logoParams);
        return holder;
    }

    private final class PastelBackdrop extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path petal = new Path();

        PastelBackdrop(Activity activity) {
            super(activity);
            setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            paint.setShader(new LinearGradient(0, 0, 0, h,
                    new int[]{0xFFF1EEFF, 0xFFFFF4F8, 0xFFEFF7FF},
                    new float[]{0f, .58f, 1f}, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, w, h, paint);
            paint.setShader(null);

            paint.setColor(0x25FFFFFF);
            canvas.drawCircle(w * .1f, h * .1f, dp(92), paint);
            canvas.drawCircle(w * .92f, h * .4f, dp(105), paint);

            drawPetal(canvas, w * .93f, h * .12f, -28, 0x36E86DAF);
            drawPetal(canvas, w * .08f, h * .58f, 24, 0x307D71EE);
            drawPetal(canvas, w * .88f, h * .82f, -35, 0x2EE86DAF);
        }

        private void drawPetal(Canvas canvas, float cx, float cy, float degrees, int color) {
            canvas.save();
            canvas.rotate(degrees, cx, cy);
            petal.reset();
            petal.moveTo(cx, cy - dp(13));
            petal.cubicTo(cx + dp(13), cy - dp(8), cx + dp(13), cy + dp(8), cx, cy + dp(14));
            petal.cubicTo(cx - dp(13), cy + dp(8), cx - dp(13), cy - dp(8), cx, cy - dp(13));
            paint.setColor(color);
            canvas.drawPath(petal, paint);
            canvas.restore();
        }
    }
}
