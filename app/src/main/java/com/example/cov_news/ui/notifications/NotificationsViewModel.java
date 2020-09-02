package com.example.cov_news.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NotificationsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is notifications fragment.\npublic class NotificationsViewModel extends ViewModel {\n" +
                "\n" +
                "    private MutableLiveData<String> mText;\n" +
                "\n" +
                "    public NotificationsViewModel() {\n" +
                "        mText = new MutableLiveData<>();\n" +
                "        mText.setValue(\"This is notifications fragment\");\n" +
                "    }\n" +
                "\n" +
                "    public LiveData<String> getText() {\n" +
                "        return mText;\n" +
                "    }\n" +
                "}");
    }

    public LiveData<String> getText() {
        return mText;
    }
}