package com.safespace.app;

import android.app.Activity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/** Screen 22: a calm counsellor-support directory for the MVP. */
final class CounselorView extends PastelScreenView {
    CounselorView(Activity activity, ScreenNavigator navigator) {
        super(activity, navigator, -1);
        setContentDescription("Counsellor support directory");
        content.addView(header("Counsellors", "Professional support when you want it", true, "", ""),
                marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(74), 0, 0, 0, dp(8)));

        TextView trend = text("Current support band: " + SupportSignalEngine.band(activity)
                        + "\n" + SupportSignalEngine.explanation(activity),
                11.5f, NAVY, true);
        trend.setGravity(Gravity.START);
        trend.setPadding(dp(14), dp(12), dp(14), dp(12));
        trend.setBackground(rounded(0xEFFFFFFF, dp(18), dp(1), 0x22765CE8));
        content.addView(trend, marginParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(10)));

        TextView intro = text("Choose the kind of support you need. Demo profiles are shown for this prototype; a deployed version would use verified professionals.",
                12, MUTED_NAVY, false);
        intro.setGravity(Gravity.CENTER);
        intro.setPadding(dp(10), dp(10), dp(10), dp(10));
        content.addView(intro, marginParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(12)));

        addCounsellor("♟", "General wellbeing counsellor", "Available today • Demo profile", 0xFFDDEEFF, 0xFF397FC5);
        addCounsellor("♡", "Trauma-informed counsellor", "Request an appointment • Demo profile", 0xFFFFE5EF, 0xFFD95788);
        addCounsellor("☼", "Student support counsellor", "School/college stress • Demo profile", 0xFFFFEEDC, 0xFFD98A34);
        addCounsellor("◌", "Family support counsellor", "Guidance for family conversations • Demo profile", 0xFFE3F5E9, 0xFF3A9862);

        LinearLayout urgent = bottomRowCard("☎", 0xFFE9E7FF, PURPLE,
                "Need quicker support?", "Open Support for helplines and trusted contacts", null, "");
        urgent.setOnClickListener(v -> navigator.openScreen(15));
        content.addView(urgent, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(76), 0, dp(6), 0, 0));
    }

    private void addCounsellor(String glyph, String title, String subtitle, int fill, int color) {
        LinearLayout card = bottomRowCard(glyph, fill, color, title, subtitle, "Request", "");
        card.setOnClickListener(v -> {
            SupportSignalEngine.recordAppointment(activity);
            Toast.makeText(activity, "Appointment request saved for the prototype", Toast.LENGTH_SHORT).show();
        });
        content.addView(card, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(78), 0, 0, 0, dp(10)));
    }
}
