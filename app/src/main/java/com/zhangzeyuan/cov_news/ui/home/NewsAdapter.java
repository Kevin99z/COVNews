package com.zhangzeyuan.cov_news.ui.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhangzeyuan.cov_news.R;

import java.util.ArrayList;
import java.util.List;

class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder>{
    private Context context;
    private List<News> mData;
    private LayoutInflater mLayoutInflator;
    private NewsListViewModel mViewModel;
    public static final int TYPE_SEARCH = 0;
    public static final int TYPE_NORMAL = 1;
    public boolean isEmpty(){
        return mData.isEmpty();
    }
    public void clear(){
        mData.clear();
        notifyDataSetChanged();
    }
    public NewsAdapter(Context c, NewsListViewModel mViewModel){
        this.context = c;
        this.mViewModel = mViewModel;
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
                return new ViewHolder(mLayoutInflator.inflate(R.layout.news_card, parent, false));
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
            mViewModel.getNewsContent(news);
            Intent intent = new Intent();
            intent.setClass(context, NewsItem.class);
            intent.putExtra("news", news);
            context.startActivity(intent);
            news.read();
//            news.save();
            mData.set(position-1, news);
            this.notifyItemChanged(position);
        });
    }


    @Override
    public int getItemCount() {
        return mData.size()+1;
    }

    public void setData(List<News> data) {
//        if(mData==null){
            mData = data;
            notifyDataSetChanged();
//        } else {
//            List<News> old = mData;
//            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
//                @Override
//                public int getOldListSize() {
//                    return old.size();
//                }
//
//                @Override
//                public int getNewListSize() {
//                    return data.size();
//                }
//
//                @Override
//                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
//                    if(oldItemPosition==0&&newItemPosition==0)return true;
//                    if((oldItemPosition==0)^(newItemPosition==0)) return false;
//                    if(newItemPosition>=data.size()||oldItemPosition>=old.size()) return false;
//                    return old.get(oldItemPosition-1).getId().equals(data.get(newItemPosition-1).getId());
//                }
//
//                @Override
//                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
//                    if(oldItemPosition==0&&newItemPosition==0)return true;
//                    if((oldItemPosition==0)^(newItemPosition==0)) return false;
//                    if(newItemPosition>=data.size()||oldItemPosition>=old.size()) return false;
//                    News oldProduct = old.get(oldItemPosition-1);
//                    News newProduct = data.get(newItemPosition-1);
//                    if(oldProduct==null || newProduct==null) return false;
//                    if(newProduct.getLongId()==null)
//                        System.out.println("oops!");
//                    return newProduct.getLongId().equals(oldProduct.getLongId());
//                }
//            });
//            mData = data;
//            result.dispatchUpdatesTo(this);
//        }
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
            itemView.findViewById(R.id.card).setBackgroundColor(Color.rgb(255,255,255));
            tv.setTextColor(Color.argb((float) 1,0,0,0));
        }
    }
}
