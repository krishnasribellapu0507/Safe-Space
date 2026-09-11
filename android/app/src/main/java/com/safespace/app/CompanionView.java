package com.safespace.app;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;

/** Screen ten: a warm, private, on-device prototype conversation. */
final class CompanionView extends FrameLayout {
    private static final int NAVY = Color.rgb(17, 41, 96);
    private static final int MUTED_NAVY = Color.rgb(92, 101, 137);
    private static final int PURPLE = Color.rgb(124, 75, 230);

    private final Activity activity;
    private final ScreenNavigator navigator;
    private final LinearLayout content;
    private final ScrollView chatScroll;
    private final LinearLayout messages;
    private final EditText composer;
    private final LinearLayout bottomBar;
    private final int navigationHeight;
    private boolean replyPending;

    CompanionView(Activity activity, ScreenNavigator navigator) {
        super(activity);
        this.activity = activity;
        this.navigator = navigator;
        navigationHeight = dp(72);

        setBackgroundColor(0xFFF8EEF7);
        setContentDescription("Your Safe Space companion conversation");
        addScenery();

        content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(10), dp(16), navigationHeight + dp(12));
        addView(content, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        content.addView(buildHeader(), new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(66)));

        LotusView lotus = new LotusView(activity);
        lotus.setContentDescription("Glowing lotus on a peaceful lake");
        content.addView(lotus, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(170)));

        chatScroll = new ScrollView(activity);
        chatScroll.setFillViewport(false);
        chatScroll.setClipToPadding(false);
        chatScroll.setVerticalScrollBarEnabled(false);
        chatScroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f);
        scrollParams.bottomMargin = dp(10);
        content.addView(chatScroll, scrollParams);

        messages = new LinearLayout(activity);
        messages.setOrientation(LinearLayout.VERTICAL);
        messages.setPadding(dp(2), dp(2), dp(2), dp(8));
        chatScroll.addView(messages, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        seedConversation();

        LinearLayout composerBar = new LinearLayout(activity);
        composerBar.setOrientation(LinearLayout.HORIZONTAL);
        composerBar.setGravity(Gravity.CENTER_VERTICAL);
        composerBar.setPadding(dp(6), dp(3), dp(5), dp(3));
        composerBar.setElevation(dp(6));
        composerBar.setBackground(rounded(0xF8FFFFFF, dp(28), dp(1), 0x327B4FE9));

        composer = new EditText(activity);
        composer.setHint("Type a message...");
        composer.setHintTextColor(0x90737A98);
        composer.setTextColor(NAVY);
        composer.setTextSize(14);
        composer.setSingleLine(true);
        composer.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        composer.setImeOptions(EditorInfo.IME_ACTION_SEND);
        composer.setPadding(dp(12), 0, dp(8), 0);
        composer.setBackgroundColor(Color.TRANSPARENT);
        composer.setTypeface(Typeface.create("sans-serif-rounded", Typeface.NORMAL));
        composer.setOnEditorActionListener((view, actionId, event) -> {
            boolean keyboardSend = actionId == EditorInfo.IME_ACTION_SEND
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER
                    && event.getAction() == KeyEvent.ACTION_DOWN);
            if (keyboardSend) {
                sendComposerText();
                return true;
            }
            return false;
        });
        composerBar.addView(composer, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));

        TextView send = text("↑", 23, Color.WHITE, true);
        send.setGravity(Gravity.CENTER);
        send.setBackground(purpleCircleRipple());
        send.setContentDescription("Send message");
        send.setOnClickListener(view -> sendComposerText());
        composerBar.addView(send, new LinearLayout.LayoutParams(dp(47), dp(47)));
        content.addView(composerBar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(57)));

        bottomBar = buildBottomNavigation();
        FrameLayout.LayoutParams navParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, navigationHeight);
        navParams.gravity = Gravity.BOTTOM;
        addView(bottomBar, navParams);

        setOnApplyWindowInsetsListener((view, insets) -> applyInsets(insets));
        requestApplyInsets();
    }

    private void addScenery() {
        ImageView scenery = new ImageView(activity);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        options.inSampleSize = 2;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        scenery.setImageBitmap(BitmapFactory.decodeResource(
                getResources(), R.drawable.serenity_background, options));
        scenery.setScaleType(ImageView.ScaleType.CENTER_CROP);
        scenery.setAlpha(.88f);
        scenery.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(scenery, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View wash = new View(activity);
        wash.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0x18FFFFFF, 0x3AEEF4FF, 0xCFF8ECF5, 0xFFF7EDF4}));
        wash.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(wash, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private WindowInsets applyInsets(WindowInsets insets) {
        content.setPadding(dp(16), insets.getSystemWindowInsetTop() + dp(8),
                dp(16), navigationHeight + insets.getSystemWindowInsetBottom() + dp(10));
        FrameLayout.LayoutParams navParams = (FrameLayout.LayoutParams) bottomBar.getLayoutParams();
        navParams.height = navigationHeight + insets.getSystemWindowInsetBottom();
        bottomBar.setPadding(dp(5), 0, dp(5), insets.getSystemWindowInsetBottom());
        bottomBar.setLayoutParams(navParams);
        return insets;
    }

    private View buildHeader() {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView back = text("←", 25, NAVY, false);
        back.setGravity(Gravity.CENTER);
        back.setBackground(circleRipple(0xA8FFFFFF));
        back.setContentDescription("Go back");
        back.setOnClickListener(view -> navigator.goBack());
        row.addView(back, new LinearLayout.LayoutParams(dp(44), dp(44)));

        LinearLayout heading = new LinearLayout(activity);
        heading.setOrientation(LinearLayout.VERTICAL);
        heading.setGravity(Gravity.CENTER);

        TextView title = text("Your Companion", 22, NAVY, true);
        title.setGravity(Gravity.CENTER);
        heading.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView subtitle = text("Always here to listen", 12, MUTED_NAVY, false);
        subtitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subtitleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subtitleParams.topMargin = dp(2);
        heading.addView(subtitle, subtitleParams);
        row.addView(heading, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        BotBadge bot = new BotBadge(activity);
        bot.setContentDescription("Safe Space companion");
        row.addView(bot, new LinearLayout.LayoutParams(dp(44), dp(44)));
        return row;
    }

    private void seedConversation() {
        addBubble("Hi! I’m here for you.  🌸\nYou can talk about anything\non your mind.", false);
        addSuggestion("I’m feeling anxious");
        addSuggestion("I need motivation");
        addSuggestion("Just want to chat");
    }

    private void addSuggestion(String value) {
        TextView chip = text(value, 13, NAVY, false);
        chip.setGravity(Gravity.CENTER_VERTICAL);
        chip.setPadding(dp(15), 0, dp(15), 0);
        chip.setElevation(dp(2));
        chip.setBackground(roundedRipple(0xEFFFFFFF, dp(18), 0x287B4FE9));
        chip.setContentDescription("Send suggestion: " + value);
        chip.setOnClickListener(view -> send(value));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dp(40));
        params.gravity = Gravity.START;
        params.setMargins(dp(13), dp(7), dp(30), 0);
        messages.addView(chip, params);
    }

    private void sendComposerText() {
        String message = composer.getText().toString().trim();
        if (message.isEmpty()) {
            composer.requestFocus();
            return;
        }
        composer.setText("");
        send(message);
    }

    private void send(String message) {
        addBubble(message, true);
        scrollToLatest();
        if (replyPending) {
            return;
        }
        replyPending = true;
        postDelayed(() -> {
            if (!isAttachedToWindow()) {
                return;
            }
            addBubble(companionReply(message), false);
            replyPending = false;
            scrollToLatest();
        }, 420L);
    }

    private String companionReply(String message) {
        String lower = message.toLowerCase(Locale.ROOT);
        if (lower.contains("anxious") || lower.contains("anxiety")
                || lower.contains("worried")) {
            return "I’m with you. Let’s slow it down together—one gentle breath at a time. 🌿";
        }
        if (lower.contains("motivation") || lower.contains("tired")
                || lower.contains("stuck")) {
            return "You don’t need to do everything today. What’s one tiny step that feels possible? ✨";
        }
        if (lower.contains("sad") || lower.contains("alone")) {
            return "Thank you for telling me. You deserve kindness right now, and I’m here to listen. 🌸";
        }
        return "I’m listening. Tell me a little more about what’s on your mind. 💜";
    }

    private void addBubble(String message, boolean fromUser) {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(fromUser ? Gravity.END : Gravity.START);

        TextView bubble = text(message, 14, NAVY, false);
        bubble.setLineSpacing(dp(2), 1f);
        bubble.setPadding(dp(15), dp(11), dp(15), dp(11));
        bubble.setMaxWidth(dp(294));
        bubble.setElevation(dp(2));
        bubble.setBackground(rounded(
                fromUser ? 0xF0E9E1FF : 0xF5FFFFFF,
                dp(18), dp(1), fromUser ? 0x3A7B4FE9 : 0x2AFFFFFF));
        bubble.setContentDescription((fromUser ? "You: " : "Companion: ") + message);

        LinearLayout.LayoutParams bubbleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bubbleParams.setMargins(fromUser ? dp(48) : 0, dp(7),
                fromUser ? 0 : dp(42), 0);
        row.addView(bubble, bubbleParams);
        messages.addView(row, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private void scrollToLatest() {
        chatScroll.post(() -> chatScroll.smoothScrollTo(0, messages.getBottom()));
    }

    private LinearLayout buildBottomNavigation() {
        LinearLayout bar = new LinearLayout(activity);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        bar.setPadding(dp(5), 0, dp(5), 0);
        bar.setElevation(dp(14));
        bar.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{0xFAFFFFFF, 0xFFF3F6FF}));

        bar.addView(navItem("⌂", "Home", 8), navItemParams());
        bar.addView(navItem("☺", "Check-in", 9), navItemParams());
        bar.addView(centerLotus(), navItemParams());
        bar.addView(navItem("◌", "Calm", 12), navItemParams());
        bar.addView(navItem("●", "Profile", 16), navItemParams());
        return bar;
    }

    private LinearLayout.LayoutParams navItemParams() {
        return new LinearLayout.LayoutParams(0, navigationHeight, 1f);
    }

    private View navItem(String icon, String label, int screen) {
        LinearLayout item = new LinearLayout(activity);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(0, dp(5), 0, dp(3));
        item.setBackground(new RippleDrawable(
                ColorStateList.valueOf(0x187B4FE9),
                rounded(Color.TRANSPARENT, dp(12), 0, Color.TRANSPARENT), null));
        item.setContentDescription(label);
        item.setOnClickListener(view -> navigator.openScreen(screen));

        TextView iconView = text(icon, 20, MUTED_NAVY, true);
        iconView.setGravity(Gravity.CENTER);
        item.addView(iconView, new LinearLayout.LayoutParams(dp(32), dp(29)));

        TextView labelView = text(label, 10, MUTED_NAVY, false);
        labelView.setGravity(Gravity.CENTER);
        item.addView(labelView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return item;
    }

    private View centerLotus() {
        LinearLayout item = new LinearLayout(activity);
        item.setGravity(Gravity.CENTER);
        item.setContentDescription("Safe Space home");
        item.setOnClickListener(view -> navigator.openScreen(8));

        ImageView lotus = new ImageView(activity);
        lotus.setImageResource(R.drawable.safe_space_logo);
        lotus.setScaleType(ImageView.ScaleType.CENTER_CROP);
        lotus.setBackground(rounded(Color.WHITE, dp(28), dp(2), 0x66C798FF));
        lotus.setElevation(dp(8));
        item.addView(lotus, new LinearLayout.LayoutParams(dp(56), dp(56)));
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

    private RippleDrawable circleRipple(int fill) {
        return new RippleDrawable(ColorStateList.valueOf(0x277B4FE9),
                rounded(fill, dp(22), dp(1), 0x32FFFFFF), null);
    }

    private RippleDrawable purpleCircleRipple() {
        GradientDrawable circle = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{0xFFA46CF0, 0xFF6946DD});
        circle.setShape(GradientDrawable.OVAL);
        return new RippleDrawable(ColorStateList.valueOf(0x35FFFFFF), circle, null);
    }

    private RippleDrawable roundedRipple(int fill, int radius, int stroke) {
        return new RippleDrawable(ColorStateList.valueOf(0x257B4FE9),
                rounded(fill, radius, dp(1), stroke), null);
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

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private final class BotBadge extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF rect = new RectF();

        BotBadge(Activity activity) {
            super(activity);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(0xDFFFFFFF);
            canvas.drawCircle(cx, cy, dp(20), paint);
            paint.setColor(0x337B4FE9);
            canvas.drawCircle(cx, cy, dp(18), paint);

            paint.setColor(PURPLE);
            paint.setStrokeWidth(dp(2));
            paint.setStyle(Paint.Style.STROKE);
            rect.set(cx - dp(10), cy - dp(7), cx + dp(10), cy + dp(8));
            canvas.drawRoundRect(rect, dp(5), dp(5), paint);
            canvas.drawLine(cx, cy - dp(7), cx, cy - dp(12), paint);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(cx, cy - dp(13), dp(2), paint);
            canvas.drawCircle(cx - dp(4), cy, dp(1.6f), paint);
            canvas.drawCircle(cx + dp(4), cy, dp(1.6f), paint);
        }
    }

    private final class LotusView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path petal = new Path();

        LotusView(Activity activity) {
            super(activity);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float cx = getWidth() / 2f;
            float cy = getHeight() * .57f;

            paint.setStyle(Paint.Style.FILL);
            paint.setShader(null);
            paint.setColor(0x31FFFFFF);
            paint.setShadowLayer(dp(22), 0, 0, 0x99FFFFFF);
            canvas.drawCircle(cx, cy, dp(64), paint);
            paint.clearShadowLayer();

            drawPetal(canvas, cx, cy, dp(39), dp(66), 0,
                    0xFFE9B8FF, 0xFF8D6BED);
            drawPetal(canvas, cx, cy + dp(7), dp(36), dp(61), -43,
                    0xFFF6B4DB, 0xFF9972EF);
            drawPetal(canvas, cx, cy + dp(7), dp(36), dp(61), 43,
                    0xFFC9C1FF, 0xFFE58ACC);
            drawPetal(canvas, cx, cy + dp(16), dp(32), dp(53), -72,
                    0xFFB9CFFF, 0xFF845FE4);
            drawPetal(canvas, cx, cy + dp(16), dp(32), dp(53), 72,
                    0xFFFFB9D7, 0xFFA671EE);

            paint.setShader(new LinearGradient(cx, cy - dp(8), cx, cy + dp(42),
                    0xFFFFECFB, 0xFFB16BE8, Shader.TileMode.CLAMP));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawOval(new RectF(cx - dp(28), cy + dp(17),
                    cx + dp(28), cy + dp(43)), paint);
            paint.setShader(null);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setColor(0x9AFFFFFF);
            canvas.drawOval(new RectF(cx - dp(77), cy + dp(43),
                    cx + dp(77), cy + dp(58)), paint);
            paint.setColor(0x55FFFFFF);
            canvas.drawOval(new RectF(cx - dp(102), cy + dp(50),
                    cx + dp(102), cy + dp(69)), paint);
        }

        private void drawPetal(Canvas canvas, float cx, float baseY,
                               float width, float height, float rotation,
                               int topColor, int bottomColor) {
            canvas.save();
            canvas.rotate(rotation, cx, baseY + dp(24));
            petal.reset();
            petal.moveTo(cx, baseY - height);
            petal.cubicTo(cx + width, baseY - height * .36f,
                    cx + width * .78f, baseY + height * .15f,
                    cx, baseY + height * .34f);
            petal.cubicTo(cx - width * .78f, baseY + height * .15f,
                    cx - width, baseY - height * .36f,
                    cx, baseY - height);
            petal.close();
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(new LinearGradient(cx, baseY - height, cx,
                    baseY + height * .34f, topColor, bottomColor,
                    Shader.TileMode.CLAMP));
            canvas.drawPath(petal, paint);
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1));
            paint.setColor(0x75FFFFFF);
            canvas.drawPath(petal, paint);
            canvas.restore();
        }
    }
}
