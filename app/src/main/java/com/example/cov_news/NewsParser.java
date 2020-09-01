package com.example.cov_news;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NewsParser{
    static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    public static List<News> readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        reader.beginObject();
        ArrayList<News> newsList = new ArrayList<>();
        while(reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("data")) {
                // "data" is a list of news
                reader.beginArray();
                while (reader.hasNext()) {
                    newsList.add(readNews(reader));
                }
                reader.endArray();
            } else reader.skipValue();
        }
        reader.endObject();
        return newsList;
    }
    public static News readNews(JsonReader reader) throws IOException {
        reader.beginObject();
        News news = new News();
        while(reader.hasNext()){
            String name = reader.nextName();
            switch (name) {
//                case "_id":
//                    Long id = Long.parseUnsignedLong(reader.nextString(), 16);
//                    break;
                case "content":
                    news.content = reader.nextString();
                    break;
                case "title":
                    news.title = reader.nextString();
                    break;
                case "authors":
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        while(reader.hasNext()) {
                            if (reader.nextName().equals("name"))
                                news.authors.add(reader.nextString());
                            else reader.skipValue();
                        }
                        reader.endObject();
                    }
                    reader.endArray();
                    break;
                case "time":
                    try {
                        news.date = dateFormat.parse(reader.nextString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    reader.skipValue();
            }
        }
        reader.endObject();
        return news;
    }

}
