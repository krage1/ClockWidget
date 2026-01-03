package ru.krage.clockwidget;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BackgroundViewModel extends ViewModel implements IConstants {

    private final MutableLiveData<Integer> selectedBkgIndex = new MutableLiveData<>();
    private List<String> backgrounds;
    private List<Integer> colors;
    private List<Integer> icons;
    private int widgetID;

    public void initBkg(Context context) {
        if (backgrounds == null) {
            backgrounds = Arrays.asList(context.getResources().getStringArray(R.array.background_array));

            try (TypedArray taColors = context.getResources().obtainTypedArray(R.array.colors)){

                // A small but useful optimization: setting the initial capacity of the list.
                // This eliminates the need for ArrayList to expand its internal array several times.
                int length = taColors.length();
                colors = new ArrayList<>(length);

                for (int i = 0; i < length; i++) {
                    // Get the resource ID. 0 is the default safe value.
                    int resourceId = taColors.getResourceId(i, 0);

                    // Additional check in case the XML array mistakenly contains
                    // Invalid value (e.g., @null). 0 is not a valid ID.
                    if (resourceId != 0) {
                        colors.add(resourceId);
                    }
                }
            }

            try (TypedArray taIcons = context.getResources().obtainTypedArray(R.array.icons)) {

                int length = taIcons.length();
                icons = new ArrayList<>(length);

                for (int i = 0; i < length; i++) {
                    int resourceId = taIcons.getResourceId(i, 0);

                    if (resourceId != 0) {
                        icons.add(resourceId);
                    }
                }
            }

            LocalPrefs prefs = new LocalPrefs(context);
            widgetID = prefs.getWidgetID();
            selectedBkgIndex.setValue(prefs.getData(KEY_INDEX_BACKGROUND, widgetID, -1));
        }
    }
    public LiveData<Integer> getSelectedBkgIndex() {
        return selectedBkgIndex;
    }
    public List<String> getBackgrounds() {
        return backgrounds;
    }
    public List<Integer> getColors() {
        return colors;
    }

    public List<Integer> getIcons() {
        return icons;
    }
    public void selectBackground(Context context, int index) {
        selectedBkgIndex.setValue(index);
        new LocalPrefs(context).setData(KEY_INDEX_BACKGROUND, index, widgetID);
    }
}
