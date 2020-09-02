package com.example.cov_news.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cov_news.News;
import com.example.cov_news.NewsParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NewsListViewModel extends ViewModel {
    final String apiAddress = "https://covid-dashboard.aminer.cn/api/events/list";
    private int page;
    String type;
    int size = 20;
    MutableLiveData<List<News>> newsFeed;//the newly fetched news
    List<News> newsList;// the whole news list
    // TODO: Implement the ViewModel
    public NewsListViewModel(){
        newsFeed = new MutableLiveData<>();
        newsList = new ArrayList<News>();
        newsFeed.setValue(newsList);
        page = 1;
    }
    public void setType(String type){this.type=type;}
    public void refresh(){
        newsList.clear();
        page = 1;
        fetchNews();
    }
    public LiveData<List<News>> getNewsFeed(){return newsFeed;}
    public List<News> getNewsList(){return newsList;}
    public News getNewsAt(int id){return newsList.get(id);}
    public void fetchNews(){
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    URL url = new URL(apiAddress+String.format("?type=%s&page=%d&size=%d", type, page, size));
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
                        List<News> tmp= NewsParser.readJsonStream(in);
                        newsList.addAll(tmp);
                        newsFeed.postValue(tmp);
                        page++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
//        try {
//            t.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }
}