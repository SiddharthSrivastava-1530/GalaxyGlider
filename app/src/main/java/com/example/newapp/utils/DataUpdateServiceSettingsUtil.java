package com.example.newapp.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.newapp.DataModel.Transaction;
import com.example.newapp.services.MyService;
import com.example.newapp.services.RecurringRideUpdateService;
import com.example.newapp.services.RunningNotificationService;

public class DataUpdateServiceSettingsUtil {

    public static void stopRecurringRideService(Context context) {
        Intent serviceIntent = new Intent(context, RecurringRideUpdateService.class);
        context.stopService(serviceIntent);
    }

    public static void startRecurringRideService(Context context, Transaction transaction) {
        Intent serviceIntent = new Intent(context, RecurringRideUpdateService.class);
        Log.e("tr", transaction.getSpaceShipName());
        serviceIntent.putExtra("current_recurring_tr", transaction);
        ContextCompat.startForegroundService(context, serviceIntent);
    }

}
