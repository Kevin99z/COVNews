package com.example.cov_news.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import com.example.cov_news.News;
import com.example.cov_news.R;
import com.example.cov_news.ui.NewsList;
import com.google.android.material.tabs.TabLayout;

import java.util.*;

public class HomeFragment extends Fragment {
    private ListView listView;
    ArrayAdapter<News> adapter; // todo: write a custom adapter
    private HomeViewModel homeViewModel;
    SwipeRefreshLayout mSwipeRefreshLayout;
    TabLayout visible_tabs;
    TabLayout hidden_tabs;
    ViewPager mViewPager;
    List<String> mTitle;
    List<NewsList> mFragment;
    List<Boolean> is_visible;
    ImageButton addButton;
    boolean movingTab;
    private int getTruePosition(int position){
        int cnt=0, i=0;
        for(; i<mTitle.size(); i++){
            if(is_visible.get(i))cnt++;
            if(cnt==position+1)break;
        }
        return i;
    }
    private void setVisible(String text, boolean visible){
        for(int i=0; i<mTitle.size(); i++){
            if(mTitle.get(i).equals(text)){
                is_visible.set(i, visible);
                break;
            }
        }
    };
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
        visible_tabs = (TabLayout) root.findViewById(R.id.mytab);
        mViewPager = (ViewPager) root.findViewById(R.id.mViewPager);
        addButton = root.findViewById(R.id.add_button);
        hidden_tabs = root.findViewById(R.id.tabs_to_add);
//        visible_tabs.addTab(visible_tabs.newTab().setText("选项卡一"));
        mTitle = new ArrayList<>();
        mTitle.add("news");
        mTitle.add("paper");
        is_visible = new ArrayList<>();
        is_visible.add(true);
        is_visible.add(true);
        movingTab=false;
        mFragment = new ArrayList<>();
        for(int i=0;i<mTitle.size(); i++) mFragment.add(new NewsList(mTitle.get(i)));
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getChildFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(getTruePosition(position));
            }

            @Override
            public int getCount() {
                int cnt = 0;
                for(int i=0; i<is_visible.size(); i++){
                    if(is_visible.get(i)) cnt++;
                }
                return cnt;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(getTruePosition(position));
            }

        };

        addButton.setOnClickListener(view -> {
            if(hidden_tabs.getVisibility()==View.INVISIBLE) {
                hidden_tabs.setVisibility(View.VISIBLE);
                hidden_tabs.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                addButton.setImage("x");
            }
            else {
                hidden_tabs.setLayoutParams(new LinearLayout.LayoutParams(0,0));
                hidden_tabs.setVisibility(View.INVISIBLE);
            }
        });
        visible_tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(hidden_tabs.getVisibility()==View.VISIBLE&&!movingTab){
                    movingTab = true;
                    String text  = (String) tab.getText();
                    setVisible(text, false);
                    adapter.notifyDataSetChanged();
                    visible_tabs.selectTab(visible_tabs.getTabAt(0));
                    hidden_tabs.addTab(hidden_tabs.newTab().setText(text), false);
                    movingTab=false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });

        hidden_tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(movingTab)return;
                movingTab=true;
                String text = (String) tab.getText();
                setVisible(text, true);
                adapter.notifyDataSetChanged();
                hidden_tabs.removeTab(tab);
                movingTab=false;
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                onTabSelected(tab);
            }
        });
        //note: use getActivity() to keep homeViewModel the same throughout main activity
//        final TextView textView = root.findViewById(R.id.text_home);
        mViewPager.setAdapter(adapter);
        visible_tabs.setupWithViewPager(mViewPager);
        return root;
    }

}