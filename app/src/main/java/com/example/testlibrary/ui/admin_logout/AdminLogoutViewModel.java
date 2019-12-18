package com.example.testlibrary.ui.admin_logout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.testlibrary.AdminLogout;

public class AdminLogoutViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AdminLogoutViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
