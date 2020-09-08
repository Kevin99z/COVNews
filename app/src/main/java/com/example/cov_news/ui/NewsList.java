package com.example.cov_news.ui;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.cov_news.R;

public class NewsList extends Fragment {
    private NewsListViewModel mViewModel;
    private RecyclerView list;
    private RecyclerView.LayoutManager mLayoutManager;
    private EditText editText;
    private Button button;
    private ProgressBar mProgressBar;
//    private int mScrollY;
//    private boolean showSearchBar = true;
//    private int searchHeight;
    MyAdapter adapter;
    SwipeRefreshLayout mSwipeRefreshLayout;
    String type;
    Boolean loading;
    Boolean editInit;
    Boolean firstTime;
    Boolean searching;
    public NewsList(String type){
        this.type = type;
        editInit = false;
        loading = false;
        firstTime = true;
        searching = false;
    }
//    public static NewsList newInstance() {
//        return new NewsList();
//    }
    public void hideKeyboard(View view) {
    InputMethodManager manager = (InputMethodManager) view.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);
    manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
}
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        View root = inflater.inflate(R.layout.news_list_fragment, container, false);
        mSwipeRefreshLayout = root.findViewById(R.id.swiperefresh);
        mProgressBar = root.findViewById(R.id.progress_bar);
        list = root.findViewById(R.id.list);
        list.setLayoutManager(mLayoutManager);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
                    // This method performs the actual data-refresh operation.
                    // The method calls setRefreshing(false) when it's finished.
            if(searching || loading) return;
                    adapter.clear();
                    mViewModel.refresh();
                }
        );
        list.addOnScrollListener(new RecyclerView.OnScrollListener(){
            public void onScrollStateChanged(@NonNull RecyclerView view, int scrollState){
                super.onScrollStateChanged(view, scrollState);
                if(!view.canScrollVertically(1)&&!searching){
                    mViewModel.getMoreNews(loading);
                }
                if(editText !=null && editText.hasFocus()){//todo:check if bug
                    hideKeyboard(editText);
                    editText.clearFocus();//取消焦点
                }
            }
        });
        return root;
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this.getParentFragment().getActivity()).get(type, NewsListViewModel.class);
        mViewModel.init(type);
        adapter = new MyAdapter(getContext());
        list.setAdapter(adapter);
        //note: bind adapter and data
        mViewModel.getNewsList().observe(getViewLifecycleOwner(), data -> {
//            if(editText==null){
//                View first = list.getChildAt(0);
//                if(first!=null) editText=first.findViewById(R.id.search_bar);
//            }
//            if(editText!=null && !editInit){
//                editInit=true;
////                editText.setOnClickListener(view -> {
////
////                });
//                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//                    @Override
//                    public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
//                        if (actionId == EditorInfo.IME_ACTION_SEARCH||keyEvent!=null&&keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
//                            NewsList.this.hideKeyboard(v);
//                            adapter.clear();
//                            mViewModel.search(v.getText(), mProgressBar);
//                            return true;
//                        }
//                        return false;
//                    }
//                });
//            }
            if(data!=null){
//                adapter.addAll(data);
                adapter.setData(data);
                mSwipeRefreshLayout.setRefreshing(false);
            }
            if(firstTime){
                firstTime=false;
                scrollToTop();
            }
        });
        if(adapter.isEmpty()){
            FetchAsyncTask asyncTask = new FetchAsyncTask(mViewModel, loading);
            asyncTask.setListView(this);
            asyncTask.execute(firstTime);
        }

    }
    public void initAfterViewCreated() {
        View v = list.getChildAt(0);
        editText = v.findViewById(R.id.TEXT);
        button = v.findViewById(R.id.button);
        //note: init button click listener
        button.setOnClickListener(view -> {//todo:rewrite search logic
            searching = false;
            adapter.clear();
            mViewModel.fetchNews(true);
            button.setVisibility(View.INVISIBLE);
            button.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH||keyEvent!=null&&keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
                    NewsList.this.hideKeyboard(v);
                    if(!searching) {
                        adapter.clear();
                        searching = true;
                        mViewModel.search(v.getText(), mProgressBar);
                        button.setVisibility(View.VISIBLE);
                        button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                    scrollToTop();
                    return true;
                }
                return false;
            }
        });
    }
    public void scrollToTop(){
        list.getLayoutManager().smoothScrollToPosition(list, new RecyclerView.State(), 0);
    }
}