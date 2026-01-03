package ru.krage.clockwidget;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalPrefs implements IConstants{
    public SharedPreferences prefs, colorPrefs;
    public SharedPreferences.Editor editor, colorEditor;
    Context context;
    
    // Create a SharedPreferences in the SharedPreferences constructor
    public LocalPrefs(Context context) {
        this.context = context;
        String PREFS_NAME = "Settings";
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        String PREFS_COLOR_NAME = "ColorSettings";
        colorPrefs = context.getSharedPreferences(PREFS_COLOR_NAME, Context.MODE_PRIVATE);
        colorEditor = context.getSharedPreferences(PREFS_COLOR_NAME, Context.MODE_PRIVATE).edit();
    }

    //*   Write data with a key
    public void setData(String keyData, int selectedData, int appWidgetID) {
        editor.putInt(keyData + appWidgetID, selectedData);
        editor.commit();
    }
    //*   Loading data by key
    public int getData(String keyData, int appWidgetID, int defValue) {
        return prefs.getInt(keyData + appWidgetID, defValue);
    }
    //*   Saving the widget ID
    public void setWidgetID(int appWidget){
        editor.putInt(KEY_WIDGET_ID, appWidget);
        editor.commit();
    }
    public int getWidgetID(){
        return prefs.getInt(KEY_WIDGET_ID, 0);
    }
    //*   Save the language index for each widget separately
    public void setIndexLanguage(int indexLanguage, int appWidget){
        editor.putInt(KEY_INDEX_LANGUAGE + appWidget, indexLanguage);
        editor.commit();
    }
    public int getIndexLanguage(int appWidget){
        return prefs.getInt(KEY_INDEX_LANGUAGE + appWidget, -1);
    }

    //@ Operations with colors
    public void setDataColors(String keyData, int color, int appWidgetID){
        colorEditor.putInt(keyData + appWidgetID , color);
        colorEditor.commit();
    }
    public int getDataColors(String keyData, int appWidgetID, int defValue){
        return colorPrefs.getInt(keyData + appWidgetID , defValue);
    }
    //*   Delete data saves
    public void deleteData(int appWidgetID){
        editor.remove(KEY_INDEX_LAYOUT + appWidgetID);
        editor.remove(KEY_INDEX_BACKGROUND + appWidgetID);
        editor.remove(KEY_IMAGE + appWidgetID);
        editor.remove(KEY_TYPE_BACKGROUND + appWidgetID);
        editor.remove(KEY_STATE_SPINNER + appWidgetID);
        editor.remove(KEY_SOLID_GRADIENT_ID + appWidgetID);
        editor.remove(KEY_START_END_ID + appWidgetID);
        editor.remove(KEY_TOGGLE_STROKE_ID + appWidgetID);
        editor.commit();
    }
    public void deleteDataColors(int appWidgetID){
        colorEditor.remove(KEY_COLOR + appWidgetID);
        colorEditor.remove(KEY_STARTCOLOR + appWidgetID);
        colorEditor.remove(KEY_ENDCOLOR + appWidgetID);
        colorEditor.remove(KEY_CENTERCOLOR + appWidgetID);
        colorEditor.remove(KEY_SOLID_STROKE_COLOR + appWidgetID);
        colorEditor.remove(KEY_SOLID_CORNER_RADIUS + appWidgetID);
        colorEditor.remove(KEY_SOLID_STROKE_WIDTH + appWidgetID);
        colorEditor.remove(KEY_GRADIENT_STROKE_COLOR + appWidgetID);
        colorEditor.remove(KEY_GRADIENT_CORNER_RADIUS + appWidgetID);
        colorEditor.remove(KEY_GRADIENT_STROKE_WIDTH + appWidgetID);
        colorEditor.commit();
    }
    public void deleteWidgetLanguage(int widgetID){
        editor.remove(KEY_INDEX_LANGUAGE + widgetID);
        editor.remove(KEY_WIDGET_ID);
        editor.commit();
    }
}
