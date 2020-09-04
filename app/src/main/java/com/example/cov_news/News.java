package com.example.cov_news;
import com.orm.SugarRecord;

import java.io.*;
import java.util.*;

public class News extends SugarRecord implements Serializable {
    String content;
    String title;
//    String id;
    ArrayList<String> authors=new ArrayList<>();
    Date date;
    String[] labels;
    public News(){

    }
    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public Date getDate() {
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
