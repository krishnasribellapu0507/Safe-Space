package com.safespace.app;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/** Screen 21: a short, reassuring success moment before returning home. */
final class SuccessView extends View {
    private static final long DISPLAY_TIME_MS = 2400L;

    private static final int INK = 0xFF45227B;
    private static final int MUTED_INK = 0xFF725D93;
    private static final int PURPLE = 0xFF8E55EE;
    private static final int BLUE = 0xFF5CA8FA;

    private final ScreenNavigator navigator;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    private final Path path = new Path();
    private final RectF rect = new RectF();
    private final float density;
    private final float scaledDensity;
    private final Typeface roundedRegular = Typeface.create("sans-serif-rounded", Typeface.NORMAL);
    private final Typeface roundedBold = Typeface.create("sans-serif-rounded", Typeface.BOLD);
    private final Runnable returnHome = this::navigateHome;

    private ValueAnimator progressAnimator;
    private float progress;
    private boolean navigated;
    private long attachedAt;

    SuccessView(Activity activity, ScreenNavigator navigator) {
        super(activity);
        this.navigator = navigator;
        density = getResources().getDisplayMetrics().density;
        scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        setClickable(true);
        setFocusable(true);
        setContentDescription("You are doing great. Progress, not perfection is the goal. Tap to continue home.");
        setOnClickListener(view -> navigateHome());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        navigated = false;
        progress = 0f;
        attachedAt = SystemClock.uptimeMillis();
        handler.removeCallbacks(returnHome);
        handler.postDelayed(returnHome, DISPLAY_TIME_MS);

        progressAnimator = ValueAnimator.ofFloat(0f, 1f);
        progressAnimator.setDuration(DISPLAY_TIME_MS - 120L);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.addUpdateListener(animation -> {
            progress = (float) animation.getAnimatedValue();
            invalidate();
        });
        progressAnimator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacks(returnHome);
        if (progressAnimator != null) {
            progressAnimator.cancel();
            progressAnimator.removeAllUpdateListeners();
            progressAnimator = null;
        }
        super.onDetachedFromWindow();
    }

    private void navigateHome() {
        if (navigated || !isAttachedToWindow()) {
            return;
        }
        // MainActivity's incoming-page slide lasts 320 ms. Deferring an unusually
        // early tap prevents it from being swallowed while that transition is busy.
        long remainingEntranceTime = 360L - (SystemClock.uptimeMillis() - attachedAt);
        if (remainingEntranceTime > 0L) {
            handler.removeCallbacks(returnHome);
            handler.postDelayed(returnHome, remainingEntranceTime);
            return;
        }
        navigated = true;
        handler.removeCallbacks(returnHome);
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }
        navigator.openScreen(8);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final float width = getWidth();
        final float height = getHeight();
        if (width <= 0f || height <= 0f) {
            return;
        }

        drawBackground(canvas, width, height);
        drawPetalsAndSparkles(canvas, width, height);

        float lotusCenterX = width * .5f;
        float lotusBaseY = height * .405f;
        float pulse = 1f + .018f * (float) Math.sin(progress * Math.PI * 2.2f);
        canvas.save();
        canvas.scale(pulse, pulse, lotusCenterX, lotusBaseY - dp(55));
        drawLotus(canvas, lotusCenterX, lotusBaseY, Math.min(dp(184), width * .49f));
        canvas.restore();

        drawCenteredText(canvas, "You're doing great!", width * .5f, height * .515f,
                25f, INK, true);
        drawCenteredText(canvas, "Just a moment...", width * .5f, height * .552f,
                14f, MUTED_INK, false);

        drawProgress(canvas, width, height * .655f);

