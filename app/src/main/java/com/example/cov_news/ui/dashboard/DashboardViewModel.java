package com.example.cov_news.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.*;

import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.data.Set;
import com.example.cov_news.DailyRecord;
import com.example.cov_news.JsonHelper;
import com.example.cov_news.ProvInfo;
import com.orm.SugarRecord;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;


public class DashboardViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<List<ProvInfo>> provInfo;
    private Date today = new Date(System.currentTimeMillis());

    public MutableLiveData<List<DataEntry>> getSeriesData() {
        return seriesData;
    }
    public MutableLiveData<List<ProvInfo>> getProvInfo(){return provInfo;}
    private MutableLiveData<List<DataEntry>> seriesData;
    final String global_url= "https://covid2019-api.herokuapp.com/v2/current";
    final String china_url = "https://zh.wikipedia.org/wiki/2019冠状病毒病中国大陆疫情";
    public DashboardViewModel() {
        mText = new MutableLiveData<>();
        seriesData = new MutableLiveData<>();
        provInfo = new MutableLiveData<>();
        mText.setValue("");
    }
    static class CountryEntry extends ValueDataEntry{
        public CountryEntry(HashMap<String, Object> map){
            super(((String)map.get("location")).replace("'"," "), (Integer)map.get("confirmed"));
            String[] keys= map.keySet().toArray(new String[keySet().size()]);
            for(int i=1; i<keys.length; i++) setValue(keys[i], (Integer)map.get(keys[i]));
        }
    }
    public void fetchChinaData(){
        System.out.println("Fetching China data...");
        Thread t = new Thread(()->{
            try{
                URL url= new URL(china_url);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setReadTimeout(5000);
                conn.connect();
                StringBuilder html = new StringBuilder();
                if(conn.getResponseCode()==200) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String tmp = reader.readLine();
                    while (tmp != null) {
                        html.append(tmp);
                        tmp = reader.readLine();
                    }
                }

                Document doc = Jsoup.parse(html.toString());
                if(doc==null) throw new IOException();
                Element table = doc.select("#mf-section-1 > table > tbody").first();
                ArrayList<ProvInfo> provinces = new ArrayList<>();
                Elements rows = table.children();
                for(int id=1; id<33; id++){
                    Element row = rows.get(id);
                    String prov_name= row.getElementsByTag("th").first().text();
                    Elements elements = row.getElementsByTag("td");
                    String confirmed = elements.get(0).text();
                    String death = elements.get(1).text();
                    String active = elements.get(7).text();
                    prov_name = prov_name.replace("區","区");
                    if(id==31) prov_name="新疆维吾尔自治区";
                    ProvInfo prov = new ProvInfo(prov_name, confirmed, death, active);
                    prov.setId(id+today.getTime());
                    prov.save();
                    provinces.add(prov);
                }
                provInfo.postValue(provinces);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }
    public void fetchGlobalData(){
        System.out.println("Fetching global data...");
            Thread t= new Thread(()->{
                String s=null;
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

