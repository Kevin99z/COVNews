package com.example.cov_news;

import com.orm.SugarRecord;

import java.io.*;

public class News extends SugarRecord implements Serializable {
    public void setContent(String content) {
        this.content = content;
    }

    String content;
    String title;
    public String type;
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    String source;
    boolean read;

    public String getLongId() {
        return _id;
    }

    String _id;

    public void setStamp(long stamp) {
        this.stamp = stamp;
    }

    long stamp;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    long time;
    public News(){
        read = false;
    }
    public String getContent() {
        return content;
    }
    public void read(){
        read = true;
    }
    public boolean isRead(){
        return read;
    }
    public String getTitle() {
        return title;
    }

    public long getStamp() {
        return stamp;
    }
    @Override
    public String toString() {
        return title;
    }
    // com.example.cov_news.News contains:
    // "_id", "authors"(list),
    // "content", "date"
    // "entities": [ 
    //                { 
    //                    "label": "attack rate",                      "url": "http://xlore.org/instance/eni1182928" 
    //                } 
    //            ],
    // "influence"
    //
}
