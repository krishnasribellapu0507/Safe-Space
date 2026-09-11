package com.safespace.app;

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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Screen 16: personal profile hub matching the soft card-based reference. */
final class ProfileView extends PastelScreenView {
    ProfileView(Activity activity, ScreenNavigator navigator) {
        super(activity, navigator, 16);
        setContentDescription("Alex's profile screen");

        content.addView(header("", "", true, "", ""), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(47), 0, 0, 0, 0));
        content.addView(buildProfileHero(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(137), 0, 0, 0, dp(12)));
        content.addView(buildSettingsCard(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,
                0, 0, 0, dp(13)));
        content.addView(buildLogout(), marginParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(53), 0, 0, 0, dp(7)));
    }

    private View buildProfileHero() {
        LinearLayout hero = new LinearLayout(activity);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

        FrameLayout avatarShell = new FrameLayout(activity);
        avatarShell.setElevation(dp(5));
        AvatarView avatar = new AvatarView(activity);
        avatar.setContentDescription("Alex's avatar");
        avatarShell.addView(avatar, new FrameLayout.LayoutParams(dp(82), dp(82)));

        TextView editBadge = text("✎", 13, Color.WHITE, true);
        editBadge.setGravity(Gravity.CENTER);
        editBadge.setBackground(gradientRounded(0xFF9A6BEA, 0xFF7354DB,
                dp(13), dp(2), Color.WHITE));
        FrameLayout.LayoutParams badgeParams = new FrameLayout.LayoutParams(dp(27), dp(27));
        badgeParams.gravity = Gravity.BOTTOM | Gravity.END;
        avatarShell.addView(editBadge, badgeParams);
        avatarShell.setOnClickListener(view -> toast("Edit profile is a prototype"));
        hero.addView(avatarShell, new LinearLayout.LayoutParams(dp(85), dp(85)));

        TextView name = text("Alex", 22, NAVY, true);
        name.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        nameParams.topMargin = dp(6);
        hero.addView(name, nameParams);

        TextView motto = text("Growing every day  ✓", 11, MUTED_NAVY, false);
        motto.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams mottoParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mottoParams.topMargin = dp(2);
        hero.addView(motto, mottoParams);
        return hero;
    }

    private View buildSettingsCard() {
        LinearLayout card = glassCard();
        card.setPadding(dp(5), dp(3), dp(5), dp(3));
        card.addView(settingRow("✎", 0xFFE9E2FF, 0xFF7852D6,
                "Edit Profile", null, () -> toast("Edit profile is a prototype")),
                rowParams());
        card.addView(divider());
        card.addView(settingRow("♡", 0xFFFFE5F0, 0xFFDB5E91,
                "My Goals", null, () -> toast("Your goals are a prototype")),
                rowParams());
        card.addView(divider());
        card.addView(settingRow("♧", 0xFFE4F1FF, 0xFF4783CF,
                "Reminders", null, () -> navigator.openScreen(17)), rowParams());
        card.addView(divider());
        card.addView(settingRow("◎", 0xFFE4F5ED, 0xFF3D9A6B,
                "Language", "English", () -> toast("Language selection is a prototype")),
                rowParams());
        card.addView(divider());
        card.addView(settingRow("⚙", 0xFFFFEDDF, 0xFFDA8148,
                "App Settings", null, () -> navigator.openScreen(19)), rowParams());
        card.addView(divider());
        card.addView(settingRow("?", 0xFFE9EAFF, 0xFF6271C8,
                "Help & Feedback", null, () -> toast("Help and feedback is a prototype")),
                rowParams());
        return card;
    }

    private LinearLayout.LayoutParams rowParams() {
        return new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(51));
    }

    private View divider() {
        View divider = new View(activity);
        divider.setBackgroundColor(0x147B5CE6);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(1));
        params.setMarginStart(dp(54));
        divider.setLayoutParams(params);
        return divider;
    }

    private View settingRow(String glyph, int fill, int color, String label,
                            String value, Runnable action) {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(9), dp(4), dp(9), dp(4));
        row.setBackground(rippleRounded(Color.TRANSPARENT, dp(15), Color.TRANSPARENT));
        row.setContentDescription(value == null ? label : label + ", " + value);
        row.setOnClickListener(view -> action.run());

        TextView icon = text(glyph, 17, color, true);
        icon.setGravity(Gravity.CENTER);
        icon.setBackground(rounded(fill, dp(11), 0, Color.TRANSPARENT));
        row.addView(icon, new LinearLayout.LayoutParams(dp(37), dp(37)));

        TextView title = text(label, 13, NAVY, true);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        titleParams.setMarginStart(dp(11));
        row.addView(title, titleParams);

        if (value != null) {
            TextView valueView = text(value, 10.5f, MUTED_NAVY, false);
            valueView.setGravity(Gravity.END);
            row.addView(valueView, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        TextView arrow = text("›", 24, 0xFF858DA7, false);
        arrow.setGravity(Gravity.CENTER);
        row.addView(arrow, new LinearLayout.LayoutParams(dp(25), dp(35)));
        return row;
    }

    private View buildLogout() {
        LinearLayout logout = new LinearLayout(activity);
        logout.setOrientation(LinearLayout.HORIZONTAL);
        logout.setGravity(Gravity.CENTER_VERTICAL);
        logout.setPadding(dp(18), 0, dp(15), 0);
        logout.setElevation(dp(2));
        logout.setBackground(rippleRounded(0xF5FFFFFF, dp(18), 0x25EC6990));
        logout.setContentDescription("Log out");
        logout.setOnClickListener(view -> toast("Log out is disabled in this prototype"));

        TextView icon = text("⇥", 20, 0xFFE14E78, true);
        icon.setGravity(Gravity.CENTER);
        logout.addView(icon, new LinearLayout.LayoutParams(dp(34), dp(34)));
        TextView label = text("Log Out", 13, 0xFFD94370, true);
        LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        labelParams.setMarginStart(dp(8));
        logout.addView(label, labelParams);
        return logout;
    }

    /** Small illustrated portrait, kept vector-like and sharp without another bitmap asset. */
    private final class AvatarView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path hair = new Path();
        private final RectF oval = new RectF();

        AvatarView(Activity activity) {
            super(activity);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth();
            float h = getHeight();
            float cx = w / 2f;

            paint.setShader(new LinearGradient(0, 0, w, h,
                    new int[]{0xFFFFD3DF, 0xFFC8B1FF, 0xFF96C8FF},
                    null, Shader.TileMode.CLAMP));
            canvas.drawCircle(cx, h / 2f, w * .49f, paint);
            paint.setShader(null);

            // Shoulders.
            paint.setColor(0xFF7050C7);
            oval.set(w * .20f, h * .69f, w * .80f, h * 1.08f);
            canvas.drawOval(oval, paint);

            // Hair silhouette.
            paint.setColor(0xFF3A223A);
            hair.reset();
            hair.moveTo(w * .25f, h * .70f);
            hair.cubicTo(w * .14f, h * .26f, w * .42f, h * .08f, w * .70f, h * .22f);
            hair.cubicTo(w * .90f, h * .36f, w * .83f, h * .72f, w * .75f, h * .82f);
            hair.lineTo(w * .25f, h * .82f);
            hair.close();
            canvas.drawPath(hair, paint);

            // Face and ears.
            paint.setColor(0xFFFFD0B8);
            oval.set(w * .31f, h * .25f, w * .70f, h * .72f);
            canvas.drawOval(oval, paint);
            canvas.drawCircle(w * .31f, h * .50f, w * .045f, paint);
            canvas.drawCircle(w * .70f, h * .50f, w * .045f, paint);

            // Hair cap.
            paint.setColor(0xFF42243E);
            hair.reset();
            hair.moveTo(w * .30f, h * .43f);
            hair.cubicTo(w * .31f, h * .19f, w * .63f, h * .13f, w * .73f, h * .36f);
            hair.cubicTo(w * .58f, h * .31f, w * .48f, h * .33f, w * .30f, h * .43f);
            hair.close();
            canvas.drawPath(hair, paint);

            // Eyes and smile.
            paint.setColor(0xFF38283B);
            canvas.drawCircle(w * .42f, h * .49f, dp(1.5f), paint);
            canvas.drawCircle(w * .59f, h * .49f, dp(1.5f), paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(1.5f));
            paint.setStrokeCap(Paint.Cap.ROUND);
            oval.set(w * .44f, h * .51f, w * .58f, h * .64f);
            canvas.drawArc(oval, 20, 140, false, paint);
            paint.setStyle(Paint.Style.FILL);

            paint.setColor(0xFFFF83A4);
            canvas.drawCircle(w * .36f, h * .57f, dp(2.3f), paint);
            canvas.drawCircle(w * .65f, h * .57f, dp(2.3f), paint);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setColor(Color.WHITE);
            canvas.drawCircle(cx, h / 2f, w * .47f, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }
}
