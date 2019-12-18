package com.example.testlibrary.ui.delete_location;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DeleteLocationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DeleteLocationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is share fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}