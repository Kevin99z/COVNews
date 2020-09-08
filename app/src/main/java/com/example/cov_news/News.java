package com.example.cov_news;

import com.orm.SugarRecord;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class News extends SugarRecord implements Serializable {
    String content;
    String title;
    public String getSource() {
        return source;
    }

    String source;
    boolean read;

    public String getLongId() {
        return _id;
    }

    String _id;

    long date;
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

    public long getDate() {
        return date;
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
