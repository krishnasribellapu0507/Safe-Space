package com.safespace.app;

import android.app.Activity;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Simple offline calm timer for the Digital Detox Bubble feature. */
final class DetoxSessionView extends PastelScreenView {
    private TextView timer;
    private TextView state;
    private CountDownTimer countdown;
    private long selectedMillis = 5 * 60_000L;
    private boolean running;

    DetoxSessionView(Activity activity, ScreenNavigator navigator) {
        super(activity, navigator, 28);
        content.addView(header("Digital Detox Bubble", "A quiet offline pause", true, "", ""),
                marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(70), 0, 0, 0, dp(12)));

        LinearLayout card = glassCard();
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setPadding(dp(18), dp(24), dp(18), dp(22));
        TextView title = text("◌", 54, PURPLE, true);
        title.setGravity(Gravity.CENTER);
        card.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(72)));

        state = text("Choose a session length", 14, MUTED_NAVY, true);
        state.setGravity(Gravity.CENTER);
        card.addView(state, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(34)));

        timer = text("5:00", 46, NAVY, true);
        timer.setGravity(Gravity.CENTER);
        card.addView(timer, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(76)));

        LinearLayout choices = new LinearLayout(activity);
        choices.setOrientation(LinearLayout.HORIZONTAL);
        choices.setGravity(Gravity.CENTER);
        choices.addView(lengthButton("5 min", 5), weight());
        choices.addView(lengthButton("10 min", 10), weight());
        choices.addView(lengthButton("20 min", 20), weight());
        card.addView(choices, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(52)));

        TextView start = text("Start / Stop", 15, Color.WHITE, true);
        start.setGravity(Gravity.CENTER);
        start.setBackground(gradientRounded(0xFF8F69EC, 0xFF6542D1, dp(22), 0, Color.TRANSPARENT));
        start.setOnClickListener(v -> toggle());
        LinearLayout.LayoutParams startLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(50));
        startLp.topMargin = dp(15);
        card.addView(start, startLp);

        TextView note = text("No streaks. No pressure. Put the phone down, breathe naturally, or listen to a calm sound if you want.",
                11, MUTED_NAVY, false);
        note.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams noteLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        noteLp.topMargin = dp(16);
        card.addView(note, noteLp);

        content.addView(card, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                0, 0, 0, dp(14)));

        LinearLayout sound = bottomRowCard("♪", 0xFFDFF4FF, 0xFF2D85C6,
                "Optional ambient sound", "Open Calm sounds without leaving your timer choice", null, "");
        sound.setOnClickListener(v -> navigator.openScreen(12));
        content.addView(sound, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(74), 0, 0, 0, dp(8)));
    }

    private TextView lengthButton(String label, int minutes) {
        TextView b = text(label, 12, NAVY, true);
        b.setGravity(Gravity.CENTER);
        b.setBackground(rippleRounded(0xEFFFFFFF, dp(16), 0x22765CE8));
        b.setOnClickListener(v -> {
            if (running) return;
            selectedMillis = minutes * 60_000L;
            timer.setText(minutes + ":00");
            state.setText(minutes + " minute calm session");
        });
        return b;
    }

    private LinearLayout.LayoutParams weight() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0,
                ViewGroup.LayoutParams.MATCH_PARENT, 1f);
        p.setMargins(dp(4), 0, dp(4), 0);
        return p;
    }

    private void toggle() {
        if (running) {
            if (countdown != null) countdown.cancel();
            running = false;
            state.setText("Paused. Choose a length or start again.");
            return;
        }
        running = true;
        state.setText("Detox Bubble active • you can leave the screen on");
        countdown = new CountDownTimer(selectedMillis, 1000L) {
            @Override public void onTick(long left) {
                long sec = (left + 999) / 1000;
                timer.setText(String.format("%d:%02d", sec / 60, sec % 60));
            }

            @Override public void onFinish() {
                running = false;
                timer.setText("Done ✓");
                state.setText("Nice. A short pause still counts.");
                SupportSignalEngine.recordCalm(activity);
            }
        }.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (countdown != null) countdown.cancel();
    }
}
