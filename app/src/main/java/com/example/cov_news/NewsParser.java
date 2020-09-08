package com.example.cov_news;

import android.annotation.SuppressLint;
import android.util.JsonReader;

import com.anychart.scales.DateTime;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsParser{
    @SuppressLint("SimpleDateFormat")
    private static DateTimeFormatter dateFormat = DateTimeFormatter.RFC_1123_DATE_TIME;
    public static List<News> readJsonStream(InputStream in, List<Integer> pagination) throws IOException {
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
            } else if(name.equals("pagination")){
                reader.beginObject();
                while(reader.hasNext()){
                    String name2 = reader.nextName();
                    if(name2.equals("page")) pagination.set(0, reader.nextInt());
                    else if(name2.equals("size")) pagination.set(1, reader.nextInt());
                    else pagination.set(2,reader.nextInt());
                }
                reader.endObject();
            }
                else reader.skipValue();
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
                case "_id":
                    news._id = reader.nextString();
                    news.setId(Long.parseUnsignedLong(news._id.substring(8), 16));
                    break;
                case "entities":
                    reader.beginArray();
                    while(reader.hasNext()){
                        reader.beginObject();
                        while(reader.hasNext()){
                            if(reader.nextName().equals("label"))
                                reader.nextString();
//                                news.labels = reader.nextString().split(" ");
                            else reader.skipValue();
                        }
                        reader.endObject();
                    }
                    reader.endArray();
                    break;
                case "source":
                    news.source = reader.nextString();
                    break;
                case "content":
                    try {
                        news.content = reader.nextString();
                    } catch(StackOverflowError e){
                        e.printStackTrace();
                    }
                    break;
                case "title":
                    news.title = reader.nextString();
                    break;
                case "authors":
                    ArrayList<String> authorsThis=new ArrayList<>();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();
                        while(reader.hasNext()) {
                            if (reader.nextName().equals("name")) {
                                authorsThis.add(reader.nextString());
                            }
                            else reader.skipValue();
                        }
                        reader.endObject();
                    }
                    reader.endArray();
//                    news.authors = authorsThis;
//                    Log.i("NewsParser", String.valueOf("Authors list size: " + news.authors.size()));
                    break;
                case "date":
                    try {
                        LocalDateTime date= LocalDateTime.from(dateFormat.parse(reader.nextString()));
                        news.date = date.toEpochSecond(ZoneOffset.UTC);
                    } catch (DateTimeParseException e) {
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
