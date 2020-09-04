package com.example.cov_news.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.data.Set;
import com.example.cov_news.DailyRecord;
import com.example.cov_news.JsonHelper;
import com.orm.SugarRecord;



public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MutableLiveData<List<DataEntry>> getSeriesData() {
        return seriesData;
    }

    private MutableLiveData<List<DataEntry>> seriesData;
    final String global_url= "https://covid2019-api.herokuapp.com/v2/current";

    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        seriesData = new MutableLiveData<>();
        mText.setValue("");
    }
    static class CountryEntry extends ValueDataEntry{
        public CountryEntry(HashMap<String, Object> map){
            super(((String)map.get("location")).replace("'"," "), (Integer)map.get("confirmed"));
            String[] keys= map.keySet().toArray(new String[keySet().size()]);
            for(int i=1; i<keys.length; i++) setValue(keys[i], (Integer)map.get(keys[i]));
        }
    }
    public void fetchData(){
        System.out.println("Fetching data...");
            Thread t= new Thread(()->{
                String s=null;
                Date today = new Date(System.currentTimeMillis());
                DailyRecord cache = SugarRecord.findById(DailyRecord.class, today.getTime());
                if(cache!=null) s=cache.getContent();
                else
                try {
                    URL url= new URL(global_url);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(5000);
                    conn.connect();
                    if(conn.getResponseCode()==200){
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder json = new StringBuilder();
                        String tmp = reader.readLine();
                        String pattern = "(\\[[\\s\\S]*\\])";
                        Pattern p = Pattern.compile(pattern,Pattern.MULTILINE);
                        while(tmp!=null){
                            json.append(tmp);
                            tmp = reader.readLine();
                        }
                        Matcher m = p.matcher(json);
                        if(m.find()) {
                            s = m.group(1);
//                            mText.postValue(m.group(1));
                            DailyRecord record = new DailyRecord(s);
                            record.save();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(s!=null) {
                    List<Object> list = JsonHelper.jsonToMapList(s);
                    List<DataEntry> entries = new ArrayList<>();
                    for (Object o : list) {
                        HashMap<String, Object> map = (LinkedHashMap<String, Object>) o;
                        entries.add(new CountryEntry(map));
                    }
                    System.out.println("posting seriesData");
                    seriesData.postValue(entries);
                }
                System.out.println("Data fetched");
            });
            t.start();
    }
    public LiveData<String> getText() {
        return mText;
    }
}

