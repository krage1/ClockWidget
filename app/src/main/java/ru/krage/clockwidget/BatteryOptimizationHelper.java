package ru.krage.clockwidget;

import android.content.Context;
import android.os.PowerManager;

public class BatteryOptimizationHelper {
    
    /**
     * Checks if the app is on the battery optimization whitelist.
     */
    public static boolean isIgnoringBatteryOptimizations(Context context) {
        PowerManager pw = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pw != null) {
            return pw.isIgnoringBatteryOptimizations(context.getPackageName());
        }
        return true; // On older versions of Android, there are no restrictions on Doze Mode
    }
}
