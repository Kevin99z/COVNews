package com.example.cov_news.ui;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cov_news.News;
import com.example.cov_news.R;
import com.example.cov_news.ui.home.HomeViewModel;

import java.util.List;

public class NewsList extends Fragment {

    private NewsListViewModel mViewModel;
    private ListView listView;
    ArrayAdapter<News> adapter; // todo: write a custom adapter
    SwipeRefreshLayout mSwipeRefreshLayout;
    String type;
    public NewsList(String type){
        this.type = type;
    }
//    public static NewsList newInstance() {
//        return new NewsList();
//    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.news_list_fragment, container, false);
        mSwipeRefreshLayout = root.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        // This method performs the actual data-refresh operation.
                        // The method calls setRefreshing(false) when it's finished.
                        mViewModel.refresh();
                    }
                }
        );
        listView = root.findViewById(R.id.list_home);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this.getParentFragment().getActivity()).get(type, NewsListViewModel.class);
        mViewModel.setType(type);
        adapter = new ArrayAdapter<News>(getActivity(),android.R.layout.simple_list_item_1, mViewModel.getNewsList());
        listView.setAdapter(adapter);
        mViewModel.getNewsFeed().observe(getViewLifecycleOwner(), new Observer<List<News>>() {
            @Override
            public void onChanged(@Nullable List<News> newsFeed) {
                if(newsFeed!=null) {
                    adapter.addAll(newsFeed);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        if(adapter.isEmpty())mViewModel.fetchNews();
    }
    // todo: 增加上拉获取新的新闻的功能

}