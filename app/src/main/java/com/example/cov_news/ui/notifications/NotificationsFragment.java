package com.example.cov_news.ui.notifications;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cov_news.R;
import com.example.cov_news.ui.NewsList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private EditText editText;
    private Button button;
    private ProgressBar mProgressBar;
    private Editable query;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        final TextView textView = root.findViewById(R.id.text_notifications);
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        editText = root.findViewById(R.id.TEXT);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH||keyEvent!=null&&keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
                    textView.setText("");
                    query = editText.getText();
                    searchQuery(String.valueOf(query), textView);
                    return true;
                }
                return false;
            }
        });
        return root;
    }

    public void searchQuery(String query, TextView textView){ //搜索所查词语
        Thread t = new Thread(() -> {
        try {
            String apiAddress = "https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=";
            @SuppressLint("DefaultLocale")
            URL url = new URL(apiAddress+String.format("%s", query));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.connect();
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                parse(in, textView);
                in.close();
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void parse(InputStream in, TextView textView) throws IOException { //找到词语信息后读取
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        reader.beginObject();
        while(reader.hasNext()){
            if(reader.nextName().equals("data")){
                reader.beginArray();
                while(reader.hasNext()){
                    reader.beginObject();
                    while(reader.hasNext()) {
                        if (reader.nextName().equals("label")) {
                            textView.append(reader.nextString() + "\n");
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                }
                reader.endArray();
            } else{
                reader.skipValue();
            }
        }
        reader.endObject();
    }

}