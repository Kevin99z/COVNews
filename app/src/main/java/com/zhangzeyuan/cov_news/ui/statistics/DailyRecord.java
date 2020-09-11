package com.zhangzeyuan.cov_news.ui.statistics;

import com.orm.SugarRecord;

import java.util.Date;

public class DailyRecord extends SugarRecord {
    public DailyRecord(){}
    public DailyRecord(String content){
        this.date = new Date(System.currentTimeMillis());
        this.content = content;
        this.setId(date.getTime());
    }
    Date date;
    String content;

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }
}
