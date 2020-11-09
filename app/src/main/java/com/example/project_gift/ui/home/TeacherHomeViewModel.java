package com.example.project_gift.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TeacherHomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TeacherHomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is teacher home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}