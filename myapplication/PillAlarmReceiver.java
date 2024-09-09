package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class PillAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String pillName = intent.getStringExtra("pillName");
        String time = intent.getStringExtra("time");

        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.putExtra("pillName", pillName);
        alarmIntent.putExtra("time", time);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(alarmIntent);

        Toast.makeText(context, "Alarm received for: " + pillName + " at " + time, Toast.LENGTH_SHORT).show();
    }
}
