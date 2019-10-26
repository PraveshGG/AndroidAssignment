package com.example.android.notificationtest;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link NotificationWidgetConfigureActivity NotificationWidgetConfigureActivity}
 */
public class NotificationWidget extends AppWidgetProvider {

    private static final String msharedPrefFile = "com.example.android.notificationtest";
    private static final String COUNT_KEY = "count";
    private static  String mColor = "";
    Button updateButton;

    static  void setColor(String color){
        mColor = color;
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        SharedPreferences prefs = context.getSharedPreferences(msharedPrefFile, 0);
        int count = prefs.getInt(COUNT_KEY + appWidgetId, 0);

        Log.d("countSize", "aa: " + count);

        String dateString = DateFormat.getTimeInstance(DateFormat.SHORT).format(new Date());

       // CharSequence widgetText = NotificationWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.notification_widget);
        views.setTextViewText(R.id.tv_appWidget_id, String.valueOf(appWidgetId));
        views.setInt(R.id.rl, "setBackgroundColor", Color.rgb(255, 0, 0));

        if( mColor.equals("red")){
            Log.d("inside", "color: " + mColor);
        }else if(mColor.equals("green")){
            views.setInt(R.id.rl, "setBackgroundColor", Color.rgb(0, 255, 0));
        }else if (mColor.equals("blue")){
            views.setInt(R.id.rl, "setBackgroundColor", Color.rgb(0, 0, 255));
        }else if(mColor.equals("black")){
            views.setInt(R.id.rl, "setBackgroundColor", Color.rgb(0, 0, 0));
        }else{
            views.setInt(R.id.rl, "setBackgroundColor", Color.rgb(0, 153, 204));
        }
        views.setTextViewText(R.id.tv_appWidget_update, context.getResources().getString(
                R.string.date_count_format, count, dateString));

        count ++;



        //shared pref ma save garni
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putInt(COUNT_KEY + appWidgetId, count);
        Log.d("countSize", "bb: " + count);
        prefEditor.apply();

        Intent intentUpdate = new Intent(context, NotificationWidget.class);
        intentUpdate.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int [] idArray = new int[]{appWidgetId};
        intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                appWidgetId,
                intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent(R.id.btn_update, pendingIntent);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }


    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            NotificationWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

