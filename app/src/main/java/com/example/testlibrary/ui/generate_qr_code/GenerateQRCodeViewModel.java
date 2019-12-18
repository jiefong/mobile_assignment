package com.example.testlibrary.ui.generate_qr_code;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GenerateQRCodeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GenerateQRCodeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}