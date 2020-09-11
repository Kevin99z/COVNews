package com.example.cov_news.ui.statistics;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;
import com.orm.SugarRecord;

@SmartTable(name="国内疫情")
public class ProvInfo extends SugarRecord {
    public ProvInfo(){

    }
    public ProvInfo(String name, String confirmed, String death, String active){
        this.name = name;
        this.confirmed = confirmed;
        this.death = death;
        this.active = active;
    }
    @SmartColumn(id=1, name="省份")
    private String name;
    @SmartColumn(id=2, name="确诊病例")
    private String confirmed;
    @SmartColumn(id=3, name="死亡病例")
    private String death;
    @SmartColumn(id=4, name="现存病例")
    private String active;
}
