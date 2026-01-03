package ru.krage.clockwidget;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class ServiceWatchdog extends Worker {
    public ServiceWatchdog(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }
    
    @NonNull
    @Override
    public Result doWork() {
        
        Context context = getApplicationContext();
        ClockWidget.startUpdate(context);
        Log.d("dog", "ServiceWatchdog");
        return Result.success();
    }
}
