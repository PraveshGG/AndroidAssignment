package com.example.android.notificationtest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn1, btn2, btn3;
    private static final String PRIMARY_CHANNEL_ID =    "primary_notification_channel";
    private static final  int NOTIFICATION_ID = 0;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap androidImage = BitmapFactory.decodeResource(
                        getResources(),R.drawable.starter_img) ;
                NotificationCompat.Builder notifyBuilder =  getNotificationBuilder();
                notifyBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(androidImage)
                .setBigContentTitle("Expanded view"));
                mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
                uiChange(false,false,true);

            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //this method will clear the notification in status bar
                mNotificationManager.cancel(NOTIFICATION_ID);

                uiChange(true,false,false);
            }
        });
        uiChange(true, false, false);

        createNotificationChannel();

    }


    //method stub for sending notifications
    public void sendNotifications(View view){
        uiChange(false,true,true);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();

        mNotificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());

    }

    public void createNotificationChannel(){
        Toast.makeText(this, "channel created", Toast.LENGTH_SHORT).show();

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            //create Notification Channel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Test Alerts",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Hey this is to notify you that notification service works :D");
            mNotificationManager.createNotificationChannel(notificationChannel);
        }else{
            Toast.makeText(this, "else ma gayo ne ta bro", Toast.LENGTH_SHORT).show();
        }
    }

    //notification builder object
    public NotificationCompat.Builder getNotificationBuilder(){

        Intent notificationIntent =new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
         this,
         NOTIFICATION_ID,
         notificationIntent,
         PendingIntent.FLAG_UPDATE_CURRENT
        );

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("Ting tong!!!")
                .setContentText("THis is the expanded view of Ting Tong Tung")
                .setSmallIcon(R.mipmap.ic_notification_icon)
                .setChannelId(PRIMARY_CHANNEL_ID )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        return notifyBuilder;

    }

    public void uiChange(boolean _btn1, boolean _btn2, boolean _btn3){
        btn1.setEnabled(_btn1);
        btn2.setEnabled(_btn2);
        btn3.setEnabled(_btn3);
    }
}
