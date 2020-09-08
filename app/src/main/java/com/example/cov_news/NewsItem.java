package com.example.cov_news;

import android.content.Intent;
import android.os.Bundle;

import com.anychart.scales.DateTime;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class NewsItem extends AppCompatActivity {
    News news;
    private TextView mHeader;
    private TextView mBody;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_item);
        mHeader = this.findViewById(R.id.title);
        mBody = this.findViewById(R.id.content);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        news = (News)intent.getSerializableExtra("news");
        if (news != null) {
            mHeader.setText(news.getTitle());
            mBody.setText(news.getContent());
            mBody.append("\n\n 来源: " + news.getSource());
            mBody.append("\n\n " + LocalDateTime.ofInstant(Instant.ofEpochSecond(news.getDate()), ZoneOffset.UTC).format(formatter));
        }
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}