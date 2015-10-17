package com.brianattwell.noise;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

public class LauncherActivity extends AppCompatActivity {

    @Bind(R.id.noise_later_button) Button laterButton;
    @Bind(R.id.noise_now_button) Button noiseNowButton;
    @Bind(R.id.delay_textView) TextView delayTextView;
    @Bind(R.id.noise_cancel_button) Button cancelButton;

    private static final int ALARM_REQUEST_CODE = 1;
    private static final String TIME_PREF_KEY = "time";

    private SharedPreferences mSharedPref;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);
        mSharedPref = getPreferences(Context.MODE_PRIVATE);

        // If the app has been restarted, the alarm manager's scheduled intent will have been erased. Clear
        // shared preferences in this case to keep the UI consistent with the state of the alarm manager.
        boolean alarmStored = (PendingIntent.getBroadcast(
            this, ALARM_REQUEST_CODE, getAlarmIntent(), PendingIntent.FLAG_NO_CREATE) != null);
        if (!alarmStored && !NoiseService.isRunning()) {
            mSharedPref.edit().clear().apply();
        }

        bindSharedPreferencesToView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                bindSharedPreferencesToView();
                mHandler.postDelayed(mRunnable, 1000);
            }
        };
        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeCallbacks(mRunnable);
        mRunnable = null;
    }

    @OnClick(R.id.noise_later_button)
    void onClickNoiseLater() {
        Intent alarmIntent = getAlarmIntent();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(LauncherActivity.this,
            ALARM_REQUEST_CODE, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long triggerAtMillis = System.currentTimeMillis() + NoiseConstants.NOISE_START_DELAY;
        manager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        mSharedPref.edit().putLong(TIME_PREF_KEY, triggerAtMillis).apply();
        bindSharedPreferencesToView();
    }

    @OnClick(R.id.noise_cancel_button)
    void onClickCancelNoise() {
        NoiseService.stopRunning();
        mSharedPref.edit().clear().apply();
        bindSharedPreferencesToView();
    }

    @OnClick(R.id.noise_now_button)
    void onClickNoiseNow() {
        Intent intent = new Intent(this, NoiseService.class);
        intent.putExtra(NoiseService.START_VOLUME_ARGS, 1.0);
        startService(intent);
        mSharedPref.edit().putLong(TIME_PREF_KEY, System.currentTimeMillis()).apply();
        bindSharedPreferencesToView();
    }

    @NonNull
    private Intent getAlarmIntent() {
        return new Intent(LauncherActivity.this, AlarmReceiver.class);
    }

    private void bindSharedPreferencesToView() {
        long time = mSharedPref.getLong("time", 0);
        if (time != 0) {
            laterButton.setVisibility(View.GONE);
            noiseNowButton.setVisibility(View.GONE);
            cancelButton.setVisibility(View.VISIBLE);
            delayTextView.setVisibility(View.VISIBLE);
            long seconds = (time - System.currentTimeMillis()) / 1000;
            if (seconds <= 0) {
                delayTextView.setText(R.string.noise_has_started);
            } else {
                String duration = String.format("Noise will start in %d hours, %d minutes, %d seconds.",
                    seconds / 3600, (seconds % 3600) / 60, (seconds % 60));
                delayTextView.setText(duration);
            }

        } else {
            laterButton.setVisibility(View.VISIBLE);
            noiseNowButton.setVisibility(View.VISIBLE);
            cancelButton.setVisibility(View.GONE);
            delayTextView.setVisibility(View.GONE);
        }
    }
}
