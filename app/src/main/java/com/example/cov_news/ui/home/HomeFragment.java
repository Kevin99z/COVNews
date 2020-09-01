package com.example.cov_news.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.cov_news.MainActivity;
import com.example.cov_news.News;
import com.example.cov_news.NewsParser;
import com.example.cov_news.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HomeFragment extends Fragment {
    final String apiAddress = "https://covid-dashboard.aminer.cn/api/events/list";
    private ListView listView;
    ArrayAdapter<News> adapter; // todo: write a custom adapter
    List<News> newsList;
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        listView = (ListView) root.findViewById(R.id.list_home);
        refresh();
        return root;
    }
    private void updateListView(){
        adapter = new ArrayAdapter<News>(getActivity(),android.R.layout.simple_list_item_1,newsList);
        listView.setAdapter(adapter);
    }
    private void refresh(){
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    int page = 1, size = 20;
                    URL url = new URL(apiAddress+String.format("?type=%s&page=%d&size=%d","paper", page, size));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.connect();
//                    // 获取所有响应头字段
//                    Map<String, List<String>> map = conn.getHeaderFields();
//                    // 遍历所有的响应头字段
//                    for (String key : map.keySet()) {
//                        System.out.println(key + "--->" + map.get(key));
//                    }
                    if (conn.getResponseCode() == 200) {
                        InputStream in = conn.getInputStream();
                        newsList = NewsParser.readJsonStream(in);
                        updateListView();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}