package com.safespace.app;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Screen 14: friendly wellbeing trends and a softly animated custom chart. */
final class InsightsView extends PastelScreenView {
    private final FrameLayout tabsHost;
    private final JourneyChart chart;
    private final TextView rangeCaption;
    private int selectedRange;

    InsightsView(Activity activity, ScreenNavigator navigator) {
        super(activity, navigator, -1);
        setContentDescription("Your Journey insights screen");

        content.addView(header("Your Journey", "Progress over time", true,
                "⇧", "Share your journey"), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(73), 0, 0, 0, dp(7)));

        tabsHost = new FrameLayout(activity);
        content.addView(tabsHost, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(45), dp(5), 0, dp(5), dp(13)));

        LinearLayout chartCard = glassCard();
        chartCard.setPadding(dp(15), dp(13), dp(11), dp(8));
        content.addView(chartCard, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(255), 0, 0, 0, dp(13)));

        LinearLayout chartHeading = new LinearLayout(activity);
        chartHeading.setOrientation(LinearLayout.HORIZONTAL);
        chartHeading.setGravity(Gravity.CENTER_VERTICAL);
        chartCard.addView(chartHeading, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(31)));

        rangeCaption = text("This week", 12, MUTED_NAVY, true);
        chartHeading.addView(rangeCaption, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView rising = text("↗  Keep going", 11, 0xFF5A9B85, true);
        rising.setGravity(Gravity.END);
        chartHeading.addView(rising, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        chart = new JourneyChart(activity);
        chartCard.addView(chart, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        content.addView(buildStats(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(102), 0, 0, 0, dp(13)));
        content.addView(buildQuote(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(100), 0, 0, 0, dp(5)));

        renderTabs();
    }

    private void renderTabs() {
        tabsHost.removeAllViews();
        LinearLayout tabs = segmentedControl(new String[]{"Week", "Month", "All"},
                selectedRange, view -> selectRange((Integer) view.getTag()));
        tabsHost.addView(tabs, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void selectRange(int range) {
        if (range == selectedRange) {
            return;
        }
        selectedRange = range;
        renderTabs();
        if (range == 0) {
            rangeCaption.setText("This week");
            chart.setSeries(new float[]{.23f, .43f, .36f, .56f, .68f, .61f, .88f},
                    new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"});
        } else if (range == 1) {
            rangeCaption.setText("This month");
            chart.setSeries(new float[]{.28f, .40f, .49f, .45f, .62f, .72f, .83f},
                    new String[]{"W1", "W2", "W3", "W4", "W5", "", ""});
        } else {
            rangeCaption.setText("All time");
            chart.setSeries(new float[]{.18f, .31f, .39f, .55f, .51f, .71f, .92f},
                    new String[]{"Jan", "Mar", "May", "Jul", "Sep", "Nov", "Now"});
        }
        announceForAccessibility(rangeCaption.getText() + " selected");
    }

    private View buildStats() {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        row.addView(statCard("♥", "Mood", "Improving", 0xFFDDF4EF, 0xFF309980),
                statParams(false));
        row.addView(statCard("▣", "Check-ins", "5 this week", 0xFFECE2FF, 0xFF7A4DD4),
                statParams(false));
        row.addView(statCard("❀", "Wellbeing", "Positive trend", 0xFFDDF2FF, 0xFF358CBE),
                statParams(true));
        return row;
    }

    private LinearLayout.LayoutParams statParams(boolean last) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        if (!last) {
            params.setMarginEnd(dp(8));
        }
        return params;
    }

    private View statCard(String icon, String title, String value, int tint, int color) {
        LinearLayout card = glassCard();
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(3), dp(8), dp(3), dp(6));
        card.setContentDescription(title + ": " + value);

        TextView symbol = text(icon, 19, color, true);
        symbol.setGravity(Gravity.CENTER);
        symbol.setBackground(rounded(tint, dp(16), 0, Color.TRANSPARENT));
        card.addView(symbol, new LinearLayout.LayoutParams(dp(34), dp(34)));

        TextView heading = text(title, 10, NAVY, true);
        heading.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams headingParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headingParams.topMargin = dp(3);
        card.addView(heading, headingParams);

        TextView detail = text(value, 8.5f, MUTED_NAVY, false);
        detail.setGravity(Gravity.CENTER);
        card.addView(detail, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private View buildQuote() {
        FrameLayout shell = new FrameLayout(activity);
        shell.setElevation(dp(3));
        shell.setPadding(dp(18), dp(10), dp(18), dp(10));
        shell.setBackground(gradientRounded(0xEFFFFFFF, 0xEFFFF0F7, dp(21),
                dp(1), 0x22E17FB6));
        shell.setContentDescription("You're doing better than you think.");
        shell.setOnClickListener(view -> toast("Your progress matters"));

        TextView decoration = text("❀", 39, 0x35E170AC, false);
        decoration.setGravity(Gravity.BOTTOM | Gravity.END);
        shell.addView(decoration, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        TextView quote = text("“You’re doing better\nthan you think.”", 15, NAVY, true);
        quote.setGravity(Gravity.CENTER);
        quote.setLineSpacing(dp(3), 1f);
        shell.addView(quote, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return shell;
    }

    /** Draws a smooth curve directly so the graph remains crisp at every screen density. */
    private final class JourneyChart extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path line = new Path();
        private final RectF plot = new RectF();
        private float[] from = {.23f, .43f, .36f, .56f, .68f, .61f, .88f};
        private float[] target = from.clone();
        private String[] labels = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        private float progress = 1f;
        private ValueAnimator animator;

        JourneyChart(Activity activity) {
            super(activity);
            setContentDescription("Smooth wellbeing line chart trending upward");
        }

        void setSeries(float[] values, String[] newLabels) {
            float[] displayed = interpolate();
            from = displayed;
            target = values.clone();
            labels = newLabels.clone();
            if (animator != null) {
                animator.cancel();
            }
            animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(480L);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.addUpdateListener(animation -> {
                progress = (float) animation.getAnimatedValue();
                invalidate();
            });
            progress = 0f;
            animator.start();
        }

        private float[] interpolate() {
            float[] values = new float[target.length];
            for (int i = 0; i < values.length; i++) {
                float start = i < from.length ? from[i] : target[i];
                values[i] = start + (target[i] - start) * progress;
            }
            return values;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float[] values = interpolate();
            plot.set(dp(13), dp(8), getWidth() - dp(10), getHeight() - dp(27));

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1));
            paint.setColor(0x18765DE5);
            for (int i = 1; i <= 3; i++) {
                float y = plot.top + plot.height() * i / 4f;
                canvas.drawLine(plot.left, y, plot.right, y, paint);
            }

            buildSmoothPath(values);
            Path fill = new Path(line);
            fill.lineTo(plot.right, plot.bottom);
            fill.lineTo(plot.left, plot.bottom);
            fill.close();
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(0, plot.top, 0, plot.bottom,
                    new int[]{0x457755E6, 0x007755E6}, null, Shader.TileMode.CLAMP));
            canvas.drawPath(fill, paint);
            paint.setShader(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(3));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(PURPLE);
            canvas.drawPath(line, paint);

            float gap = plot.width() / (values.length - 1);
            for (int i = 0; i < values.length; i++) {
                float x = plot.left + gap * i;
                float y = plot.bottom - values[i] * plot.height();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                canvas.drawCircle(x, y, dp(i == values.length - 1 ? 5 : 3.4f), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(2));
                paint.setColor(i == values.length - 1 ? 0xFF8B52E5 : 0xFF7764DA);
                canvas.drawCircle(x, y, dp(i == values.length - 1 ? 5 : 3.4f), paint);

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(MUTED_NAVY);
                paint.setTextSize(dp(8.5f));
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(labels[i], x, getHeight() - dp(6), paint);
            }
        }

        private void buildSmoothPath(float[] values) {
            line.reset();
            float gap = plot.width() / (values.length - 1);
            float firstY = plot.bottom - values[0] * plot.height();
            line.moveTo(plot.left, firstY);
            for (int i = 1; i < values.length; i++) {
                float x0 = plot.left + gap * (i - 1);
                float y0 = plot.bottom - values[i - 1] * plot.height();
                float x1 = plot.left + gap * i;
                float y1 = plot.bottom - values[i] * plot.height();
                float middle = (x0 + x1) / 2f;
                line.cubicTo(middle, y0, middle, y1, x1, y1);
            }
        }
    }
}
