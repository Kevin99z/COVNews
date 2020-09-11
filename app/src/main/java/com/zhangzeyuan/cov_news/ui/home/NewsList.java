package com.zhangzeyuan.cov_news.ui.home;

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
import android.widget.Toast;

import com.zhangzeyuan.cov_news.R;


public class NewsList extends Fragment {
    private NewsListViewModel mViewModel;
    private RecyclerView listView;
    private LinearLayoutManager mLayoutManager;
    private EditText editText;
    private Button button;
    private ProgressBar mProgressBar;
    NewsAdapter adapter;
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

    public void hideKeyboard(View view) {
    InputMethodManager manager = (InputMethodManager) view.getContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE);
    manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
}
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.news_list_fragment, container, false);
        mLayoutManager = new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false);
        mProgressBar = root.findViewById(R.id.progress_bar);
        listView = root.findViewById(R.id.list);
        listView.setLayoutManager(mLayoutManager);
        mSwipeRefreshLayout = root.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if(loading) {
                Toast.makeText(getActivity(), "已经在加载中", Toast.LENGTH_LONG).show();
            }
            else if(searching) {
                button.performClick();
            }
            else mViewModel.initNews();
        }
        );
        listView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            public void onScrollStateChanged(@NonNull RecyclerView view, int scrollState){
                super.onScrollStateChanged(view, scrollState);
                if(mLayoutManager.findLastVisibleItemPosition()==adapter.getItemCount()-1&&!searching&&!loading){
                    mViewModel.fetchNews(adapter.getItemCount());
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
        adapter = new NewsAdapter(getContext(), mViewModel);
        listView.setAdapter(adapter);
        //note: bind adapter and data
        mViewModel.getNewsList().observe(getViewLifecycleOwner(), data -> {
            if(data!=null){
                adapter.setData(data);
            }
            if(mSwipeRefreshLayout.isRefreshing()){
                scrollToTop();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        });
        if(adapter.isEmpty()){
            InitAsyncTask asyncTask = new InitAsyncTask(mViewModel, loading);
            mSwipeRefreshLayout.setRefreshing(true);
            asyncTask.setListView(this);
            asyncTask.execute(firstTime);
        }

    }
    public void initAfterViewCreated() {
        View v = listView.getChildAt(0);
        editText = v.findViewById(R.id.TEXT);
        editText.setHint(String.format("在%s中搜索", type));
        button = v.findViewById(R.id.button);
        //note: init button click listener
        button.setOnClickListener(view -> {
            searching = false;
            mViewModel.stopSearch();
            mViewModel.initNews();
            button.setVisibility(View.INVISIBLE);
            button.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT));
        });
        editText.setOnClickListener(new EditText.OnClickListener(){
            @Override
            public void onClick(View view) {
                scrollToTop();
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH||keyEvent!=null&&keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
                    NewsList.this.hideKeyboard(v);
                    if(searching) mViewModel.stopSearch();
                    //                        adapter.clear();
                    searching = true;
                    mViewModel.search(v.getText(), mProgressBar);
                    Toast.makeText(getContext(), "开始搜索", Toast.LENGTH_SHORT).show();
                    button.setVisibility(View.VISIBLE);
                    button.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    scrollToTop();
                    return true;
                }
                return false;
            }
        });
    }
    public void scrollToTop(){
        listView.getLayoutManager().smoothScrollToPosition(listView, new RecyclerView.State(), 0);
    }
}