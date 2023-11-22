package com.example.newapp.utils;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

import com.example.newapp.services.RunningNotificationService;

public class ServiceSettingsUtil {
    public static void stopRideService(Context context) {
        Intent serviceIntent = new Intent(context, RunningNotificationService.class);
        context.stopService(serviceIntent);
    }

    public static void startRideService(Context context,String companyName,String spaceShipName,
                                        String fromPlanet,String toPlanet,String distance) {
        Intent serviceIntent = new Intent(context, RunningNotificationService.class);

        serviceIntent.putExtra("companyName", companyName);
        serviceIntent.putExtra("spaceShipName", spaceShipName);
        serviceIntent.putExtra("fromPlanet", fromPlanet);
        serviceIntent.putExtra("toPlanet", toPlanet);
        serviceIntent.putExtra("distance", distance+" light years");

        ContextCompat.startForegroundService(context, serviceIntent);
    }
//    public static boolean isCaptureServiceRunning(Context context) {
//        String name = ContextCompat.getSystemServiceName(context, ServiceSettingsUtil.class);
//        if (name == null || name.isEmpty())
//            return false;
//        Toast.makeText(context, name, Toast.LENGTH_LONG).show();
//        return name.equals("ForegroundServiceChannel");
//    }
}
