package ru.krage.clockwidget;

public class WidgetResources {
    
    // --- Viewer IDs---
    public static final int[] IMAGE_VIEW_IDS = {R.id.im_hour1, R.id.im_hour2, R.id.im_minute1,
            R.id.im_minute2, R.id.im_second1, R.id.im_second2, R.id.im_day1, R.id.im_day2,
            R.id.im_month1, R.id.im_month2, R.id.im_year1, R.id.im_year2, R.id.im_year3, R.id.im_year4};
    public static final int[] DOT_VIEW_IDS = {R.id.im_dot, R.id.im_dot1, R.id.im_dot2};
    
    // --- Hours, minutes style 1 ---
    public static final int[] HOURS_STYLE_1 = {R.drawable.hour_0, R.drawable.hour_1,
            R.drawable.hour_2, R.drawable.hour_3, R.drawable.hour_4, R.drawable.hour_5,
            R.drawable.hour_6, R.drawable.hour_7, R.drawable.hour_8, R.drawable.hour_9};
    // --- Hours, minutes style 2 ---
    public static final int[] HOURS_STYLE_2 = {R.drawable.hour1_0,R.drawable.hour1_1,
            R.drawable.hour1_2,R.drawable.hour1_3,R.drawable.hour1_4,R.drawable.hour1_5,
            R.drawable.hour1_6,R.drawable.hour1_7,R.drawable.hour1_8,R.drawable.hour1_9};
    // --- Hours, minutes style 3 ---
    public static final int[] HOURS_STYLE_3 = {R.drawable.hour2_0,R.drawable.hour2_1,R.drawable.hour2_2
            ,R.drawable.hour2_3,R.drawable.hour2_4,R.drawable.hour2_5,R.drawable.hour2_6,
            R.drawable.hour2_7,R.drawable.hour2_8,R.drawable.hour2_9};
    // --- Hours, minutes style 4 ---
    public static final int[] HOURS_STYLE_4 = {R.drawable.hour3_0,R.drawable.hour3_1,R.drawable.hour3_2
            ,R.drawable.hour3_3,R.drawable.hour3_4,R.drawable.hour3_5,R.drawable.hour3_6,
            R.drawable.hour3_7,R.drawable.hour3_8,R.drawable.hour3_9};
    
    public static final int[][] STYLE_HOURS = {HOURS_STYLE_1, HOURS_STYLE_2, HOURS_STYLE_3, HOURS_STYLE_4};
    
    // --- Seconds Style 1 ---
    public static final int[] SECONDS_STYLE_1 = {R.drawable.sec_0, R.drawable.sec_1,
            R.drawable.sec_2, R.drawable.sec_3, R.drawable.sec_4, R.drawable.sec_5,
            R.drawable.sec_6, R.drawable.sec_7, R.drawable.sec_8, R.drawable.sec_9};
    // --- Seconds Style 2 ---
    public static final int[] SECONDS_STYLE_2 = {R.drawable.sec1_0,R.drawable.sec1_1,
            R.drawable.sec1_2,R.drawable.sec1_3,R.drawable.sec1_4,R.drawable.sec1_5,
            R.drawable.sec1_6,R.drawable.sec1_7,R.drawable.sec1_8,R.drawable.sec1_9};
    // --- Seconds Style 3 ---
    public static final int[] SECONDS_STYLE_3 = {R.drawable.sec2_0,R.drawable.sec2_1,
            R.drawable.sec2_2, R.drawable.sec2_3,R.drawable.sec2_4,R.drawable.sec2_5,
            R.drawable.sec2_6, R.drawable.sec2_7,R.drawable.sec2_8,R.drawable.sec2_9};
    // --- Seconds Style 4 ---
    public static final int[] SECONDS_STYLE_4 = {R.drawable.sec3_0,R.drawable.sec3_1,
            R.drawable.sec3_2, R.drawable.sec3_3,R.drawable.sec3_4,R.drawable.sec3_5,
            R.drawable.sec3_6, R.drawable.sec3_7,R.drawable.sec3_8,R.drawable.sec3_9};
    
    public static final int[][] STYLE_SECONDS = {SECONDS_STYLE_1, SECONDS_STYLE_2, SECONDS_STYLE_3,
            SECONDS_STYLE_4};
    
    // --- Date style 1 ---
    public static final int[] DATES_STYLE_1 = {R.drawable.date_0, R.drawable.date_1,
            R.drawable.date_2, R.drawable.date_3, R.drawable.date_4, R.drawable.date_5,
            R.drawable.date_6, R.drawable.date_7, R.drawable.date_8, R.drawable.date_9};
    // --- Date style 2 ---
    public static final int[] DATES_STYLE_2 = {R.drawable.date1_0,R.drawable.date1_1,
            R.drawable.date1_2, R.drawable.date1_3,R.drawable.date1_4,R.drawable.date1_5,
            R.drawable.date1_6, R.drawable.date1_7,R.drawable.date1_8,R.drawable.date1_9};
    // --- Date style 3 ---
    public static final int[] DATES_STYLE_3 = {R.drawable.date2_0,R.drawable.date2_1,
            R.drawable.date2_2,R.drawable.date2_3,R.drawable.date2_4,R.drawable.date2_5,
            R.drawable.date2_6,R.drawable.date2_7,R.drawable.date2_8,R.drawable.date2_9};
    // --- Date style 4 ---
    public static final int[] DATES_STYLE_4 = {R.drawable.date3_0,R.drawable.date3_1,
            R.drawable.date3_2,R.drawable.date3_3,R.drawable.date3_4,R.drawable.date3_5,
            R.drawable.date3_6,R.drawable.date3_7,R.drawable.date3_8,R.drawable.date3_9};
    
