package com.muler.covid19.careFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CareVModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CareVModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Loading data.....");
    }

    public LiveData<String> getText() {
        return mText;
    }
}