package com.example.cov_news.ui;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.cov_news.News;
import com.example.cov_news.R;

import java.util.List;

public class NewsList extends Fragment {

    private NewsListViewModel mViewModel;
    private ListView mListView;
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
        mListView = root.findViewById(R.id.list);
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
        mListView.setOnScrollListener(new AbsListView.OnScrollListener(){
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
            }
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){
                // 当不滚动时
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 判断是否滚动到底部
                    if (view.getLastVisiblePosition() == view.getCount() - 1) {
                        //加载更多功能的代码
                        mViewModel.fetchNews();
                    }
                }
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this.getParentFragment().getActivity()).get(type, NewsListViewModel.class);
        mViewModel.setType(type);
        adapter = new ArrayAdapter<News>(getActivity(),android.R.layout.simple_list_item_1, mViewModel.getNewsList());
        mListView.setAdapter(adapter);
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

}