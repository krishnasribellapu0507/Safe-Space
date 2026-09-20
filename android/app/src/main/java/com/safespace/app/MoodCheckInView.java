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
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

/** Screen nine: a calm, two-row mood check-in. */
final class MoodCheckInView extends FrameLayout {
    private static final int NAVY = Color.rgb(17, 41, 96);
    private static final int PURPLE = Color.rgb(124, 75, 230);
    private static final int MUTED = Color.rgb(101, 108, 139);

    private final Activity activity;
    private final ScreenNavigator navigator;
    private final ScrollView scrollView;
    private final MoodOption[] moodOptions = new MoodOption[6];
    private String selectedMood = "Happy";

    MoodCheckInView(Activity activity, ScreenNavigator navigator) {
        super(activity);
        this.activity = activity;
        this.navigator = navigator;

        setBackgroundColor(0xFFF7F1FA);
        setContentDescription("Mood check-in screen");
        addView(new MoodBackdrop(activity), new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        scrollView = new ScrollView(activity);
        scrollView.setFillViewport(true);
        scrollView.setClipToPadding(false);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        addView(scrollView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        buildContent();
        setOnApplyWindowInsetsListener((view, insets) -> applyInsets(insets));
        requestApplyInsets();
    }

    private WindowInsets applyInsets(WindowInsets insets) {
        scrollView.setPadding(0, insets.getSystemWindowInsetTop(), 0,
                insets.getSystemWindowInsetBottom());
        return insets;
    }

    private void buildContent() {
        LinearLayout content = new LinearLayout(activity);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(dp(20), dp(12), dp(20), dp(22));
        scrollView.addView(content, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        TextView back = text("←", 25, NAVY, false);
        back.setGravity(Gravity.CENTER);
        back.setBackground(circleRipple(0xA8FFFFFF));
        back.setContentDescription("Go back");
        back.setOnClickListener(view -> navigator.goBack());
        LinearLayout.LayoutParams backParams = new LinearLayout.LayoutParams(dp(44), dp(44));
        backParams.gravity = Gravity.START;
        content.addView(back, backParams);

        TextView title = text("How are you feeling\ntoday?", 25, NAVY, true);
        title.setGravity(Gravity.CENTER);
        title.setLineSpacing(dp(2), 1f);
        content.addView(title, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                0, dp(3), 0, dp(23)));

        LinearLayout firstRow = moodRow();
        moodOptions[0] = mood("Happy", 0, 0xFFA9E7BD, 0xFF248657);
        moodOptions[1] = mood("Calm", 1, 0xFFAED9FA, 0xFF3273B8);
        moodOptions[2] = mood("Okay", 2, 0xFFFFD5A1, 0xFFD66A24);
        firstRow.addView(moodOptions[0], moodParams());
        firstRow.addView(moodOptions[1], moodParams());
        firstRow.addView(moodOptions[2], moodParams());
        content.addView(firstRow, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(103)));

        LinearLayout secondRow = moodRow();
        moodOptions[3] = mood("Sad", 3, 0xFFC5B3FA, 0xFF6250CB);
        moodOptions[4] = mood("Stressed", 4, 0xFFF5AFCC, 0xFFC54278);
        moodOptions[5] = mood("Angry", 5, 0xFFFFCAA0, 0xFFD85A2E);
        secondRow.addView(moodOptions[3], moodParams());
        secondRow.addView(moodOptions[4], moodParams());
        secondRow.addView(moodOptions[5], moodParams());
        content.addView(secondRow, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(103), 0, dp(2), 0, dp(20)));

        LinearLayout noteCard = new LinearLayout(activity);
        noteCard.setOrientation(LinearLayout.VERTICAL);
        noteCard.setPadding(dp(16), dp(13), dp(16), dp(10));
        noteCard.setElevation(dp(2));
        noteCard.setBackground(rounded(0xEFFFFFFF, dp(17), dp(1), 0x25A57BCE));

        TextView noteLabel = text("Add a note (optional)", 13, NAVY, true);
        noteLabel.setGravity(Gravity.START);
        noteCard.addView(noteLabel, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        EditText note = new EditText(activity);
        note.setTextSize(14);
        note.setTextColor(NAVY);
        note.setHintTextColor(0x89737A98);
        note.setHint("Type here...");
        note.setGravity(Gravity.TOP | Gravity.START);
        note.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        note.setSingleLine(false);
        note.setMaxLines(4);
        note.setPadding(0, dp(9), 0, 0);
        note.setBackgroundColor(Color.TRANSPARENT);
        note.setTypeface(Typeface.create("sans-serif-rounded", Typeface.NORMAL));
        noteCard.addView(note, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(82)));
        content.addView(noteCard, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(128)));

