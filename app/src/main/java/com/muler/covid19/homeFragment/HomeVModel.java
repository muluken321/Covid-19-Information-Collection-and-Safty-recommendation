package com.muler.covid19.homeFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeVModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeVModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Loading data.....");
    }

    public LiveData<String> getText() {
        return mText;
    }
}