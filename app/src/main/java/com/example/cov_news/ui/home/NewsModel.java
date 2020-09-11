package com.example.cov_news.ui.home;

import android.annotation.SuppressLint;

import androidx.lifecycle.MutableLiveData;

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
    final String api_events = "https://covid-dashboard.aminer.cn/api/dist/events.json";
    final String api_event  = "https://covid-dashboard.aminer.cn/api/event/";
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
        sortedNews = new ArrayList<>();
    }
    public void loadNewsFromDataBase(){
        sortedNews = News.findWithQuery(News.class, "SELECT * FROM NEWS ORDER BY stamp DESC");
    }
    public void fetchFromEvents(){
        updating = true;
        Thread t = new Thread(()->{
            try {
                URL url = new URL(api_events);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    List<News> input = NewsParser.readJsonStream(in, pagination);
                    for(int i =0; i<input.size(); i++){
                        News item = input.get(i);
//                        News cache = SugarRecord.findById(News.class, item.getId());
//                        if (cache != null && cache.getLongId().equals(item.getLongId())) {
//                            input = input.subList(0, i);
//                            break;
//                        }
                        item.setStamp(System.currentTimeMillis());
                    }
                    sortedNews = input;
                    new Thread(()-> SugarRecord.saveInTx(input)).start();
                }
                updating = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }
    public void getContent(News news){
        Thread t = new Thread(()-> {
            News result = null;
            try {
                URL url = new URL(api_event + news.getLongId());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    result = NewsParser.readEvent(in);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (result == null) news.setContent("没有正文内容");
            else {
                news.setContent(result.getContent());
                news.setTime(result.getTime());
                news.setSource(result.getSource());
                news.save();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        if(sortedNews.size()<hi&&!updating) {
            fetchFromEvents();
        }
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
        loadNewsFromDataBase();
        List<News> tmp = new ArrayList<>();
        int len = tmp.size();
        int cnt = 0;
        int total = sortedNews.size();
        for(int i = 0; i<total; i++){
            if(!searching) break;
            cnt++;
            News item = sortedNews.get(i);
            if(item.getTitle().contains(text))
            {
                tmp.add(item);
                if(tmp.size()-10>len){
                    len = tmp.size();
                    result.postValue(tmp);
                    searchAsyncTask.onProgressUpdate(cnt*100/total);
                }
            }
        }
        searching = false;
    }
    public void stopSearch(){
        searching = false;
    }
}
