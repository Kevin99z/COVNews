package com.example.cov_news.ui.graph;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonReader;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cov_news.NoScrollListView;
import com.example.cov_news.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class queryInfo extends AppCompatActivity {

    private TextView textViewQuery, title;
    private NoScrollListView relation_list, prop_list;
    private String query;
    private ImageView imageView;
    private TextView loadMore;
    private MoreAdapter moreAdapter;
    private ArrayAdapter<String> stringAdapter;
    private ArrayList<String> props;
    ArrayList<String> queryList = new ArrayList<String>();
    private boolean isShowMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_info);
        textViewQuery = this.findViewById(R.id.text_view_id);
        imageView = this.findViewById(R.id.img_view);
        prop_list = this.findViewById(R.id.prop_list);
        relation_list = this.findViewById(R.id.list_view);
        this.textViewQuery.setTextColor(Color.BLACK);
        textViewQuery.setMovementMethod(new ScrollingMovementMethod());
//        list.setAdapter(arrayAdapter);
        moreAdapter = new MoreAdapter();
        relation_list.setAdapter(moreAdapter);
        loadMore = findViewById(R.id.loadMore);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShowMore) {
                    loadMore.setText("点击显示更多");
                } else {
                    loadMore.setText("点击收起");
                }
                isShowMore = !isShowMore;
                moreAdapter.notifyDataSetChanged();
            }
        });
        loadMore.setText("点击显示更多");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            query = bundle.getString("Query");
            props=new ArrayList<>();
            querySearch();
            stringAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, props);
            prop_list.setAdapter(stringAdapter);
            title = this.findViewById(R.id.title);
            title.setText(query);
//            arrayAdapter.notifyDataSetChanged();
//            listViewQuery.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        relation_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(relation_list.getContext(), queryInfo.class);
                String arr[] = relation_list.getItemAtPosition(i).toString().split(" ");
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
        boolean finished=false;
        String tmp;
        while(reader.hasNext()){
            if(reader.nextName().equals("data")){
                reader.beginArray();
                while(reader.hasNext()){
                    if(finished) reader.skipValue();
                    reader.beginObject();
                    boolean valid = true;
                    while(reader.hasNext()){
                        if(!valid) {
                            reader.nextName();
                            reader.skipValue();
                        }
                        else switch(reader.nextName()){
                            case "label":
                                if(query.equals(reader.nextString())) finished=true;
                                else valid = false;
                                break;
                            case "abstractInfo":
                                reader.beginObject();
                                while(reader.hasNext()){
                                    switch(reader.nextName()){
                                        case "enwiki":
                                            tmp = reader.nextString();
                                            if(tmp.length()>1)
                                            textViewQuery.append("维基百科:\n"+tmp);
                                            break;
                                        case "baidu":
                                            tmp = reader.nextString();
                                            if(tmp.length()>1)
                                            textViewQuery.append("百度百科:\n"+tmp);
                                            break;
                                        case "zhwiki":
                                            tmp = reader.nextString();
                                            if(tmp.length()>1)
                                                textViewQuery.append("知乎百科:\n"+tmp);
                                            break;
                                        case "COVID":
                                            reader.beginObject();
                                            while(reader.hasNext()){
                                                switch(reader.nextName()){
                                                    case "properties":
                                                        reader.beginObject();
                                                        while(reader.hasNext())
                                                            props.add(String.format("%s: %s",reader.nextName(), reader.nextString()));
                                                        reader.endObject();
                                                        break;
                                                    case "relations":
                                                        reader.beginArray();
                                                        while(reader.hasNext()){
                                                            String listItem = "";
                                                            reader.beginObject();
                                                            while(reader.hasNext()) {
                                                                switch (reader.nextName()) {
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

    private class MoreAdapter extends BaseAdapter {

        public MoreAdapter(){

        }
        @Override
        public int getCount() {
            // 重点区域
            if (isShowMore) {
                return queryList.size();
            } else {
                return Math.min(queryList.size(), 3);
            }
        }

        @Override
        public Object getItem(int i) {
            return queryList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View itemView = View.inflate(getApplicationContext(), R.layout.list_view_item, null);
            TextView name = itemView.findViewById(R.id.name);
            name.setText(queryList.get(i));
            return itemView;

        }
    }
}