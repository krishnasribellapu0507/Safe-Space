package com.safespace.app;

import android.Manifest;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/** Screen seven: friendly permission choices backed by Android runtime permission prompts. */
final class PermissionsScreenView extends FrameLayout {
    private static final int NAVY = Color.rgb(16, 38, 95);
    private static final int PURPLE = Color.rgb(123, 79, 233);
    private static final int MUTED_NAVY = 0xB810265F;

    private final Activity activity;
    private final Runnable onContinue;
    private boolean leaving;
    private PermissionRow notificationsRow;
    private PermissionRow microphoneRow;
    private PermissionRow locationRow;
    private PermissionRow smsRow;
    private PermissionRow phoneRow;

    PermissionsScreenView(Activity activity, Runnable onContinue) {
        super(activity);
        this.activity = activity;
        this.onContinue = onContinue;

        setBackgroundColor(0xFFD9EEFF);
        addView(new SkyBackdropView(activity), new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ScrollView scrollView = new ScrollView(activity);
        scrollView.setFillViewport(true);
        scrollView.setClipToPadding(false);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        addView(scrollView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(dp(24), dp(24), dp(24), dp(26));
        scrollView.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ShieldView shield = new ShieldView(activity);
        add(content, shield, dp(112), dp(112), 0, dp(4), 0, dp(12));

        TextView title = text("Almost There!", 29, NAVY, true);
        add(content, title, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(7));

        TextView subtitle = text(
                "To give you the best experience,\nwe need a few permissions.",
                14, MUTED_NAVY, false);
        subtitle.setLineSpacing(dp(2), 1f);
        add(content, subtitle, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(22));

        notificationsRow = new PermissionRow(
                "Notifications", "For reminders and calming-audio controls", 0, true, false);
        add(content, notificationsRow, ViewGroup.LayoutParams.MATCH_PARENT, dp(78),
                0, 0, 0, dp(12));

        microphoneRow = new PermissionRow(
                "Microphone", "For voice check-ins", 1, true, true);
        add(content, microphoneRow, ViewGroup.LayoutParams.MATCH_PARENT, dp(78),
                0, 0, 0, dp(12));

        locationRow = new PermissionRow(
                "Location", "For SOS location sharing and nearby care", 2, true, false);
        add(content, locationRow, ViewGroup.LayoutParams.MATCH_PARENT, dp(78),
                0, 0, 0, dp(12));

        smsRow = new PermissionRow(
                "SMS", "To alert the trusted contact you choose", 0, true, false);
        add(content, smsRow, ViewGroup.LayoutParams.MATCH_PARENT, dp(78),
                0, 0, 0, dp(12));

        phoneRow = new PermissionRow(
                "Phone", "To call your trusted contact from SOS", 1, true, false);
        add(content, phoneRow, ViewGroup.LayoutParams.MATCH_PARENT, dp(78),
                0, 0, 0, dp(12));

        PermissionRow storage = new PermissionRow(
                "Local files", "Journals stay in app storage; no broad file access needed", 2, false, true);
        add(content, storage, ViewGroup.LayoutParams.MATCH_PARENT, dp(78),
                0, 0, 0, dp(24));

        Button continueButton = new Button(activity);
        continueButton.setAllCaps(false);
        continueButton.setText("Continue");
        continueButton.setTextColor(Color.WHITE);
        continueButton.setTextSize(16);
        continueButton.setTypeface(Typeface.create("sans-serif-rounded", Typeface.BOLD));
        continueButton.setGravity(Gravity.CENTER);
        continueButton.setElevation(dp(5));
        continueButton.setBackground(purpleButtonBackground());
        continueButton.setContentDescription("Continue to Safe Space home");
        continueButton.setOnClickListener(view -> requestSelectedPermissions());
        add(content, continueButton, ViewGroup.LayoutParams.MATCH_PARENT, dp(56),
                0, 0, 0, dp(8));

        TextView later = text("Maybe later", 14, NAVY, true);
        later.setPadding(dp(12), dp(10), dp(12), dp(10));
        later.setClickable(true);
        later.setFocusable(true);
        later.setContentDescription("Skip permission choices and continue");
        later.setOnClickListener(view -> finish());
        add(content, later, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(10));

        TextView note = text(
                "Safe Space only asks Android for permissions needed by features you enable.",
                11, 0x8010265F, false);
        add(content, note, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, 0);

        setOnApplyWindowInsetsListener((view, insets) -> {
            scrollView.setPadding(0, insets.getSystemWindowInsetTop(), 0,
                    insets.getSystemWindowInsetBottom());
            return insets;
        });
    }

    private void requestSelectedPermissions() {
        List<String> needed = new ArrayList<>();
        if (notificationsRow != null && notificationsRow.selected && Build.VERSION.SDK_INT >= 33
                && activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.POST_NOTIFICATIONS);
        }
        if (microphoneRow != null && microphoneRow.selected
                && activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.RECORD_AUDIO);
        }
        if (locationRow != null && locationRow.selected
                && activity.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (smsRow != null && smsRow.selected
                && activity.checkSelfPermission(Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.SEND_SMS);
        }
        if (phoneRow != null && phoneRow.selected
                && activity.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.CALL_PHONE);
        }
        if (needed.isEmpty()) {
            finish();
        } else {
            activity.requestPermissions(needed.toArray(new String[0]), 704);
        }
    }

