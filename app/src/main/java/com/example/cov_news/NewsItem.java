package com.example.cov_news;

import android.content.Intent;
import android.os.Bundle;

import com.anychart.scales.DateTime;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class NewsItem extends AppCompatActivity implements WbShareCallback {
    News news;
    private TextView mHeader;
    private TextView mBody;
    private WbShareHandler shareHandler;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WbSdk.install(this, new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE));
        setContentView(R.layout.activity_news_item);
        mHeader = this.findViewById(R.id.title);
        mBody = this.findViewById(R.id.content);
        shareHandler = new WbShareHandler(this);
        shareHandler.registerApp();
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
                WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
                TextObject content = new TextObject();
                content.text = news.title +'\n'+ news.content;
                weiboMessage.textObject = content;
                shareHandler.shareMessage(weiboMessage, false);
            }
        });
    }

    @Override
    public void onWbShareSuccess() {
        Toast.makeText(this, "分享成功", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWbShareCancel() {
        Toast.makeText(this, "取消分享", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onWbShareFail() {
            Toast.makeText(this, "分享失败\n"+"Error:", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        shareHandler.doResultIntent(intent, this);
    }
}