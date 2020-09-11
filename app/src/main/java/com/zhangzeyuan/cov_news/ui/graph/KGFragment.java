package com.zhangzeyuan.cov_news.ui.graph;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.JsonReader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.zhangzeyuan.cov_news.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class KGFragment extends Fragment {

    private KGViewModel KGViewModel;
    private EditText editText;
    private Button button;
    private ProgressBar mProgressBar;
    private Editable query;
    private ArrayList<String> queryList = new ArrayList<String>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        KGViewModel =
                ViewModelProviders.of(this).get(KGViewModel.class);
        View root = inflater.inflate(R.layout.fragment_graph, container, false);
        ListView listView = root.findViewById(R.id.list_view);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_list_item_1, queryList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
                Intent intent = new Intent(getContext(), queryInfo.class);
                intent.putExtra("Query", listView.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });
        editText = root.findViewById(R.id.TEXT);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH||keyEvent!=null&&keyEvent.getKeyCode()==KeyEvent.KEYCODE_ENTER) {
                    query = editText.getText();
                    queryList.clear();
                    searchQuery(String.valueOf(query));
                    InputMethodManager manager = (InputMethodManager) root.getContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(root.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        editText.setHint("键入以搜索");
        return root;
    }

    public void searchQuery(String query){ //搜索所查词语
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
            if(reader.nextName().equals("data")){
                reader.beginArray();
                while(reader.hasNext()){
                    reader.beginObject();
                    while(reader.hasNext()) {
                        if (reader.nextName().equals("label")) {
                            queryList.add(reader.nextString());
                        } else {
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