        drawCenteredText(canvas, "\u201cProgress, not perfection", width * .5f, height * .747f,
                15f, INK, true);
        drawCenteredText(canvas, "is the goal.\u201d", width * .5f, height * .777f,
                15f, INK, true);
    }

    private void drawBackground(Canvas canvas, float width, float height) {
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(0f, 0f, width, height,
                new int[]{0xFFF2F8FF, 0xFFEDEEFF, 0xFFFFEFF6, 0xFFF9F5FF},
                new float[]{0f, .34f, .72f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawRect(0f, 0f, width, height, paint);

        paint.setShader(new RadialGradient(width * .5f, height * .35f,
                Math.max(width * .52f, dp(190)),
                new int[]{0xBFFFFFFF, 0x4DCFAEFF, 0x00CFAEFF},
                new float[]{0f, .48f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawCircle(width * .5f, height * .35f,
                Math.max(width * .52f, dp(190)), paint);

        paint.setShader(new RadialGradient(width * .18f, height * .72f,
                width * .48f, 0x52FFB9D9, 0x00FFB9D9, Shader.TileMode.CLAMP));
        canvas.drawCircle(width * .18f, height * .72f, width * .48f, paint);

        paint.setShader(new RadialGradient(width * .87f, height * .18f,
                width * .42f, 0x4AAEDAFF, 0x00AEDAFF, Shader.TileMode.CLAMP));
        canvas.drawCircle(width * .87f, height * .18f, width * .42f, paint);
        paint.setShader(null);
    }

    private void drawPetalsAndSparkles(Canvas canvas, float width, float height) {
        float drift = (float) Math.sin(progress * Math.PI * 2f);
        drawFloatingPetal(canvas, width * .14f, height * .145f + dp(4) * drift,
                dp(17), dp(31), -34f, 0xA8FF8FC8, 0x48FFFFFF);
        drawFloatingPetal(canvas, width * .87f, height * .115f - dp(3) * drift,
                dp(14), dp(27), 36f, 0x9FA689FF, 0x42FFFFFF);
        drawFloatingPetal(canvas, width * .91f, height * .46f + dp(3) * drift,
                dp(13), dp(25), 55f, 0x93F47CB8, 0x42FFFFFF);
        drawFloatingPetal(canvas, width * .075f, height * .61f - dp(4) * drift,
                dp(15), dp(28), -56f, 0x8FBE91FF, 0x44FFFFFF);
        drawFloatingPetal(canvas, width * .83f, height * .875f + dp(3) * drift,
                dp(17), dp(31), 29f, 0x8AFF91CA, 0x44FFFFFF);
        drawFloatingPetal(canvas, width * .18f, height * .91f - dp(2) * drift,
                dp(11), dp(23), -31f, 0x78A981F7, 0x35FFFFFF);

        drawSparkle(canvas, width * .76f, height * .23f, dp(7), 0xBFFFFFFF);
        drawSparkle(canvas, width * .20f, height * .36f, dp(5), 0x99FFFFFF);
        drawSparkle(canvas, width * .87f, height * .70f, dp(4), 0x94FFFFFF);
        drawSparkle(canvas, width * .28f, height * .82f, dp(3), 0x90FFFFFF);
    }

    private void drawFloatingPetal(Canvas canvas, float cx, float cy, float width,
                                    float height, float degrees, int start, int end) {
        canvas.save();
        canvas.rotate(degrees, cx, cy);
        path.reset();
        path.moveTo(cx, cy - height * .5f);
        path.cubicTo(cx - width * .72f, cy - height * .24f,
                cx - width * .56f, cy + height * .38f, cx, cy + height * .5f);
        path.cubicTo(cx + width * .47f, cy + height * .18f,
                cx + width * .58f, cy - height * .28f, cx, cy - height * .5f);
        path.close();
        paint.setShader(new LinearGradient(cx - width, cy - height * .5f,
                cx + width, cy + height * .5f, start, end, Shader.TileMode.CLAMP));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, paint);
        paint.setShader(null);
        canvas.restore();
    }

    private void drawSparkle(Canvas canvas, float cx, float cy, float radius, int color) {
        paint.setShader(null);
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        path.reset();
        path.moveTo(cx, cy - radius);
        path.quadTo(cx + radius * .18f, cy - radius * .18f, cx + radius, cy);
        path.quadTo(cx + radius * .18f, cy + radius * .18f, cx, cy + radius);
        path.quadTo(cx - radius * .18f, cy + radius * .18f, cx - radius, cy);
        path.quadTo(cx - radius * .18f, cy - radius * .18f, cx, cy - radius);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawLotus(Canvas canvas, float cx, float baseY, float size) {
        float half = size * .5f;

        paint.setShader(new RadialGradient(cx, baseY - size * .31f, size * .60f,
                new int[]{0xA8FFFFFF, 0x50B58CFF, 0x00B58CFF},
                new float[]{0f, .52f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, baseY - size * .31f, size * .60f, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(1.15f));
        paint.setColor(0x66FFFFFF);
        canvas.drawCircle(cx, baseY - size * .30f, size * .47f, paint);
        paint.setStyle(Paint.Style.FILL);

        // Back petals create the broad blue-lilac silhouette seen in the reference.
        drawLotusPetal(canvas, cx, baseY - size * .01f, half * .58f, size * .59f,
                -67f, 0xFF7DB7FA, 0xFFF5DFFF);
        drawLotusPetal(canvas, cx, baseY - size * .01f, half * .58f, size * .59f,
                67f, 0xFFF198D1, 0xFFE7E5FF);
        drawLotusPetal(canvas, cx, baseY - size * .025f, half * .60f, size * .66f,
                -46f, 0xFF8B98FA, 0xFFE6DEFF);
        drawLotusPetal(canvas, cx, baseY - size * .025f, half * .60f, size * .66f,
                46f, 0xFFF185CA, 0xFFFFE5F3);
        drawLotusPetal(canvas, cx, baseY - size * .04f, half * .57f, size * .72f,
                -25f, 0xFF7CA7F8, 0xFFE9E2FF);
        drawLotusPetal(canvas, cx, baseY - size * .04f, half * .57f, size * .72f,
                25f, 0xFFED8CD3, 0xFFFFEAF5);

        // Tall center and brighter front petals.
        drawLotusPetal(canvas, cx, baseY - size * .045f, half * .56f, size * .78f,
                0f, 0xFF8E73F1, 0xFFF7D9FF);
        drawLotusPetal(canvas, cx, baseY + size * .055f, half * .66f, size * .54f,
                -55f, 0xFF79B7FA, 0xFFF2E8FF);
        drawLotusPetal(canvas, cx, baseY + size * .055f, half * .66f, size * .54f,
                55f, 0xFFEF8DCE, 0xFFFFEBF6);
        drawLotusPetal(canvas, cx, baseY + size * .075f, half * .67f, size * .54f,
                -29f, 0xFF9877F2, 0xFFF6E4FF);
        drawLotusPetal(canvas, cx, baseY + size * .075f, half * .67f, size * .54f,
                29f, 0xFFF07FC7, 0xFFFFEDF7);
        drawLotusPetal(canvas, cx, baseY + size * .085f, half * .59f, size * .54f,
                0f, 0xFFC17EF0, 0xFFFFE9F9);

        paint.setShader(new RadialGradient(cx, baseY + size * .095f, half * .9f,
                new int[]{0x70FFFFFF, 0x3C9A6BF0, 0x009A6BF0},
                new float[]{0f, .54f, 1f}, Shader.TileMode.CLAMP));
        rect.set(cx - half * .95f, baseY + size * .035f,
                cx + half * .95f, baseY + size * .19f);
        canvas.drawOval(rect, paint);
        paint.setShader(null);
    }

    private void drawLotusPetal(Canvas canvas, float cx, float baseY,
                                float width, float height, float degrees,
                                int bottomColor, int tipColor) {
        canvas.save();
        canvas.rotate(degrees, cx, baseY);
        path.reset();
        path.moveTo(cx, baseY);
        path.cubicTo(cx - width * .72f, baseY - height * .28f,
                cx - width * .50f, baseY - height * .76f, cx, baseY - height);
        path.cubicTo(cx + width * .50f, baseY - height * .76f,
                cx + width * .72f, baseY - height * .28f, cx, baseY);
        path.close();

        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(cx, baseY, cx, baseY - height,
                bottomColor, tipColor, Shader.TileMode.CLAMP));
        canvas.drawPath(path, paint);
        paint.setShader(null);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(.8f));
        paint.setColor(0x70FFFFFF);
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }

    private void drawProgress(Canvas canvas, float width, float centerY) {
        float left = Math.max(dp(48), width * .17f);
        float right = Math.min(width - dp(48), width * .83f);
        float barHeight = dp(8);

        rect.set(left, centerY - barHeight * .5f, right, centerY + barHeight * .5f);
        paint.setShader(null);
        paint.setColor(0x70BFC9EA);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(rect, barHeight, barHeight, paint);

        float eased = new DecelerateInterpolator(1.35f).getInterpolation(progress);
        float fillRight = left + (right - left) * Math.max(.04f, eased);
        rect.set(left, centerY - barHeight * .5f, fillRight, centerY + barHeight * .5f);
        paint.setShader(new LinearGradient(left, 0f, right, 0f,
                PURPLE, BLUE, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(rect, barHeight, barHeight, paint);
        paint.setShader(null);

        float glowRadius = dp(9);
        paint.setShader(new RadialGradient(fillRight, centerY, glowRadius,
                0xA8FFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP));
        canvas.drawCircle(fillRight, centerY, glowRadius, paint);
        paint.setShader(null);
    }

    private void drawCenteredText(Canvas canvas, String value, float x, float baseline,
                                  float sizeSp, int color, boolean bold) {
        paint.setShader(null);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(bold ? roundedBold : roundedRegular);
        paint.setTextSize(sp(sizeSp));
        paint.setColor(color);
        canvas.drawText(value, x, baseline, paint);
    }

    private float dp(float value) {
        return value * density;
    }

    private float sp(float value) {
        return value * scaledDensity;
    }
}
