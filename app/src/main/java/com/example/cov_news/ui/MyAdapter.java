package com.example.cov_news.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cov_news.News;
import com.example.cov_news.NewsItem;
import com.example.cov_news.R;

import java.util.ArrayList;
import java.util.List;

class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    private Context context;
    private List<News> mData;
    private LayoutInflater mLayoutInflator;
    public static final int TYPE_SEARCH = 0;
    public static final int TYPE_NORMAL = 1;
    public boolean isEmpty(){
        return mData.isEmpty();
    }
    public void clear(){
        mData.clear();
        notifyDataSetChanged();
    }
    public MyAdapter(Context c){
        this.context = c;
        mData = new ArrayList<>();
        mLayoutInflator = LayoutInflater.from(c);
    }
    public void addAll(List<News> data){
        int start = mData.size();
        mData.addAll(data);
        notifyItemRangeChanged(start+1, data.size());
    }

    @Override
    public int getItemViewType(int position) {
        return position==0? TYPE_SEARCH : TYPE_NORMAL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if(viewType==TYPE_SEARCH) {
                return new ViewHolder(mLayoutInflator.inflate(R.layout.search_bar, parent, false));
            }
            else{
                return new ViewHolder(mLayoutInflator.inflate(R.layout.view_rv_item, parent, false));
            }
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(position==0) return;
        News news = mData.get(position-1);
        holder.tv.setText(news.getTitle());
        if(news.isRead()) holder.setRead();
        else holder.setUnread();
        //note: set click listener
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setClass(context, NewsItem.class);
            intent.putExtra("news", news);
            context.startActivity(intent);
            news.read();
            news.save();
            mData.set(position-1, news);
            this.notifyItemChanged(position);
        });
    }


    @Override
    public int getItemCount() {
        return mData.size()+1;
    }

    public void setData(List<News> data) {
        if(mData==null){
            mData = data;
            notifyDataSetChanged();
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mData.size();
                }

                @Override
                public int getNewListSize() {
                    return data.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mData.get(oldItemPosition).getId().equals(data.get(newItemPosition).getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    News oldProduct = mData.get(oldItemPosition);
                    if(oldProduct==null) return false;
                    News newProduct = data.get(newItemPosition);
                    if(newProduct.getLongId()==null)
                        System.out.println("oops!");
                    return newProduct.getLongId().equals(oldProduct.getLongId());
                }
            });
            mData = data;
            result.dispatchUpdatesTo(this);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.item_tv);
        }
        public void setRead(){
            itemView.findViewById(R.id.card).setBackgroundColor(Color.argb((float) 0.47,0,0,0));
            tv.setTextColor(Color.argb((float) 0.7,0,0,0));
        }
        public void setUnread(){
            itemView.findViewById(R.id.card).setBackgroundColor(Color.argb((float) 0,0,0,0));
            tv.setTextColor(Color.argb((float) 1,0,0,0));
        }
    }
}
