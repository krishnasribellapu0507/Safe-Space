package com.safespace.app;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/** Central motion rules: calm, short, cancellable and reduced-motion aware. */
final class MotionSystem {
    private MotionSystem() {}

    static boolean reducedMotion(Context context) {
        return AccessibilityCenter.reducedMotion(context);
    }

    static void bindPress(View view) {
        view.setOnTouchListener((v, event) -> {
            if (reducedMotion(v.getContext())) return false;
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(.98f).scaleY(.98f).alpha(.96f)
                        .setDuration(120L).setInterpolator(new DecelerateInterpolator()).start();
            } else if (event.getActionMasked() == MotionEvent.ACTION_UP
                    || event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).alpha(1f)
                        .setDuration(180L).setInterpolator(new DecelerateInterpolator()).start();
            }
            return false;
        });
    }

    static void enter(View view, long delayMs) {
        if (reducedMotion(view.getContext())) {
            view.setAlpha(1f);
            view.setTranslationY(0f);
            return;
        }
        view.setAlpha(0f);
        view.setTranslationY(18f * view.getResources().getDisplayMetrics().density);
        view.animate().alpha(1f).translationY(0f).setStartDelay(delayMs)
                .setDuration(280L).setInterpolator(new DecelerateInterpolator()).start();
    }
}
