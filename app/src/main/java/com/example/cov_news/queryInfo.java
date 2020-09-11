package com.example.cov_news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;

public class queryInfo extends AppCompatActivity {

    private TextView textViewQuery;
    private ListView listViewQuery;
    private String query;
    private ImageView imageView;
    ArrayList<String> queryList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_info);
        textViewQuery = this.findViewById(R.id.text_view_id);
        this.textViewQuery.setTextColor(Color.BLACK);
        textViewQuery.setMovementMethod(new ScrollingMovementMethod());

        imageView = this.findViewById(R.id.img_view);

        listViewQuery = this.findViewById(R.id.list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, queryList);
        listViewQuery.setAdapter(arrayAdapter);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            query = bundle.getString("Query");
            textViewQuery.setText(query + ":\n");
            querySearch();
        }
        listViewQuery.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                Intent intent = new Intent(listViewQuery.getContext(), queryInfo.class);
                String arr[] = listViewQuery.getItemAtPosition(i).toString().split(" ");
                intent.putExtra("Query", arr[1]);
                startActivity(intent);
            }
        });
    }

    private void querySearch(){
        Thread t = new Thread(() -> {
            try {
                String apiAddress = "https://innovaapi.aminer.cn/covid/api/v1/pneumonia/entityquery?entity=";
                @SuppressLint("DefaultLocale")
                URL url = new URL(apiAddress+String.format("%s", query));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    InputStream in = conn.getInputStream();
                    parse(in);
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void parse(InputStream in) throws IOException { //找到词语信息后读取
        JsonReader reader = new JsonReader(new InputStreamReader(in));
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("data")){
                reader.beginArray();
                while(reader.hasNext()){
                    reader.beginObject();
                    while(reader.hasNext()){
                        String name2 = reader.nextName();
                        switch(name2){
                            case "abstractInfo":
                                reader.beginObject();
                                while(reader.hasNext()){
                                    String name3 = reader.nextName();
                                    switch(name3){
                                        case "enwiki":
                                            textViewQuery.append(reader.nextString());
                                            break;
                                        case "baidu":
                                            textViewQuery.append(reader.nextString());
                                            break;
                                        case "zhwiki":
                                            textViewQuery.append(reader.nextString());
                                            break;
                                        case "COVID":
                                            reader.beginObject();
                                            while(reader.hasNext()){
                                                String name4 = reader.nextName();
                                                switch(name4){
                                                    case "relations":
                                                        reader.beginArray();
                                                        while(reader.hasNext()){
                                                            String listItem = "";
                                                            reader.beginObject();
                                                            while(reader.hasNext()) {
                                                                String name5 = reader.nextName();
                                                                switch (name5) {
                                                                    case "relation":
                                                                        listItem = reader.nextString() + ": ";
                                                                        break;
                                                                    case "label":
                                                                        listItem = listItem + reader.nextString();
                                                                        break;
                                                                    default:
                                                                        reader.skipValue();
                                                                }
                                                            }
                                                            queryList.add(listItem);
                                                            reader.endObject();
                                                        }
                                                        reader.endArray();
                                                        break;
                                                    default:
                                                        reader.skipValue();
                                                }
                                            }
                                            reader.endObject();
                                            break;
                                        default:
                                            reader.skipValue();
                                        }
                                }
                                reader.endObject();
                                break;
                            case "img":
                                try {
                                    String url = reader.nextString();
                                    if (url != null) {
                                        InputStream URLcontent = (InputStream) new URL(url).getContent();
                                        Drawable image = Drawable.createFromStream(URLcontent, "your source link");
                                        imageView.setImageDrawable(image);
                                    }
                                }
                                catch(IllegalStateException e){
                                    reader.skipValue();
                                }
                                break;
                            default:
                                reader.skipValue();
                        }
                    }
                    reader.endObject();
                }
                reader.endArray();
            } else{
                reader.skipValue();
            }
        }
        reader.endObject();
    }


}