package com.muler.covid19.moreInfoFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MoreVModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MoreVModel() {
        mText = new MutableLiveData<>();

        mText.setValue("Loading data.....");

    }

    public LiveData<String> getText() {
        return mText;
    }


}