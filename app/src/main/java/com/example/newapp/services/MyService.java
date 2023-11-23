package com.example.newapp.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.newapp.DataModel.Transaction;
import com.example.newapp.utils.MyReceiver;

import java.util.Calendar;

public class MyService extends Service {

    private static final String TAG = "MyService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Getting the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Getting the current date and time
        Calendar calendar = Calendar.getInstance();

        // Setting the time to 00:00:00
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // If the time has already passed for today, move it to the next day
        if (System.currentTimeMillis() > calendar.getTimeInMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Get the time in milliseconds
        long triggerTime = calendar.getTimeInMillis();

        // Creating an Intent that will be broadCasted when the alarm fires
        Intent alarmIntent = new Intent(this, MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

        // Setting a repeating alarm for every day at 00:00
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, AlarmManager.INTERVAL_DAY, pendingIntent);

        // Return START_STICKY to ensure the service restarts if it gets terminated by the system
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy");
    }
}

