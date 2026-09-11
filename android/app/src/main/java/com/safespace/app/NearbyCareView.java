package com.safespace.app;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/** Opens live nearby mental-health care listings without inventing doctors or fees. */
final class NearbyCareView extends PastelScreenView {
    NearbyCareView(Activity activity, ScreenNavigator navigator) {
        super(activity, navigator, -1);
        content.addView(header("Nearby mental-health care", "Live Maps results near you", true, "", ""),
                marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(68), 0, 0, 0, dp(10)));

        TextView info = text("Choose what you need. Safe Space opens current map listings so phone numbers, hours, reviews and directions stay up to date.", 12, MUTED_NAVY, false);
        info.setPadding(dp(13), dp(12), dp(13), dp(12));
        info.setBackground(rounded(0xEFFFFFFF, dp(16), dp(1), 0x227B5CE6));
        content.addView(info, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0, 0, 0, dp(12)));

        addSearch("Ψ", "Psychiatrists nearby", "Hospitals and clinics with psychiatry services", "psychiatrist hospital near me");
        addSearch("♡", "Psychologists nearby", "Psychologists and counselling centres", "psychologist counselling near me");
        addSearch("✚", "Mental-health hospitals", "Hospitals with mental-health departments", "mental health hospital near me");
        addSearch("⌂", "Government hospitals", "Government mental-health and psychiatry services", "government psychiatry hospital near me");

        TextView fee = text("Doctor availability and consultation fees are not consistently published by Maps. When a hospital publishes them, open its listing/website from Maps. Safe Space will not guess medical fees or doctor availability.", 11, MUTED_NAVY, false);
        fee.setLineSpacing(dp(2), 1f);
        content.addView(fee, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, dp(4), dp(8), dp(4), 0));
    }

    private void addSearch(String icon, String title, String subtitle, String query) {
        LinearLayout card = bottomRowCard(icon, 0xFFE4F3FF, 0xFF347EC3, title, subtitle, "Map", "");
        card.setOnClickListener(v -> openMaps(query));
        content.addView(card, marginParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(78), 0, 0, 0, dp(10)));
    }

    private void openMaps(String query) {
        Uri geo = Uri.parse("geo:0,0?q=" + Uri.encode(query));
        Intent maps = new Intent(Intent.ACTION_VIEW, geo);
        maps.setPackage("com.google.android.apps.maps");
        try {
            activity.startActivity(maps);
        } catch (ActivityNotFoundException e) {
            String q = URLEncoder.encode(query, StandardCharsets.UTF_8);
            activity.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/search/?api=1&query=" + q)));
        }
    }
}
