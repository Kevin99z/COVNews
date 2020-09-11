package com.example.cov_news.ui.scholar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cov_news.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static android.R.*;


public class ScholarFragment extends Fragment {
    private final String api = "https://innovaapi.aminer.cn/predictor/api/v1/valhalla/highlight/get_ncov_expers_list?v=2";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_scholar, container, false);
        ListView listView = root.findViewById(R.id.list);
        SwipeRefreshLayout refreshLayout =  root.findViewById(R.id.swiperefresh);
        List<Scholar> scholars = new ArrayList<>();
        Thread t = new Thread(() -> {
            try {
                @SuppressLint("DefaultLocale")
                URL url = new URL(api);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    scholars.addAll(ScholarParser.readJson(in));
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        refreshLayout.setRefreshing(true);
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ArrayAdapter<Scholar> arrayAdapter = new ArrayAdapter<Scholar>(root.getContext(), layout.simple_list_item_1, scholars);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                Intent intent = new Intent(getContext(), ScholarItem.class);
                intent.putExtra("scholar", scholars.get(i));
                startActivity(intent);
            }
        });
        refreshLayout.setRefreshing(false);
        refreshLayout.setEnabled(false);
        return root;
    }
}
