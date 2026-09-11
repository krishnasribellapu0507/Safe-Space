package com.safespace.app;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayDeque;

public final class MainActivity extends Activity implements ScreenNavigator {
    private FrameLayout root;
    private SafeSpaceView introView;
    private AuthScreenView authView;
    private OnboardingQuestionsView onboardingQuestionsView;
    private PermissionsScreenView permissionsView;
    private View activeAppView;
    private int activeScreenNumber;
    private final ArrayDeque<Integer> screenHistory = new ArrayDeque<>();
    private boolean pageTransitionRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureEdgeToEdge();
        if (getSharedPreferences("safe_space_settings", MODE_PRIVATE).getBoolean("discreet_mode", false)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        }
        root = new FrameLayout(this);
        introView = new SafeSpaceView(this, () -> showAuthScreen(true));
        root.addView(introView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        setContentView(root);
    }

    private void showAuthScreen(boolean signUp) {
        if (authView != null) {
            return;
        }
        applySystemBars(false, Color.rgb(242, 249, 255));
        authView = new AuthScreenView(
                this, signUp, this::showQuestionnaireScreen);
        int width = getResources().getDisplayMetrics().widthPixels;
        authView.setTranslationX(width);
        root.addView(authView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        authView.animate().translationX(0f).setDuration(320L)
                .setInterpolator(new DecelerateInterpolator()).start();
    }

    private void showQuestionnaireScreen() {
        if (onboardingQuestionsView != null || permissionsView != null || activeAppView != null || pageTransitionRunning) {
            return;
        }
        pageTransitionRunning = true;
        hideKeyboard();
        boolean dark = ThemeManager.isDark(this);
        applySystemBars(dark, dark ? Color.rgb(38, 15, 79) : Color.rgb(220, 242, 255));

        OnboardingQuestionsView incoming = new OnboardingQuestionsView(this, this::showPermissionsScreen);
        onboardingQuestionsView = incoming;
        int width = getResources().getDisplayMetrics().widthPixels;
        incoming.setTranslationX(width);
        root.addView(incoming, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        AuthScreenView outgoing = authView;
        if (outgoing != null) {
            outgoing.animate().translationX(-width * .16f).setDuration(320L)
                    .setInterpolator(new DecelerateInterpolator()).start();
        }
        incoming.animate().translationX(0f).setDuration(320L)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    if (outgoing != null) root.removeView(outgoing);
                    authView = null;
                    pageTransitionRunning = false;
                }).start();
    }

