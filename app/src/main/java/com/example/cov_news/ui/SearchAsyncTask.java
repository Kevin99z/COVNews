package com.example.cov_news.ui;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.MutableLiveData;

import com.example.cov_news.News;

import java.util.List;

@SuppressWarnings("deprecation")
class SearchAsyncTask extends AsyncTask<String, Integer, Void> {
    private ProgressBar progressBar;
    private MutableLiveData<List<News>> newsList;
    private NewsModel dataModel;
    public SearchAsyncTask(ProgressBar bar, MutableLiveData<List<News>> newsList, NewsModel model){
        this.progressBar = bar;
        this.newsList = newsList;
        this.dataModel = model;
    }
    @Override
    protected Void doInBackground(String... strings) {
        dataModel.search(strings[0], newsList, this);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressBar.setProgress(values[0]);
    }
}
