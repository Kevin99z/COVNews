package com.example.cov_news.ui.home;

import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.orm.SugarRecord;

import java.util.ArrayList;

class TabManager extends SugarRecord {
    ArrayList<String> tabs;
    ArrayList<Integer> tabs_on;
    TabLayout tabLayout, tabLayout2;
    Boolean isHidden = true;
    RecyclerView tabList;
    ImageButton addButton;
    public TabManager(TabLayout tabLayout, TabLayout tabLayout2, ImageButton addButton, ArrayList<Integer> tabs_on){
        this.tabLayout = tabLayout;
        this.tabLayout2 = tabLayout2;

    }

}
