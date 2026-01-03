package ru.krage.clockwidget;

import android.widget.RemoteViews;

import java.util.Calendar;

public class WidgetUpdater {
    /**
     * The main method to update a widget
     * @param rootViews RemoteViews widget
     * @param indexStyle Index of the selected style (0, 1, 2...)
     * @param indexLanguage Language index (0 - EN, 1 - RU, etc.)
     */
    public void updateTimeDate(RemoteViews rootViews, int indexStyle, int indexLanguage) {
        // Array Overflow Protection (Safety check)
        if (indexStyle < 0 || indexStyle >= WidgetResources.STYLE_HOURS.length) indexStyle = 0;
        // For the language, the check is more difficult, since the array is 3-dimensional, but the
        // principle is the same
        
        // 1. Getting time
        Calendar calendar = Calendar.getInstance();
        
        // Extracting the values
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Месяцы в Java с 0
        int dayMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int dayWeek = calendar.get(Calendar.DAY_OF_WEEK); // 1 = Sunday, 7 = Saturday
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        
        // 2. Prepare an array of numbers for display
        // The order in the array of imageIDs is:
        // H1, H2, M1, M2, S1, S2, D1, D2, Mo1, Mo2, Y1, Y2, Y3, Y4
        
        int[] digits = new int[14];
        
        // Hours
        digits[0] = hour / 10;
        digits[1] = hour % 10;
        // Minutes
        digits[2] = minute / 10;
        digits[3] = minute % 10;
        // Seconds
        digits[4] = second / 10;
        digits[5] = second % 10;
        // Day
        digits[6] = dayMonth / 10;
        digits[7] = dayMonth % 10;
        // Month
        digits[8] = month / 10;
        digits[9] = month % 10;
        // Year
        digits[10] = year / 1000;
        digits[11] = (year % 1000) / 100;
        digits[12] = (year % 100) / 10;
        digits[13] = year % 10;
        
        // 3. Update the View in loops.
        
        // Update the Hours and Minutes (indexes 0-3 in imageIDs)
        // Use an array of STYLE_HOURS
        for (int i = 0; i < 4; i++) {
            int digitToDraw = digits[i];
            int resId = WidgetResources.STYLE_HOURS[indexStyle][digitToDraw];
            rootViews.setImageViewResource(WidgetResources.IMAGE_VIEW_IDS[i], resId);
        }
        
        // Updating Seconds (indexes 4-5)
        for (int i = 4; i < 6; i++) {
            int digitToDraw = digits[i];
            int resId = WidgetResources.STYLE_SECONDS[indexStyle][digitToDraw];
            rootViews.setImageViewResource(WidgetResources.IMAGE_VIEW_IDS[i], resId);
        }
        
        // Update the date (day, month, year) (zip codes 6-13)
        // Use an array of STYLE_DATES
        for (int i = 6; i < 14; i++) {
            int digitToDraw = digits[i];
            int resId = WidgetResources.STYLE_DATES[indexStyle][digitToDraw];
            rootViews.setImageViewResource(WidgetResources.IMAGE_VIEW_IDS[i], resId);
        }
        
        // 4. Static Elements (Points and Day of the Week)
        
        // Dots (date separators)
        int dotRes = WidgetResources.STYLE_DOTS[indexStyle];
        for (int id : WidgetResources.DOT_VIEW_IDS) {
            rootViews.setImageViewResource(id, dotRes);
        }
        
        // Colon (time separator)
        rootViews.setImageViewResource(R.id.im_doubleDot, WidgetResources.STYLE_DOUBLE_DOTS[indexStyle]);
        
        // Day of the week
        int weekRes = WidgetResources.STYLES_WEEKS[indexStyle][indexLanguage][dayWeek];
        rootViews.setImageViewResource(R.id.im_dayWeek, weekRes);
    }
}
