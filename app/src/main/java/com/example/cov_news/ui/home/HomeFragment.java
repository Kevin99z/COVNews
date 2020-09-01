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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cov_news.News;
import com.example.cov_news.R;

import java.util.List;

public class HomeFragment extends Fragment {
    private ListView listView;
    ArrayAdapter<News> adapter; // todo: write a custom adapter
    private HomeViewModel homeViewModel;
    SwipeRefreshLayout mSwipeRefreshLayout;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        mSwipeRefreshLayout = root.findViewById(R.id.swiperefresh);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        adapter = new ArrayAdapter<News>(getActivity(),android.R.layout.simple_list_item_1, homeViewModel.getNewsList());
        listView = (ListView) root.findViewById(R.id.list_home);
        listView.setAdapter(adapter);
        homeViewModel.getNewsFeed().observe(getViewLifecycleOwner(), new Observer<List<News>>() {
            @Override
            public void onChanged(@Nullable List<News> newsFeed) {
                if(newsFeed!=null)
                    adapter.addAll(newsFeed);
            }
        });
        update();

        return root;
    }
    // todo: 增加上拉获取新的新闻的功能
    // todo: 下拉刷新全部新闻

    private void update(){
        homeViewModel.fetchNews();
    }


}