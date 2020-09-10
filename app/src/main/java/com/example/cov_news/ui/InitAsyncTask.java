package com.example.cov_news.ui;

import android.os.AsyncTask;

@SuppressWarnings("deprecation")
class InitAsyncTask extends AsyncTask<Boolean, Integer, Object> {
    private NewsListViewModel viewModel;
    private Boolean loading;
    private NewsList listView;
    public InitAsyncTask(NewsListViewModel viewModel, Boolean loading) {
        this.viewModel = viewModel;
        this.loading = loading;
    }

    @Override
    protected Boolean doInBackground(Boolean... booleans) {
//        viewModel.fetchNews(booleans.length>0?booleans[0]:false);
        viewModel.initNews();
        return true;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loading = true;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        loading = false;
        if(listView!=null) listView.initAfterViewCreated();
    }

    public void setListView(NewsList listView) {
        this.listView = listView;
    }
}
