package com.example.cov_news.ui.graph;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class KGViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public KGViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}