    void completeAfterPermissions() {
        finish();
    }

    private void finish() {
        if (leaving) {
            return;
        }
        leaving = true;
        if (onContinue != null) {
            onContinue.run();
        }
    }

    private TextView text(String value, float sizeSp, int color, boolean bold) {
        TextView view = new TextView(activity);
        view.setText(value);
        view.setTextSize(sizeSp);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER);
        view.setTypeface(Typeface.create("sans-serif-rounded",
                bold ? Typeface.BOLD : Typeface.NORMAL));
        return view;
    }

    private GradientDrawable cardBackground(boolean selected) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(selected ? 0xF4FFFFFF : 0xE8FFFFFF);
        drawable.setCornerRadius(dp(18));
        drawable.setStroke(dp(selected ? 2 : 1),
                selected ? 0x667B4FE9 : 0x33558DCA);
        return drawable;
    }

    private GradientDrawable statusBackground(boolean selected) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(selected ? PURPLE : 0x00FFFFFF);
        drawable.setStroke(dp(2), selected ? PURPLE : 0x66558DCA);
        return drawable;
    }

    private GradientDrawable purpleButtonBackground() {
        GradientDrawable drawable = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0xFF9C6AF0, 0xFF7042DD});
        drawable.setCornerRadius(dp(28));
        return drawable;
    }

    private void add(LinearLayout parent, View child, int width, int height,
                     int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(left, top, right, bottom);
        parent.addView(child, params);
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private final class PermissionRow extends LinearLayout {
        private final TextView status;
        private final boolean optional;
        private boolean selected;

        PermissionRow(String title, String subtitle, int iconType,
                      boolean initiallySelected, boolean optional) {
            super(activity);
            this.selected = initiallySelected;
            this.optional = optional;
            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL);
            setPadding(dp(14), dp(10), dp(13), dp(10));
            setElevation(dp(3));
            setClickable(true);
            setFocusable(true);

            PermissionIconView icon = new PermissionIconView(activity, iconType);
            addView(icon, new LinearLayout.LayoutParams(dp(46), dp(46)));

            LinearLayout labels = new LinearLayout(activity);
            labels.setOrientation(VERTICAL);
            labels.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams labelsParams = new LinearLayout.LayoutParams(
                    0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            labelsParams.setMarginStart(dp(13));
            labelsParams.setMarginEnd(dp(8));
            addView(labels, labelsParams);

            TextView titleView = text(title, 15, NAVY, true);
            titleView.setGravity(Gravity.START);
            labels.addView(titleView, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TextView subtitleView = text(
                    optional ? subtitle + " (optional)" : subtitle,
                    11, 0xA810265F, false);
            subtitleView.setGravity(Gravity.START);
            labels.addView(subtitleView, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            status = text("", 15, Color.WHITE, true);
            status.setGravity(Gravity.CENTER);
            addView(status, new LinearLayout.LayoutParams(dp(30), dp(30)));

            setOnClickListener(view -> {
                selected = !selected;
                refresh();
                announceForAccessibility(title + (selected ? " selected" : " not selected"));
            });
            refresh();
        }

        private void refresh() {
            setBackground(cardBackground(selected));
            status.setText(selected ? "✓" : "+");
            status.setTextColor(selected ? Color.WHITE : 0xCC356895);
            status.setBackground(statusBackground(selected));
            setContentDescription((optional ? "Optional " : "")
                    + "permission choice. " + (selected ? "Selected" : "Not selected")
                    + ". Double tap to change.");
        }
    }

    private final class PermissionIconView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF bounds = new RectF();
        private final Path path = new Path();
        private final int type;

        PermissionIconView(Activity activity, int type) {
            super(activity);
            this.type = type;
            setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float width = getWidth();
            float height = getHeight();
            float cx = width / 2f;
            float cy = height / 2f;
            int baseColor = type == 0 ? 0xFFFF5F94 : type == 1 ? 0xFF4B8EF7 : 0xFF39B779;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(type == 0 ? 0xFFFFE5EE : type == 1 ? 0xFFE2F0FF : 0xFFE2F7EC);
            canvas.drawRoundRect(new RectF(0, 0, width, height), dp(13), dp(13), paint);

            paint.setColor(baseColor);
            paint.setStrokeWidth(dp(2.4f));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            if (type == 0) {
                // Bell.
                bounds.set(cx - dp(8), cy - dp(9), cx + dp(8), cy + dp(7));
                paint.setStyle(Paint.Style.FILL);
                canvas.drawOval(bounds, paint);
                paint.setColor(type == 0 ? 0xFFFFE5EE : Color.WHITE);
                canvas.drawRect(cx - dp(5), cy + dp(1), cx + dp(5), cy + dp(8), paint);
                paint.setColor(baseColor);
                canvas.drawRoundRect(new RectF(cx - dp(10), cy + dp(5),
                        cx + dp(10), cy + dp(8)), dp(2), dp(2), paint);
                canvas.drawCircle(cx, cy + dp(11), dp(2), paint);
            } else if (type == 1) {
                // Microphone.
                paint.setStyle(Paint.Style.STROKE);
                bounds.set(cx - dp(6), cy - dp(12), cx + dp(6), cy + dp(5));
                canvas.drawRoundRect(bounds, dp(6), dp(6), paint);
                path.reset();
                path.moveTo(cx - dp(10), cy + dp(1));
                path.cubicTo(cx - dp(10), cy + dp(11), cx + dp(10), cy + dp(11),
                        cx + dp(10), cy + dp(1));
                canvas.drawPath(path, paint);
                canvas.drawLine(cx, cy + dp(11), cx, cy + dp(15), paint);
                canvas.drawLine(cx - dp(6), cy + dp(15), cx + dp(6), cy + dp(15), paint);
            } else {
                // Folder.
                paint.setStyle(Paint.Style.FILL);
                path.reset();
                path.moveTo(cx - dp(12), cy - dp(9));
                path.lineTo(cx - dp(2), cy - dp(9));
                path.lineTo(cx + dp(2), cy - dp(5));
                path.lineTo(cx + dp(12), cy - dp(5));
                path.quadTo(cx + dp(14), cy - dp(5), cx + dp(14), cy - dp(2));
                path.lineTo(cx + dp(14), cy + dp(10));
                path.quadTo(cx + dp(14), cy + dp(12), cx + dp(11), cy + dp(12));
                path.lineTo(cx - dp(11), cy + dp(12));
                path.quadTo(cx - dp(14), cy + dp(12), cx - dp(14), cy + dp(9));
                path.lineTo(cx - dp(14), cy - dp(6));
                path.quadTo(cx - dp(14), cy - dp(9), cx - dp(12), cy - dp(9));
                path.close();
                canvas.drawPath(path, paint);
            }
        }
    }

    private final class ShieldView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();

        ShieldView(Activity activity) {
            super(activity);
            setContentDescription("Privacy shield");
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0x22FFFFFF);
            canvas.drawCircle(cx, cy, dp(52), paint);
            paint.setColor(0x35FFFFFF);
            canvas.drawCircle(cx, cy, dp(43), paint);

            path.reset();
            path.moveTo(cx, cy - dp(36));
            path.cubicTo(cx + dp(12), cy - dp(29), cx + dp(25), cy - dp(25),
                    cx + dp(31), cy - dp(24));
            path.lineTo(cx + dp(29), cy + dp(3));
            path.cubicTo(cx + dp(27), cy + dp(22), cx + dp(12), cy + dp(34),
                    cx, cy + dp(40));
            path.cubicTo(cx - dp(12), cy + dp(34), cx - dp(27), cy + dp(22),
                    cx - dp(29), cy + dp(3));
            path.lineTo(cx - dp(31), cy - dp(24));
            path.cubicTo(cx - dp(24), cy - dp(25), cx - dp(12), cy - dp(29),
                    cx, cy - dp(36));
            path.close();

            paint.setShader(new LinearGradient(cx - dp(30), cy - dp(35),
                    cx + dp(31), cy + dp(40),
                    new int[]{0xFFB88AFF, 0xFF6A78EC}, null, Shader.TileMode.CLAMP));
            canvas.drawPath(path, paint);
            paint.setShader(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(3));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(cx, cy - dp(1), dp(11), paint);
            canvas.drawLine(cx, cy + dp(10), cx, cy + dp(18), paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy - dp(1), dp(3), paint);
        }
    }

    private final class SkyBackdropView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        SkyBackdropView(Activity activity) {
            super(activity);
            setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float width = getWidth();
            float height = getHeight();
            paint.setShader(new LinearGradient(0, 0, 0, height,
                    new int[]{0xFFC8E6FF, 0xFFE6F4FF, 0xFFF7FBFF},
                    new float[]{0f, .52f, 1f}, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);

            paint.setColor(0x36FFFFFF);
            canvas.drawCircle(width * .18f, height * .11f, dp(74), paint);
            canvas.drawCircle(width * .92f, height * .24f, dp(94), paint);

            drawCloud(canvas, width * .04f, height * .18f, dp(88), 0x62FFFFFF);
            drawCloud(canvas, width * .73f, height * .07f, dp(112), 0x70FFFFFF);
            drawCloud(canvas, width * .69f, height * .82f, dp(125), 0x4AFFFFFF);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1.6f));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(0x88FFFFFF);
            drawSparkle(canvas, width * .15f, height * .34f, dp(6));
            drawSparkle(canvas, width * .88f, height * .42f, dp(8));
            drawSparkle(canvas, width * .18f, height * .73f, dp(5));
            paint.setStyle(Paint.Style.FILL);
        }

        private void drawCloud(Canvas canvas, float left, float centerY,
                               float cloudWidth, int color) {
            paint.setColor(color);
            float h = cloudWidth * .35f;
            canvas.drawOval(new RectF(left, centerY - h * .20f,
                    left + cloudWidth, centerY + h * .62f), paint);
            canvas.drawCircle(left + cloudWidth * .27f, centerY - h * .12f,
                    h * .42f, paint);
            canvas.drawCircle(left + cloudWidth * .57f, centerY - h * .29f,
                    h * .58f, paint);
            canvas.drawCircle(left + cloudWidth * .79f, centerY - h * .04f,
                    h * .38f, paint);
        }

        private void drawSparkle(Canvas canvas, float x, float y, float radius) {
            canvas.drawLine(x - radius, y, x + radius, y, paint);
            canvas.drawLine(x, y - radius, x, y + radius, paint);
        }
    }
}
