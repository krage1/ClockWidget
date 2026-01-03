package ru.krage.clockwidget;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class SharedViewModel extends ViewModel {

    //* Transferring Data Between Fragments
    private final MutableLiveData<Pair<String, Integer>> selectedData = new MutableLiveData<>();

    //* Generic LiveData (for passing data to ContainerActivity)
    public LiveData<Pair<String, Integer>> getSelectedData() {
        return selectedData;
    }
    public void select(String key, int value) {
        selectedData.setValue(new Pair<>(key, value));
    }
}
