package com.example.cov_news.ui;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

import com.example.cov_news.News;
import com.example.cov_news.NewsParser;
import com.orm.SugarRecord;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class NewsModel {
    final String apiAddress = "https://covid-dashboard.aminer.cn/api/events/list";
    private ArrayList<Integer> pagination;
    final int size = 20;
    int page=1;
    String type;
    List<News> sortedNews;
    boolean updating, searching;
    int total_lo_bound;
    public NewsModel(String type) {
        this.type = type;
        pagination = new ArrayList<>(Arrays.asList(1,0,0));
        total_lo_bound = 300000;
        loadNewsFromDataBase();
        page = sortedNews.size()/20;
    }
    private void loadNewsFromDataBase(){
        sortedNews = News.findWithQuery(News.class, String.format("SELECT * FROM NEWS WHERE type='%s' ORDER BY date DESC", type));
    }
    public void update(){
        updating = true;
        pagination.set(2, Math.max(total_lo_bound/size, pagination.get(2)));
        Thread t= new Thread(()-> {
            boolean finished=false;
            while (!finished && (page < pagination.get(2) || page == 1)) {
                for (int max_try = 0; max_try < 300; max_try++) {
                    try {
                        @SuppressLint("DefaultLocale")
                        URL url = new URL(apiAddress + String.format("?type=%s&page=%d&size=%d", type, page, size));
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(10000);
                        conn.setReadTimeout(10000);
                        conn.connect();
                        if (conn.getResponseCode() == 200) {
                            InputStream in = conn.getInputStream();
                            List<News> input = NewsParser.readJsonStream(in, pagination);
                            total_lo_bound = (pagination.get(2)-1)*size;
                            in.close();
                            boolean near_finished = sortedNews.size()> total_lo_bound;
                            for (int i = 0; i < input.size(); i++) {
                                News item = input.get(i);
                                item.type = type;
                                if(near_finished) {
                                    News cache = SugarRecord.findById(News.class, item.getId());
                                    if (cache != null && cache.getLongId().equals(item.getLongId())) {
                                        finished = true;
                                        input = input.subList(0, i);
                                        break;
                                    }
                                }
                            }
                            SugarRecord.saveInTx(input);
                            loadNewsFromDataBase();
                            page++;
                        }
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            updating = false;
        });
        t.start();
    }
    public List<News> getNews(int lo, int hi) {
        if(sortedNews.size()<hi&&!updating) update();
        while(sortedNews.size()<hi&&updating) {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return sortedNews.subList(lo, (Math.min(sortedNews.size(), hi)));
    }
    public void search(CharSequence text, MutableLiveData<List<News>> result){
        List<News> tmp = new ArrayList<>();
        int len = tmp.size();
        for(News item : sortedNews){
            if(item.getTitle().contains(text)||item.getContent().contains(text))
            {
                tmp.add(item);
                if(tmp.size()-10>len){
                    len = tmp.size();
                    result.postValue(tmp);
                }
            }
        }
    }

    public void search(CharSequence text, MutableLiveData<List<News>> result, SearchAsyncTask searchAsyncTask) {
        searching = true;
        int current_size = sortedNews.size();
        if(current_size<total_lo_bound) update();
        loadNewsFromDataBase();
        List<News> tmp = new ArrayList<>();
        int len = tmp.size();
        int cnt = 0;
        for(int i = 0; updating||i<sortedNews.size(); i++){
            if(!searching) break;
            cnt++;
            while(i>=sortedNews.size()){
                try {
                    sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            News item = sortedNews.get(i);
            if(item.getTitle().contains(text)||item.getContent().contains(text))
            {
                tmp.add(item);
                if(tmp.size()-10>len){
                    len = tmp.size();
                    result.postValue(tmp);
                    searchAsyncTask.onProgressUpdate(cnt*100/total_lo_bound);
                }
            }
        }
        searching = false;
    }
    public void stopSearch(){
        searching = false;
    }
}
