package ru.krage.clockwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class ClockWidget extends AppWidgetProvider implements IConstants {
    
    private static final String TAG = "ClockWidget";
    
    //* volatile provides visibility into variable changes between threads
    private static volatile ScheduledExecutorService executorService;
    
    //* Broadcasting widget updates to Intent
    private static final String FORCE_UPDATES = "ru.krage.clockwidget.FORCE_UPDATES";
    
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        
        //* Update all instances
        for (int appWidgetId : appWidgetIds) {
            
            //* At the start or update of the system, we do a FULL update (background + time + clicks)
                updateAppWidget(context, appWidgetManager, appWidgetId, true);
        }
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        
        //* Timer Signal Processing
        if (Objects.equals(intent.getAction(), FORCE_UPDATES)) {
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
            int[] ids = widgetManager.getAppWidgetIds(thisAppWidget);
            
            //* If there are no widgets, stop the timer to save battery
            if (ids.length == 0) {
                stopUpdate();
                return;
            }
            
            for (int appWidgetID : ids) {
                //* Every second we do a PARTIAL update (time only)
                updateAppWidget(context, widgetManager, appWidgetID, false);
            }
        }
    }
    
    /**
     * Universal update method.
     * @param isFullUpdate
     * true - update everything (background, clicks, time). A difficult operation.
     * false - update only the time. Easy operation for every second call.
     */
    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, boolean isFullUpdate) {
        
        LocalPrefs prefs = new LocalPrefs(context);
        int indexLang = prefs.getIndexLanguage(appWidgetId);
        
        //* Choosing a layout
        int layoutId = (indexLang == 1) ? R.layout.clock_widget : R.layout.clock_widget_e;
        RemoteViews views = new RemoteViews(context.getPackageName(), layoutId);
        
        //* 1. Обновляем время (Делаем это ВСЕГДА)
        configureTime(views, prefs, appWidgetId, indexLang);
        
        //* 2. If the update is complete, set up the background and clicks
        if (isFullUpdate) {
            configureBackground(context, views, prefs, appWidgetId);
            configureClickIntent(context, views, appWidgetId);
            
            //* Using updateAppWidget to completely redraw
            appWidgetManager.updateAppWidget(appWidgetId, views);
        } else {
            // If partially, use partiallyUpdateAppWidget
            // This tells the system that it only needs to update the changed ImageView,
            // without recreating the entire widget.
            appWidgetManager.partiallyUpdateAppWidget(appWidgetId, views);
        }
    }
    
    /**
     * Background loading logic
     */
    private static void configureBackground(Context context, RemoteViews views, LocalPrefs prefs,
                                            int appWidgetId) {
        
        int indexType = prefs.getData(KEY_TYPE_BACKGROUND, appWidgetId, -1);
        
        //* Reset the background before installation (just in case)
        views.setImageViewBitmap(R.id.iv_background, null);
        
        if (indexType == 10) {
            //* System Resource
            int imageBackground = prefs.getData(KEY_IMAGE, appWidgetId, -1);
            if (imageBackground != -1) {
                views.setInt(R.id.iv_background, "setImageResource", imageBackground);
            }
        } else if (indexType == 20) {
            //* File from disk
            // Bitmap is loaded locally, used and immediately destroyed by the GC,
            // when out of sight
            Bitmap localBitmap = loadBitmapFromFile(context, appWidgetId);
            if (localBitmap != null) {
                views.setImageViewBitmap(R.id.iv_background, localBitmap);
                // Note: localBitmap.recycle() CANNOT be called here,
                // because RemoteViews may not yet have time to transmit data to the system.
            }
        }
    }
    
    /**
     * Time Setting Logic (via WidgetUpdater)
     */
    private static void configureTime(RemoteViews views, LocalPrefs prefs, int appWidgetId,
                                      int indexLang) {
        
        int indexLayout = prefs.getData(KEY_INDEX_LAYOUT, appWidgetId, -1);
        
        if (indexLayout != -1) {
            
            //* Using an optimized WidgetUpdater
            WidgetUpdater updater = new WidgetUpdater();
            updater.updateTimeDate(views, indexLayout, indexLang);
        }
    }
    
    /**
     * Setting up Intent for clicking on the widget
     */
    private static void configureClickIntent(Context context, RemoteViews views, int appWidgetId) {
        Intent configIntent = new Intent(context, ConfigActivity.class);
        configIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        
        //* The FLAG_IMMUTABLE flag is required for Android 12+
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        flags |= PendingIntent.FLAG_IMMUTABLE;
        
        PendingIntent pIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, flags);
        views.setOnClickPendingIntent(R.id.rlBackground, pIntent);
    }
    
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        
        //* Let's start updating the widget and watchdog timer
        startUpdate(context);
        scheduleWatchdog(context);
    }
    
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        //* Stop updating the widget
        stopUpdate();
    }
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        LocalPrefs prefs = new LocalPrefs(context);
        for (int id : appWidgetIds) {
            // Deleting widget data
            prefs.deleteData(id);
            // Remove widget color data
            prefs.deleteDataColors(id);
            // Delete the widget image file
            deleteFileBitmap(context, id);
            // Remove the widget language index
            prefs.deleteWidgetLanguage(id);
        }
    }
    
    //@ --- Timer Control ---
    public static void startUpdate(Context context){
        
        //* Checking the context for null for security
        if (context == null) return;
        
        //* Synchronization to prevent multiple timers from running
        synchronized (ClockWidget.class) {
            if (executorService == null || executorService.isShutdown()) {
                executorService = Executors.newSingleThreadScheduledExecutor();
                
                //* Using the ApplicationContext to avoid Activity Context leaks
                Context appContext = context.getApplicationContext();
                Intent alarmIntent = new Intent(appContext, ClockWidget.class);
                alarmIntent.setAction(FORCE_UPDATES);
                alarmIntent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND | Intent.FLAG_RECEIVER_NO_ABORT);
                
                executorService.scheduleWithFixedDelay(() -> {
                    try {
                        appContext.sendBroadcast(alarmIntent);
                    } catch (Exception e) {
                        Log.e(TAG, "Error sending broadcast", e);
                    }
                }, 0, 1000, TimeUnit.MILLISECONDS);
            }
        }
    }
        
    public void stopUpdate(){
        synchronized (ClockWidget.class) {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdownNow();
                executorService = null;
            }
        }
    }
    
    public static Bitmap loadBitmapFromFile(Context context, int widgetID){
        String fileName = "imgBitmap" + widgetID + ".webp";
        File file = new File(context.getCacheDir(), fileName);
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                return BitmapFactory.decodeStream(fis);
            } catch (IOException e) {
                Log.e(TAG, "Error loading bitmap", e);
            }
        }
        return null;
    }
    
    public void deleteFileBitmap(Context context, int widgetID){
        String fileName = "imgBitmap" + widgetID + ".webp";
        File file = new File(context.getCacheDir(), fileName);
        if (file.exists()) {
            if (!file.delete()) {
                Log.e(TAG, "Failed to delete file: " + file.getAbsolutePath());
            }
        }
    }
    
    //@ Method to call from ConfigActivity after changing settings
    public static void updateBackground(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {
        //* Call the generic method with the flag fullUpdate = true
        updateAppWidget(context, appWidgetManager, appWidgetId, true);
    }
    // Creating a task to start the watchdog service
    public static void scheduleWatchdog(Context context) {
        PeriodicWorkRequest watchdogRequest = new PeriodicWorkRequest.Builder(ServiceWatchdog.class,
                        15, TimeUnit.MINUTES).build();
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("ClockWatchdog",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, watchdogRequest);
    }
}