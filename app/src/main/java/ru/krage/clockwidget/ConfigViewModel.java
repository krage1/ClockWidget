package ru.krage.clockwidget;

import android.app.Application;
import android.app.LocaleManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.LocaleList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ConfigViewModel extends AndroidViewModel implements IConstants {

    private final LocalPrefs prefs;
    private final String[] langsArray;

    private final MutableLiveData<Integer> layoutIndex = new MutableLiveData<>(-1);
    private final MutableLiveData<Integer> backgroundIndex = new MutableLiveData<>(-1);
    private final MutableLiveData<Integer> bitmapType = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> languageIndex = new MutableLiveData<>(-1);

    private final MutableLiveData<Boolean> addButtonEnabled = new MutableLiveData<>(false);

    private int widgetID = AppWidgetManager.INVALID_APPWIDGET_ID;

    //* constructor
    public ConfigViewModel(@NonNull Application application) {
        super(application);
        prefs = new LocalPrefs(application.getApplicationContext());
        langsArray = application.getResources().getStringArray(R.array.langs);
    }
    //* Save the widget ID
    public void setWidgetID(int id) {
        this.widgetID = id;
        if (id != AppWidgetManager.INVALID_APPWIDGET_ID) {
            prefs.setWidgetID(id);
            restorePrefs();
        }
    }
    //* Extract previously saved data, if any
    private void restorePrefs(){
        layoutIndex.setValue(prefs.getData(KEY_INDEX_LAYOUT, widgetID, -1));
        backgroundIndex.setValue(prefs.getData(KEY_INDEX_BACKGROUND, widgetID, -1));
        bitmapType.setValue(prefs.getData(KEY_TYPE_BACKGROUND, widgetID, 0));

        updateAddButtonState();
    }
    //* Calculating the user's system language and setting the program's primary language, if any
    //* is not on the list, set as the main English language.
    public void detectSystemLanguage() {

        int indexLang = prefs.getIndexLanguage(widgetID);
        if (indexLang >= 0) {
            languageIndex.setValue(indexLang);
            return;
        }

        LocaleList localeList;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            LocaleManager localeManager = (LocaleManager) getApplication().getSystemService(Context.LOCALE_SERVICE);

            // Get a list of locales set by the user in the SYSTEM settings
            // This ignores the locale set specifically for that app.
            localeList = localeManager.getSystemLocales();
            String sysLocale = localeList.get(0).getLanguage();
            setLangValue(sysLocale);
        } else {
                Configuration config = Resources.getSystem().getConfiguration();
                String sysLocale = config.getLocales().get(0).getLanguage();
                setLangValue(sysLocale);
            }
    }
    private void setLangValue(String s){
        for (int i = 0; i < langsArray.length; i++) {
            if (langsArray[i].equalsIgnoreCase(s)) {
                languageIndex.setValue(i);
                prefs.setIndexLanguage(i, widgetID);
                return;
            }
        }
        languageIndex.setValue(0);
        prefs.setIndexLanguage(0, widgetID);
    }
    //* Save the widget style index
    public void setLayoutIndex(int index) {
        layoutIndex.setValue(index);
        prefs.setData(KEY_INDEX_LAYOUT, index, widgetID);
        updateAddButtonState();
    }
    //* Save the widget background index
    public void setBackgroundIndex(int index) {
        backgroundIndex.setValue(index);
        prefs.setData(KEY_INDEX_BACKGROUND, index, widgetID);
        prefs.setData(KEY_TYPE_BACKGROUND, 10, widgetID);
        try (TypedArray img = getApplication().getResources().obtainTypedArray(R.array.img)) {
            //$   Save the background image
            if (index != - 1) {
                prefs.setData(KEY_IMAGE, img.getResourceId(index, 0), widgetID);
//                img.recycle();
            }
        }
        updateAddButtonState();
    }
    //* Save the index of the widget's custom background
    public void setUserBackground(int type) {
        bitmapType.setValue(type);
        prefs.setData(KEY_TYPE_BACKGROUND, type, widgetID);
        updateAddButtonState();
    }
    //* Save the widget language index
    public void setLanguageIndex(int index) {
        languageIndex.setValue(index);
        prefs.setIndexLanguage(index, widgetID);
    }
    //* Widget Add/Update button status
    private void updateAddButtonState() {
        int layout = layoutIndex.getValue() != null ? layoutIndex.getValue() : -1;
        int background = backgroundIndex.getValue() != null ? backgroundIndex.getValue() : -1;
        int bitmap = bitmapType.getValue() != null ? bitmapType.getValue() : 0;

        boolean enabled = layout != -1 && (background != -1 || bitmap != 0);
        addButtonEnabled.setValue(enabled);
    }
    public boolean updateAddButtonText(){
        return Boolean.TRUE.equals(addButtonEnabled.getValue());
    }
    //* --- LiveData getters ---
    public LiveData<Integer> getLanguageIndex() { return languageIndex; }
    public LiveData<Boolean> isAddButtonEnabled() { return addButtonEnabled; }
    public String[] getLangsArray() { return langsArray; }
    public int getWidgetId() { return widgetID; }

    //* Return the flag index of the country whose language is selected as the main widget
    public int getFlagResId(int index) {
        int resId;
        try (TypedArray flags = getApplication().getResources().obtainTypedArray(R.array.flags)) {
            resId = flags.getResourceId(index, 0);
//            flags.recycle();
        }
        return resId;
    }
}
