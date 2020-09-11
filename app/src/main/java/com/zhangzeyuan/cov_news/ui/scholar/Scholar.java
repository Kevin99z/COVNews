package com.zhangzeyuan.cov_news.ui.scholar;

import com.orm.SugarRecord;

import java.io.Serializable;

public class Scholar extends SugarRecord implements Serializable {
    public String img_url;//avatar
    public String id;
    public String name;
    public String name_zh;
    public int num_followed;
    public int num_viewed;
    public String affiliation;
    public String affiliation_zh;
    public String bio;
    public String edu;
    public String homepage;
    public String note;
    public String position;
    public String work;
    public boolean isPassedAway;

    @Override
    public String toString() {
        if(name_zh!=null&&!name_zh.isEmpty())
        return name_zh;
        else return name;
    }
}