    public static final int[][] STYLE_DATES = {DATES_STYLE_1, DATES_STYLE_2, DATES_STYLE_3,DATES_STYLE_4};
    
    // --- Days of the week style 1 ---
    public static final int[] WEEKS_STYLE_1_RU = {0, R.drawable.week_1, R.drawable.week_2,
            R.drawable.week_3, R.drawable.week_4, R.drawable.week_5, R.drawable.week_6,
            R.drawable.week_7};
    public static final int[] WEEKS_STYLE_1_EN = {0, R.drawable.week_1_en,R.drawable.week_2_en,
            R.drawable.week_3_en,R.drawable.week_4_en,R.drawable.week_5_en,R.drawable.week_6_en,
            R.drawable.week_7_en};
    public static final int[] WEEKS_STYLE_1_DE = {0, R.drawable.week_1_de,R.drawable.week_2_de,
            R.drawable.week_3_de,R.drawable.week_4_de,R.drawable.week_5_de,R.drawable.week_6_de,
            R.drawable.week_7_de};
    public static final int[] WEEKS_STYLE_1_FR = {0, R.drawable.week_1_fr,R.drawable.week_2_fr,
            R.drawable.week_3_fr,R.drawable.week_4_fr,R.drawable.week_5_fr,R.drawable.week_6_fr,
            R.drawable.week_7_fr};
    public static final int[] WEEKS_STYLE_1_ES = {0, R.drawable.week_1_es,R.drawable.week_2_es,
            R.drawable.week_3_es,R.drawable.week_4_es,R.drawable.week_5_es,R.drawable.week_6_es,
            R.drawable.week_7_es};
    public static final int[] WEEKS_STYLE_1_IT = {0, R.drawable.week_1_it,R.drawable.week_2_it,
            R.drawable.week_3_it,R.drawable.week_4_it,R.drawable.week_5_it,R.drawable.week_6_it,
            R.drawable.week_7_it};
    public static final int[][] WEEK_STYLES_1 = {WEEKS_STYLE_1_EN, WEEKS_STYLE_1_RU, WEEKS_STYLE_1_DE,
            WEEKS_STYLE_1_FR, WEEKS_STYLE_1_ES, WEEKS_STYLE_1_IT};
    // --- Days of the week style 2 ---
    public static final int[] WEEKS_STYLE_2_RU = {0,R.drawable.week1_1,R.drawable.week1_2,
            R.drawable.week1_3, R.drawable.week1_4,R.drawable.week1_5,R.drawable.week1_6,
            R.drawable.week1_7};
    public static final int[] WEEKS_STYLE_2_EN = {0,R.drawable.week1_1_en,R.drawable.week1_2_en,
            R.drawable.week1_3_en,R.drawable.week1_4_en,R.drawable.week1_5_en,R.drawable.week1_6_en,
            R.drawable.week1_7_en};
    public static final int[] WEEKS_STYLE_2_DE = {0,R.drawable.week1_1_de,R.drawable.week1_2_de,
            R.drawable.week1_3_de,R.drawable.week1_4_de,R.drawable.week1_5_de,R.drawable.week1_6_de,
            R.drawable.week1_7_de};
    public static final int[] WEEKS_STYLE_2_FR = {0,R.drawable.week1_1_fr,R.drawable.week1_2_fr,
            R.drawable.week1_3_fr,R.drawable.week1_4_fr,R.drawable.week1_5_fr,R.drawable.week1_6_fr,
            R.drawable.week1_7_fr};
    public static final int[] WEEKS_STYLE_2_ES = {0,R.drawable.week1_1_es,R.drawable.week1_2_es,
            R.drawable.week1_3_es,R.drawable.week1_4_es,R.drawable.week1_5_es,R.drawable.week1_6_es,
            R.drawable.week1_7_es};
    public static final int[] WEEKS_STYLE_2_IT = {0,R.drawable.week1_1_it,R.drawable.week1_2_it,
            R.drawable.week1_3_it,R.drawable.week1_4_it,R.drawable.week1_5_it,R.drawable.week1_6_it,
            R.drawable.week1_7_it};
    public static final int[][] WEEK_STYLES_2 = {WEEKS_STYLE_2_EN, WEEKS_STYLE_2_RU, WEEKS_STYLE_2_DE,
            WEEKS_STYLE_2_FR, WEEKS_STYLE_2_ES, WEEKS_STYLE_2_IT};
    // --- Days of the week style 3 ---
    public static final int[] WEEKS_STYLE_3_RU = {0,R.drawable.week2_1,R.drawable.week2_2,
            R.drawable.week2_3,R.drawable.week2_4,R.drawable.week2_5,R.drawable.week2_6,
            R.drawable.week2_7};
    public static final int[] WEEKS_STYLE_3_EN = {0,R.drawable.week2_1_en,R.drawable.week2_2_en,
            R.drawable.week2_3_en,R.drawable.week2_4_en,R.drawable.week2_5_en,R.drawable.week2_6_en,
            R.drawable.week2_7_en};
    public static final int[] WEEKS_STYLE_3_DE = {0,R.drawable.week2_1_de,R.drawable.week2_2_de,
            R.drawable.week2_3_de,R.drawable.week2_4_de,R.drawable.week2_5_de,R.drawable.week2_6_de,
            R.drawable.week2_7_de};
    public static final int[] WEEKS_STYLE_3_FR = {0,R.drawable.week2_1_fr,R.drawable.week2_2_fr,
            R.drawable.week2_3_fr,R.drawable.week2_4_fr,R.drawable.week2_5_fr,R.drawable.week2_6_fr,
            R.drawable.week2_7_fr};
    public static final int[] WEEKS_STYLE_3_ES = {0,R.drawable.week2_1_es,R.drawable.week2_2_es,
            R.drawable.week2_3_es,R.drawable.week2_4_es,R.drawable.week2_5_es,R.drawable.week2_6_es,
            R.drawable.week2_7_es};
    public static final int[] WEEKS_STYLE_3_IT = {0,R.drawable.week2_1_it,R.drawable.week2_2_it,
            R.drawable.week2_3_it,R.drawable.week2_4_it,R.drawable.week2_5_it,R.drawable.week2_6_it,
            R.drawable.week2_7_it};
    public static final int[][] WEEK_STYLES_3 = {WEEKS_STYLE_3_EN, WEEKS_STYLE_3_RU, WEEKS_STYLE_3_DE,
            WEEKS_STYLE_3_FR,WEEKS_STYLE_3_ES, WEEKS_STYLE_3_IT};
    // --- Days of the week style 4 ---
    public static final int[] WEEKS_STYLE_4_RU = {0,R.drawable.week3_1,R.drawable.week3_2,
            R.drawable.week3_3,R.drawable.week3_4,R.drawable.week3_5,R.drawable.week3_6,
            R.drawable.week3_7};
    public static final int[] WEEKS_STYLE_4_EN = {0,R.drawable.week3_1_en,R.drawable.week3_2_en,
            R.drawable.week3_3_en,R.drawable.week3_4_en,R.drawable.week3_5_en,R.drawable.week3_6_en,
            R.drawable.week3_7_en};
    public static final int[] WEEKS_STYLE_4_DE = {0,R.drawable.week3_1_de,R.drawable.week3_2_de,
            R.drawable.week3_3_de,R.drawable.week3_4_de,R.drawable.week3_5_de,R.drawable.week3_6_de,
            R.drawable.week3_7_de};
    public static final int[] WEEKS_STYLE_4_FR = {0,R.drawable.week3_1_fr,R.drawable.week3_2_fr,
            R.drawable.week3_3_fr,R.drawable.week3_4_fr,R.drawable.week3_5_fr,R.drawable.week3_6_fr,
            R.drawable.week3_7_fr};
    public static final int[] WEEKS_STYLE_4_ES = {0,R.drawable.week3_1_es,R.drawable.week3_2_es,
            R.drawable.week3_3_es,R.drawable.week3_4_es,R.drawable.week3_5_es,R.drawable.week3_6_es,
            R.drawable.week3_7_es};
    public static final int[] WEEKS_STYLE_4_IT = {0,R.drawable.week3_1_it,R.drawable.week3_2_it,
            R.drawable.week3_3_it,R.drawable.week3_4_it,R.drawable.week3_5_it,R.drawable.week3_6_it,
            R.drawable.week3_7_it};
    public static final int[][] WEEK_STYLES_4 = {WEEKS_STYLE_4_EN, WEEKS_STYLE_4_RU,WEEKS_STYLE_4_DE,
            WEEKS_STYLE_4_FR, WEEKS_STYLE_4_ES, WEEKS_STYLE_4_IT};
    
    // --- All Styles of Days of the Week ---
    public static final int[][][] STYLES_WEEKS = {WEEK_STYLES_1, WEEK_STYLES_2, WEEK_STYLES_3,
            WEEK_STYLES_4};
    // --- Dot Styles ---
    public static final int[] STYLE_DOTS = {R.drawable.dot,R.drawable.dot1,R.drawable.dot2,R.drawable.dot3};
    public static final int[] STYLE_DOUBLE_DOTS = {R.drawable.double_dot,R.drawable.double_dot1,
            R.drawable.double_dot2,R.drawable.double_dot3};
}
