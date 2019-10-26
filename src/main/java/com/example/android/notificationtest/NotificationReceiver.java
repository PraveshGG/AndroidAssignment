package com.example.android.notificationtest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String msg = intent.getAction();


        if (msg.equals("Hybrid")) {
            Toast.makeText(context, "Hybrid CALLED", Toast.LENGTH_SHORT).show();
        }
        else  if (msg.equals("Satellite")) {
            Toast.makeText(context, "Satellite CALLED", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Terrain CALLED", Toast.LENGTH_SHORT).show();
        }
       // Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();



    }
}