        Space flexibleSpace = new Space(activity);
        LinearLayout.LayoutParams spaceParams = new LinearLayout.LayoutParams(dp(1), 0, 1f);
        spaceParams.topMargin = dp(26);
        content.addView(flexibleSpace, spaceParams);

        TextView save = text("Save", 16, Color.WHITE, true);
        save.setGravity(Gravity.CENTER);
        save.setElevation(dp(5));
        save.setBackground(purpleRipple());
        save.setContentDescription("Save " + selectedMood + " mood check-in");
        save.setOnClickListener(view -> {
            SupportSignalEngine.recordMood(activity, selectedMood, note.getText().toString());
            new android.app.AlertDialog.Builder(activity)
                    .setTitle("Thanks for checking in.")
                    .setMessage("Want to add a few optional details about sleep, stress, energy, safety and connection?")
                    .setPositiveButton("Add details", (dialog, which) -> navigator.openScreen(29))
                    .setNegativeButton("Done", (dialog, which) -> navigator.openScreen(21))
                    .show();
        });
        content.addView(save, marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(56), 0, dp(20), 0, 0));

        refreshMoods();
    }

    private LinearLayout moodRow() {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        return row;
    }

    private LinearLayout.LayoutParams moodParams() {
        return new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
    }

    private MoodOption mood(String label, int expression, int fill, int ink) {
        MoodOption option = new MoodOption(label, expression, fill, ink);
        option.setOnClickListener(view -> {
            selectedMood = label;
            refreshMoods();
            announceForAccessibility(label + " selected");
        });
        return option;
    }

    private void refreshMoods() {
        for (MoodOption option : moodOptions) {
            if (option != null) {
                option.setSelectedMood(option.label.equals(selectedMood));
            }
        }
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
        GradientDrawable content = rounded(fill, dp(22), dp(1), 0x32FFFFFF);
        return new RippleDrawable(ColorStateList.valueOf(0x277B4FE9), content, null);
    }

    private RippleDrawable purpleRipple() {
        GradientDrawable content = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{0xFF9B68EF, 0xFF7345DF});
        content.setCornerRadius(dp(28));
        return new RippleDrawable(ColorStateList.valueOf(0x35FFFFFF), content, null);
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

    private LinearLayout.LayoutParams marginParams(int width, int height,
                                                    int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(left, top, right, bottom);
        return params;
    }

    private int dp(float value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private final class MoodOption extends LinearLayout {
        private final String label;
        private final MoodFace face;
        private final TextView caption;

        MoodOption(String label, int expression, int fill, int ink) {
            super(activity);
            this.label = label;
            setOrientation(VERTICAL);
            setGravity(Gravity.CENTER);
            setPadding(dp(4), dp(2), dp(4), dp(4));
            setClickable(true);
            setFocusable(true);

            face = new MoodFace(activity, expression, fill, ink);
            addView(face, new LinearLayout.LayoutParams(dp(58), dp(58)));

            caption = text(label, 12, NAVY, true);
            caption.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams captionParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            captionParams.topMargin = dp(6);
            addView(caption, captionParams);
        }

        void setSelectedMood(boolean selected) {
            face.setChosen(selected);
            caption.setTextColor(selected ? PURPLE : NAVY);
            setContentDescription(label + (selected ? ", selected" : " mood"));
        }
    }

    private final class MoodFace extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final RectF mouth = new RectF();
        private final int expression;
        private final int fill;
        private final int ink;
        private boolean chosen;

        MoodFace(Activity activity, int expression, int fill, int ink) {
            super(activity);
            this.expression = expression;
            this.fill = fill;
            this.ink = ink;
            setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        }

        void setChosen(boolean chosen) {
            this.chosen = chosen;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;
            float radius = Math.min(cx, cy) - dp(4);

            if (chosen) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(0x38FFFFFF);
                canvas.drawCircle(cx, cy, radius + dp(4), paint);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(2));
                paint.setColor(0xB07B4FE9);
                canvas.drawCircle(cx, cy, radius + dp(3), paint);
            }

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(fill);
            canvas.drawCircle(cx, cy, radius, paint);
            paint.setColor(ink);
            canvas.drawCircle(cx - radius * .35f, cy - radius * .18f, dp(2.2f), paint);
            canvas.drawCircle(cx + radius * .35f, cy - radius * .18f, dp(2.2f), paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2.2f));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(ink);
            if (expression == 0) {
                mouth.set(cx - radius * .36f, cy - radius * .04f,
                        cx + radius * .36f, cy + radius * .42f);
                canvas.drawArc(mouth, 12, 156, false, paint);
            } else if (expression == 1 || expression == 2) {
                float offset = expression == 1 ? 0 : dp(1);
                canvas.drawLine(cx - radius * .30f, cy + radius * .25f + offset,
                        cx + radius * .30f, cy + radius * .25f + offset, paint);
            } else {
                mouth.set(cx - radius * .34f, cy + radius * .15f,
                        cx + radius * .34f, cy + radius * .52f);
                canvas.drawArc(mouth, 198, 144, false, paint);
                if (expression == 4) {
                    canvas.drawLine(cx - radius * .44f, cy - radius * .35f,
                            cx - radius * .20f, cy - radius * .29f, paint);
                    canvas.drawLine(cx + radius * .20f, cy - radius * .29f,
                            cx + radius * .44f, cy - radius * .35f, paint);
                } else if (expression == 5) {
                    canvas.drawLine(cx - radius * .47f, cy - radius * .36f,
                            cx - radius * .18f, cy - radius * .25f, paint);
                    canvas.drawLine(cx + radius * .18f, cy - radius * .25f,
                            cx + radius * .47f, cy - radius * .36f, paint);
                }
            }
            paint.setStyle(Paint.Style.FILL);
        }
    }

    private final class MoodBackdrop extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path petal = new Path();

        MoodBackdrop(Activity activity) {
            super(activity);
            setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float width = getWidth();
            float height = getHeight();
            paint.setShader(new LinearGradient(0, 0, 0, height,
                    new int[]{0xFFE6F3FF, 0xFFF5EEFF, 0xFFFFEEF3},
                    new float[]{0f, .52f, 1f}, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);

            paint.setColor(0x4CFFFFFF);
            canvas.drawCircle(width * .11f, height * .17f, dp(78), paint);
            canvas.drawCircle(width * .95f, height * .46f, dp(105), paint);
            canvas.drawCircle(width * .08f, height * .90f, dp(88), paint);

            drawPetal(canvas, width * .89f, height * .12f, dp(14), -28, 0x42F583BD);
            drawPetal(canvas, width * .08f, height * .67f, dp(11), 32, 0x42A281F3);
            drawPetal(canvas, width * .91f, height * .86f, dp(15), -42, 0x3CF583BD);
        }

        private void drawPetal(Canvas canvas, float x, float y, float size,
                               float rotation, int color) {
            canvas.save();
            canvas.rotate(rotation, x, y);
            petal.reset();
            petal.moveTo(x, y - size);
            petal.cubicTo(x + size, y - size * .25f, x + size * .72f,
                    y + size * .7f, x, y + size);
            petal.cubicTo(x - size * .72f, y + size * .7f, x - size,
                    y - size * .25f, x, y - size);
            petal.close();
            paint.setColor(color);
            canvas.drawPath(petal, paint);
            canvas.restore();
        }
    }
}
