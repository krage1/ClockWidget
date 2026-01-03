package ru.krage.clockwidget;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LangViewModel extends ViewModel {

    private final MutableLiveData<Integer> selectedLangIndex = new MutableLiveData<>();
    private List<String> languages;
    private List<Integer> flagIds;
    private List<String> langCodes;

    public void initLang(Context context) {
        if (languages == null) {
            languages = Arrays.asList(context.getResources().getStringArray(R.array.lang_array));
            langCodes = Arrays.asList(context.getResources().getStringArray(R.array.langs));


            try (TypedArray typedFlags = context.getResources().obtainTypedArray(R.array.flags)) {

                int length = typedFlags.length();
                flagIds = new ArrayList<>(length);

                for (int i = 0; i < length; i++) {
                    int resourceId = typedFlags.getResourceId(i, 0);

                    if (resourceId != 0) {
                        flagIds.add(resourceId);
                    }
                }
            }

            LocalPrefs prefs = new LocalPrefs(context);
            int widgetID = prefs.getWidgetID();
            selectedLangIndex.setValue(prefs.getIndexLanguage(widgetID));
        }
    }
    public LiveData<Integer> getSelectedLangIndex() {
        return selectedLangIndex;
    }
    public List<String> getLanguages() {
        return languages;
    }
    public List<Integer> getFlagIds() {
        return flagIds;
    }
    public String getLangCode(int index) {
        if (langCodes != null && index >= 0 && index < langCodes.size()) {
            return langCodes.get(index);
        }
        return null;
    }
    public void selectLanguage(Context context, int index) {
        selectedLangIndex.setValue(index);
        LocalPrefs prefs = new LocalPrefs(context);
        int widgetID = prefs.getWidgetID();
        prefs.setIndexLanguage(index, widgetID);
    }
}
