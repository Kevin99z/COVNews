package com.example.cov_news.ui.home;

import android.widget.ProgressBar;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Thread.sleep;

public class NewsListViewModel extends ViewModel {
    private ArrayList<Integer> pagination;
    final String apiAddress = "https://covid-dashboard.aminer.cn/api/events/list";
    private int page;
    private NewsModel dataModel;
    String type;
    final int size = 20;
    int onDisplay = 0;
    SearchAsyncTask searchTask;
    MutableLiveData<List<News>> newsList;// the whole news list
    MutableLiveData<List<News>> newsFeed;//the newly fetched news

    public MutableLiveData<List<News>> getNewsList() {
        return newsList;
    }


    List<News> records; // offline records
    // TODO: Implement the ViewModel
    public NewsListViewModel(){

//        newsFeed = new MutableLiveData<>();
//        newsList = new ArrayList<News>();
//        newsFeed.setValue(newsList);
        page = 1;
        pagination = new ArrayList<Integer>(Arrays.asList(1,0,0));
        newsList = new MutableLiveData<>();
    }

    public void init(String type){this.type=type; dataModel=new NewsModel(type);}
//    public void refresh(){
//        page = 1;
//        onDisplay = 0;
//        Thread t = new Thread(()-> newsList.postValue(dataModel.getNews(0, size)));
//        t.start();
//    }
//    public LiveData<List<News>> getNewsFeed(){return newsFeed;}
//    public List<News> getNewsList(){return newsList;}
//    public News getNewsAt(int pos){return newsList.get(pos);}
//    private void loadFromDatabase(){
//        //todo: add field timestamp
//        if(records==null) records = News.listAll(News.class, "date DESC");
//        int start = newsList.size();
//        List<News> tmp = new ArrayList<>();
////        for(int i=1; i<=size; i++){
////            News item = News.findById(News.class, (long)(i+start));
////            tmp.add(item);
////        }
//        newsFeed.postValue(records.subList(start, start+size));
//        page++;
//    }
//    public void readNews(long id){
//        SugarRecord.findById(News.class, id).read();
//    }

    public void initNews(){//todo:consider do update
        Thread t = new Thread(()-> {
            dataModel.loadNewsFromDataBase();
            newsList.postValue(dataModel.getNews(0, page*size));
        });
        t.start();
    }
    public void fetchNews(int onDisplay){
        Thread t = new Thread(()-> newsList.postValue(dataModel.getNews(0, onDisplay+size)));
        t.start();
        page++;
    }
    public void fetchNews(Boolean update){
//        Thread t = new Thread(() -> {
//            try {
//                @SuppressLint("DefaultLocale")
//                URL url = new URL(apiAddress+String.format("?type=%s&page=%d&size=%d", type, page, size));
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setRequestMethod("GET");
//                conn.setConnectTimeout(10000);
//                conn.setReadTimeout(10000);
//                conn.connect();
//                if (conn.getResponseCode() == 200) {
//                    InputStream in = conn.getInputStream();
//                    List<News> input= NewsParser.readJsonStream(in, pagination);
//                    in.close();
////                    int start = newsList.size()+100000;
//                    List<News> new_news = new ArrayList<>();
//                    for(int i=1; i<=size; i++) {
//                        News item = input.get(i-1);
//                        News tmp = SugarRecord.findById(News.class, item.getId());
//                        if(tmp==null)
//                            new_news.add(item);
//                        else if(tmp.getDate()==item.getDate()&&tmp.isRead())
//                            item.read();
//                    }
//                    newsFeed.postValue(input);
//                    SugarRecord.saveInTx(new_news);
//                    page++;
//                }
//                else loadFromDatabase();
//            } catch (IOException e) {
//                loadFromDatabase();
//            }
//        });
//        t.start();
//        try {
//            t.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        if(newsList.getValue()!=null)onDisplay = newsList.getValue().size();
        if((page-1)*size > onDisplay) {
            if(!update) return; // when all news are fetched
            else {//note: this is used after search
                Thread t = new Thread(()-> newsList.postValue(dataModel.getNews(0, (page-1)*size)));
                t.start();
                return;
            }
        }
        if(update) dataModel.update();

        Thread t = new Thread(()-> {
            newsList.postValue(dataModel.getNews(0, onDisplay+size));
        });
        t.start();
        page++;
    }
    public void search(CharSequence text, ProgressBar bar) {
//        Thread t = new Thread(()-> dataModel.search(text, newsList));
//        t.start();
        searchTask = new SearchAsyncTask(bar, newsList, dataModel);
        searchTask.execute(text.toString());
    }
    public void stopSearch(){
        if(searchTask!=null) searchTask.cancel(true);
    }
//    public void getMoreNews(Boolean loading){
//        fetchNews();
//        if(!loading) {
//            InitAsyncTask asyncTask = new InitAsyncTask(this, loading);
//            asyncTask.execute();
//        }
//    }

    //        page = 1;
//
//        @SuppressLint("DefaultLocale")
//        Thread t = new Thread(()->{
//            try {
//                List<News> results = new ArrayList<>();
//                boolean finished = false;
//                while(!finished){
//                    URL url = new URL(apiAddress+String.format("?type=%s&page=%d&size=%d", type, page, size));
//                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                    conn.setRequestMethod("GET");
//                    conn.setConnectTimeout(10000);
//                    conn.setReadTimeout(10000);
//                    conn.connect();
//                    if (conn.getResponseCode() == 200) {
//                        InputStream in = conn.getInputStream();
//                        List<News> input = NewsParser.readJsonStream(in, pagination);
//                        if(pagination.get(2)==page) finished = true;
//                        in.close();
//    //                    int start = newsList.size()+100000;
//                        for (int i = 1; i <= size; i++) {
//                            News item = input.get(i - 1);
//                            if (item.getTitle().contains(text) || item.getContent().contains(text))
//                                results.add(item);
//                        }
//                        newsFeed.postValue(results);
//                        page++;
//                }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        });
//        t.start();
//    }
    public void getNewsContent(News news){
        dataModel.getContent(news);
    }
}