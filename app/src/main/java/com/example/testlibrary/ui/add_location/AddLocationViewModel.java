package com.example.testlibrary.ui.add_location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AddLocationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AddLocationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is add location fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}