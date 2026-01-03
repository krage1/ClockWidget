package ru.krage.clockwidget;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LayoutViewModel extends ViewModel implements IConstants{

    private final MutableLiveData<Integer> selectedStyleIndex = new MutableLiveData<>();
    private List<String> styles;
    private List<Integer> styleImages;
    private int widgetID;

    public void initStyle(Context context) {
        if (styles == null) {
            styles = Arrays.asList(context.getResources().getStringArray(R.array.style_array));

            try (TypedArray ta = context.getResources().obtainTypedArray(R.array.styles)) {

                int length = ta.length();
                styleImages = new ArrayList<>(length);

                for (int i = 0; i < length; i++) {
                    int resourceId = ta.getResourceId(i, 0);

                    if (resourceId != 0) {
                        styleImages.add(resourceId);
                    }
                }
            }

            LocalPrefs prefs = new LocalPrefs(context);
            widgetID = prefs.getWidgetID();
            selectedStyleIndex.setValue(prefs.getData(KEY_INDEX_LAYOUT, widgetID, -1));
        }
    }
    public LiveData<Integer> getSelectedStyleIndex() {
        return selectedStyleIndex;
    }
    public List<String> getStyles() {
        return styles;
    }
    public List<Integer> getStyleImages() {
        return styleImages;
    }
    public void selectStyle(Context context, int index) {
        selectedStyleIndex.setValue(index);
        new LocalPrefs(context).setData(KEY_INDEX_LAYOUT, index, widgetID);
    }
}
