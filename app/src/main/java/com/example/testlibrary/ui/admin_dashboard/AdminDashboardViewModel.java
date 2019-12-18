package com.example.testlibrary.ui.admin_dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AdminDashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public AdminDashboardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is tools fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}