    private void showPermissionsScreen() {
        if (permissionsView != null || activeAppView != null || pageTransitionRunning) {
            return;
        }
        pageTransitionRunning = true;
        hideKeyboard();
        applySystemBars(false, Color.rgb(231, 244, 255));

        PermissionsScreenView incoming = new PermissionsScreenView(this, this::showHomeScreen);
        permissionsView = incoming;
        int width = getResources().getDisplayMetrics().widthPixels;
        incoming.setTranslationX(width);
        root.addView(incoming, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        OnboardingQuestionsView outgoing = onboardingQuestionsView;
        if (outgoing != null) {
            outgoing.animate().translationX(-width * .16f).setDuration(320L)
                    .setInterpolator(new DecelerateInterpolator()).start();
        }
        incoming.animate().translationX(0f).setDuration(320L)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    if (outgoing != null) {
                        root.removeView(outgoing);
                    }
                    onboardingQuestionsView = null;
                    pageTransitionRunning = false;
                }).start();
    }

    private void showHomeScreen() {
        if (activeAppView != null || pageTransitionRunning) {
            return;
        }
        pageTransitionRunning = true;
        boolean dark = ThemeManager.isDark(this);
        applySystemBars(dark, dark ? Color.rgb(38, 15, 79) : Color.rgb(220, 242, 255));
        SosNotification.refreshIfEnabled(this);

        HomeDashboardView incoming = new HomeDashboardView(this, this);
        ThemeManager.applyToScreen(this, incoming);
        int width = getResources().getDisplayMetrics().widthPixels;
        incoming.setTranslationX(width);
        root.addView(incoming, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        PermissionsScreenView outgoing = permissionsView;
        if (outgoing != null) {
            outgoing.animate().translationX(-width * .16f).setDuration(320L)
                    .setInterpolator(new DecelerateInterpolator()).start();
        }
        incoming.animate().translationX(0f).setDuration(320L)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    if (outgoing != null) {
                        root.removeView(outgoing);
                    }
                    permissionsView = null;
                    activeAppView = incoming;
                    activeScreenNumber = 8;
                    screenHistory.clear();
                    pageTransitionRunning = false;
                }).start();
    }

    @Override
    public void openScreen(int screenNumber) {
        if (screenNumber < 8 || screenNumber > 28 || activeAppView == null
                || pageTransitionRunning || screenNumber == activeScreenNumber) {
            return;
        }
        boolean returningHome = screenNumber == 8;
        if (returningHome) {
            screenHistory.clear();
        } else {
            screenHistory.push(activeScreenNumber);
        }
        transitionToAppScreen(screenNumber, returningHome);
    }

    @Override
    public void goBack() {
        if (activeAppView == null || pageTransitionRunning) {
            return;
        }
        if (activeScreenNumber == 8) {
            finish();
            return;
        }
        int previous = screenHistory.isEmpty() ? 8 : screenHistory.pop();
        transitionToAppScreen(previous, true);
    }

    private void transitionToAppScreen(int screenNumber, boolean backwards) {
        View incoming = createAppScreen(screenNumber);
        if (incoming != null) ThemeManager.applyToScreen(this, incoming);
        if (incoming == null) {
            return;
        }
        pageTransitionRunning = true;
        applySystemBars(screenNumber == 20,
                screenNumber == 20 ? Color.rgb(7, 20, 48) : Color.rgb(238, 246, 255));

        int width = getResources().getDisplayMetrics().widthPixels;
        int direction = backwards ? -1 : 1;
        incoming.setTranslationX(direction * width);
        root.addView(incoming, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        View outgoing = activeAppView;
        outgoing.animate().translationX(-direction * width * .16f).setDuration(320L)
                .setInterpolator(new DecelerateInterpolator()).start();
        incoming.animate().translationX(0f).setDuration(320L)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> {
                    root.removeView(outgoing);
                    activeAppView = incoming;
                    activeScreenNumber = screenNumber;
                    pageTransitionRunning = false;
                }).start();
    }

    private View createAppScreen(int screenNumber) {
        switch (screenNumber) {
            case 8:
                return new HomeDashboardView(this, this);
            case 9:
                return new MoodCheckInView(this, this);
            case 10:
                return new CompanionView(this, this);
            case 11:
                return new JournalView(this, this);
            case 12:
                return new RelaxView(this, this);
            case 13:
                return new ActivitiesView(this, this);
            case 14:
                return new InsightsView(this, this);
            case 15:
                return new SupportView(this, this);
            case 16:
                return new ProfileView(this, this);
            case 17:
                return new RemindersView(this, this);
            case 18:
                return new CommunityView(this, this);
            case 19:
                return new SettingsView(this, this);
            case 20:
                return new NightModeView(this, this);
            case 21:
                return new SuccessView(this, this);
            case 22:
                return new CounselorView(this, this);
            case 23:
                return new MenuView(this, this);
            case 24:
                return new MindfulGameView(this, this);
            case 25:
                return new SafetyView(this, this);
            case 26:
                return new NearbyCareView(this, this);
            case 27:
                return new SafeSpace360View(this, this);
            case 28:
                return new DetoxSessionView(this, this);
            default:
                return null;
        }
    }


    @Override
    public void setAppearance(int mode) {
        ThemeManager.setMode(this, mode);
        if (activeAppView == null || pageTransitionRunning) return;
        View replacement = createAppScreen(activeScreenNumber);
        if (replacement == null) return;
        ThemeManager.applyToScreen(this, replacement);
        root.addView(replacement, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        root.removeView(activeAppView);
        activeAppView = replacement;
        boolean dark = ThemeManager.isDark(this);
        applySystemBars(dark, dark ? Color.rgb(38, 15, 79) : Color.rgb(220, 242, 255));
    }

    @Override
    public int getAppearance() {
        return ThemeManager.getMode(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 704 && permissionsView != null) {
            permissionsView.completeAfterPermissions();
        }
    }

    private void hideAuthScreen() {
        if (authView == null || pageTransitionRunning) {
            return;
        }
        applySystemBars(false, Color.rgb(247, 234, 244));
        AuthScreenView closingView = authView;
        authView = null;
        closingView.animate().translationX(closingView.getWidth()).setDuration(260L)
                .setInterpolator(new DecelerateInterpolator())
                .withEndAction(() -> root.removeView(closingView)).start();
    }

    @Override
    public void onBackPressed() {
        if (pageTransitionRunning) {
            return;
        }
        if (activeAppView != null) {
            if (activeScreenNumber == 8) {
                finish();
            } else {
                goBack();
            }
        } else if (permissionsView != null) {
            finish();
        } else if (onboardingQuestionsView != null) {
            if (!onboardingQuestionsView.handleBack()) {
                finish();
            }
        } else if (authView != null) {
            hideAuthScreen();
        } else {
            super.onBackPressed();
        }
    }

    private void hideKeyboard() {
        View focused = getCurrentFocus();
        if (focused == null) {
            return;
        }
        InputMethodManager manager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            manager.hideSoftInputFromWindow(focused.getWindowToken(), 0);
        }
        focused.clearFocus();
    }

    private void configureEdgeToEdge() {
        applySystemBars(false, Color.rgb(247, 234, 244));
    }

    private void applySystemBars(boolean nightMode, int navigationColor) {
        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(navigationColor);
        int systemUi = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        if (!nightMode) {
            systemUi |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }
        if (!nightMode && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            systemUi |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }
        window.getDecorView().setSystemUiVisibility(systemUi);
    }

    private static final class SafeSpaceView extends View {
        private static final int NAVY = Color.rgb(16, 38, 95);
        private static final int PURPLE = Color.rgb(123, 79, 233);
        private static final int LIGHT_PURPLE = Color.rgb(201, 155, 255);

        private final String[] titles = {
                "Find Your\nSafe Space",
                "Talk\nFreely",
                "Small Steps\nBig Progress"
        };
        private final String[] bodies = {
                "A supportive journey\nfor your mind, always\nwith you.",
                "Share your thoughts,\nfeelings and worries,\nwithout judgment.",
                "Track your mood, build\nhealthy habits and feel\nmore in control."
        };
        private final String[] buttons = {"Next", "Next", "Get Started"};

        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        private final Rect bitmapSource = new Rect();
        private final RectF bitmapDestination = new RectF();
        private final RectF buttonBounds = new RectF();
        private final Handler handler = new Handler(Looper.getMainLooper());
        private final Bitmap background;
        private final Bitmap logo;
        private final Runnable openSignUp;
        private final float density;
        private final float scaledDensity;
        private final Runnable revealOnboarding = this::showFirstOnboarding;

        private int page = -1;
        private int topInset;
        private int bottomInset;
        private float touchDownX;
        private float touchDownY;
        private float splashProgress;
        private float pageTransitionProgress;
        private int transitionFromPage = -1;
        private int transitionToPage = -1;
        private int transitionDirection;
        private ValueAnimator progressAnimator;
        private ValueAnimator pageAnimator;

        SafeSpaceView(Context context, Runnable openSignUp) {
            super(context);
            this.openSignUp = openSignUp;
            density = getResources().getDisplayMetrics().density;
            scaledDensity = getResources().getDisplayMetrics().scaledDensity;
            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inScaled = false;
            bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
            background = BitmapFactory.decodeResource(
                    getResources(), R.drawable.serenity_background, bitmapOptions);
            BitmapFactory.Options logoOptions = new BitmapFactory.Options();
            logoOptions.inScaled = false;
            logoOptions.inSampleSize = 2;
            logo = BitmapFactory.decodeResource(
                    getResources(), R.drawable.safe_space_logo, logoOptions);
            if (background != null) {
                bitmapSource.set(0, 0, background.getWidth(), background.getHeight());
            }
            setFocusable(true);
            setClickable(true);
            setContentDescription("Safe Space opening screen. A calmer you. A brighter tomorrow.");
            startSplashSequence();
        }

        private void startSplashSequence() {
            progressAnimator = ValueAnimator.ofFloat(0f, 1f);
            progressAnimator.setDuration(1650L);
            progressAnimator.addUpdateListener(animation -> {
                splashProgress = (float) animation.getAnimatedValue();
                invalidate();
            });
            progressAnimator.start();
            handler.postDelayed(revealOnboarding, 1800L);
        }

        private void showFirstOnboarding() {
            // Keep the view fully opaque when leaving the splash. The shared background is
            // already on screen, so changing only the foreground content avoids a bright frame.
            page = 0;
            updateAccessibilityDescription();
            invalidate();
        }

        @Override
        public WindowInsets onApplyWindowInsets(WindowInsets insets) {
            topInset = insets.getSystemWindowInsetTop();
            bottomInset = insets.getSystemWindowInsetBottom();
            invalidate();
            return insets;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            drawBackground(canvas);
            if (page < 0) {
                drawSplash(canvas);
            } else {
                drawOnboarding(canvas);
            }
        }

        private void drawBackground(Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            if (background == null || width == 0 || height == 0) {
                canvas.drawColor(Color.rgb(206, 220, 255));
                return;
            }

            float imageRatio = background.getWidth() / (float) background.getHeight();
            float viewRatio = width / (float) height;
            if (imageRatio > viewRatio) {
                float scaledWidth = height * imageRatio;
                float left = (width - scaledWidth) / 2f;
                bitmapDestination.set(left, 0, left + scaledWidth, height);
            } else {
                float scaledHeight = width / imageRatio;
                float top = (height - scaledHeight) / 2f;
                bitmapDestination.set(0, top, width, top + scaledHeight);
            }
            paint.setAlpha(255);
            paint.setShader(null);
            canvas.drawBitmap(background, bitmapSource, bitmapDestination, paint);

            paint.setShader(new LinearGradient(0, 0, 0, height,
                    new int[]{0x42FFFFFF, 0x05FFFFFF, 0x10FFFFFF, 0x5CFFF7FB},
                    new float[]{0f, .28f, .68f, 1f}, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);
        }

        private void drawSplash(Canvas canvas) {
            float width = getWidth();
            float height = getHeight();
            float logoY = Math.max(dp(250), height * .42f);
            float logoSize = Math.min(width * .64f, dp(250));

            drawLogo(canvas, width / 2f, logoY, logoSize);

            paint.setColor(NAVY);
            paint.setTypeface(android.graphics.Typeface.create("sans-serif-rounded", android.graphics.Typeface.BOLD));
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(sp(18));
            drawLines(canvas, "A calmer you\nA brighter tomorrow", width / 2f,
                    height * .755f, dp(25));

            float trackWidth = Math.min(dp(155), width * .42f);
            float trackHeight = dp(5);
            float trackY = height - bottomInset - dp(47);
            RectF track = new RectF(width / 2f - trackWidth / 2f, trackY,
                    width / 2f + trackWidth / 2f, trackY + trackHeight);
            paint.setColor(0x88FFFFFF);
            canvas.drawRoundRect(track, dp(3), dp(3), paint);
            RectF fill = new RectF(track.left, track.top,
                    track.left + track.width() * splashProgress, track.bottom);
            paint.setColor(PURPLE);
            canvas.drawRoundRect(fill, dp(3), dp(3), paint);
        }

        private void drawLogo(Canvas canvas, float centerX, float centerY, float size) {
            if (logo == null) {
                return;
            }
            RectF logoBounds = new RectF(centerX - size / 2f, centerY - size / 2f,
                    centerX + size / 2f, centerY + size / 2f);
            paint.setAlpha(255);
            paint.setShader(null);
            canvas.drawBitmap(logo, null, logoBounds, paint);
        }

        private void drawOnboarding(Canvas canvas) {
            if (pageAnimator != null && pageAnimator.isRunning()) {
                float width = getWidth();
                float outgoingX = -transitionDirection * width * pageTransitionProgress;
                float incomingX = transitionDirection * width * (1f - pageTransitionProgress);
                drawOnboardingPage(canvas, transitionFromPage, outgoingX);
                drawOnboardingPage(canvas, transitionToPage, incomingX);
                return;
            }
            drawOnboardingPage(canvas, page, 0f);
        }

        private void drawOnboardingPage(Canvas canvas, int pageIndex, float offsetX) {
            int restoreCount = canvas.save();
            canvas.translate(offsetX, 0f);
            float width = getWidth();
            float height = getHeight();
            float titleBaseline = topInset + dp(75);

            paint.setShader(null);
            paint.setColor(NAVY);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(android.graphics.Typeface.create("sans-serif-rounded", android.graphics.Typeface.BOLD));
            paint.setTextSize(sp(29));
            drawLines(canvas, titles[pageIndex], width / 2f, titleBaseline, dp(34));

            int titleLines = titles[pageIndex].split("\\n").length;
            float bodyBaseline = titleBaseline + dp(titleLines * 34 + 17);
            paint.setTypeface(android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.NORMAL));
            paint.setTextSize(sp(15));
            paint.setColor(0xE610265F);
            drawLines(canvas, bodies[pageIndex], width / 2f, bodyBaseline, dp(22));

            if (pageIndex == 1) {
                drawConversationTiles(canvas, width, height);
            } else if (pageIndex == 2) {
                drawProgressTile(canvas, width / 2f, height * .49f);
            }

            float buttonHeight = dp(56);
            float buttonBottom = height - bottomInset - dp(20);
            buttonBounds.set(dp(24), buttonBottom - buttonHeight, width - dp(24), buttonBottom);
            RectF buttonShadow = new RectF(buttonBounds);
            buttonShadow.offset(0, dp(5));
            paint.setColor(0x346844B8);
            canvas.drawRoundRect(buttonShadow, buttonHeight / 2f, buttonHeight / 2f, paint);
            paint.setColor(0xF2FAF5FF);
            canvas.drawRoundRect(buttonBounds, buttonHeight / 2f, buttonHeight / 2f, paint);

            paint.setColor(NAVY);
            paint.setTypeface(android.graphics.Typeface.create("sans-serif-rounded", android.graphics.Typeface.BOLD));
            paint.setTextSize(sp(16));
            paint.setTextAlign(Paint.Align.CENTER);
            Paint.FontMetrics buttonMetrics = paint.getFontMetrics();
            float buttonBaseline = buttonBounds.centerY()
                    - (buttonMetrics.ascent + buttonMetrics.descent) / 2f;
            canvas.drawText(buttons[pageIndex], buttonBounds.centerX(), buttonBaseline, paint);

            drawPagerDots(canvas, width / 2f, buttonBounds.top - dp(23), pageIndex);
            canvas.restoreToCount(restoreCount);
        }

        private void drawPagerDots(Canvas canvas, float centerX, float centerY, int pageIndex) {
            float spacing = dp(16);
            for (int i = 0; i < 3; i++) {
                float x = centerX + (i - 1) * spacing;
                if (i == pageIndex) {
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(PURPLE);
                    canvas.drawCircle(x, centerY, dp(4.5f), paint);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(dp(1.5f));
                    paint.setColor(0xBBFFFFFF);
                    canvas.drawCircle(x, centerY, dp(7), paint);
                    paint.setStyle(Paint.Style.FILL);
                } else {
                    paint.setColor(0xB8FFFFFF);
                    canvas.drawCircle(x, centerY, dp(3.5f), paint);
                }
            }
        }

        private void drawConversationTiles(Canvas canvas, float width, float height) {
            drawGlassTile(canvas, width * .30f, height * .47f, dp(64), -8f, 0);
            drawGlassTile(canvas, width * .64f, height * .42f, dp(60), 10f, 1);
            drawGlassTile(canvas, width * .72f, height * .56f, dp(63), -7f, 2);
        }

        private void drawGlassTile(Canvas canvas, float centerX, float centerY,
                                  float size, float rotation, int icon) {
            canvas.save();
            canvas.rotate(rotation, centerX, centerY);
            RectF tile = new RectF(centerX - size / 2f, centerY - size / 2f,
                    centerX + size / 2f, centerY + size / 2f);
            RectF tileShadow = new RectF(tile);
            tileShadow.offset(0, dp(6));
            paint.setColor(0x2F6F4CB4);
            canvas.drawRoundRect(tileShadow, dp(18), dp(18), paint);
            paint.setColor(0xEFFFFFFF);
            canvas.drawRoundRect(tile, dp(18), dp(18), paint);
            paint.setColor(PURPLE);
            paint.setStrokeWidth(dp(3));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);

            if (icon == 0) {
                RectF bubble = new RectF(centerX - size * .22f, centerY - size * .18f,
                        centerX + size * .22f, centerY + size * .13f);
                canvas.drawRoundRect(bubble, dp(7), dp(7), paint);
                canvas.drawLine(centerX - size * .08f, bubble.bottom,
                        centerX - size * .16f, centerY + size * .24f, paint);
                paint.setStyle(Paint.Style.FILL);
                for (int i = -1; i <= 1; i++) {
                    canvas.drawCircle(centerX + i * size * .1f, centerY - size * .025f,
                            dp(2), paint);
                }
            } else if (icon == 1) {
                RectF arc = new RectF(centerX - size * .18f, centerY - size * .20f,
                        centerX + size * .18f, centerY + size * .18f);
                canvas.drawArc(arc, 205, 310, false, paint);
                canvas.drawCircle(centerX + size * .17f, centerY + size * .09f, dp(3), paint);
                canvas.drawLine(centerX - size * .06f, centerY + size * .20f,
                        centerX + size * .09f, centerY + size * .23f, paint);
            } else {
                Path heart = new Path();
                heart.moveTo(centerX, centerY + size * .20f);
                heart.cubicTo(centerX - size * .34f, centerY,
                        centerX - size * .20f, centerY - size * .25f, centerX, centerY - size * .08f);
                heart.cubicTo(centerX + size * .20f, centerY - size * .25f,
                        centerX + size * .34f, centerY, centerX, centerY + size * .20f);
                canvas.drawPath(heart, paint);
            }
            paint.setStyle(Paint.Style.FILL);
            canvas.restore();
        }

        private void drawProgressTile(Canvas canvas, float centerX, float centerY) {
            float size = dp(82);
            RectF tile = new RectF(centerX - size / 2f, centerY - size / 2f,
                    centerX + size / 2f, centerY + size / 2f);
            RectF tileShadow = new RectF(tile);
            tileShadow.offset(0, dp(7));
            paint.setColor(0x356A47B5);
            canvas.drawRoundRect(tileShadow, dp(21), dp(21), paint);
            paint.setColor(0xEFFFFFFF);
            canvas.drawRoundRect(tile, dp(21), dp(21), paint);

            float barWidth = dp(9);
            float gap = dp(7);
            float base = centerY + dp(22);
            float[] heights = {dp(20), dp(34), dp(49)};
            int[] colors = {0xFFB98AF6, 0xFF9A65EE, 0xFF7144D4};
            for (int i = 0; i < 3; i++) {
                float left = centerX + (i - 1) * (barWidth + gap) - barWidth / 2f;
                RectF bar = new RectF(left, base - heights[i], left + barWidth, base);
                paint.setColor(colors[i]);
                canvas.drawRoundRect(bar, barWidth / 2f, barWidth / 2f, paint);
            }
        }

        private void drawLines(Canvas canvas, String text, float centerX,
                               float firstBaseline, float lineHeight) {
            String[] lines = text.split("\\n");
            for (int i = 0; i < lines.length; i++) {
                canvas.drawText(lines[i], centerX, firstBaseline + i * lineHeight, paint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (page < 0) {
                return true;
            }
            if (pageAnimator != null && pageAnimator.isRunning()) {
                return true;
            }
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                touchDownX = event.getX();
                touchDownY = event.getY();
                return true;
            }
            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                float deltaX = event.getX() - touchDownX;
                float deltaY = event.getY() - touchDownY;
                if (Math.abs(deltaX) > dp(48) && Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (deltaX < 0 && page < 2) {
                        setPage(page + 1);
                    } else if (deltaX > 0 && page > 0) {
                        setPage(page - 1);
                    }
                    performClick();
                    return true;
                }
                if (buttonBounds.contains(event.getX(), event.getY())) {
                    performClick();
                    if (page < 2) {
                        setPage(page + 1);
                    } else {
                        completeOnboarding();
                    }
                }
                return true;
            }
            return true;
        }

        @Override
        public boolean performClick() {
            super.performClick();
            return true;
        }

        private void setPage(int newPage) {
            if (newPage < 0 || newPage > 2 || newPage == page) {
                return;
            }
            transitionFromPage = page;
            transitionToPage = newPage;
            transitionDirection = newPage > page ? 1 : -1;
            pageTransitionProgress = 0f;

            pageAnimator = ValueAnimator.ofFloat(0f, 1f);
            pageAnimator.setDuration(320L);
            pageAnimator.setInterpolator(new DecelerateInterpolator());
            pageAnimator.addUpdateListener(animation -> {
                pageTransitionProgress = (float) animation.getAnimatedValue();
                invalidate();
            });
            pageAnimator.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    page = transitionToPage;
                    pageTransitionProgress = 1f;
                    pageAnimator = null;
                    updateAccessibilityDescription();
                    invalidate();
                }
            });
            pageAnimator.start();
        }

        private void completeOnboarding() {
            getContext().getSharedPreferences("safe_space", Context.MODE_PRIVATE)
                    .edit().putBoolean("first_four_complete", true).apply();
            openSignUp.run();
        }

        private void updateAccessibilityDescription() {
            setContentDescription(titles[page].replace('\n', ' ') + ". "
                    + bodies[page].replace('\n', ' ') + ". " + buttons[page] + " button.");
            announceForAccessibility(titles[page].replace('\n', ' '));
        }

        private float dp(float value) {
            return value * density;
        }

        private float sp(float value) {
            return value * scaledDensity;
        }

        @Override
        protected void onDetachedFromWindow() {
            handler.removeCallbacks(revealOnboarding);
            if (progressAnimator != null) {
                progressAnimator.cancel();
            }
            if (pageAnimator != null) {
                pageAnimator.cancel();
            }
            super.onDetachedFromWindow();
        }
    }
}
