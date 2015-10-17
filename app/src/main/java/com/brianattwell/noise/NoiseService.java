package com.brianattwell.noise;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

public class NoiseService extends Service {
    public static final String START_VOLUME_ARGS = "start_volume";
    private static final int NOTIFICATION_ID = 1;
    private static NoiseService mInstance;
    private volatile boolean mIsRunning;

    public int onStartCommand(final Intent intent, int flags, int startId) {
        mInstance = this;
        mIsRunning = true;
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                final long startTime = System.currentTimeMillis();
                NoiseGenerator generator = new NoiseGenerator();
                double amplitude = intent.getDoubleExtra(START_VOLUME_ARGS, NoiseConstants.MINIMUM_AMPLITUDE);
                while (mIsRunning) {
                    amplitude += Math.min((System.currentTimeMillis() - startTime)
                        / NoiseConstants.AMPLITUDE_RAMP_DURATION_MS, 1);
                    showNotification((int) (amplitude * 100));
                    generator.playSound(amplitude);
                }
                stopForeground(true);
                return null;
            }
        }.execute();
        return 0;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Nothing needs to bind to this service. The service runs in the main process.
        return null;
    }

    public static void stopRunning() {
        if (mInstance != null) {
            mInstance.mIsRunning = false;
        }
    }

    public static boolean isRunning() {
        return mInstance != null && mInstance.mIsRunning;
    }

    private void showNotification(int percent) {
        // Run this service as a foreground service. This service is almost
        // guaranteed not to be killed by the system. We don't even need a wakelock :)
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
            new Intent(getApplicationContext(), LauncherActivity.class),
            PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
            new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(getString(R.string.noise_playing))
                .setContentText(String.format("Volume at %d%%", percent))
                .setContentIntent(pi);
        startForeground(NOTIFICATION_ID, builder.build());
    }
}