package com.example.cov_news.ui.scholar;

import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ScholarParser {
    static public Scholar parseScholar(JsonReader reader){
        Scholar s = new Scholar();
        try {
            reader.beginObject();
            while(reader.hasNext()){
                switch (reader.nextName()){
                    case "avatar":
                        s.img_url=reader.nextString();
                        break;
                    case "id":
                        s.setId(Long.parseUnsignedLong(reader.nextString().substring(8), 16));
                        break;
                    case "name":
                        s.name = reader.nextString();
                        break;
                    case "name_zh":
                        s.name_zh = reader.nextString();
                        break;
                    case "num_followed":
                        s.num_followed = reader.nextInt();
                        break;
                    case "num_viewed":
                        s.num_viewed = reader.nextInt();
                        break;
                    case "profile":
                        reader.beginObject();
                        while(reader.hasNext()){
                            switch ((reader.nextName())){
                                case "affiliation":
                                    s.affiliation = reader.nextString();
                                    break;
                                case "affiliation_zh":
                                    s.affiliation_zh = reader.nextString();
                                    break;
                                case "bio":
                                    s.bio = reader.nextString();
                                    break;
                                case "edu":
                                    s.edu = reader.nextString();
                                    break;
                                case "homepage":
                                    s.homepage = reader.nextString();
                                    break;
                                case "note":
                                    s.note = reader.nextString();
                                    break;
                                case "position":
                                    s.position = reader.nextString();
                                    break;
                                case "work":
                                    s.work = reader.nextString();
                                    break;
                                default:
                                    reader.skipValue();
                            }
                        }
                        reader.endObject();
                        break;
                    case "is_passedaway":
                        s.isPassedAway = reader.nextBoolean();
                        break;
                    default:
                        reader.skipValue();
                }
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return s;
    }
    static public List<Scholar> readJson(InputStream in){
        List<Scholar> scholars = new ArrayList<>();
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        try {
            reader.beginObject();
            while(reader.hasNext()){
                if(reader.nextName().equals("data")){
                    reader.beginArray();
                    while(reader.hasNext()) scholars.add(parseScholar(reader));
                    reader.endArray();
                } else reader.skipValue();
            }
            reader.endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return scholars;
    }
}
