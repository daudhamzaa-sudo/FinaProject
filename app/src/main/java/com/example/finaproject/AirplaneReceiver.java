package com.example.finaproject;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AirplaneReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        boolean isOn = intent.getBooleanExtra("state", false);

        Intent i = new Intent("AIRPLANE_MODE_CHANGED");
        i.putExtra("state", isOn);

        context.sendBroadcast(i);
    }
}