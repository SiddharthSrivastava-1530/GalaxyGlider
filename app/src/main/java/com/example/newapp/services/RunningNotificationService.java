package com.example.newapp.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.example.newapp.Activities.AllTransactionsList;
import com.example.newapp.R;

public class RunningNotificationService extends Service {

    final private String CHANNEL_ID = "ForegroundServiceChannel";

    public RunningNotificationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();


        String companyName = intent.getStringExtra("companyName");
        String spaceShipName = intent.getStringExtra("spaceShipName");
        String fromPlanet = intent.getStringExtra("fromPlanet");
        String toPlanet = intent.getStringExtra("toPlanet");
        String distance = intent.getStringExtra("distance");

        Intent notificationIntent = new Intent(this, AllTransactionsList.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(notificationIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("New Journey with " + companyName)
                .setContentText("You are on an exciting space adventure")
                .setTicker(getText(R.string.app_name))
                .setContentIntent(pendingIntent)  // Set the modified PendingIntent here
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Space Ship: " + spaceShipName + "\n"
                                + "Welcome to the exciting journey in space!\n"
                                + "Journey Details:\n"
                                + "From: " + fromPlanet + "\n"
                                + "To: " + toPlanet + "\n"
                                + "Distance: " + distance))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Notification notification = builder.build();

        startForeground(1, notification);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Foreground Service Channel";
            String description = "To send foreground notification";
            int importance = NotificationManager.IMPORTANCE_NONE;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}