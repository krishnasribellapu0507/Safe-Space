package com.safespace.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Build;
import android.os.IBinder;

import java.util.Random;

/** Foreground ambient-audio service with notification controls. */
public final class CalmAudioService extends Service {
    static final String ACTION_PLAY = "com.safespace.app.PLAY_CALM";
    static final String ACTION_STOP = "com.safespace.app.STOP_CALM";
    static final String EXTRA_TRACK = "track";
    private static final String CHANNEL = "calm_audio";
    private static final int NOTIFICATION_ID = 44;

    private volatile boolean playing;
    private Thread audioThread;
    private AudioTrack audioTrack;

    @Override public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(CHANNEL,
                    "Calming sounds", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Background playback for Safe Space calming sounds");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;
        if (ACTION_STOP.equals(intent.getAction())) {
            stopPlayback();
            return START_NOT_STICKY;
        }
        if (ACTION_PLAY.equals(intent.getAction())) {
            int track = Math.max(0, Math.min(3, intent.getIntExtra(EXTRA_TRACK, 0)));
            String[] names = {"Gentle Rain", "Ocean Waves", "Forest Birds", "Soft Night"};
            startForeground(NOTIFICATION_ID, notification(names[track]));
            startAmbient(track);
        }
        return START_STICKY;
    }

    private void startAmbient(int mode) {
        stopAudioThread();
        playing = true;
        audioThread = new Thread(() -> {
            final int rate = 22050;
            int min = AudioTrack.getMinBufferSize(rate,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
            int size = Math.max(min, 4096);
            audioTrack = new AudioTrack.Builder()
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                    .setAudioFormat(new AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(rate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO).build())
                    .setBufferSizeInBytes(size)
                    .setTransferMode(AudioTrack.MODE_STREAM).build();
            audioTrack.setVolume(0.18f);
            audioTrack.play();
            Random random = new Random();
            short[] samples = new short[size / 2];
            double smooth = 0;
            double phase = 0;
            while (playing) {
                for (int i = 0; i < samples.length; i++) {
                    double white = random.nextDouble() * 2.0 - 1.0;
                    smooth = smooth * (mode == 0 ? 0.82 : mode == 1 ? 0.965 : mode == 2 ? 0.74 : 0.985)
                            + white * (mode == 0 ? 0.18 : mode == 1 ? 0.035 : mode == 2 ? 0.26 : 0.015);
                    phase += (mode == 2 ? 0.012 : mode == 1 ? 0.004 : 0.002);
                    double wave = mode == 1 ? Math.sin(phase) * 0.28 : mode == 2 ? Math.sin(phase * 4.0) * 0.08 : 0;
                    double value = Math.max(-1, Math.min(1, smooth + wave));
                    samples[i] = (short) (value * 9000);
                }
                AudioTrack t = audioTrack;
                if (t != null) t.write(samples, 0, samples.length);
            }
            AudioTrack t = audioTrack;
            if (t != null) {
                try { t.stop(); } catch (Exception ignored) {}
                t.release();
                audioTrack = null;
            }
        }, "SafeSpaceAmbientAudio");
        audioThread.start();
    }

    private Notification notification(String name) {
        Intent open = new Intent(this, MainActivity.class);
        PendingIntent openPi = PendingIntent.getActivity(this, 10, open,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Intent stop = new Intent(this, CalmAudioService.class).setAction(ACTION_STOP);
        PendingIntent stopPi = PendingIntent.getService(this, 11, stop,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification.Builder b = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(this, CHANNEL) : new Notification.Builder(this);
        return b.setSmallIcon(R.drawable.safe_space_logo)
                .setContentTitle("Safe Space • " + name)
                .setContentText("Calming sound is playing")
                .setContentIntent(openPi)
                .setOngoing(true)
                .addAction(new Notification.Action.Builder(null, "Stop", stopPi).build())
                .build();
    }

    private void stopAudioThread() {
        playing = false;
        Thread t = audioThread;
        audioThread = null;
        if (t != null) t.interrupt();
    }

    private void stopPlayback() {
        stopAudioThread();
        stopForeground(true);
        stopSelf();
    }

    @Override public void onDestroy() {
        stopAudioThread();
        super.onDestroy();
    }

    @Override public IBinder onBind(Intent intent) { return null; }
}
