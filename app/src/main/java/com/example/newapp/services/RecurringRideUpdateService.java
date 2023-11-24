package com.example.newapp.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.newapp.DataModel.Transaction;
import com.example.newapp.R;
import com.example.newapp.utils.MyReceiver;
import com.example.newapp.utils.RecurringRideReceiver;

import java.security.Provider;
import java.util.Calendar;

public class RecurringRideUpdateService extends Service {

    private static final String TAG = "RecurringRideUpdateService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Transaction transaction = (Transaction) intent.getSerializableExtra("current_recurring_tr");
        Log.e("tr", transaction.getSpaceShipName());
        // Getting the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        // Getting the current date and time
        Calendar calendar = Calendar.getInstance();

        int slot = Integer.parseInt(transaction.getSlotNo());
        if(slot == 0) {
            calendar.set(Calendar.HOUR_OF_DAY, 0);
        } else if (slot == 1) {
            calendar.set(Calendar.HOUR_OF_DAY, 3);
        } else if (slot == 2) {
            calendar.set(Calendar.HOUR_OF_DAY, 6);
        } else if (slot == 3) {
            calendar.set(Calendar.HOUR_OF_DAY, 9);
        } else if (slot == 4) {
            calendar.set(Calendar.HOUR_OF_DAY, 12);
        } else if (slot == 5) {
            calendar.set(Calendar.HOUR_OF_DAY, 15);
        } else if (slot == 6) {
            calendar.set(Calendar.HOUR_OF_DAY, 18);
        } else if (slot == 7) {
            calendar.set(Calendar.HOUR_OF_DAY, 21);
        }
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
        Intent alarmIntent = new Intent(this, RecurringRideReceiver.class);
        alarmIntent.putExtra("current_recurring_tr", transaction);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

        // Setting a repeating alarm for every day at 00:00
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, AlarmManager.INTERVAL_DAY, pendingIntent);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "your_channel_id";
            CharSequence channelName = "Your Channel Name";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create a notification for the foreground service
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "your_channel_id")
                .setContentTitle("Galaxy Glider")
                .setContentText("You are on a recurring ride..")
                .setSmallIcon(R.drawable.app_icon);

        // Set the notification to be ongoing (not dismissible by the user)
        builder.setOngoing(true);

        // Set priority to PRIORITY_LOW, PRIORITY_DEFAULT, or PRIORITY_HIGH depending on your app's needs
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        // Set the notification category to CATEGORY_SERVICE
        builder.setCategory(NotificationCompat.CATEGORY_SERVICE);

        // Build the notification
        Notification notification = builder.build();

        // Start the service as a foreground service with the notification
        startForeground(2, notification);

        // Return START_STICKY to ensure the service restarts if it gets terminated by the system
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Service onDestroy");
    }

}
