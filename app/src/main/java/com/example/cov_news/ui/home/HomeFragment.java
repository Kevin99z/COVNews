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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
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
    TabLayout mytab;
    ViewPager mViewPager;
    List<String> mTitle;
    List<NewsList> mFragment;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mytab = (TabLayout) root.findViewById(R.id.mytab);
        mViewPager = (ViewPager) root.findViewById(R.id.mViewPager);
        mytab.addTab(mytab.newTab().setText("选项卡一"));
        mTitle = new ArrayList<>();
        mTitle.add("news");
        mTitle.add("paper");
        mFragment = new ArrayList<>();
        mFragment.add(new NewsList("news"));
        mFragment.add(new NewsList("paper"));
        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitle.get(position);
            }
        });
        mytab.setupWithViewPager(mViewPager);

        //note: use getActivity() to keep homeViewModel the same throughout main activity
        homeViewModel = ViewModelProviders.of(getActivity()).get(HomeViewModel.class);
//        final TextView textView = root.findViewById(R.id.text_home);


        return root;
    }



}