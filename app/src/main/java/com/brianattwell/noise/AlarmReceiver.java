package com.brianattwell.noise;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
        context.startService(new Intent(context, NoiseService.class));
    }
}
